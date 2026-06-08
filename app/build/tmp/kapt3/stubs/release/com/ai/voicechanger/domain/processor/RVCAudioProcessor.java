package com.ai.voicechanger.domain.processor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0001\u001eB\u0005\u00a2\u0006\u0002\u0010\u0002J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0002J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\bH\u0002J\u0010\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J,\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00132\u0006\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u0016H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0006J\u000e\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001f"}, d2 = {"Lcom/ai/voicechanger/domain/processor/RVCAudioProcessor;", "", "()V", "config", "Lcom/ai/voicechanger/domain/processor/RVCAudioProcessor$AudioConfig;", "estimateF0", "", "audioData", "", "start", "", "end", "extractF0", "floatArrayToFloatBuffer", "Ljava/nio/ByteBuffer;", "floatArray", "floatBufferToFloatArray", "buffer", "processAudio", "Lkotlin/Result;", "inputData", "model", "Lcom/ai/voicechanger/domain/processor/RVCInferenceModel;", "processAudio-0E7RQCE", "(Ljava/nio/ByteBuffer;Lcom/ai/voicechanger/domain/processor/RVCInferenceModel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setPitchChange", "", "semitones", "setSampleRate", "sampleRate", "AudioConfig", "app_release"})
public final class RVCAudioProcessor {
    @org.jetbrains.annotations.NotNull
    private com.ai.voicechanger.domain.processor.RVCAudioProcessor.AudioConfig config;
    
    public RVCAudioProcessor() {
        super();
    }
    
    private final float[] floatBufferToFloatArray(java.nio.ByteBuffer buffer) {
        return null;
    }
    
    private final java.nio.ByteBuffer floatArrayToFloatBuffer(float[] floatArray) {
        return null;
    }
    
    private final float[] extractF0(float[] audioData) {
        return null;
    }
    
    private final float estimateF0(float[] audioData, int start, int end) {
        return 0.0F;
    }
    
    public final void setPitchChange(float semitones) {
    }
    
    public final void setSampleRate(int sampleRate) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0007H\u00c6\u0003JE\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000f\u00a8\u0006 "}, d2 = {"Lcom/ai/voicechanger/domain/processor/RVCAudioProcessor$AudioConfig;", "", "sampleRate", "", "hopSize", "winSize", "f0Min", "", "f0Max", "pitchChange", "(IIIFFF)V", "getF0Max", "()F", "getF0Min", "getHopSize", "()I", "getPitchChange", "getSampleRate", "getWinSize", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"})
    public static final class AudioConfig {
        private final int sampleRate = 0;
        private final int hopSize = 0;
        private final int winSize = 0;
        private final float f0Min = 0.0F;
        private final float f0Max = 0.0F;
        private final float pitchChange = 0.0F;
        
        public AudioConfig(int sampleRate, int hopSize, int winSize, float f0Min, float f0Max, float pitchChange) {
            super();
        }
        
        public final int getSampleRate() {
            return 0;
        }
        
        public final int getHopSize() {
            return 0;
        }
        
        public final int getWinSize() {
            return 0;
        }
        
        public final float getF0Min() {
            return 0.0F;
        }
        
        public final float getF0Max() {
            return 0.0F;
        }
        
        public final float getPitchChange() {
            return 0.0F;
        }
        
        public AudioConfig() {
            super();
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        public final float component6() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.ai.voicechanger.domain.processor.RVCAudioProcessor.AudioConfig copy(int sampleRate, int hopSize, int winSize, float f0Min, float f0Max, float pitchChange) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}