package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AvatarStyle

@Entity(tableName = "avatars")
data class AvatarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val roleTitle: String,
    val niche: String,
    val drawableResName: String,
    val customImageUri: String = "",
    val defaultStyle: AvatarStyle = AvatarStyle.PIP_CIRCLE,
    val personalityPrompt: String,
    val isCustom: Boolean = false
)
