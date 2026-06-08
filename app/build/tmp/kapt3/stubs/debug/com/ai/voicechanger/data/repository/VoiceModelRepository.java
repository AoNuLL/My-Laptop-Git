package com.ai.voicechanger.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J$\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0012\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00160\u0015J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0007\u001a\u00020\bH\u0002J:\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000f0\f2\u0006\u0010\u0019\u001a\u00020\b2\b\u0010\u001a\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\nH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0010\u0010 \u001a\u00020\u001f2\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J$\u0010!\u001a\u00020\"2\u0006\u0010\u0019\u001a\u00020\b2\b\u0010\u001a\u001a\u0004\u0018\u00010\b2\b\u0010\u001b\u001a\u0004\u0018\u00010\nH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006#"}, d2 = {"Lcom/ai/voicechanger/data/repository/VoiceModelRepository;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "copyUriToFile", "Ljava/io/File;", "uri", "Landroid/net/Uri;", "extension", "", "deleteModel", "Lkotlin/Result;", "", "model", "Lcom/ai/voicechanger/data/model/VoiceModel;", "deleteModel-gIAlu-s", "(Lcom/ai/voicechanger/data/model/VoiceModel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "generateModelDescription", "file", "getAllModels", "Lkotlinx/coroutines/flow/Flow;", "", "getFileName", "importModel", "modelUri", "indexUri", "customName", "importModel-BWLJW6A", "(Landroid/net/Uri;Landroid/net/Uri;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isValidIndexFile", "", "isValidPthFile", "validateAndCopyModel", "Lcom/ai/voicechanger/data/model/RVCModelInfo;", "app_debug"})
public final class VoiceModelRepository {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    
    public VoiceModelRepository(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    private final com.ai.voicechanger.data.model.RVCModelInfo validateAndCopyModel(android.net.Uri modelUri, android.net.Uri indexUri, java.lang.String customName) {
        return null;
    }
    
    private final java.io.File copyUriToFile(android.net.Uri uri, java.lang.String extension) {
        return null;
    }
    
    private final java.lang.String getFileName(android.net.Uri uri) {
        return null;
    }
    
    private final boolean isValidPthFile(java.io.File file) {
        return false;
    }
    
    private final boolean isValidIndexFile(java.io.File file) {
        return false;
    }
    
    private final java.lang.String generateModelDescription(java.io.File file) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.ai.voicechanger.data.model.VoiceModel>> getAllModels() {
        return null;
    }
}