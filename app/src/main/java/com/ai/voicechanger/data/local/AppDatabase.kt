package com.ai.voicechanger.data.local

import android.content.Context
import androidx.room.*
import com.ai.voicechanger.AppApplication
import com.ai.voicechanger.data.model.VoiceModel
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "audio_files")
data class AudioFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filePath: String,
    val duration: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isProcessed: Boolean = false,
    val modelPath: String? = null
)

@Entity(tableName = "voice_packs")
data class VoicePack(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val modelPath: String,
    val index: String,
    val description: String = "",
    val isDownloaded: Boolean = false
)

@Dao
interface AudioFileDao {
    @Query("SELECT * FROM audio_files ORDER BY createdAt DESC")
    fun getAll(): Flow<List<AudioFile>>
    
    @Insert
    suspend fun insert(file: AudioFile): Long
    
    @Delete
    suspend fun delete(file: AudioFile)
    
    @Query("DELETE FROM audio_files WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface VoicePackDao {
    @Query("SELECT * FROM voice_packs")
    fun getAll(): Flow<List<VoicePack>>
    
    @Insert
    suspend fun insert(pack: VoicePack)
    
    @Update
    suspend fun update(pack: VoicePack)
    
    @Delete
    suspend fun delete(pack: VoicePack)
}

@Dao
interface VoiceModelDao {
    @Query("SELECT * FROM voice_models ORDER BY createdAt DESC")
    fun getAll(): Flow<List<VoiceModel>>
    
    @Query("SELECT * FROM voice_models WHERE id = :id")
    suspend fun getById(id: Long): VoiceModel?
    
    @Insert
    suspend fun insert(model: VoiceModel): Long
    
    @Update
    suspend fun update(model: VoiceModel)
    
    @Delete
    suspend fun delete(model: VoiceModel)
    
    @Query("DELETE FROM voice_models WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Database(entities = [AudioFile::class, VoicePack::class, VoiceModel::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audioFileDao(): AudioFileDao
    abstract fun voicePackDao(): VoicePackDao
    abstract fun voiceModelDao(): VoiceModelDao
    
    companion object {
        @Volatile private var instance: AppDatabase? = null
        
        fun get(): AppDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    AppApplication.instance,
                    AppDatabase::class.java,
                    "voicechanger_db"
                ).build()
                instance = db
                db
            }
        }
    }
}
