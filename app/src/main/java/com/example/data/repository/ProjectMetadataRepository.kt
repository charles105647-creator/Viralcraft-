package com.example.data.repository

import android.content.Context
import com.example.data.local.LocalAssetManager
import com.example.data.local.dao.ProjectMetadataDao
import com.example.data.local.entities.ProjectMetadataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository pattern implementation for managing user video project metadata
 * backed by Room Database.
 */
class ProjectMetadataRepository(
    private val projectMetadataDao: ProjectMetadataDao
) {

    val allProjectMetadata: Flow<List<ProjectMetadataEntity>> =
        projectMetadataDao.getAllProjectMetadata()

    val projectCount: Flow<Int> =
        projectMetadataDao.getProjectCount()

    suspend fun getProjectMetadataById(id: Long): ProjectMetadataEntity? = withContext(Dispatchers.IO) {
        projectMetadataDao.getProjectMetadataById(id)
    }

    fun searchByDraftName(query: String): Flow<List<ProjectMetadataEntity>> =
        projectMetadataDao.searchByDraftName(query)

    fun getRecentDrafts(limit: Int = 10): Flow<List<ProjectMetadataEntity>> =
        projectMetadataDao.getRecentDrafts(limit)

    suspend fun insertProjectMetadata(metadata: ProjectMetadataEntity): Long = withContext(Dispatchers.IO) {
        projectMetadataDao.insertProjectMetadata(metadata)
    }

    suspend fun insertAll(list: List<ProjectMetadataEntity>) = withContext(Dispatchers.IO) {
        projectMetadataDao.insertAll(list)
    }

    suspend fun updateProjectMetadata(metadata: ProjectMetadataEntity) = withContext(Dispatchers.IO) {
        projectMetadataDao.updateProjectMetadata(metadata)
    }

    suspend fun deleteProjectMetadata(metadata: ProjectMetadataEntity) = withContext(Dispatchers.IO) {
        projectMetadataDao.deleteProjectMetadata(metadata)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        projectMetadataDao.deleteById(id)
    }

    /**
     * Creates a new draft record with physical local assets rendered and stored on device.
     */
    suspend fun saveDraftWithAssets(
        context: Context,
        draftName: String,
        scriptText: String,
        durationSeconds: Int = 30,
        aspectRatio: String = "9:16",
        resolution: String = "1080x1920",
        notes: String = ""
    ): ProjectMetadataEntity = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val tempId = now % 100000

        val assets = LocalAssetManager.generateLocalAssetsForProject(
            context = context,
            projectId = tempId,
            draftName = draftName,
            scriptText = scriptText,
            durationSeconds = durationSeconds
        )

        val entity = ProjectMetadataEntity(
            videoDraftName = draftName,
            creationTimestamp = now,
            lastModifiedTimestamp = now,
            videoFilePath = assets.videoFilePath,
            thumbnailFilePath = assets.thumbnailFilePath,
            audioFilePath = assets.audioFilePath,
            generatedAssetPaths = assets.allAssetPaths,
            durationSeconds = durationSeconds,
            aspectRatio = aspectRatio,
            resolution = resolution,
            fileSizeBytes = assets.totalSizeBytes,
            status = "DRAFT",
            notes = notes
        )

        val insertedId = projectMetadataDao.insertProjectMetadata(entity)
        entity.copy(id = insertedId)
    }
}
