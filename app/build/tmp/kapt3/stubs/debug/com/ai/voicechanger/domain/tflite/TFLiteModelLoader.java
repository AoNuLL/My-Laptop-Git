package com.ai.voicechanger.domain.tflite;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010$\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001%B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000b\u001a\u00020\fJ\u0012\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0010J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0012\u001a\u00020\nJ\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\nJ\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0016\u0010\u001c\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u0001J-\u0010\u001f\u001a\u00020\f2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010!2\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010#\u00a2\u0006\u0002\u0010$R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/ai/voicechanger/domain/tflite/TFLiteModelLoader;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "gpuDelegate", "Lorg/tensorflow/lite/gpu/GpuDelegate;", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "isLoaded", "", "close", "", "getInputShape", "", "index", "", "getOutputShape", "isModelLoaded", "loadModel", "Lcom/ai/voicechanger/domain/tflite/TFLiteModelLoader$LoadResult;", "modelPath", "", "useGPU", "loadModelFile", "Ljava/nio/MappedByteBuffer;", "modelFile", "Ljava/io/File;", "run", "input", "output", "runForMultipleInputsOutputs", "inputs", "", "outputs", "", "([Ljava/lang/Object;Ljava/util/Map;)V", "LoadResult", "app_debug"})
public final class TFLiteModelLoader {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable
    private org.tensorflow.lite.Interpreter interpreter;
    @org.jetbrains.annotations.Nullable
    private org.tensorflow.lite.gpu.GpuDelegate gpuDelegate;
    private boolean isLoaded = false;
    
    public TFLiteModelLoader(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.ai.voicechanger.domain.tflite.TFLiteModelLoader.LoadResult loadModel(@org.jetbrains.annotations.NotNull
    java.lang.String modelPath, boolean useGPU) {
        return null;
    }
    
    private final java.nio.MappedByteBuffer loadModelFile(java.io.File modelFile) {
        return null;
    }
    
    public final void run(@org.jetbrains.annotations.NotNull
    java.lang.Object input, @org.jetbrains.annotations.NotNull
    java.lang.Object output) {
    }
    
    public final void runForMultipleInputsOutputs(@org.jetbrains.annotations.NotNull
    java.lang.Object[] inputs, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, ? extends java.lang.Object> outputs) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final int[] getInputShape(int index) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final int[] getOutputShape(int index) {
        return null;
    }
    
    public final void close() {
    }
    
    public final boolean isModelLoaded() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0011\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J5\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Lcom/ai/voicechanger/domain/tflite/TFLiteModelLoader$LoadResult;", "", "success", "", "message", "", "inputShape", "", "outputShape", "(ZLjava/lang/String;[I[I)V", "getInputShape", "()[I", "getMessage", "()Ljava/lang/String;", "getOutputShape", "getSuccess", "()Z", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    public static final class LoadResult {
        private final boolean success = false;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String message = null;
        @org.jetbrains.annotations.Nullable
        private final int[] inputShape = null;
        @org.jetbrains.annotations.Nullable
        private final int[] outputShape = null;
        
        public LoadResult(boolean success, @org.jetbrains.annotations.NotNull
        java.lang.String message, @org.jetbrains.annotations.Nullable
        int[] inputShape, @org.jetbrains.annotations.Nullable
        int[] outputShape) {
            super();
        }
        
        public final boolean getSuccess() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final int[] getInputShape() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final int[] getOutputShape() {
            return null;
        }
        
        public final boolean component1() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final int[] component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final int[] component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.ai.voicechanger.domain.tflite.TFLiteModelLoader.LoadResult copy(boolean success, @org.jetbrains.annotations.NotNull
        java.lang.String message, @org.jetbrains.annotations.Nullable
        int[] inputShape, @org.jetbrains.annotations.Nullable
        int[] outputShape) {
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