package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AvatarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AvatarDao {
    @Query("SELECT * FROM avatars ORDER BY id ASC")
    fun getAllAvatars(): Flow<List<AvatarEntity>>

    @Query("SELECT * FROM avatars WHERE id = :id LIMIT 1")
    suspend fun getAvatarById(id: Long): AvatarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatar(avatar: AvatarEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatars(avatars: List<AvatarEntity>)

    @Update
    suspend fun updateAvatar(avatar: AvatarEntity)

    @Delete
    suspend fun deleteAvatar(avatar: AvatarEntity)
}
