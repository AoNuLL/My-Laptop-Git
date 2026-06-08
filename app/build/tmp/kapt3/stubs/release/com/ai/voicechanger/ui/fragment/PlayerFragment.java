package com.ai.voicechanger.ui.fragment;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\b\u0010\u0012\u001a\u00020\u000fH\u0002J$\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u000fH\u0016J\u001a\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u00142\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\u0010\u0010\u001e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\b\u0010\u001f\u001a\u00020\u000fH\u0002J\u0010\u0010 \u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/ai/voicechanger/ui/fragment/PlayerFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/ai/voicechanger/databinding/FragmentPlayerBinding;", "adapter", "Lcom/ai/voicechanger/ui/adapter/VoicePackAdapter;", "audioPlayer", "Lcom/ai/voicechanger/domain/player/AudioPlayer;", "binding", "getBinding", "()Lcom/ai/voicechanger/databinding/FragmentPlayerBinding;", "database", "Lcom/ai/voicechanger/data/local/AppDatabase;", "deleteAudio", "", "audioFile", "Lcom/ai/voicechanger/data/local/AudioFile;", "loadAudioFiles", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "playAudio", "setupRecyclerView", "showDeleteConfirmation", "app_release"})
public final class PlayerFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable
    private com.ai.voicechanger.databinding.FragmentPlayerBinding _binding;
    private com.ai.voicechanger.data.local.AppDatabase database;
    private com.ai.voicechanger.ui.adapter.VoicePackAdapter adapter;
    private com.ai.voicechanger.domain.player.AudioPlayer audioPlayer;
    
    public PlayerFragment() {
        super();
    }
    
    private final com.ai.voicechanger.databinding.FragmentPlayerBinding getBinding() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void loadAudioFiles() {
    }
    
    private final void playAudio(com.ai.voicechanger.data.local.AudioFile audioFile) {
    }
    
    private final void showDeleteConfirmation(com.ai.voicechanger.data.local.AudioFile audioFile) {
    }
    
    private final void deleteAudio(com.ai.voicechanger.data.local.AudioFile audioFile) {
    }
    
    @java.lang.Override
    public void onDestroyView() {
    }
}