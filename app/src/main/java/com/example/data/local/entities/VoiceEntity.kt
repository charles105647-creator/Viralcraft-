package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.VoiceEmotion

@Entity(tableName = "voices")
data class VoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val toneLabel: String,
    val samplePhrase: String,
    val emotion: VoiceEmotion = VoiceEmotion.ENTHUSIASTIC,
    val pitch: Float = 1.0f,
    val speed: Float = 1.15f, // 1.15x is optimal for TikTok & Shopee fast conversion
    val isClonedVoice: Boolean = false,
    val sampleRecordingUri: String = "",
    val cloneStatus: String = "Pronto para uso",
    val audioDurationSeconds: Int = 12
)
