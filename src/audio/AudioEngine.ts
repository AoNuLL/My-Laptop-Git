import { PitchShifter } from './PitchShifter';
import { FormantShifter } from './FormantShifter';
import { OnnxInference, InferenceResult } from './OnnxInference';
import { RVCConfig, AudioState, WaveformData, ProcessingMode, RVCModelMeta } from '../types';

export class AudioEngine {
  private audioContext: AudioContext | null = null;
  private mediaStream: MediaStream | null = null;
  private sourceNode: MediaStreamAudioSourceNode | null = null;
  private analyserInput: AnalyserNode | null = null;
  private analyserOutput: AnalyserNode | null = null;
  private gainNode: GainNode | null = null;
  private scriptNode: ScriptProcessorNode | null = null;
  private noiseGateNode: GainNode | null = null;
  private reverbGain: GainNode | null = null;
  private dryGain: GainNode | null = null;

  private pitchShifter: PitchShifter | null = null;
  private formantShifter: FormantShifter | null = null;
  private onnxInference: OnnxInference | null = null;

  private config: RVCConfig = {
    pitchShift: 0,
    formantShift: 0,
    gain: 1.0,
    noiseGate: 0.02,
    reverb: 0,
  };

  private mode: ProcessingMode = 'dsp';
  private accumulatedAudio: Float32Array;
  private accOffset: number = 0;
  private f0Estimate: number = 220;

  private onStateChange: ((state: AudioState) => void) | null = null;
  private onWaveform: ((data: WaveformData) => void) | null = null;
  private animationFrame: number = 0;
  private isRunning: boolean = false;

  constructor() {
    this.accumulatedAudio = new Float32Array(16384);
  }

  async start(onState: (state: AudioState) => void, onWave: (data: WaveformData) => void): Promise<void> {
    this.onStateChange = onState;
    this.onWaveform = onWave;

    this.audioContext = new AudioContext({ sampleRate: 44100 });
    await this.audioContext.resume();

    this.mediaStream = await navigator.mediaDevices.getUserMedia({
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
        sampleRate: 44100,
        channelCount: 1,
      },
    });

    this.pitchShifter = new PitchShifter(this.audioContext.sampleRate);
    this.formantShifter = new FormantShifter(this.audioContext.sampleRate);

    this.setupAudioGraph();
    this.isRunning = true;
    this.startMetering();

    this.emitState({ isRecording: true, isProcessing: true, inputLevel: 0, outputLevel: 0, latency: 0 });
  }

  async setMode(mode: ProcessingMode, modelId?: string): Promise<void> {
    this.mode = mode;
    if (mode === 'onnx' && modelId) {
      if (!this.onnxInference) {
        this.onnxInference = new OnnxInference(this.audioContext?.sampleRate || 44100);
      }
      await this.onnxInference.loadModel(modelId);
    }
  }

  setOnnxModel(modelId: string): void {
    this.mode = 'onnx';
    this.onnxInference = new OnnxInference(this.audioContext?.sampleRate || 44100);
    this.onnxInference.loadModel(modelId);
  }

  getMode(): ProcessingMode {
    return this.mode;
  }

  getOnnxMeta(): RVCModelMeta | null {
    return this.onnxInference?.getMeta() || null;
  }

  private setupAudioGraph(): void {
    if (!this.audioContext || !this.mediaStream) return;

    this.sourceNode = this.audioContext.createMediaStreamSource(this.mediaStream);

    this.analyserInput = this.audioContext.createAnalyser();
    this.analyserInput.fftSize = 2048;
    this.analyserInput.smoothingTimeConstant = 0.8;

    this.analyserOutput = this.audioContext.createAnalyser();
    this.analyserOutput.fftSize = 2048;
    this.analyserOutput.smoothingTimeConstant = 0.8;

    this.gainNode = this.audioContext.createGain();
    this.gainNode.gain.value = this.config.gain;

    this.dryGain = this.audioContext.createGain();
    this.reverbGain = this.audioContext.createGain();
    this.dryGain.gain.value = 1;
    this.reverbGain.gain.value = this.config.reverb;

    const bufferSize = 2048;
    this.scriptNode = this.audioContext.createScriptProcessor(bufferSize, 1, 1);

    this.sourceNode.connect(this.analyserInput);
    this.analyserInput.connect(this.scriptNode);

    this.scriptNode.onaudioprocess = (e) => {
      this.processAudio(e);
    };

    this.scriptNode.connect(this.dryGain);
    this.scriptNode.connect(this.reverbGain);

    this.dryGain.connect(this.gainNode);
    this.reverbGain.connect(this.gainNode);
    this.gainNode.connect(this.analyserOutput);
    this.analyserOutput.connect(this.audioContext!.destination);
  }

  private processAudio(e: AudioProcessingEvent): void {
    if (!this.pitchShifter || !this.formantShifter) return;

    const inputData = e.inputBuffer.getChannelData(0);
    const outputData = e.outputBuffer.getChannelData(0);

    outputData.fill(0);

    let maxAmplitude = 0;
    for (let i = 0; i < inputData.length; i++) {
      maxAmplitude = Math.max(maxAmplitude, Math.abs(inputData[i]));
    }

    if (maxAmplitude < this.config.noiseGate) return;

    const newInput = new Float32Array(inputData);

    if (this.mode === 'onnx' && this.onnxInference?.isLoaded()) {
      this.estimateF0(newInput);

      const copyLen = Math.min(newInput.length, this.accumulatedAudio.length - this.accOffset);
      this.accumulatedAudio.set(newInput.subarray(0, copyLen), this.accOffset);
      this.accOffset += copyLen;

      if (this.accOffset >= 4096) {
        const chunk = this.accumulatedAudio.slice(0, this.accOffset);
        this.onnxInference.process(chunk, this.f0Estimate).then((result: InferenceResult) => {
          const resampled = this.resampleLinear(result.output, outputData.length);
          for (let i = 0; i < Math.min(resampled.length, outputData.length); i++) {
            outputData[i] = resampled[i];
          }
        }).catch(() => {
          this.fallbackDspProcess(newInput, outputData);
        });

        this.accOffset = 0;
        this.accumulatedAudio.fill(0);
      }
    } else {
      this.fallbackDspProcess(newInput, outputData);
    }
  }

  private fallbackDspProcess(input: Float32Array, output: Float32Array): void {
    let processed = this.pitchShifter!.process(input, this.config.pitchShift);

    if (this.config.formantShift !== 0) {
      processed = this.formantShifter!.process(processed);
    }

    const resampled = this.resampleLinear(processed, output.length);
    for (let i = 0; i < Math.min(resampled.length, output.length); i++) {
      output[i] = resampled[i];
    }
  }

  private estimateF0(audio: Float32Array): void {
    let sum = 0;
    const len = Math.min(audio.length, 1024);
    for (let i = 0; i < len; i++) sum += audio[i] * audio[i];
    if (sum < 1e-6) return;

    const corr = new Float32Array(len);
    for (let lag = 20; lag < Math.min(500, len); lag++) {
      let s = 0;
      for (let i = 0; i < len - lag; i++) {
        s += audio[i] * audio[i + lag];
      }
      corr[lag] = s;
    }

    let bestLag = 40;
    let bestVal = -1;
    for (let lag = 40; lag < Math.min(500, len); lag++) {
      if (corr[lag] > bestVal) {
        bestVal = corr[lag];
        bestLag = lag;
      }
    }

    if (bestVal > 0) {
      this.f0Estimate = 44100 / bestLag;
    }
  }

  private resampleLinear(input: Float32Array, targetLength: number): Float32Array {
    const output = new Float32Array(targetLength);
    if (input.length === targetLength) return input;

    const ratio = (input.length - 1) / (targetLength - 1);
    for (let i = 0; i < targetLength; i++) {
      const pos = i * ratio;
      const posFloor = Math.floor(pos);
      const posFrac = pos - posFloor;
      const idx = Math.min(posFloor, input.length - 2);
      output[i] = input[idx] * (1 - posFrac) + input[idx + 1] * posFrac;
    }
    return output;
  }

  updateConfig(config: Partial<RVCConfig>): void {
    this.config = { ...this.config, ...config };

    if (this.formantShifter) {
      this.formantShifter.setShift(this.config.formantShift);
    }
    if (this.gainNode && this.audioContext) {
      this.gainNode.gain.setTargetAtTime(this.config.gain, this.audioContext.currentTime, 0.05);
    }
    if (this.reverbGain && this.audioContext) {
      this.reverbGain.gain.setTargetAtTime(this.config.reverb, this.audioContext.currentTime, 0.1);
    }
    if (this.dryGain && this.audioContext) {
      this.dryGain.gain.setTargetAtTime(1 - this.config.reverb * 0.5, this.audioContext.currentTime, 0.1);
    }
  }

  private startMetering(): void {
    const update = () => {
      if (!this.isRunning) return;

      const waveformData = this.getWaveformData();
      const inputLevel = this.getRMSLevel(this.analyserInput);
      const outputLevel = this.getRMSLevel(this.analyserOutput);

      if (this.onWaveform && waveformData) {
        this.onWaveform(waveformData);
      }

      if (this.onStateChange) {
        const latency = this.audioContext
          ? (this.audioContext.baseLatency || 0.005) * 1000
          : 0;
        this.onStateChange({
          isRecording: true,
          isProcessing: true,
          inputLevel,
          outputLevel,
          latency,
        });
      }

      this.animationFrame = requestAnimationFrame(update);
    };
    update();
  }

  private getWaveformData(): WaveformData | null {
    if (!this.analyserOutput) return null;

    const timeData = new Float32Array(this.analyserOutput.fftSize);
    const freqData = new Uint8Array(this.analyserOutput.frequencyBinCount);
    this.analyserOutput.getFloatTimeDomainData(timeData);
    this.analyserOutput.getByteFrequencyData(freqData);

    return { timeData, freqData };
  }

  private getRMSLevel(analyser: AnalyserNode | null): number {
    if (!analyser) return 0;
    const data = new Float32Array(analyser.fftSize);
    analyser.getFloatTimeDomainData(data);

    let sum = 0;
    for (let i = 0; i < data.length; i++) {
      sum += data[i] * data[i];
    }
    return Math.sqrt(sum / data.length);
  }

  stop(): void {
    this.isRunning = false;
    cancelAnimationFrame(this.animationFrame);

    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach((t) => t.stop());
    }
    if (this.audioContext) {
      this.audioContext.close();
    }

    this.onnxInference?.dispose();
    this.onnxInference = null;
    this.accOffset = 0;
    this.mode = 'dsp';

    this.sourceNode = null;
    this.analyserInput = null;
    this.analyserOutput = null;
    this.gainNode = null;
    this.scriptNode = null;
    this.audioContext = null;
    this.mediaStream = null;
  }

  private emitState(state: AudioState): void {
    if (this.onStateChange) this.onStateChange(state);
  }
}
