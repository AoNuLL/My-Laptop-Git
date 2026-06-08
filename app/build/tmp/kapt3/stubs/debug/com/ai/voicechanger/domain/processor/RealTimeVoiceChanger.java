package com.ai.voicechanger.domain.processor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0017\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u000212B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0002J\b\u0010!\u001a\u00020\rH\u0002J\b\u0010\"\u001a\u00020\u000fH\u0002J\u0010\u0010#\u001a\u00020 2\u0006\u0010$\u001a\u00020%H\u0002J\u0006\u0010&\u001a\u00020\tJ\u000e\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020*J\u001c\u0010+\u001a\b\u0012\u0004\u0012\u00020(0,H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b-\u0010.J\b\u0010/\u001a\u00020(H\u0002J\u0006\u00100\u001a\u00020(R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\t0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0017\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00063"}, d2 = {"Lcom/ai/voicechanger/domain/processor/RealTimeVoiceChanger;", "", "model", "Lcom/ai/voicechanger/domain/processor/RVCInferenceModel;", "audioProcessor", "Lcom/ai/voicechanger/domain/processor/RVCAudioProcessor;", "(Lcom/ai/voicechanger/domain/processor/RVCInferenceModel;Lcom/ai/voicechanger/domain/processor/RVCAudioProcessor;)V", "_latencyMs", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_state", "Lcom/ai/voicechanger/domain/processor/RealTimeVoiceChanger$State;", "audioRecord", "Landroid/media/AudioRecord;", "audioTrack", "Landroid/media/AudioTrack;", "config", "Lcom/ai/voicechanger/domain/processor/RealTimeVoiceChanger$RealTimeConfig;", "isRunning", "", "latencyMs", "Lkotlinx/coroutines/flow/StateFlow;", "getLatencyMs", "()Lkotlinx/coroutines/flow/StateFlow;", "processingJob", "Lkotlinx/coroutines/Job;", "recordingJob", "state", "getState", "byteBufferToShortArray", "", "buffer", "Ljava/nio/ByteBuffer;", "createAudioRecord", "createAudioTrack", "floatArrayToByteBuffer", "floatArray", "", "getCurrentLatency", "setPitchChange", "", "semitones", "", "start", "Lkotlin/Result;", "start-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startProcessingLoop", "stop", "RealTimeConfig", "State", "app_debug"})
public final class RealTimeVoiceChanger {
    @org.jetbrains.annotations.NotNull
    private final com.ai.voicechanger.domain.processor.RVCInferenceModel model = null;
    @org.jetbrains.annotations.NotNull
    private final com.ai.voicechanger.domain.processor.RVCAudioProcessor audioProcessor = null;
    @org.jetbrains.annotations.Nullable
    private android.media.AudioRecord audioRecord;
    @org.jetbrains.annotations.Nullable
    private android.media.AudioTrack audioTrack;
    private boolean isRunning = false;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job recordingJob;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job processingJob;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ai.voicechanger.domain.processor.RealTimeVoiceChanger.State> _state = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ai.voicechanger.domain.processor.RealTimeVoiceChanger.State> state = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _latencyMs = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> latencyMs = null;
    @org.jetbrains.annotations.NotNull
    private final com.ai.voicechanger.domain.processor.RealTimeVoiceChanger.RealTimeConfig config = null;
    
    public RealTimeVoiceChanger(@org.jetbrains.annotations.NotNull
    com.ai.voicechanger.domain.processor.RVCInferenceModel model, @org.jetbrains.annotations.NotNull
    com.ai.voicechanger.domain.processor.RVCAudioProcessor audioProcessor) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ai.voicechanger.domain.processor.RealTimeVoiceChanger.State> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getLatencyMs() {
        return null;
    }
    
    private final android.media.AudioRecord createAudioRecord() {
        return null;
    }
    
    private final android.media.AudioTrack createAudioTrack() {
        return null;
    }
    
    private final void startProcessingLoop() {
    }
    
    private final java.nio.ByteBuffer floatArrayToByteBuffer(float[] floatArray) {
        return null;
    }
    
    private final short[] byteBufferToShortArray(java.nio.ByteBuffer buffer) {
        return null;
    }
    
    public final void stop() {
    }
    
    public final void setPitchChange(float semitones) {
    }
    
    public final long getCurrentLatency() {
        return 0L;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00072\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2 = {"Lcom/ai/voicechanger/domain/processor/RealTimeVoiceChanger$RealTimeConfig;", "", "sampleRate", "", "bufferSize", "hopSize", "useGPU", "", "(IIIZ)V", "getBufferSize", "()I", "getHopSize", "getSampleRate", "getUseGPU", "()Z", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "", "app_debug"})
    public static final class RealTimeConfig {
        private final int sampleRate = 0;
        private final int bufferSize = 0;
        private final int hopSize = 0;
        private final boolean useGPU = false;
        
        public RealTimeConfig(int sampleRate, int bufferSize, int hopSize, boolean useGPU) {
            super();
        }
        
        public final int getSampleRate() {
            return 0;
        }
        
        public final int getBufferSize() {
            return 0;
        }
        
        public final int getHopSize() {
            return 0;
        }
        
        public final boolean getUseGPU() {
            return false;
        }
        
        public RealTimeConfig() {
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
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.ai.voicechanger.domain.processor.RealTimeVoiceChanger.RealTimeConfig copy(int sampleRate, int bufferSize, int hopSize, boolean useGPU) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lcom/ai/voicechanger/domain/processor/RealTimeVoiceChanger$State;", "", "(Ljava/lang/String;I)V", "IDLE", "INITIALIZING", "RECORDING", "PROCESSING", "PLAYING", "STOPPED", "ERROR", "app_debug"})
    public static enum State {
        /*public static final*/ IDLE /* = new IDLE() */,
        /*public static final*/ INITIALIZING /* = new INITIALIZING() */,
        /*public static final*/ RECORDING /* = new RECORDING() */,
        /*public static final*/ PROCESSING /* = new PROCESSING() */,
        /*public static final*/ PLAYING /* = new PLAYING() */,
        /*public static final*/ STOPPED /* = new STOPPED() */,
        /*public static final*/ ERROR /* = new ERROR() */;
        
        State() {
        }
        
        @org.jetbrains.annotations.NotNull
        public static kotlin.enums.EnumEntries<com.ai.voicechanger.domain.processor.RealTimeVoiceChanger.State> getEntries() {
            return null;
        }
    }
}