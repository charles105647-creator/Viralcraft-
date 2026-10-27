package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.AvatarEntity
import com.example.data.model.FontStyleChoice
import com.example.data.model.SocialPlatform
import com.example.data.model.TextBgStyle
import com.example.data.model.TextOverlay
import com.example.data.model.TextVerticalPosition
import com.example.data.model.VideoClip
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.TikTokCyan
import com.example.ui.viewmodel.MainViewModel

/**
 * Real-time 9:16 Video Editor Preview Component.
 * Accurately displays active clips (with cut and trim boundaries),
 * active animated subtitles/text overlays, avatar PiP, and platform UI safe-zone guides.
 */
@Composable
fun RealtimeVideoPreviewComponent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clips by viewModel.editorClips.collectAsStateWithLifecycle()
    val textOverlays by viewModel.editorTextOverlays.collectAsStateWithLifecycle()
    val playhead by viewModel.editorPlayhead.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isEditorPlaying.collectAsStateWithLifecycle()
    val speed by viewModel.previewPlaybackSpeed.collectAsStateWithLifecycle()
    val platformGuide by viewModel.previewPlatformGuide.collectAsStateWithLifecycle()
    val showSafeZones by viewModel.previewShowSafeZones.collectAsStateWithLifecycle()
    val showAvatarPiP by viewModel.previewShowAvatarPiP.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreenPreview.collectAsStateWithLifecycle()
    val avatars by viewModel.avatars.collectAsStateWithLifecycle()
    val currentProject by viewModel.editorVideoProject.collectAsStateWithLifecycle()

    val totalDuration = viewModel.getTotalEditorDuration()

    val activeAvatar = remember(currentProject, avatars) {
        val avatarId = currentProject?.avatarId ?: 1L
        avatars.find { it.id == avatarId } ?: avatars.firstOrNull()
    }

    // Resolve Active Clip and relative position inside clip
    val (activeClip, clipIndex, elapsedInClip) = remember(playhead, clips) {
        var acc = 0f
        var foundClip: VideoClip? = null
        var foundIndex = 0
        var elapsed = 0f

        for (i in clips.indices) {
            val clip = clips[i]
            val duration = clip.trimmedDurationSeconds
            if (playhead >= acc && playhead <= acc + duration) {
                foundClip = clip
                foundIndex = i
                elapsed = (playhead - acc) * clip.speed
                break
            }
            acc += duration
        }

        if (foundClip == null && clips.isNotEmpty()) {
            foundClip = clips.last()
            foundIndex = clips.size - 1
            elapsed = foundClip.trimmedDurationSeconds * foundClip.speed
        }

        Triple(foundClip, foundIndex, elapsed)
    }

    // Transition indicator: triggers a brief cut badge when crossing cut boundaries
    var lastClipIndex by remember { mutableStateOf(clipIndex) }
    var showCutFlash by remember { mutableStateOf(false) }

    LaunchedEffect(clipIndex) {
        if (clipIndex != lastClipIndex) {
            lastClipIndex = clipIndex
            showCutFlash = true
            kotlinx.coroutines.delay(400)
            showCutFlash = false
        }
    }

    // Resolve active text overlay(s) at playhead
    val activeTextOverlay = remember(playhead, textOverlays) {
        textOverlays.firstOrNull { playhead >= it.startTimeSeconds && playhead <= it.endTimeSeconds }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Preview Header Bar with Status and Quick Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Live Rec / Preview Indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) Color(0xFFFF5252) else Color(0xFF10B981))
                )
                Text(
                    text = if (isPlaying) "PREVIEW EM TEMPO REAL" else "PAUSADO NO CORTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) Color(0xFFFF8A80) else Color(0xFFA7F3D0)
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Text(
                        text = "9:16",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Safe Zone Guide Toggle
                IconButton(
                    onClick = { viewModel.togglePreviewSafeZones() },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (showSafeZones) BrandPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant)
                        .testTag("toggle_safe_zones_button")
                ) {
                    Icon(
                        imageVector = if (showSafeZones) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Safe Zones",
                        tint = if (showSafeZones) BrandPrimary else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Avatar PiP Toggle
                IconButton(
                    onClick = { viewModel.togglePreviewAvatarPiP() },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (showAvatarPiP) BrandSecondary.copy(alpha = 0.25f) else DarkSurfaceVariant)
                        .testTag("toggle_avatar_pip_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar PiP",
                        tint = if (showAvatarPiP) BrandSecondary else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Fullscreen Button
                IconButton(
                    onClick = { viewModel.toggleFullscreenPreview(true) },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .testTag("open_fullscreen_preview_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Tela Cheia",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 9:16 Real-Time Screen Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(1.5.dp, Color(0xFF2D2447), RoundedCornerShape(16.dp))
                .testTag("realtime_video_preview_canvas"),
            contentAlignment = Alignment.Center
        ) {
            PreviewCanvasContent(
                activeClip = activeClip,
                clipIndex = clipIndex,
                totalClips = clips.size,
                elapsedInClip = elapsedInClip,
                activeTextOverlay = activeTextOverlay,
                activeAvatar = if (showAvatarPiP) activeAvatar else null,
                isPlaying = isPlaying,
                showSafeZones = showSafeZones,
                platformGuide = platformGuide,
                showCutFlash = showCutFlash,
                playhead = playhead,
                totalDuration = totalDuration
            )
        }

        // Live Scrubber & Timecode
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Replay from start
            IconButton(
                onClick = { viewModel.setEditorPlayhead(0f) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .testTag("preview_replay_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Voltar ao Início",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Jump to Prev Cut
            IconButton(
                onClick = { viewModel.jumpToPreviousCut() },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .testTag("jump_prev_cut_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Corte Anterior",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Play / Pause
            IconButton(
                onClick = { viewModel.toggleEditorPlayback() },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary)
                    .testTag("preview_play_toggle_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Jump to Next Cut
            IconButton(
                onClick = { viewModel.jumpToNextCut() },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .testTag("jump_next_cut_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Próximo Corte",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Scrubber Slider
            Slider(
                value = playhead,
                onValueChange = { viewModel.setEditorPlayhead(it) },
                valueRange = 0f..totalDuration.coerceAtLeast(1f),
                colors = SliderDefaults.colors(
                    thumbColor = BrandSecondary,
                    activeTrackColor = BrandSecondary,
                    inactiveTrackColor = Color(0xFF261E38)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("preview_scrubber_slider")
            )

            // Timecode Badge
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Text(
                    text = "${String.format("%.1f", playhead)}s / ${String.format("%.1f", totalDuration)}s",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        // Secondary Control Bar: Frame Step, Speed & Platform Guides
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Frame stepping (-0.1s / +0.1s)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.clickable { viewModel.stepEditorPlayhead(-0.1f) }
                ) {
                    Text(
                        text = "-0.1s",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.clickable { viewModel.stepEditorPlayhead(0.1f) }
                ) {
                    Text(
                        text = "+0.1s",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // Subtitle Jump
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.clickable { viewModel.jumpToNextSubtitle() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Subtitles, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "Próx. Legenda", fontSize = 10.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Playback Speed Chips (0.5x, 1.0x, 1.5x)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(0.5f, 1.0f, 1.5f).forEach { s ->
                    val isSelected = speed == s
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) BrandPrimary else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BrandPrimary else DarkCardBorder
                        ),
                        modifier = Modifier.clickable { viewModel.setPlaybackSpeed(s) }
                    ) {
                        Text(
                            text = "${s}x",
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Dialog if triggered
    if (isFullscreen) {
        FullscreenRealtimePreviewModal(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleFullscreenPreview(false) }
        )
    }
}

/**
 * Pure 9:16 canvas rendering of the video cuts, text overlay, avatar, and platform simulation.
 */
@Composable
fun PreviewCanvasContent(
    activeClip: VideoClip?,
    clipIndex: Int,
    totalClips: Int,
    elapsedInClip: Float,
    activeTextOverlay: TextOverlay?,
    activeAvatar: AvatarEntity?,
    isPlaying: Boolean,
    showSafeZones: Boolean,
    platformGuide: SocialPlatform?,
    showCutFlash: Boolean,
    playhead: Float,
    totalDuration: Float
) {
    val context = LocalContext.current

    // Subtle Ken Burns motion for preview dynamism
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
    val subtleZoom by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Clip Background Image
        val coverRes = activeClip?.drawableRes ?: "showcase_smartwatch_1790132114921"
        val bgResId = context.resources.getIdentifier(coverRes, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (isPlaying) subtleZoom else 1.0f)
            )
        }

        // Vignette Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // 2. Safe Zone Guides (if enabled)
        if (showSafeZones) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 20.dp)
                    .drawBehind {
                        drawRect(
                            color = Color(0xFF00E5FF).copy(alpha = 0.6f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    }
            ) {
                Text(
                    text = "Área Segura 9:16 (Safe Zone)",
                    fontSize = 9.sp,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }

        // 3. Platform UI Simulation (Shopee, TikTok, Instagram)
        if (platformGuide != null) {
            when (platformGuide) {
                SocialPlatform.TIKTOK -> {
                    // TikTok Right Action Sidebar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // User Profile Pic with Follow Plus
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .offset(y = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF0050)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Heart
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("18.4K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Comment
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("842", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Bookmark
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("3.1K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Share
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("1.2K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Rotating Audio Vinyl
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(1.dp, Color.Gray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }

                    // TikTok Bottom Description
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 12.dp, bottom = 12.dp, end = 50.dp)
                    ) {
                        Text("@achadinhos_virais", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Link na bio com desconto! 🔥 #tiktokmademebuyit", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp)
                    }
                }

                SocialPlatform.SHOPEE -> {
                    // Shopee Orange Cart Floating Button
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 12.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = ShopeeOrange,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text("Comprar com Cupom", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Top Shopee Video Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ShopeeOrange.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 12.dp, top = 36.dp)
                    ) {
                        Text(
                            text = "Achadinho Shopee • Frete Grátis",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                SocialPlatform.INSTAGRAM -> {
                    // Instagram Reels Right Action Sidebar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Icon(imageVector = Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                else -> {}
            }
        }

        // 4. Top Clip & Cut Timing Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(10.dp))
                    Text(
                        text = "Clipe ${clipIndex + 1}/$totalClips: ${activeClip?.title ?: "Cena"}",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Text(
                    text = "${String.format("%.1f", elapsedInClip)}s corte • ${activeClip?.speed ?: 1.0f}x",
                    fontSize = 10.sp,
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // 5. Presenter Avatar PiP Overlay (simulates active AI talking avatar)
        if (activeAvatar != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 36.dp)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(2.dp, if (isPlaying) BrandPrimary else Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val avatarResId = context.resources.getIdentifier(
                    activeAvatar.drawableResName,
                    "drawable",
                    context.packageName
                )
                if (avatarResId != 0) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = "Avatar Apresentador",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Audio speech wave ring while playing
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BrandSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(8.dp))
                    }
                }
            }
        }

        // 6. Active Subtitle / Text Overlay Rendering
        if (activeTextOverlay != null) {
            val textColor = Color(activeTextOverlay.textColorHex)
            val alignmentModifier = when (activeTextOverlay.positionY) {
                TextVerticalPosition.TOP -> Modifier.align(Alignment.TopCenter).padding(top = 40.dp)
                TextVerticalPosition.CENTER -> Modifier.align(Alignment.Center)
                TextVerticalPosition.BOTTOM -> Modifier.align(Alignment.BottomCenter).padding(bottom = if (platformGuide != null) 36.dp else 14.dp)
            }

            val customFontFamily = when (activeTextOverlay.fontStyle) {
                FontStyleChoice.DEFAULT_BOLD -> FontFamily.Default
                FontStyleChoice.MODERN_SANS -> FontFamily.SansSerif
                FontStyleChoice.VIRAL_IMPACT -> FontFamily.Monospace
                FontStyleChoice.ELEGANT_SERIF -> FontFamily.Serif
                FontStyleChoice.NEON_DISPLAY -> FontFamily.Cursive
            }

            val bgModifier = when (activeTextOverlay.bgStyle) {
                TextBgStyle.SOLID_BOX -> Modifier
                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                TextBgStyle.PILL_BADGE -> Modifier
                    .background(textColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .border(1.5.dp, textColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                TextBgStyle.OUTLINE -> Modifier
                    .border(1.5.dp, textColor, RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                TextBgStyle.NONE -> Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            }

            Box(modifier = alignmentModifier.padding(horizontal = 16.dp)) {
                Text(
                    text = activeTextOverlay.text,
                    color = textColor,
                    fontSize = activeTextOverlay.fontSizeSp.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = customFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = bgModifier
                )
            }
        }

        // 7. Cut Flash Transition Indicator
        AnimatedVisibility(
            visible = showCutFlash,
            enter = fadeIn(tween(100)) + scaleIn(tween(100)),
            exit = fadeOut(tween(250)) + scaleOut(tween(250)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Transição: ${activeClip?.title ?: "Corte"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Immersive Fullscreen Preview Modal.
 * Lets the creator test the complete 9:16 vertical experience with simulated platform overlays,
 * safe zones, audio volume, speed toggling, and frame stepping.
 */
@Composable
fun FullscreenRealtimePreviewModal(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val clips by viewModel.editorClips.collectAsStateWithLifecycle()
    val textOverlays by viewModel.editorTextOverlays.collectAsStateWithLifecycle()
    val playhead by viewModel.editorPlayhead.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isEditorPlaying.collectAsStateWithLifecycle()
    val speed by viewModel.previewPlaybackSpeed.collectAsStateWithLifecycle()
    val platformGuide by viewModel.previewPlatformGuide.collectAsStateWithLifecycle()
    val showSafeZones by viewModel.previewShowSafeZones.collectAsStateWithLifecycle()
    val showAvatarPiP by viewModel.previewShowAvatarPiP.collectAsStateWithLifecycle()
    val avatars by viewModel.avatars.collectAsStateWithLifecycle()
    val currentProject by viewModel.editorVideoProject.collectAsStateWithLifecycle()

    val totalDuration = viewModel.getTotalEditorDuration()

    val activeAvatar = remember(currentProject, avatars) {
        val avatarId = currentProject?.avatarId ?: 1L
        avatars.find { it.id == avatarId } ?: avatars.firstOrNull()
    }

    val (activeClip, clipIndex, elapsedInClip) = remember(playhead, clips) {
        var acc = 0f
        var foundClip: VideoClip? = null
        var foundIndex = 0
        var elapsed = 0f

        for (i in clips.indices) {
            val clip = clips[i]
            val duration = clip.trimmedDurationSeconds
            if (playhead >= acc && playhead <= acc + duration) {
                foundClip = clip
                foundIndex = i
                elapsed = (playhead - acc) * clip.speed
                break
            }
            acc += duration
        }

        if (foundClip == null && clips.isNotEmpty()) {
            foundClip = clips.last()
            foundIndex = clips.size - 1
            elapsed = foundClip.trimmedDurationSeconds * foundClip.speed
        }

        Triple(foundClip, foundIndex, elapsed)
    }

    val activeTextOverlay = remember(playhead, textOverlays) {
        textOverlays.firstOrNull { playhead >= it.startTimeSeconds && playhead <= it.endTimeSeconds }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Main 9:16 Video Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp),
                contentAlignment = Alignment.Center
            ) {
                PreviewCanvasContent(
                    activeClip = activeClip,
                    clipIndex = clipIndex,
                    totalClips = clips.size,
                    elapsedInClip = elapsedInClip,
                    activeTextOverlay = activeTextOverlay,
                    activeAvatar = if (showAvatarPiP) activeAvatar else null,
                    isPlaying = isPlaying,
                    showSafeZones = showSafeZones,
                    platformGuide = platformGuide,
                    showCutFlash = false,
                    playhead = playhead,
                    totalDuration = totalDuration
                )
            }

            // Top Floating Header: Close button, Platform switcher, Safe Zone toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .testTag("fullscreen_close_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
                }

                // Platform overlay switcher
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val platforms = listOf(
                        null to "Limpo",
                        SocialPlatform.TIKTOK to "TikTok",
                        SocialPlatform.SHOPEE to "Shopee",
                        SocialPlatform.INSTAGRAM to "Reels"
                    )

                    platforms.forEach { (p, label) ->
                        val isSelected = platformGuide == p
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BrandPrimary else Color.Black.copy(alpha = 0.65f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BrandPrimary else Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.clickable { viewModel.setPreviewPlatformGuide(p) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { viewModel.togglePreviewSafeZones() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (showSafeZones) BrandPrimary else Color.Black.copy(alpha = 0.65f))
                ) {
                    Icon(
                        imageVector = if (showSafeZones) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Safe Zones",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom Floating Transport & Scrubber Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.Black.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Scrubber with timecode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${String.format("%.1f", playhead)}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )

                        Slider(
                            value = playhead,
                            onValueChange = { viewModel.setEditorPlayhead(it) },
                            valueRange = 0f..totalDuration.coerceAtLeast(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = BrandSecondary,
                                activeTrackColor = BrandSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${String.format("%.1f", totalDuration)}s",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Transport action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.jumpToPreviousCut() }) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Corte Anterior", tint = Color.White)
                        }

                        IconButton(onClick = { viewModel.stepEditorPlayhead(-0.1f) }) {
                            Icon(imageVector = Icons.Default.FastRewind, contentDescription = "-0.1s", tint = Color.White)
                        }

                        IconButton(
                            onClick = { viewModel.toggleEditorPlayback() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.stepEditorPlayhead(0.1f) }) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = "+0.1s", tint = Color.White)
                        }

                        IconButton(onClick = { viewModel.jumpToNextCut() }) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Próximo Corte", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
