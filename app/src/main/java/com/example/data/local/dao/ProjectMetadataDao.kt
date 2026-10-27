package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ProjectMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectMetadataDao {
    @Query("SELECT * FROM project_metadata ORDER BY creationTimestamp DESC")
    fun getAllProjectMetadata(): Flow<List<ProjectMetadataEntity>>

    @Query("SELECT * FROM project_metadata WHERE id = :id LIMIT 1")
    suspend fun getProjectMetadataById(id: Long): ProjectMetadataEntity?

    @Query("SELECT * FROM project_metadata WHERE videoDraftName LIKE '%' || :query || '%' ORDER BY creationTimestamp DESC")
    fun searchByDraftName(query: String): Flow<List<ProjectMetadataEntity>>

    @Query("SELECT * FROM project_metadata ORDER BY creationTimestamp DESC LIMIT :limit")
    fun getRecentDrafts(limit: Int): Flow<List<ProjectMetadataEntity>>

    @Query("SELECT COUNT(*) FROM project_metadata")
    fun getProjectCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectMetadata(metadata: ProjectMetadataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(metadataList: List<ProjectMetadataEntity>)

    @Update
    suspend fun updateProjectMetadata(metadata: ProjectMetadataEntity)

    @Delete
    suspend fun deleteProjectMetadata(metadata: ProjectMetadataEntity)

    @Query("DELETE FROM project_metadata WHERE id = :id")
    suspend fun deleteById(id: Long)
}
