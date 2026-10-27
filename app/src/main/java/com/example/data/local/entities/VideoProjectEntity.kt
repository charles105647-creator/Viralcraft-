package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AvatarStyle
import com.example.data.model.VideoStatus

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val productId: Long,
    val avatarId: Long,
    val voiceId: Long,
    val aspectRatio: String = "9:16",
    val avatarStyle: AvatarStyle = AvatarStyle.PIP_CIRCLE,
    val hookScript: String,
    val problemScript: String,
    val solutionScript: String,
    val offerScript: String,
    val ctaScript: String,
    val fullScriptText: String,
    val targetPlatformsCsv: String = "SHOPEE,TIKTOK,INSTAGRAM,FACEBOOK",
    val durationSeconds: Int = 30,
    val backgroundMusic: String = "Viral Beat Phonk Lo-Fi",
    val status: VideoStatus = VideoStatus.READY,
    val coverImageRes: String = "showcase_smartwatch_1790132114921",
    val estimatedViews: Int = 18450,
    val estimatedSales: Int = 42,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    // Metadata properties
    val videoDraftName: String = title,
    val creationTimestamp: Long = createdAtEpoch,
    val videoFilePath: String? = null,
    val thumbnailFilePath: String? = null,
    val audioFilePath: String? = null,
    val generatedAssetPaths: List<String> = emptyList(),
    val fileSizeBytes: Long = 0L,
    val customImageUri: String = "",
    val affiliateLink: String = "https://shopee.com.br/afiliado/produto"
)
