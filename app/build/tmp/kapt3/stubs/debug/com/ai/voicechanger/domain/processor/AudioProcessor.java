package com.ai.voicechanger.domain.processor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\n\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001)B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u0018\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nH\u0002J4\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J(\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00072\u0006\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\u0007H\u0002J\u0018\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u0004H\u0002JH\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001e\u001a\u00020\u000f2\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\r0 H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b!\u0010\"J \u0010#\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u00042\u0006\u0010%\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\u0007H\u0002J \u0010\'\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u00042\u0006\u0010%\u001a\u00020(2\u0006\u0010&\u001a\u00020\u0007H\u0002\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006*"}, d2 = {"Lcom/ai/voicechanger/domain/processor/AudioProcessor;", "", "()V", "applyPitchShift", "", "audioData", "semitones", "", "applyVoiceChange", "modelPath", "", "convertFormat", "Lkotlin/Result;", "", "inputFile", "Ljava/io/File;", "outputFormat", "Lcom/ai/voicechanger/domain/processor/AudioProcessor$AudioFormat;", "outputFile", "convertFormat-BWLJW6A", "(Ljava/io/File;Lcom/ai/voicechanger/domain/processor/AudioProcessor$AudioFormat;Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createWavFile", "pcmData", "sampleRate", "channels", "bitsPerSample", "mixAudio", "original", "effect", "processWithModel", "outputPath", "progressCallback", "Lkotlin/Function1;", "processWithModel-yxL6bBk", "(Ljava/io/File;Ljava/lang/String;Ljava/io/File;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "writeInt", "array", "value", "offset", "writeShort", "", "AudioFormat", "app_debug"})
public final class AudioProcessor {
    
    public AudioProcessor() {
        super();
    }
    
    private final byte[] applyVoiceChange(byte[] audioData, java.lang.String modelPath) {
        return null;
    }
    
    private final byte[] mixAudio(byte[] original, byte[] effect) {
        return null;
    }
    
    private final byte[] applyPitchShift(byte[] audioData, int semitones) {
        return null;
    }
    
    private final byte[] createWavFile(byte[] pcmData, int sampleRate, int channels, int bitsPerSample) {
        return null;
    }
    
    private final void writeShort(byte[] array, short value, int offset) {
    }
    
    private final void writeInt(byte[] array, int value, int offset) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/ai/voicechanger/domain/processor/AudioProcessor$AudioFormat;", "", "(Ljava/lang/String;I)V", "WAV", "MP3", "AAC", "app_debug"})
    public static enum AudioFormat {
        /*public static final*/ WAV /* = new WAV() */,
        /*public static final*/ MP3 /* = new MP3() */,
        /*public static final*/ AAC /* = new AAC() */;
        
        AudioFormat() {
        }
        
        @org.jetbrains.annotations.NotNull
        public static kotlin.enums.EnumEntries<com.ai.voicechanger.domain.processor.AudioProcessor.AudioFormat> getEntries() {
            return null;
        }
    }
}