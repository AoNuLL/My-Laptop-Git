package com.ai.voicechanger.domain.export;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ4\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000e2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0013H\u0002J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u000b\u001a\u00020\fR\u0014\u0010\u0003\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001a"}, d2 = {"Lcom/ai/voicechanger/domain/export/ExportManager;", "", "()V", "context", "Landroid/content/Context;", "getContext", "()Landroid/content/Context;", "processor", "Lcom/ai/voicechanger/domain/processor/AudioProcessor;", "deleteExportedFile", "", "file", "Ljava/io/File;", "exportAudio", "Lkotlin/Result;", "inputFile", "format", "Lcom/ai/voicechanger/domain/processor/AudioProcessor$AudioFormat;", "outputName", "", "exportAudio-BWLJW6A", "(Ljava/io/File;Lcom/ai/voicechanger/domain/processor/AudioProcessor$AudioFormat;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMimeType", "extension", "shareFile", "", "app_debug"})
public final class ExportManager {
    @org.jetbrains.annotations.NotNull
    private final com.ai.voicechanger.domain.processor.AudioProcessor processor = null;
    
    public ExportManager() {
        super();
    }
    
    private final android.content.Context getContext() {
        return null;
    }
    
    public final void shareFile(@org.jetbrains.annotations.NotNull
    java.io.File file) {
    }
    
    private final java.lang.String getMimeType(java.lang.String extension) {
        return null;
    }
    
    public final boolean deleteExportedFile(@org.jetbrains.annotations.NotNull
    java.io.File file) {
        return false;
    }
}