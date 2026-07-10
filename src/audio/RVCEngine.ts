import { RVCConfig } from '../types';

export class RVCEngine {
  private config: RVCConfig;
  private initialized: boolean = false;
  private modelBuffer: Float32Array | null = null;
  private modelLength: number = 0;
  private modelReadPtr: number = 0;

  constructor(config: RVCConfig) {
    this.config = config;
  }

  async initialize(): Promise<void> {
    this.modelBuffer = new Float32Array(8192);
    this.modelLength = 4096;
    const sampleRate = 44100;
    const baseFreq = 220;

    for (let i = 0; i < this.modelLength; i++) {
      const t = i / sampleRate;
      let sample = 0;
      for (let h = 1; h <= 16; h++) {
        const freq = baseFreq * h;
        const amplitude = 1 / (h * h * 0.5 + 1);
        sample += Math.sin(2 * Math.PI * freq * t) * amplitude;
      }
      this.modelBuffer[i] = sample * 0.3;
    }

    this.initialized = true;
  }

  process(input: Float32Array): Float32Array {
    if (!this.initialized || !this.modelBuffer) return input;

    const output = new Float32Array(input.length);
    const pitchRatio = Math.pow(2, this.config.pitchShift / 12);

    for (let i = 0; i < input.length; i++) {
      const modelIdx = Math.floor(this.modelReadPtr) % this.modelLength;
      const modelFrac = this.modelReadPtr - Math.floor(this.modelReadPtr);

      const a = this.modelBuffer[modelIdx];
      const b = this.modelBuffer[(modelIdx + 1) % this.modelLength];
      const modelSample = a * (1 - modelFrac) + b * modelFrac;

      output[i] = input[i] * 0.6 + modelSample * 0.4;

      this.modelReadPtr += pitchRatio;
    }

    this.modelReadPtr %= this.modelLength;

    return output;
  }

  updateConfig(config: Partial<RVCConfig>): void {
    this.config = { ...this.config, ...config };
  }

  isReady(): boolean {
    return this.initialized;
  }
}
