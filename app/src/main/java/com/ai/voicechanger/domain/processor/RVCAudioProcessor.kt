package com.ai.voicechanger.domain.processor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

class RVCAudioProcessor {
    
    data class AudioConfig(
        val sampleRate: Int = 44100,
        val hopSize: Int = 512,
        val winSize: Int = 2048,
        val f0Min: Float = 50f,
        val f0Max: Float = 1200f,
        val pitchChange: Float = 0f // 音调变化 (半音)
    )
    
    private var config: AudioConfig = AudioConfig()
    
    suspend fun processAudio(
        inputData: ByteBuffer,
        model: RVCInferenceModel
    ): Result<ByteBuffer> = withContext(Dispatchers.Default) {
        try {
            val audioData = floatBufferToFloatArray(inputData)
            
            val f0Data = extractF0(audioData)
            
            val processedData = model.infer(audioData, f0Data)
            
            val outputBuffer = floatArrayToFloatBuffer(processedData)
            
            Result.success(outputBuffer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun floatBufferToFloatArray(buffer: ByteBuffer): FloatArray {
        val floatArray = FloatArray(buffer.remaining() / 4)
        buffer.order(ByteOrder.nativeOrder())
        buffer.asFloatBuffer().get(floatArray)
        return floatArray
    }
    
    private fun floatArrayToFloatBuffer(floatArray: FloatArray): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(floatArray.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        val floatBuffer = buffer.asFloatBuffer()
        floatBuffer.put(floatArray)
        floatBuffer.rewind()
        return buffer
    }
    
    private fun extractF0(audioData: FloatArray): FloatArray {
        val f0Length = audioData.size / config.hopSize
        val f0Data = FloatArray(f0Length)
        
        for (i in 0 until f0Length) {
            val start = i * config.hopSize
            val end = minOf(start + config.winSize, audioData.size)
            
            if (end - start < config.winSize / 2) {
                f0Data[i] = 0f
                continue
            }
            
            f0Data[i] = estimateF0(audioData, start, end)
        }
        
        return f0Data
    }
    
    private fun estimateF0(audioData: FloatArray, start: Int, end: Int): Float {
        val segment = audioData.sliceArray(start until end)
        
        val acfSize = config.winSize / 4
        val acf = FloatArray(acfSize)
        
        for (lag in 0 until acfSize) {
            var sum = 0f
            for (i in segment.indices step 2) {
                if (i + lag < segment.size) {
                    sum += segment[i] * segment[i + lag]
                }
            }
            acf[lag] = sum / segment.size
        }
        
        var maxIdx = 0
        var maxVal = acf[0]
        for (i in 1 until acfSize) {
            if (acf[i] > maxVal) {
                maxVal = acf[i]
                maxIdx = i
            }
        }
        
        val period = maxIdx + 1
        val f0 = config.sampleRate.toFloat() / period
        
        return if (f0 in config.f0Min..config.f0Max) f0 else 0f
    }
    
    fun setPitchChange(semitones: Float) {
        config = config.copy(pitchChange = semitones)
    }
    
    fun setSampleRate(sampleRate: Int) {
        config = config.copy(sampleRate = sampleRate)
    }
}
