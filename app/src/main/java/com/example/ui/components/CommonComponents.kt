package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.VideoStatus
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.InstagramPink
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.TikTokCyan

@Composable
fun PlatformBadge(platform: SocialPlatform, modifier: Modifier = Modifier, isSelected: Boolean = false) {
    val (color, label) = when (platform) {
        SocialPlatform.SHOPEE -> ShopeeOrange to "Shopee Vídeo"
        SocialPlatform.TIKTOK -> TikTokCyan to "TikTok Shop"
        SocialPlatform.INSTAGRAM -> InstagramPink to "Instagram Reels"
        SocialPlatform.FACEBOOK -> FacebookBlue to "Facebook Reels"
        SocialPlatform.YOUTUBE -> Color(0xFFFF0000) to "YouTube Shorts"
        SocialPlatform.KWAI -> Color(0xFFFF7700) to "Kwai Vídeo"
    }

    Surface(
        color = if (isSelected) color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null,
        modifier = modifier.testTag("platform_badge_${platform.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun VideoStatusBadge(status: VideoStatus, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        VideoStatus.READY -> Color(0xFF10B981) to "Pronto 9:16"
        VideoStatus.GENERATING -> Color(0xFFFFB300) to "Gerando IA"
        VideoStatus.SCHEDULED -> Color(0xFF3B82F6) to "Agendado"
        VideoStatus.PUBLISHED -> Color(0xFF8B5CF6) to "Publicado"
        VideoStatus.DRAFT -> Color.Gray to "Rascunho"
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PostStatusBadge(status: PostStatus, modifier: Modifier = Modifier) {
    val (color, icon, text) = when (status) {
        PostStatus.PUBLISHED -> Triple(Color(0xFF10B981), Icons.Default.CheckCircle, "Publicado")
        PostStatus.POSTING -> Triple(Color(0xFFFF9800), Icons.Default.PlayArrow, "Postando...")
        PostStatus.QUEUED -> Triple(Color(0xFF06B6D4), Icons.Default.Schedule, "Na Fila")
        PostStatus.IDLE -> Triple(Color.Gray, Icons.Default.Schedule, "Pendente")
        PostStatus.FAILED -> Triple(Color(0xFFFF5252), Icons.Default.Schedule, "Falhou")
    }

    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun AudioWaveformBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 12,
    barColor: Color = Color(0xFFA78BFA),
    maxHeight: Dp = 28.dp
) {
    val transition = rememberInfiniteTransition(label = "wave_bars")

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val animDuration = 400 + (i * 70) % 500
            val animatedHeightFraction by transition.animateFloat(
                initialValue = 0.2f,
                targetValue = if (isPlaying) 0.95f else 0.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = animDuration, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(if (isPlaying) animatedHeightFraction else 0.25f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}
