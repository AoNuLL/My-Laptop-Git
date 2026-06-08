package com.ai.voicechanger.ui.adapter;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0016B;\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0002\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u001c\u0010\r\u001a\u00020\b2\n\u0010\u000e\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u001c\u0010\u0010\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fH\u0016J\u0014\u0010\u0014\u001a\u00020\b2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/ai/voicechanger/ui/adapter/VoiceModelAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/ai/voicechanger/ui/adapter/VoiceModelAdapter$ViewHolder;", "models", "", "Lcom/ai/voicechanger/data/model/VoiceModel;", "onModelClick", "Lkotlin/Function1;", "", "onDeleteClick", "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "updateList", "newModels", "ViewHolder", "app_release"})
public final class VoiceModelAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.ai.voicechanger.ui.adapter.VoiceModelAdapter.ViewHolder> {
    @org.jetbrains.annotations.NotNull
    private java.util.List<com.ai.voicechanger.data.model.VoiceModel> models;
    @org.jetbrains.annotations.NotNull
    private final kotlin.jvm.functions.Function1<com.ai.voicechanger.data.model.VoiceModel, kotlin.Unit> onModelClick = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.jvm.functions.Function1<com.ai.voicechanger.data.model.VoiceModel, kotlin.Unit> onDeleteClick = null;
    
    public VoiceModelAdapter(@org.jetbrains.annotations.NotNull
    java.util.List<com.ai.voicechanger.data.model.VoiceModel> models, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.ai.voicechanger.data.model.VoiceModel, kotlin.Unit> onModelClick, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.ai.voicechanger.data.model.VoiceModel, kotlin.Unit> onDeleteClick) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.ai.voicechanger.ui.adapter.VoiceModelAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.ai.voicechanger.ui.adapter.VoiceModelAdapter.ViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    public final void updateList(@org.jetbrains.annotations.NotNull
    java.util.List<com.ai.voicechanger.data.model.VoiceModel> newModels) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/ai/voicechanger/ui/adapter/VoiceModelAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/ai/voicechanger/databinding/ItemVoiceModelBinding;", "(Lcom/ai/voicechanger/ui/adapter/VoiceModelAdapter;Lcom/ai/voicechanger/databinding/ItemVoiceModelBinding;)V", "bind", "", "model", "Lcom/ai/voicechanger/data/model/VoiceModel;", "formatDate", "", "timestamp", "", "formatSize", "bytes", "app_release"})
    public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final com.ai.voicechanger.databinding.ItemVoiceModelBinding binding = null;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull
        com.ai.voicechanger.databinding.ItemVoiceModelBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.ai.voicechanger.data.model.VoiceModel model) {
        }
        
        private final java.lang.String formatSize(long bytes) {
            return null;
        }
        
        private final java.lang.String formatDate(long timestamp) {
            return null;
        }
    }
}