package com.ai.voicechanger.domain.processor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.pow

class AudioProcessor {
    
    suspend fun processWithModel(
        inputFile: File,
        modelPath: String,
        outputPath: File,
        progressCallback: (Int) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            progressCallback(10)
            
            if (!File(modelPath).exists()) {
                return@withContext Result.failure(IllegalStateException("Model file not found: $modelPath"))
            }
            
            progressCallback(30)
            
            val audioData = inputFile.readBytes()
            progressCallback(50)
            
            val processedData = applyVoiceChange(audioData, modelPath)
            progressCallback(80)
            
            outputPath.writeBytes(processedData)
            progressCallback(100)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun applyVoiceChange(audioData: ByteArray, modelPath: String): ByteArray {
        val audioFile = File(modelPath)
        
        if (audioFile.extension == "wav" || audioFile.extension == "pcm") {
            return mixAudio(audioData, audioFile.readBytes())
        }
        
        return applyPitchShift(audioData, 12)
    }
    
    suspend fun convertFormat(
        inputFile: File,
        outputFormat: AudioFormat,
        outputFile: File
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val audioData = inputFile.readBytes()
            
            when (outputFormat) {
                AudioFormat.WAV -> {
                    val wavData = createWavFile(audioData, 44100, 1, 16)
                    outputFile.writeBytes(wavData)
                }
                AudioFormat.MP3 -> {
                    outputFile.writeBytes(audioData)
                }
                AudioFormat.AAC -> {
                    outputFile.writeBytes(audioData)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    enum class AudioFormat { WAV, MP3, AAC }
    
    private fun mixAudio(original: ByteArray, effect: ByteArray): ByteArray {
        val minLength = minOf(original.size, effect.size)
        val result = ByteArray(minLength)
        
        var i = 0
        while (i < minLength) {
            val origSample = ((original[i + 1].toInt() and 0xFF) shl 8) or (original[i].toInt() and 0xFF)
            val effectSample = ((effect[i + 1].toInt() and 0xFF) shl 8) or (effect[i].toInt() and 0xFF)
            
            val mixed = (origSample + effectSample) / 2
            val clamped = mixed.coerceIn(-32768, 32767)
            
            result[i] = (clamped and 0xFF).toByte()
            result[i + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
            i += 2
        }
        
        return result
    }
    
    private fun applyPitchShift(audioData: ByteArray, semitones: Int): ByteArray {
        val result = ByteArray(audioData.size)
        val ratio = 2.0.pow(semitones / 12.0)
        val shiftAmount = (audioData.size * (1 - 1 / ratio)).toInt()
        
        for (i in audioData.indices) {
            val sourceIndex = ((i + shiftAmount) % audioData.size).coerceIn(0, audioData.size - 1)
            result[i] = audioData[sourceIndex]
        }
        
        return result
    }
    
    private fun createWavFile(pcmData: ByteArray, sampleRate: Int, channels: Int, bitsPerSample: Int): ByteArray {
        val blockAlign = (channels * bitsPerSample / 8).toShort()
        val byteRate = (sampleRate * blockAlign.toInt()).toInt()
        val dataSize = pcmData.size
        
        val header = ByteArray(44)
        
        System.arraycopy("RIFF".toByteArray(), 0, header, 0, 4)
        writeInt(header, 36 + dataSize, 4)
        System.arraycopy("WAVE".toByteArray(), 0, header, 8, 4)
        System.arraycopy("fmt ".toByteArray() + byteArrayOf(16, 0, 0, 0), 0, header, 12, 8)
        writeShort(header, 1, 20)
        writeShort(header, channels.toShort(), 22)
        writeInt(header, sampleRate, 24)
        writeInt(header, byteRate, 28)
        writeShort(header, blockAlign, 32)
        writeShort(header, bitsPerSample.toShort(), 34)
        System.arraycopy("data".toByteArray(), 0, header, 36, 4)
        writeInt(header, dataSize, 40)
        
        return header + pcmData
    }
    
    private fun writeShort(array: ByteArray, value: Short, offset: Int) {
        array[offset] = (value.toInt() and 0xFF).toByte()
        array[offset + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
    }
    
    private fun writeInt(array: ByteArray, value: Int, offset: Int) {
        array[offset] = (value and 0xFF).toByte()
        array[offset + 1] = ((value shr 8) and 0xFF).toByte()
        array[offset + 2] = ((value shr 16) and 0xFF).toByte()
        array[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }
}
