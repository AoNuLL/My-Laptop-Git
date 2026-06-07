package com.ai.voicechanger.domain.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.ai.voicechanger.AppApplication
import com.ai.voicechanger.data.local.FilePathManager
import com.ai.voicechanger.domain.processor.AudioProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ExportManager {
    private val context: Context get() = AppApplication.instance
    private val processor = AudioProcessor()
    
    suspend fun exportAudio(
        inputFile: File,
        format: AudioProcessor.AudioFormat,
        outputName: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val outputFile = File(FilePathManager.exportDir, "${outputName}.${format.name.lowercase()}")
            
            processor.convertFormat(inputFile, format, outputFile)
            
            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = getMimeType(file.extension)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "分享音频文件"))
    }
    
    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "wav" -> "audio/wav"
            "mp3" -> "audio/mpeg"
            "aac" -> "audio/aac"
            else -> "audio/*"
        }
    }
    
    fun deleteExportedFile(file: File): Boolean {
        return if (file.exists() && file.parentFile == FilePathManager.exportDir) {
            file.delete()
        } else {
            false
        }
    }
}
