package com.ai.voicechanger.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.ai.voicechanger.data.local.FilePathManager
import com.ai.voicechanger.data.model.RVCModelInfo
import com.ai.voicechanger.data.model.VoiceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class VoiceModelRepository(private val context: Context) {
    
    suspend fun importModel(
        modelUri: Uri,
        indexUri: Uri?,
        customName: String? = null
    ): Result<VoiceModel> = withContext(Dispatchers.IO) {
        try {
            val modelInfo = validateAndCopyModel(modelUri, indexUri, customName)
            
            val voiceModel = VoiceModel(
                name = modelInfo.name,
                modelPath = modelInfo.modelPath,
                indexPath = modelInfo.indexPath,
                description = modelInfo.description,
                fileSize = modelInfo.fileSize
            )
            
            val id = com.ai.voicechanger.data.local.AppDatabase.get()
                .voiceModelDao().insert(voiceModel)
            
            Result.success(voiceModel.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateAndCopyModel(
        modelUri: Uri,
        indexUri: Uri?,
        customName: String?
    ): RVCModelInfo {
        val modelFile = copyUriToFile(modelUri, ".pth")
        
        if (!isValidPthFile(modelFile)) {
            modelFile.delete()
            throw IllegalArgumentException("不是有效的 RVC 模型文件 (.pth)")
        }
        
        val name = customName ?: modelFile.nameWithoutExtension
        val description = generateModelDescription(modelFile)
        
        var indexPath: String? = null
        if (indexUri != null) {
            val indexFile = copyUriToFile(indexUri, ".index")
            if (isValidIndexFile(indexFile)) {
                indexPath = indexFile.absolutePath
            } else {
                indexFile.delete()
            }
        }
        
        return RVCModelInfo(
            name = name,
            modelPath = modelFile.absolutePath,
            indexPath = indexPath,
            description = description,
            fileSize = modelFile.length(),
            isValid = true
        )
    }
    
    private fun copyUriToFile(uri: Uri, extension: String): File {
        val fileName = getFileName(uri) ?: "model_${System.currentTimeMillis()}$extension"
        val targetFile = File(FilePathManager.modelDir, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return targetFile
    }
    
    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex)
                    }
                }
            }
        }
        
        if (fileName == null) {
            fileName = uri.lastPathSegment
        }
        
        return fileName
    }
    
    private fun isValidPthFile(file: File): Boolean {
        try {
            val content = FileInputStream(file).use { input ->
                val bytes = ByteArray(minOf(1024, file.length().toInt()))
                input.read(bytes)
                String(bytes, Charsets.UTF_8)
            }
            return file.extension == "pth" || content.contains("pk")
        } catch (e: Exception) {
            return false
        }
    }
    
    private fun isValidIndexFile(file: File): Boolean {
        return file.extension == "index" && file.length() > 0
    }
    
    private fun generateModelDescription(file: File): String {
        val sizeMB = file.length() / 1024.0 / 1024.0
        return "模型大小：${String.format("%.2f", sizeMB)} MB\n导入时间：${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}"
    }
    
    suspend fun deleteModel(model: VoiceModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            File(model.modelPath).delete()
            model.indexPath?.let { File(it).delete() }
            
            com.ai.voicechanger.data.local.AppDatabase.get()
                .voiceModelDao().delete(model)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getAllModels() = com.ai.voicechanger.data.local.AppDatabase.get()
        .voiceModelDao().getAll()
}
