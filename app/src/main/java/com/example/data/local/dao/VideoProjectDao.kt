package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.VideoProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY creationTimestamp DESC")
    fun getAllVideoProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id LIMIT 1")
    suspend fun getVideoProjectById(id: Long): VideoProjectEntity?

    @Query("SELECT * FROM video_projects WHERE videoDraftName LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY creationTimestamp DESC")
    fun searchByDraftName(query: String): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE videoFilePath IS NOT NULL ORDER BY creationTimestamp DESC")
    fun getProjectsWithLocalAssets(): Flow<List<VideoProjectEntity>>

    @Query("SELECT COUNT(*) FROM video_projects")
    fun getVideoCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoProject(videoProject: VideoProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoProjects(videoProjects: List<VideoProjectEntity>)

    @Update
    suspend fun updateVideoProject(videoProject: VideoProjectEntity)

    @Delete
    suspend fun deleteVideoProject(videoProject: VideoProjectEntity)

    @Query("UPDATE video_projects SET videoFilePath = :videoPath, thumbnailFilePath = :thumbPath, audioFilePath = :audioPath, generatedAssetPaths = :assetPaths, fileSizeBytes = :fileSize WHERE id = :id")
    suspend fun updateLocalAssetPaths(
        id: Long,
        videoPath: String?,
        thumbPath: String?,
        audioPath: String?,
        assetPaths: List<String>,
        fileSize: Long
    )
}
