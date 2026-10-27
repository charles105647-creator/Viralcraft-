package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.VoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    @Query("SELECT * FROM voices ORDER BY isClonedVoice DESC, id ASC")
    fun getAllVoices(): Flow<List<VoiceEntity>>

    @Query("SELECT * FROM voices WHERE id = :id LIMIT 1")
    suspend fun getVoiceById(id: Long): VoiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoice(voice: VoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoices(voices: List<VoiceEntity>)

    @Update
    suspend fun updateVoice(voice: VoiceEntity)

    @Delete
    suspend fun deleteVoice(voice: VoiceEntity)
}
