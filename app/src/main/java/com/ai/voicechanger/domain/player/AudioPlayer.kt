package com.ai.voicechanger.domain.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ai.voicechanger.AppApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AudioPlayer {
    private val context: Context get() = AppApplication.instance
    private var exoPlayer: ExoPlayer? = null
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    private var audioTrack: AudioTrack? = null
    
    init {
        exoPlayer = ExoPlayer.Builder(context)
            .build()
            .also { player ->
                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        _playbackState.value = when (state) {
                            Player.STATE_IDLE -> PlaybackState.IDLE
                            Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                            Player.STATE_READY -> PlaybackState.READY
                            Player.STATE_ENDED -> PlaybackState.ENDED
                            else -> PlaybackState.IDLE
                        }
                    }
                    
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            _playbackState.value = PlaybackState.PLAYING
                        } else if (_playbackState.value != PlaybackState.ENDED) {
                            _playbackState.value = PlaybackState.PAUSED
                        }
                    }
                })
            }
    }
    
    fun playFile(file: File) {
        val mediaItem = MediaItem.fromUri(
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        )
        
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }
    
    fun playPcmData(pcmFile: File, sampleRate: Int = 44100) {
        Thread {
            try {
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
                
                audioTrack?.play()
                _playbackState.value = PlaybackState.PLAYING
                
                pcmFile.inputStream().use { input ->
                    val buffer = ByteArray(bufferSize)
                    while (input.read(buffer).also { it -> if (it > 0) audioTrack?.write(buffer, 0, it) } > 0) {
                        // Continue playing
                    }
                }
                
                audioTrack?.stop()
                _playbackState.value = PlaybackState.ENDED
            } catch (e: Exception) {
                _playbackState.value = PlaybackState.ERROR(e.message ?: "Unknown error")
            } finally {
                audioTrack?.release()
                audioTrack = null
            }
        }.start()
    }
    
    fun pause() {
        exoPlayer?.pause()
    }
    
    fun resume() {
        exoPlayer?.play()
    }
    
    fun stop() {
        exoPlayer?.stop()
        audioTrack?.stop()
        _playbackState.value = PlaybackState.IDLE
    }
    
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }
    
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    
    fun getDuration(): Long = exoPlayer?.duration ?: 0L
    
    fun release() {
        stop()
        exoPlayer?.release()
        exoPlayer = null
    }
}

sealed class PlaybackState {
    object IDLE : PlaybackState()
    object BUFFERING : PlaybackState()
    object READY : PlaybackState()
    object PLAYING : PlaybackState()
    object PAUSED : PlaybackState()
    object ENDED : PlaybackState()
    data class ERROR(val message: String) : PlaybackState()
}
