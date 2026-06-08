package com.ai.voicechanger.domain.processor

import com.ai.voicechanger.domain.tflite.TFLiteModelLoader

class RVCInferenceModel(
    private val modelLoader: TFLiteModelLoader
) {
    
    private var inputShape: IntArray? = null
    private var outputShape: IntArray? = null
    
    fun loadModel(modelPath: String, useGPU: Boolean = false): Boolean {
        val result = modelLoader.loadModel(modelPath, useGPU)
        
        if (result.success) {
            inputShape = result.inputShape
            outputShape = result.outputShape
            return true
        }
        
        return false
    }
    
    fun infer(audioData: FloatArray, f0Data: FloatArray): FloatArray {
        if (!modelLoader.isModelLoaded()) {
            throw IllegalStateException("模型未加载")
        }
        
        val inputShape = inputShape ?: throw IllegalStateException("输入形状未知")
        val outputShape = outputShape ?: throw IllegalStateException("输出形状未知")
        
        val inputData = prepareInput(audioData, f0Data, inputShape)
        
        val outputSize = outputShape.reduce { acc, i -> acc * i }
        val outputData = Array(1) { FloatArray(outputSize) }
        
        modelLoader.runForMultipleInputsOutputs(inputData, mapOf(0 to outputData[0]))
        
        return postprocessOutput(outputData[0], audioData.size)
    }
    
    private fun prepareInput(
        audioData: FloatArray,
        f0Data: FloatArray,
        inputShape: IntArray
    ): Array<Any> {
        val totalInputSize = inputShape.reduce { acc, i -> acc * i }
        
        val inputBuffer = FloatArray(totalInputSize)
        
        audioData.copyInto(inputBuffer, 0, 0, minOf(audioData.size, totalInputSize / 2))
        
        val f0Offset = totalInputSize / 2
        f0Data.copyInto(inputBuffer, f0Offset, 0, minOf(f0Data.size, totalInputSize / 2))
        
        val input = arrayOf<Any>(inputBuffer)
        
        return input
    }
    
    private fun postprocessOutput(outputData: FloatArray, targetSize: Int): FloatArray {
        val result = FloatArray(targetSize)
        
        val copySize = minOf(outputData.size, targetSize)
        outputData.copyInto(result, 0, 0, copySize)
        
        return applyNormalization(result)
    }
    
    private fun applyNormalization(data: FloatArray): FloatArray {
        var maxAmp = 0f
        for (sample in data) {
            maxAmp = maxOf(maxAmp, kotlin.math.abs(sample))
        }
        
        if (maxAmp > 0.001f) {
            val scale = 0.9f / maxAmp
            for (i in data.indices) {
                data[i] = (data[i] * scale).coerceIn(-1f, 1f)
            }
        }
        
        return data
    }
    
    fun close() {
        modelLoader.close()
    }
    
    fun isModelLoaded(): Boolean = modelLoader.isModelLoaded()
}
