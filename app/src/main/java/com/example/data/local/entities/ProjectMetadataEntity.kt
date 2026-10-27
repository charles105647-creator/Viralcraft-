package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity to persist user video project metadata, including draft names,
 * creation timestamps, and local file paths to generated video assets.
 */
@Entity(tableName = "project_metadata")
data class ProjectMetadataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoDraftName: String,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val lastModifiedTimestamp: Long = System.currentTimeMillis(),
    val videoFilePath: String? = null,
    val thumbnailFilePath: String? = null,
    val audioFilePath: String? = null,
    val generatedAssetPaths: List<String> = emptyList(),
    val durationSeconds: Int = 30,
    val aspectRatio: String = "9:16",
    val resolution: String = "1080x1920",
    val fileSizeBytes: Long = 0L,
    val status: String = "DRAFT", // DRAFT, GENERATED, EXPORTED, ARCHIVED
    val notes: String = ""
)
