package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AvatarStyle
import com.example.data.model.SocialPlatform
import com.example.ui.components.AudioWaveformBars
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.TikTokCyan
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import android.content.Intent
import android.net.Uri
import coil.compose.AsyncImage

@Composable
fun VideoPlayerScreen(
    viewModel: MainViewModel,
    onNavigateToEditor: () -> Unit,
    onNavigateToPublish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentVideo by viewModel.currentPlayingVideo.collectAsStateWithLifecycle()
    val activePlatform by viewModel.playerPreviewPlatform.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.audioEngine.isPlaying.collectAsStateWithLifecycle()
    val avatars by viewModel.avatars.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val socialAccounts by viewModel.socialAccounts.collectAsStateWithLifecycle()

    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var speechSpeed by remember { mutableFloatStateOf(1.15f) }

    val video = currentVideo
    val avatar = remember(video, avatars) {
        avatars.find { it.id == (video?.avatarId ?: 1) } ?: avatars.firstOrNull()
    }
    val product = remember(video, products) {
        products.find { it.id == (video?.productId ?: 1) } ?: products.firstOrNull()
    }

    // Auto progress animation when playing
    LaunchedEffect(isAudioPlaying) {
        if (isAudioPlaying) {
            val totalMillis = ((video?.durationSeconds ?: 30) * 1000L)
            val startTime = System.currentTimeMillis()
            while (isAudioPlaying && playbackProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                playbackProgress = (elapsed.toFloat() / totalMillis).coerceIn(0f, 1f)
                delay(100)
            }
        }
    }

    // Speech subtitle text according to progress
    val currentSubtitleText = remember(playbackProgress, video) {
        if (video == null) "Carregando vídeo..."
        else when {
            playbackProgress < 0.20f -> video.hookScript
            playbackProgress < 0.45f -> video.problemScript
            playbackProgress < 0.75f -> video.solutionScript
            playbackProgress < 0.90f -> video.offerScript
            else -> video.ctaScript
        }
    }

    // Subtle Ken Burns slow zoom animation
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_zoom"
    )

    // Avatar pulse animation when speaking
    val avatarPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isAudioPlaying) 1.12f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Platform preview switcher pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Simulador 9:16",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            // Channel Switcher
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SocialPlatform.values().forEach { platform ->
                    val isSelected = activePlatform == platform
                    Surface(
                        color = if (isSelected) Color(platform.brandColorHex).copy(alpha = 0.25f) else DarkSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(platform.brandColorHex)) else null,
                        modifier = Modifier
                            .clickable { viewModel.setPlayerPreviewPlatform(platform) }
                            .testTag("preview_platform_${platform.name.lowercase()}")
                    ) {
                        Text(
                            text = platform.displayName.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(platform.brandColorHex) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 9:16 Realistic Smartphone Video Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2D2447)),
                modifier = Modifier
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("player_9_16_canvas")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. PRODUCT BACKGROUND VIDEO / SHOWCASE
                    val coverRes = video?.coverImageRes ?: "showcase_smartwatch_1790132114921"
                    val bgResId = context.resources.getIdentifier(coverRes, "drawable", context.packageName)
                    if (!video?.customImageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = video!!.customImageUri,
                            contentDescription = "Vídeo do Produto 9:16",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(zoomScale)
                        )
                    } else {
                        if (bgResId != 0) {
                            Image(
                                painter = painterResource(id = bgResId),
                                contentDescription = "Vídeo do Produto 9:16",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(zoomScale)
                            )
                        }
                    }

                    // Subtle dark gradient for readability of subtitles
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // 2. AVATAR PRESENTER OVERLAY
                    val avatarResName = avatar?.drawableResName ?: "avatar_sophia_1790132087493"
                    val avatarResId = context.resources.getIdentifier(avatarResName, "drawable", context.packageName)

                    if (video?.avatarStyle == AvatarStyle.SPLIT_SCREEN) {
                        // Top Split Presenter
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .align(Alignment.TopCenter)
                        ) {
                            if (avatarResId != 0) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = avatar?.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    } else {
                        // PIP Circular Bubble Avatar (default)
                        Box(
                            modifier = Modifier
                                .padding(top = 40.dp, start = 14.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .scale(avatarPulse)
                                    .clip(CircleShape)
                                    .border(
                                        2.5.dp,
                                        if (isAudioPlaying) BrandSecondary else BrandPrimary,
                                        CircleShape
                                    )
                                    .background(Color.DarkGray)
                            ) {
                                if (avatarResId != 0) {
                                    Image(
                                        painter = painterResource(id = avatarResId),
                                        contentDescription = avatar?.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            // Speech Audio Waves Indicator Badge
                            if (isAudioPlaying) {
                                Surface(
                                    color = BrandSecondary,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AudioWaveformBars(isPlaying = true, barCount = 6, barColor = Color.White, maxHeight = 10.dp)
                                    }
                                }
                            }
                        }
                    }

                    // 3. SOCIAL MEDIA UI OVERLAY SIMULATION
                    when (activePlatform) {
                        SocialPlatform.SHOPEE -> {
                            // Shopee Vídeo UI
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Dynamic Subtitle Banner
                                DynamicSubtitleCard(subtitle = currentSubtitleText)

                                // Shopee Product Card
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF0F0F0))
                                        ) {
                                            if (!video?.customImageUri.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = video!!.customImageUri,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else if (bgResId != 0) {
                                                Image(
                                                    painter = painterResource(id = bgResId),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = product?.title ?: "Smartwatch Ultra Pro",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "R$ ${String.format("%.2f", product?.promoPrice ?: 139.90)}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = ShopeeOrange
                                                )
                                                Text(
                                                    text = "-${product?.discountPercent ?: 52}%",
                                                    fontSize = 10.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .background(ShopeeOrange, RoundedCornerShape(3.dp))
                                                        .padding(horizontal = 3.dp)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                val targetLink = video?.affiliateLink?.ifBlank { null }
                                                    ?: product?.productLink
                                                    ?: "https://shopee.com.br"
                                                runCatching {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetLink))
                                                    context.startActivity(intent)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ShopeeOrange),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Comprar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        SocialPlatform.TIKTOK -> {
                            // TikTok Shop UI
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Right actions column
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 8.dp, bottom = 40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    TikTokActionIcon(icon = Icons.Default.Favorite, count = "45.8K", tint = Color.Red)
                                    TikTokActionIcon(icon = Icons.Default.ChatBubble, count = "1.3K")
                                    TikTokActionIcon(icon = Icons.Default.Bookmark, count = "9.4K")
                                    TikTokActionIcon(icon = Icons.Default.Share, count = "3.2K")
                                }

                                // Bottom details
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(start = 12.dp, end = 64.dp, bottom = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    DynamicSubtitleCard(subtitle = currentSubtitleText)

                                    // TikTok Yellow Shop Badge
                                    Surface(
                                        color = Color(0xFFFE2C55),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            val targetLink = video?.affiliateLink?.ifBlank { null }
                                                ?: product?.productLink
                                                ?: "https://vt.tiktok.com"
                                            runCatching {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetLink))
                                                context.startActivity(intent)
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Text("TikTok Shop • R$ ${String.format("%.2f", product?.promoPrice ?: 139.90)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }

                                    val currentAcc = socialAccounts.firstOrNull { it.platform == SocialPlatform.TIKTOK }
                                    Text(
                                        text = currentAcc?.accountHandle ?: "@viralcraft_brasil",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Para tudo e olha esse achadinho! #tiktokmademebuyit #achadinhos #review",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        SocialPlatform.INSTAGRAM, SocialPlatform.FACEBOOK, SocialPlatform.YOUTUBE, SocialPlatform.KWAI -> {
                            // Reels Clean UI
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DynamicSubtitleCard(subtitle = currentSubtitleText)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${activePlatform.displayName} • Áudio Original em Alta",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = product?.title ?: "Oferta Exclusiva",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Surface(
                                        color = BrandPrimary,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Link na Bio",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Playback Timeline and Controls
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentSec = ((video?.durationSeconds ?: 30) * playbackProgress).toInt()
                    Text(
                        text = "${currentSec}s",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = playbackProgress,
                        onValueChange = { playbackProgress = it },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = BrandSecondary,
                            activeTrackColor = BrandSecondary
                        )
                    )
                    Text(
                        text = "${video?.durationSeconds ?: 30}s",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Play / Pause / Speed / Share row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Stop voice
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isAudioPlaying) {
                                    viewModel.stopVideoVoice()
                                } else {
                                    val textToNarrate = video?.fullScriptText
                                        ?: "Para tudo e olha essa promoção do smartwatch ultra titânio!"
                                    viewModel.playVideoVoice(textToNarrate, 1.0f, speechSpeed)
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrandSecondary)
                                .testTag("player_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause narração",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isAudioPlaying) "Voz IA Narrando..." else "Reproduzir Áudio IA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Avatar & Legendas Sincronizados",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Speed Pill Switcher
                    Surface(
                        color = BrandPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            speechSpeed = when (speechSpeed) {
                                1.0f -> 1.15f
                                1.15f -> 1.25f
                                else -> 1.0f
                            }
                        }
                    ) {
                        Text(
                            text = "${speechSpeed}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Open in Editor CTA
                    IconButton(
                        onClick = {
                            if (video != null) {
                                viewModel.loadProjectIntoEditor(video)
                            }
                            onNavigateToEditor()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandSecondary.copy(alpha = 0.2f))
                            .testTag("open_in_editor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Editar vídeo",
                            tint = BrandSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Direct Publish CTA
                    Button(
                        onClick = onNavigateToPublish,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("publish_video_cta")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Postar Agora", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun DynamicSubtitleCard(subtitle: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.78f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = subtitle,
            color = Color(0xFFFFEB3B),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun TikTokActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, count: String, tint: Color = Color.White) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
        Text(text = count, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
