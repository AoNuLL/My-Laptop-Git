package com.ai.voicechanger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_models")
data class VoiceModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val modelPath: String,  // .pth 文件路径
    val indexPath: String?, // .index 文件路径 (可选)
    val description: String = "",
    val isDownloaded: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val fileSize: Long = 0L,
    val isLoaded: Boolean = false,
    val useGPU: Boolean = false
)

data class RVCModelInfo(
    val name: String,
    val modelPath: String,
    val indexPath: String? = null,
    val description: String = "",
    val fileSize: Long = 0L,
    val isValid: Boolean = true,
    val errorMessage: String? = null
)
