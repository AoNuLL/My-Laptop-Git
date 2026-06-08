package com.ai.voicechanger.domain.recorder;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0006\u0010\u001b\u001a\u00020\u0018J!\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00180\u001d2\u0006\u0010\u0019\u001a\u00020\u001a\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001fJ\u0006\u0010 \u001a\u00020\u0018R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\n\u001a\u00020\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u000f\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006!"}, d2 = {"Lcom/ai/voicechanger/domain/recorder/AudioRecorder;", "", "()V", "_recordingState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ai/voicechanger/domain/recorder/RecordingState;", "audioFormat", "", "audioRecord", "Landroid/media/AudioRecord;", "bufferSize", "getBufferSize", "()I", "bufferSize$delegate", "Lkotlin/Lazy;", "channelConfig", "isRecording", "", "recordingState", "Lkotlinx/coroutines/flow/StateFlow;", "getRecordingState", "()Lkotlinx/coroutines/flow/StateFlow;", "sampleRate", "recordToFile", "", "outputFile", "Ljava/io/File;", "release", "startRecording", "Lkotlin/Result;", "startRecording-IoAF18A", "(Ljava/io/File;)Ljava/lang/Object;", "stopRecording", "app_debug"})
public final class AudioRecorder {
    @org.jetbrains.annotations.Nullable
    private android.media.AudioRecord audioRecord;
    private boolean isRecording = false;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ai.voicechanger.domain.recorder.RecordingState> _recordingState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ai.voicechanger.domain.recorder.RecordingState> recordingState = null;
    private final int sampleRate = 44100;
    private final int channelConfig = android.media.AudioFormat.CHANNEL_IN_MONO;
    private final int audioFormat = android.media.AudioFormat.ENCODING_PCM_16BIT;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy bufferSize$delegate = null;
    
    public AudioRecorder() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ai.voicechanger.domain.recorder.RecordingState> getRecordingState() {
        return null;
    }
    
    private final int getBufferSize() {
        return 0;
    }
    
    private final void recordToFile(java.io.File outputFile) {
    }
    
    public final void stopRecording() {
    }
    
    public final void release() {
    }
}