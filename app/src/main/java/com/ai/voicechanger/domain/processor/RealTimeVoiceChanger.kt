package com.ai.voicechanger.domain.processor

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RealTimeVoiceChanger(
    private val model: RVCInferenceModel,
    private val audioProcessor: RVCAudioProcessor
) {
    
    enum class State {
        IDLE,
        INITIALIZING,
        RECORDING,
        PROCESSING,
        PLAYING,
        STOPPED,
        ERROR
    }
    
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var isRunning = false
    private var recordingJob: Job? = null
    private var processingJob: Job? = null
    
    private val _state = MutableStateFlow<State>(State.IDLE)
    val state: StateFlow<State> = _state.asStateFlow()
    
    private val _latencyMs = MutableStateFlow(0L)
    val latencyMs: StateFlow<Long> = _latencyMs.asStateFlow()
    
    private val config = RealTimeConfig()
    
    data class RealTimeConfig(
        val sampleRate: Int = 44100,
        val bufferSize: Int = 2048,
        val hopSize: Int = 512,
        val useGPU: Boolean = false
    )
    
    suspend fun start(): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            _state.value = State.INITIALIZING
            
            if (!model.isModelLoaded()) {
                return@withContext Result.failure(IllegalStateException("模型未加载"))
            }
            
            audioRecord = createAudioRecord()
            audioTrack = createAudioTrack()
            
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return@withContext Result.failure(IllegalStateException("AudioRecord 初始化失败"))
            }
            
            if (audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
                return@withContext Result.failure(IllegalStateException("AudioTrack 初始化失败"))
            }
            
            isRunning = true
            _state.value = State.RECORDING
            
            startProcessingLoop()
            
            Result.success(Unit)
        } catch (e: Exception) {
            _state.value = State.ERROR
            Result.failure(e)
        }
    }
    
    private fun createAudioRecord(): AudioRecord {
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        
        val bufferSize = AudioRecord.getMinBufferSize(
            config.sampleRate,
            channelConfig,
            audioFormat
        ) * 2
        
        return AudioRecord(
            MediaRecorder.AudioSource.MIC,
            config.sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )
    }
    
    private fun createAudioTrack(): AudioTrack {
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        
        val bufferSize = AudioTrack.getMinBufferSize(
            config.sampleRate,
            channelConfig,
            audioFormat
        ) * 2
        
        return AudioTrack.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(config.sampleRate)
                    .setChannelMask(channelConfig)
                    .setEncoding(audioFormat)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }
    
    private fun startProcessingLoop() {
        val inputBuffer = ShortArray(config.bufferSize)
        val floatBuffer = FloatArray(config.bufferSize)
        
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            audioRecord?.startRecording()
            audioTrack?.play()
            
            while (isRunning) {
                val readResult = audioRecord?.read(inputBuffer, 0, inputBuffer.size) ?: -1
                
                when {
                    readResult > 0 -> {
                        val processStartTime = System.currentTimeMillis()
                        
                        for (i in 0 until readResult) {
                            floatBuffer[i] = inputBuffer[i] / 32768f
                        }
                        
                        processingJob = CoroutineScope(Dispatchers.Default).launch {
                            try {
                                val byteBuffer = floatArrayToByteBuffer(floatBuffer.sliceArray(0 until readResult))
                                
                                val result = audioProcessor.processAudio(byteBuffer, model)
                                
                                result.onSuccess { processedBuffer ->
                                    val processedData = byteBufferToShortArray(processedBuffer)
                                    
                                    audioTrack?.write(processedData, 0, processedData.size)
                                    
                                    val processEndTime = System.currentTimeMillis()
                                    _latencyMs.value = processEndTime - processStartTime
                                }
                                
                                result.onFailure { error ->
                                    _state.value = State.ERROR
                                }
                            } catch (e: Exception) {
                                _state.value = State.ERROR
                            }
                        }
                    }
                    readResult == AudioRecord.ERROR_INVALID_OPERATION -> {
                        _state.value = State.ERROR
                        isRunning = false
                    }
                    readResult == AudioRecord.ERROR_BAD_VALUE -> {
                        _state.value = State.ERROR
                        isRunning = false
                    }
                }
                
                yield()
            }
        }
    }
    
    private fun floatArrayToByteBuffer(floatArray: FloatArray): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(floatArray.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        val floatBuffer = buffer.asFloatBuffer()
        floatBuffer.put(floatArray)
        floatBuffer.rewind()
        return buffer
    }
    
    private fun byteBufferToShortArray(buffer: ByteBuffer): ShortArray {
        val floatBuffer = buffer.asFloatBuffer()
        val floatArray = FloatArray(buffer.remaining() / 4)
        floatBuffer.get(floatArray)
        
        val shortArray = ShortArray(floatArray.size)
        for (i in floatArray.indices) {
            shortArray[i] = (floatArray[i] * 32767f).toInt().toShort()
        }
        
        return shortArray
    }
    
    fun stop() {
        isRunning = false
        _state.value = State.STOPPED
        
        recordingJob?.cancel()
        processingJob?.cancel()
        
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            // Ignore
        }
        
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        
        audioRecord = null
        audioTrack = null
        _state.value = State.IDLE
    }
    
    fun setPitchChange(semitones: Float) {
        audioProcessor.setPitchChange(semitones)
    }
    
    fun getCurrentLatency(): Long = _latencyMs.value
}
