package com.ai.voicechanger.domain.processor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0002J\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\tJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0018\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u0011J\u0018\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J+\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u001b2\u0006\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u00a2\u0006\u0002\u0010\u001cR\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/ai/voicechanger/domain/processor/RVCInferenceModel;", "", "modelLoader", "Lcom/ai/voicechanger/domain/tflite/TFLiteModelLoader;", "(Lcom/ai/voicechanger/domain/tflite/TFLiteModelLoader;)V", "inputShape", "", "outputShape", "applyNormalization", "", "data", "close", "", "infer", "audioData", "f0Data", "isModelLoaded", "", "loadModel", "modelPath", "", "useGPU", "postprocessOutput", "outputData", "targetSize", "", "prepareInput", "", "([F[F[I)[Ljava/lang/Object;", "app_debug"})
public final class RVCInferenceModel {
    @org.jetbrains.annotations.NotNull
    private final com.ai.voicechanger.domain.tflite.TFLiteModelLoader modelLoader = null;
    @org.jetbrains.annotations.Nullable
    private int[] inputShape;
    @org.jetbrains.annotations.Nullable
    private int[] outputShape;
    
    public RVCInferenceModel(@org.jetbrains.annotations.NotNull
    com.ai.voicechanger.domain.tflite.TFLiteModelLoader modelLoader) {
        super();
    }
    
    public final boolean loadModel(@org.jetbrains.annotations.NotNull
    java.lang.String modelPath, boolean useGPU) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final float[] infer(@org.jetbrains.annotations.NotNull
    float[] audioData, @org.jetbrains.annotations.NotNull
    float[] f0Data) {
        return null;
    }
    
    private final java.lang.Object[] prepareInput(float[] audioData, float[] f0Data, int[] inputShape) {
        return null;
    }
    
    private final float[] postprocessOutput(float[] outputData, int targetSize) {
        return null;
    }
    
    private final float[] applyNormalization(float[] data) {
        return null;
    }
    
    public final void close() {
    }
    
    public final boolean isModelLoaded() {
        return false;
    }
}