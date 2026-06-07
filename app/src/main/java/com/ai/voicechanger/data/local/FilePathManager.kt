package com.ai.voicechanger.data.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.ai.voicechanger.AppApplication
import java.io.File

object FilePathManager {
    private val context: Context get() = AppApplication.instance
    
    val audioDir: File by lazy {
        File(context.filesDir, "recordings").apply { mkdirs() }
    }
    
    val processedDir: File by lazy {
        File(context.filesDir, "processed").apply { mkdirs() }
    }
    
    val modelDir: File by lazy {
        File(context.filesDir, "models").apply { mkdirs() }
    }
    
    val exportDir: File by lazy {
        File(context.filesDir, "exports").apply { mkdirs() }
    }
    
    fun createAudioFile(name: String): File {
        return File(audioDir, "${name}_${System.currentTimeMillis()}.wav")
    }
    
    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
