package com.ai.voicechanger.domain.player;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0013J\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0019J\u0018\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00192\b\b\u0002\u0010\u001c\u001a\u00020\u001dJ\u0006\u0010\u001e\u001a\u00020\u0016J\u0006\u0010\u001f\u001a\u00020\u0016J\u000e\u0010 \u001a\u00020\u00162\u0006\u0010!\u001a\u00020\u0013J\u0006\u0010\"\u001a\u00020\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006#"}, d2 = {"Lcom/ai/voicechanger/domain/player/AudioPlayer;", "", "()V", "_playbackState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/ai/voicechanger/domain/player/PlaybackState;", "audioTrack", "Landroid/media/AudioTrack;", "context", "Landroid/content/Context;", "getContext", "()Landroid/content/Context;", "exoPlayer", "Landroidx/media3/exoplayer/ExoPlayer;", "playbackState", "Lkotlinx/coroutines/flow/StateFlow;", "getPlaybackState", "()Lkotlinx/coroutines/flow/StateFlow;", "getCurrentPosition", "", "getDuration", "pause", "", "playFile", "file", "Ljava/io/File;", "playPcmData", "pcmFile", "sampleRate", "", "release", "resume", "seekTo", "position", "stop", "app_release"})
public final class AudioPlayer {
    @org.jetbrains.annotations.Nullable
    private androidx.media3.exoplayer.ExoPlayer exoPlayer;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.ai.voicechanger.domain.player.PlaybackState> _playbackState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.ai.voicechanger.domain.player.PlaybackState> playbackState = null;
    @org.jetbrains.annotations.Nullable
    private android.media.AudioTrack audioTrack;
    
    public AudioPlayer() {
        super();
    }
    
    private final android.content.Context getContext() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.ai.voicechanger.domain.player.PlaybackState> getPlaybackState() {
        return null;
    }
    
    public final void playFile(@org.jetbrains.annotations.NotNull
    java.io.File file) {
    }
    
    public final void playPcmData(@org.jetbrains.annotations.NotNull
    java.io.File pcmFile, int sampleRate) {
    }
    
    public final void pause() {
    }
    
    public final void resume() {
    }
    
    public final void stop() {
    }
    
    public final void seekTo(long position) {
    }
    
    public final long getCurrentPosition() {
        return 0L;
    }
    
    public final long getDuration() {
        return 0L;
    }
    
    public final void release() {
    }
}