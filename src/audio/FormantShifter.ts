export class FormantShifter {
  private sampleRate: number;
  private shiftAmount: number = 0;

  constructor(sampleRate: number) {
    this.sampleRate = sampleRate;
  }

  setShift(semitones: number): void {
    this.shiftAmount = semitones;
  }

  process(input: Float32Array): Float32Array {
    if (this.shiftAmount === 0) return input;
    return this.shiftFormants(input, this.shiftAmount);
  }

  private shiftFormants(input: Float32Array, shift: number): Float32Array {
    const output = new Float32Array(input.length);
    const factors = [0.8, 0.95, 1.0, 1.05, 1.2];
    const ratio = Math.pow(2, shift / 36);

    const filterSize = 128;
    const allpassDelay = new Float32Array(filterSize);
    let delayIdx = 0;

    for (let i = 0; i < input.length; i++) {
      allpassDelay[delayIdx] = input[i];
      const readIdx = (delayIdx - Math.floor(filterSize * ratio * 0.1) + filterSize) % filterSize;

      let sum = 0;
      for (let f = 0; f < factors.length; f++) {
        const idx = Math.floor(i * factors[f]) % input.length;
        const delayedIdx = (delayIdx - Math.floor(filterSize * ratio * (1 - factors[f] * 0.1)) + filterSize) % filterSize;
        sum += input[idx] * 0.3 + allpassDelay[delayedIdx] * 0.7;
      }
      output[i] = sum / factors.length;

      delayIdx = (delayIdx + 1) % filterSize;
    }

    return output;
  }
}
