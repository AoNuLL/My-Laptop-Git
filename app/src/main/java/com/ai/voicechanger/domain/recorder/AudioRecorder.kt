package com.ai.voicechanger.domain.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

class AudioRecorder {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.IDLE)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()
    
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    
    private val bufferSize: Int by lazy {
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2
    }
    
    fun startRecording(outputFile: File): Result<Unit> {
        return try {
            val audioSource = MediaRecorder.AudioSource.MIC
            
            audioRecord = AudioRecord(
                audioSource,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
            
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return Result.failure(IllegalStateException("AudioRecord initialization failed"))
            }
            
            audioRecord?.startRecording()
            isRecording = true
            _recordingState.value = RecordingState.RECORDING
            
            Thread {
                recordToFile(outputFile)
            }.start()
            
            Result.success(Unit)
        } catch (e: Exception) {
            _recordingState.value = RecordingState.ERROR(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }
    
    private fun recordToFile(outputFile: File) {
        FileOutputStream(outputFile).use { fos ->
            val buffer = ByteArray(bufferSize)
            var totalBytes = 0L
            
            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (read > 0) {
                    fos.write(buffer, 0, read)
                    totalBytes += read
                }
            }
        }
    }
    
    fun stopRecording() {
        isRecording = false
        _recordingState.value = RecordingState.STOPPING
        
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            // Ignore
        } finally {
            audioRecord = null
            _recordingState.value = RecordingState.IDLE
        }
    }
    
    fun release() {
        stopRecording()
    }
}

sealed class RecordingState {
    object IDLE : RecordingState()
    object RECORDING : RecordingState()
    object STOPPING : RecordingState()
    data class ERROR(val message: String) : RecordingState()
}
