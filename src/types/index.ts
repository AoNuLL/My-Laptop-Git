export interface VoicePreset {
  id: string;
  name: string;
  description: string;
  pitchShift: number;
  formantShift: number;
  icon: string;
}

export interface AudioState {
  isRecording: boolean;
  isProcessing: boolean;
  inputLevel: number;
  outputLevel: number;
  latency: number;
}

export interface RVCConfig {
  pitchShift: number;
  formantShift: number;
  gain: number;
  noiseGate: number;
  reverb: number;
}

export interface WaveformData {
  timeData: Float32Array;
  freqData: Uint8Array;
}

export interface RVCModelMeta {
  id: string;
  name: string;
  fileName: string;
  fileSize: number;
  importedAt: number;
  sampleRate: number;
  hopSize: number;
  f0InputKey: string;
  embedInputKey: string;
  audioOutputKey: string;
  hasEmbedInput: boolean;
  inputNames: string[];
  outputNames: string[];
  status: 'loading' | 'ready' | 'error';
  errorMessage?: string;
}

export type ProcessingMode = 'dsp' | 'onnx';

export interface ModelLoadProgress {
  stage: string;
  progress: number;
}
