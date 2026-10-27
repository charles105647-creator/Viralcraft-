package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SocialPlatform

@Entity(tableName = "social_accounts")
data class SocialAccountEntity(
    @PrimaryKey
    val platform: SocialPlatform,
    val accountHandle: String,
    val accountName: String,
    val isConnected: Boolean = true,
    val autoPublishEnabled: Boolean = true,
    val followersFormatted: String = "128.4K",
    val authTokenMask: String = "oauth_live_tok_••••8932",
    val lastSyncEpoch: Long = System.currentTimeMillis(),
    val profileLink: String = "",
    val directPublishApiUrl: String = ""
)
