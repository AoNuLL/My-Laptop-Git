export class PitchShifter {
  private sampleRate: number;
  private phase: number = 0;
  private lastPhase: Float32Array = new Float32Array(2048);

  constructor(sampleRate: number) {
    this.sampleRate = sampleRate;
  }

  process(input: Float32Array, semitones: number): Float32Array {
    if (semitones === 0) return input;

    const ratio = Math.pow(2, semitones / 12);
    const outputLength = Math.floor(input.length / ratio);
    const output = new Float32Array(outputLength);

    let outIdx = 0;
    while (outIdx < outputLength) {
      const srcIdx = outIdx * ratio;
      const srcFloor = Math.floor(srcIdx);
      const srcFrac = srcIdx - srcFloor;

      if (srcFloor + 1 < input.length) {
        output[outIdx] = input[srcFloor] * (1 - srcFrac) + input[srcFloor + 1] * srcFrac;
      } else if (srcFloor < input.length) {
        output[outIdx] = input[srcFloor];
      }

      outIdx++;
    }

    return output;
  }
}
