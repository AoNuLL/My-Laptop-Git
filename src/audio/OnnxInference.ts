import { RVCModelMeta } from '../types';
import { modelManager } from './ModelManager';
import { AudioResampler } from './AudioResampler';

export interface InferenceResult {
  output: Float32Array;
  inferenceTime: number;
}

export class OnnxInference {
  private session: any = null;
  private meta: RVCModelMeta | null = null;
  private resampler: AudioResampler;
  private inputSampleRate: number;

  constructor(inputSampleRate: number) {
    this.inputSampleRate = inputSampleRate;
    this.resampler = new AudioResampler();
  }

  async loadModel(modelId: string): Promise<RVCModelMeta> {
    const meta = await modelManager.getMeta(modelId);
    if (!meta) throw new Error('Model not found: ' + modelId);

    const data = await modelManager.getModelData(modelId);
    if (!data) throw new Error('Model data not found: ' + modelId);

    const ort = await import('onnxruntime-web');

    this.session = await ort.InferenceSession.create(data, {
      executionProviders: ['wasm'],
      graphOptimizationLevel: 'all',
      enableCpuMemArena: true,
    });

    this.meta = meta;
    return meta;
  }

  async process(input: Float32Array, f0: number): Promise<InferenceResult> {
    if (!this.session || !this.meta) {
      throw new Error('Model not loaded');
    }

    const modelSampleRate = this.meta.sampleRate;
    const t0 = performance.now();

    const resampled = this.resampler.resample(
      input,
      this.inputSampleRate,
      modelSampleRate,
    );

    const hopSize = this.meta.hopSize;
    const frameSize = hopSize * 4;
    const numFrames = Math.max(1, Math.floor((resampled.length - frameSize) / hopSize) + 1);

    const audio = this.padToLength(resampled, numFrames * hopSize + frameSize - hopSize);

    const melInput = this.computeMelSpectrogram(audio, numFrames, hopSize, frameSize);

    const feeds: Record<string, any> = {};

    if (this.meta.hasEmbedInput && this.meta.embedInputKey) {
      const embed = this.generatePlaceholderEmbed(numFrames);
      feeds[this.meta.embedInputKey] = embed;
    }

    if (this.meta.f0InputKey) {
      const f0Tensor = new Float32Array(numFrames).fill(f0);
      feeds[this.meta.f0InputKey] = this.makeTensor(f0Tensor, [1, numFrames]);
    }

    feeds[this.meta.inputNames[0]] = melInput;

    const results = await this.session.run(feeds);
    const outputKey = this.meta.audioOutputKey;
    const outputTensor = results[outputKey];
    const outputData = new Float32Array(outputTensor.data);

    const finalOutput = this.resampler.resample(
      outputData,
      modelSampleRate,
      this.inputSampleRate,
    );

    const t1 = performance.now();

    return {
      output: finalOutput,
      inferenceTime: t1 - t0,
    };
  }

  private computeMelSpectrogram(
    audio: Float32Array,
    numFrames: number,
    hopSize: number,
    frameSize: number,
  ): any {
    const nMel = 128;
    const melData = new Float32Array(1 * nMel * numFrames);
    const window = this.hannWindow(frameSize);

    for (let f = 0; f < numFrames; f++) {
      const offset = f * hopSize;
      const fftReal = new Float32Array(frameSize);
      const fftImag = new Float32Array(frameSize);

      for (let i = 0; i < frameSize; i++) {
        fftReal[i] = (audio[offset + i] || 0) * window[i];
      }

      this.realFFT(fftReal, fftImag);

      const powerSpec = new Float32Array(frameSize / 2);
      for (let i = 0; i < frameSize / 2; i++) {
        powerSpec[i] = fftReal[i] * fftReal[i] + fftImag[i] * fftImag[i];
      }

      const melBands = this.linearToMel(powerSpec, nMel);

      const baseIdx = f * nMel;
      for (let m = 0; m < nMel; m++) {
        melData[baseIdx + m] = Math.log(Math.max(melBands[m], 1e-5));
      }
    }

    const ort = (window as any).ort;
    if (ort) {
      return new ort.Tensor('float32', melData, [1, nMel, numFrames]);
    }

    return {
      type: 'float32',
      data: melData,
      dims: [1, nMel, numFrames],
    };
  }

  private makeTensor(data: Float32Array, dims: number[]): any {
    const ort = (window as any).ort;
    if (ort) {
      return new ort.Tensor('float32', data, dims);
    }
    return { type: 'float32', data, dims };
  }

  private realFFT(real: Float32Array, imag: Float32Array): void {
    const n = real.length;
    if (n <= 1 || (n & (n - 1)) !== 0) return;

    let j = 0;
    for (let i = 0; i < n; i++) {
      if (i < j) {
        [real[i], real[j]] = [real[j], real[i]];
      }
      let m = n >> 1;
      while (m > 0 && j >= m) {
        j -= m;
        m >>= 1;
      }
      j += m;
    }

    for (let len = 2; len <= n; len <<= 1) {
      const halfLen = len >> 1;
      const angle = (-2 * Math.PI) / len;
      const wReal = Math.cos(angle);
      const wImag = Math.sin(angle);

      for (let i = 0; i < n; i += len) {
        let curReal = 1;
        let curImag = 0;

        for (let k = 0; k < halfLen; k++) {
          const idxA = i + k;
          const idxB = i + k + halfLen;

          const tReal = curReal * real[idxB] - curImag * imag[idxB];
          const tImag = curReal * imag[idxB] + curImag * real[idxB];

          real[idxB] = real[idxA] - tReal;
          imag[idxB] = imag[idxA] - tImag;
          real[idxA] += tReal;
          imag[idxA] += tImag;

          const newReal = curReal * wReal - curImag * wImag;
          const newImag = curReal * wImag + curImag * wReal;
          curReal = newReal;
          curImag = newImag;
        }
      }
    }
  }

  private linearToMel(powerSpec: Float32Array, nMel: number): Float32Array {
    const nFft = powerSpec.length;
    const sampleRate = this.meta?.sampleRate || 40000;
    const mel = new Float32Array(nMel);

    const melLow = this.hzToMel(80);
    const melHigh = this.hzToMel(sampleRate / 2);
    const melStep = (melHigh - melLow) / (nMel + 1);

    for (let m = 0; m < nMel; m++) {
      const melCenter = melLow + (m + 1) * melStep;
      const hzCenter = this.melToHz(melCenter);
      const fftBin = (hzCenter / (sampleRate / 2)) * (nFft - 1);

      let sum = 0;
      let weightSum = 0;
      const binLow = Math.max(0, Math.floor(fftBin - 1));
      const binHigh = Math.min(nFft - 1, Math.ceil(fftBin + 1));

      for (let b = binLow; b <= binHigh; b++) {
        const weight = 1 - Math.abs(b - fftBin) / 2;
        sum += powerSpec[b] * weight;
        weightSum += weight;
      }
      mel[m] = weightSum > 0 ? sum / weightSum : 0;
    }

    return mel;
  }

  private hzToMel(hz: number): number {
    return 2595 * Math.log10(1 + hz / 700);
  }

  private melToHz(mel: number): number {
    return 700 * (Math.pow(10, mel / 2595) - 1);
  }

  private hannWindow(size: number): Float32Array {
    const w = new Float32Array(size);
    for (let i = 0; i < size; i++) {
      w[i] = 0.5 * (1 - Math.cos((2 * Math.PI * i) / (size - 1)));
    }
    return w;
  }

  private generatePlaceholderEmbed(numFrames: number): any {
    const embedDim = 256;
    const embed = new Float32Array(1 * embedDim * numFrames);
    for (let i = 0; i < embed.length; i++) {
      embed[i] = (Math.random() - 0.5) * 0.1;
    }
    const ort = (window as any).ort;
    if (ort) {
      return new ort.Tensor('float32', embed, [1, embedDim, numFrames]);
    }
    return { type: 'float32', data: embed, dims: [1, embedDim, numFrames] };
  }

  private padToLength(input: Float32Array, length: number): Float32Array {
    if (input.length >= length) return input.slice(0, length);
    const output = new Float32Array(length);
    output.set(input);
    return output;
  }

  isLoaded(): boolean {
    return this.session !== null && this.meta !== null;
  }

  getMeta(): RVCModelMeta | null {
    return this.meta;
  }

  dispose(): void {
    this.session = null;
    this.meta = null;
  }
}
