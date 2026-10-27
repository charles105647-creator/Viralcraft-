package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform

@Entity(tableName = "post_schedules")
data class PostScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoProjectId: Long,
    val platform: SocialPlatform,
    val caption: String,
    val hashtags: String,
    val scheduledTimeEpoch: Long,
    val status: PostStatus = PostStatus.QUEUED,
    val externalPostId: String = "",
    val publishedUrl: String = "",
    val errorMessage: String = "",
    val logs: String = "Agendado no sistema de postagem automática"
)
