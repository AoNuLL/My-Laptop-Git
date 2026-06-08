package com.ai.voicechanger.domain.tflite

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteModelLoader(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private var isLoaded = false
    
    data class LoadResult(
        val success: Boolean,
        val message: String,
        val inputShape: IntArray? = null,
        val outputShape: IntArray? = null
    )
    
    fun loadModel(modelPath: String, useGPU: Boolean = false): LoadResult {
        return try {
            val modelFile = File(modelPath)
            
            if (!modelFile.exists()) {
                return LoadResult(
                    success = false,
                    message = "模型文件不存在：$modelPath"
                )
            }
            
            val modelBuffer = loadModelFile(modelFile)
            
            val options = Interpreter.Options().apply {
                if (useGPU) {
                    gpuDelegate = GpuDelegate()
                    addDelegate(gpuDelegate)
                }
                setNumThreads(4)
                setUseXNNPACK(true)
            }
            
            interpreter = Interpreter(modelBuffer, options)
            isLoaded = true
            
            val inputShape = interpreter?.getInputTensor(0)?.shape()
            val outputShape = interpreter?.getOutputTensor(0)?.shape()
            
            LoadResult(
                success = true,
                message = "模型加载成功",
                inputShape = inputShape,
                outputShape = outputShape
            )
        } catch (e: Exception) {
            LoadResult(
                success = false,
                message = "模型加载失败：${e.message}",
                inputShape = null,
                outputShape = null
            )
        }
    }
    
    private fun loadModelFile(modelFile: File): MappedByteBuffer {
        val fileInputStream = FileInputStream(modelFile)
        val fileChannel = fileInputStream.channel
        val startOffset = 0L
        val declaredLength = fileChannel.size()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    fun run(input: Any, output: Any) {
        if (!isLoaded || interpreter == null) {
            throw IllegalStateException("模型未加载")
        }
        interpreter?.run(input, output)
    }
    
    fun runForMultipleInputsOutputs(inputs: Array<Any>, outputs: Map<Int, Any>) {
        if (!isLoaded || interpreter == null) {
            throw IllegalStateException("模型未加载")
        }
        interpreter?.runForMultipleInputsOutputs(inputs, outputs)
    }
    
    fun getInputShape(index: Int = 0): IntArray? {
        return interpreter?.getInputTensor(index)?.shape()
    }
    
    fun getOutputShape(index: Int = 0): IntArray? {
        return interpreter?.getOutputTensor(index)?.shape()
    }
    
    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
        interpreter = null
        gpuDelegate = null
        isLoaded = false
    }
    
    fun isModelLoaded(): Boolean = isLoaded
}
