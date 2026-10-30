package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material.icons.filled.VolumeUp
import coil.compose.AsyncImage
import com.example.data.model.AvatarStyle
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.ScrollableTabRow
import com.example.data.model.ClipTransition
import com.example.data.model.TransitionEffect
import com.example.data.model.VideoExportQuality
import com.example.ui.theme.BrandGreen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.audio.AudioLibrary
import com.example.data.model.BackgroundAudioTrack
import com.example.data.model.FontStyleChoice
import com.example.data.model.TextBgStyle
import com.example.data.model.TextOverlay
import com.example.data.model.TextVerticalPosition
import com.example.data.model.VideoClip
import com.example.ui.components.AudioWaveformBars
import com.example.ui.components.RealtimeVideoPreviewComponent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.TikTokCyan
import com.example.ui.viewmodel.MainViewModel

val VIRAL_COLOR_PALETTE = listOf(
    0xFFFFE600 to "Amarelo Viral (TikTok)",
    0xFFFFFFFF to "Branco Puro",
    0xFFFF5722 to "Laranja Shopee",
    0xFF00F2FE to "Ciano Neon",
    0xFFA855F7 to "Roxo Neon",
    0xFF10B981 to "Verde Vendas",
    0xFFFF0055 to "Rosa Pink",
    0xFF000000 to "Preto Intenso"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    viewModel: MainViewModel,
    onNavigateToPlayer: () -> Unit,
    onNavigateToScriptAssistant: () -> Unit,
    onNavigateToAvatarGallery: (() -> Unit)? = null,
    onNavigateToPublish: (() -> Unit)? = null,
    onBackToMenu: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clips by viewModel.editorClips.collectAsStateWithLifecycle()
    val textOverlays by viewModel.editorTextOverlays.collectAsStateWithLifecycle()
    val audioTrack by viewModel.editorAudioTrack.collectAsStateWithLifecycle()
    val playhead by viewModel.editorPlayhead.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isEditorPlaying.collectAsStateWithLifecycle()
    val selectedClipId by viewModel.selectedClipId.collectAsStateWithLifecycle()
    val selectedTextId by viewModel.selectedTextId.collectAsStateWithLifecycle()
    val project by viewModel.editorVideoProject.collectAsStateWithLifecycle()
    val avatars by viewModel.avatars.collectAsStateWithLifecycle()

    BackHandler(enabled = onBackToMenu != null) {
        onBackToMenu?.invoke()
    }
    val exportQuality by viewModel.editorExportQuality.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Clipes, 1: Transições, 2: Texto, 3: Áudio, 4: Avatar
    var showExportQualityDialog by remember { mutableStateOf(false) }
    var selectedExportQualityChoice by remember(exportQuality) { mutableStateOf(exportQuality) }

    // System Audio Picker for user background upload
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var displayName = "Meu Áudio de Fundo"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    displayName = cursor.getString(nameIndex)
                }
            }
            val userTrack = AudioLibrary.createFromUserFile(displayName, uri.toString())
            viewModel.selectBackgroundAudio(userTrack)
        }
    }

    val totalDuration = viewModel.getTotalEditorDuration()

    val selectedClip = remember(selectedClipId, clips) {
        clips.find { it.id == selectedClipId } ?: clips.firstOrNull()
    }

    val selectedText = remember(selectedTextId, textOverlays) {
        textOverlays.find { it.id == selectedTextId } ?: textOverlays.firstOrNull()
    }

    if (showExportQualityDialog) {
        AlertDialog(
            onDismissRequest = { showExportQualityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.HighQuality, contentDescription = null, tint = BrandGreen)
                    Text("Qualidade de Exportação", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Escolha a resolução ideal para o seu vídeo antes de salvar ou publicar nas redes:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    VideoExportQuality.values().forEach { quality ->
                        val isSelected = selectedExportQualityChoice == quality
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BrandGreen.copy(alpha = 0.15f) else Color(0xFF130E24)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) BrandGreen else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedExportQualityChoice = quality }
                                .testTag("quality_option_${quality.name.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = quality.label,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (quality == VideoExportQuality.ULTRA_HD_4K) Color(0xFFFFD700).copy(alpha = 0.2f) else BrandGreen.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (quality == VideoExportQuality.ULTRA_HD_4K) "Cinema 4K" else "Recomendado",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (quality == VideoExportQuality.ULTRA_HD_4K) Color(0xFFFFD700) else BrandGreen,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selecionado",
                                            tint = BrandGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${quality.resolution} • ${quality.fps} FPS • ${quality.bitrateMbps} Mbps",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandSecondary
                                )

                                Text(
                                    text = quality.description,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setEditorExportQuality(selectedExportQualityChoice)
                        viewModel.saveEditorToProject(selectedExportQualityChoice)
                        showExportQualityDialog = false
                        onNavigateToPlayer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_export_quality_button")
                ) {
                    Text("Salvar e Exportar Vídeo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExportQualityDialog = false },
                    modifier = Modifier.testTag("cancel_export_quality_button")
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Header with Back Button, Save and Post Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onBackToMenu != null) {
                    IconButton(
                        onClick = { onBackToMenu() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .testTag("editor_back_to_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar ao Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Editor de Vídeo 9:16",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Surface(
                            color = BrandPrimary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", totalDuration)}s",
                                color = BrandPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                // Clickable Export Quality indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { showExportQualityDialog = true }
                        .testTag("editor_quality_selector_chip")
                ) {
                    Surface(
                        color = if (exportQuality == VideoExportQuality.ULTRA_HD_4K) Color(0xFFFFD700).copy(alpha = 0.2f) else BrandGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = null,
                                tint = if (exportQuality == VideoExportQuality.ULTRA_HD_4K) Color(0xFFFFD700) else BrandGreen,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = exportQuality.label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (exportQuality == VideoExportQuality.ULTRA_HD_4K) Color(0xFFFFD700) else BrandGreen
                            )
                        }
                    }
                    Text(
                        text = "• Alterar qualidade",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                // Save button
                Button(
                    onClick = {
                        showExportQualityDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_editor_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salvar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Post directly after edit button
                if (onNavigateToPublish != null) {
                    Button(
                        onClick = {
                            viewModel.saveEditorToProject(exportQuality)
                            onNavigateToPublish.invoke()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("post_after_edit_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Postar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(10.dp))

        // Real-Time 9:16 Video Preview Component with live cuts, subtitles, safe-zones & scrubber
        RealtimeVideoPreviewComponent(viewModel = viewModel)

        Spacer(modifier = Modifier.height(6.dp))

        // Multi-Track Timeline Visual Bar (Clips, Text, Audio)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Clips Track (Horizontal segments)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF130E24)),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    clips.forEach { clip ->
                        val isSelected = clip.id == selectedClipId
                        val weight = (clip.trimmedDurationSeconds / totalDuration).coerceAtLeast(0.05f)
                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) BrandPrimary else Color(0xFF2E264E))
                                .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) Color.White else Color.Transparent)
                                .clickable { viewModel.selectClipForEditing(clip.id) }
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = clip.title.split(".").lastOrNull()?.trim() ?: clip.title,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 2. Subtitles Track
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF130E24)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    textOverlays.forEach { overlay ->
                        val isSelected = overlay.id == selectedTextId
                        Surface(
                            color = Color(overlay.textColorHex).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(3.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White) else null,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .clickable { viewModel.selectTextOverlay(overlay.id) }
                        ) {
                            Text(
                                text = overlay.text,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (overlay.textColorHex == 0xFFFFFFFF) Color.Black else Color.White,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                // 3. Audio Track Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(13.dp))
                        Text(
                            text = "${audioTrack.title} (${(audioTrack.volume * 100).toInt()}%)",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AudioWaveformBars(isPlaying = isPlaying, barCount = 10, barColor = BrandSecondary, maxHeight = 12.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool Tabs: 1. Clipes | 2. Transições | 3. Texto | 4. Trilha | 5. Avatar
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurfaceVariant,
            contentColor = BrandPrimary,
            edgePadding = 6.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = BrandSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Clipes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_editor_clips")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.AutoAwesomeMotion, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (activeTab == 1) BrandSecondary else Color(0xFFAFA7CD))
                        Text("Transições", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_editor_transitions")
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Texto & Cores", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_editor_text")
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Audiotrack, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Trilha Sonora", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_editor_audio")
            )
            Tab(
                selected = activeTab == 4,
                onClick = { activeTab = 4 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Avatar IA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_editor_avatar")
            )
        }


        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content Panels (Scrollable)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (activeTab) {
                0 -> {
                    // TAB 0: CORTAR & APARAR CLIPES
                    item {
                        if (selectedClip != null) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Aparar: ${selectedClip.title}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${String.format("%.1f", selectedClip.trimmedDurationSeconds)}s",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandPrimary
                                        )
                                    }

                                    // Trim Dual Sliders (Início e Fim)
                                    Text(
                                        text = "Início do Corte: ${String.format("%.1f", selectedClip.startTrimSeconds)}s",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Slider(
                                        value = selectedClip.startTrimSeconds,
                                        onValueChange = { newStart ->
                                            viewModel.updateClipTrim(selectedClip.id, newStart, selectedClip.endTrimSeconds)
                                        },
                                        valueRange = 0f..selectedClip.originalDurationSeconds - 0.5f,
                                        colors = SliderDefaults.colors(thumbColor = BrandPrimary, activeTrackColor = BrandPrimary)
                                    )

                                    Text(
                                        text = "Fim do Corte: ${String.format("%.1f", selectedClip.endTrimSeconds)}s (de ${selectedClip.originalDurationSeconds}s)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Slider(
                                        value = selectedClip.endTrimSeconds,
                                        onValueChange = { newEnd ->
                                            viewModel.updateClipTrim(selectedClip.id, selectedClip.startTrimSeconds, newEnd)
                                        },
                                        valueRange = selectedClip.startTrimSeconds + 0.5f..selectedClip.originalDurationSeconds,
                                        colors = SliderDefaults.colors(thumbColor = BrandSecondary, activeTrackColor = BrandSecondary)
                                    )

                                    // Action Buttons: Split at Playhead, Duplicate, Delete
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.splitClipAtPlayhead() },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .testTag("split_clip_button")
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Dividir no Cursor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        IconButton(
                                            onClick = { viewModel.duplicateClip(selectedClip.id) },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2E264E))
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicar", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteClip(selectedClip.id) },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF5252).copy(alpha = 0.2f))
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add Clip Button
                        Button(
                            onClick = { viewModel.addClip("Cena do Produto Adicional") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF261D3B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Adicionar Novo Clipe ao Final", color = BrandPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                1 -> {
                    // TAB 1: EFEITOS DE TRANSIÇÃO PROFISSIONAIS
                    item {
                        if (selectedClip != null) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Transição para o Próximo Clipe",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Clipe selecionado: ${selectedClip.title}",
                                                fontSize = 11.sp,
                                                color = BrandSecondary
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BrandSecondary.copy(alpha = 0.2f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = "${selectedClip.transitionToNext.effect.label} (${selectedClip.transitionToNext.durationSeconds}s)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandSecondary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    // Transition Effects Grid
                                    Text(
                                        text = "Biblioteca de Efeitos de Transição:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TransitionEffect.values().toList().chunked(2).forEach { pair ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                pair.forEach { effect ->
                                                    val isEffectSelected = selectedClip.transitionToNext.effect == effect
                                                    Card(
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = if (isEffectSelected) BrandSecondary.copy(alpha = 0.2f) else DarkSurfaceVariant
                                                        ),
                                                        border = androidx.compose.foundation.BorderStroke(
                                                            1.dp,
                                                            if (isEffectSelected) BrandSecondary else DarkCardBorder
                                                        ),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable {
                                                                viewModel.updateClipTransition(
                                                                    selectedClip.id,
                                                                    selectedClip.transitionToNext.copy(effect = effect)
                                                                )
                                                            }
                                                            .testTag("transition_effect_${effect.name.lowercase()}")
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(10.dp),
                                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                            ) {
                                                                Text(
                                                                    text = when (effect) {
                                                                        TransitionEffect.NONE -> "✂️"
                                                                        TransitionEffect.CROSS_FADE -> "✨"
                                                                        TransitionEffect.SLIDE_LEFT -> "◀️"
                                                                        TransitionEffect.SLIDE_RIGHT -> "▶️"
                                                                        TransitionEffect.ZOOM_IN -> "🔍"
                                                                        TransitionEffect.ZOOM_OUT -> "🔎"
                                                                        TransitionEffect.WIPE_UP -> "⬆️"
                                                                        TransitionEffect.FLASH_WHITE -> "⚡"
                                                                    },
                                                                    fontSize = 14.sp
                                                                )
                                                                Text(
                                                                    text = effect.label,
                                                                    fontSize = 12.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isEffectSelected) BrandSecondary else Color.White
                                                                )
                                                            }
                                                            Text(
                                                                text = effect.description,
                                                                fontSize = 10.sp,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                maxLines = 2,
                                                                lineHeight = 13.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Duration selector
                                    Text(
                                        text = "Duração da Transição:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(0.3f to "0.3s", 0.5f to "0.5s", 0.8f to "0.8s", 1.0f to "1.0s").forEach { (dur, label) ->
                                            val isDurSelected = kotlin.math.abs(selectedClip.transitionToNext.durationSeconds - dur) < 0.05f
                                            Button(
                                                onClick = {
                                                    viewModel.updateClipTransition(
                                                        selectedClip.id,
                                                        selectedClip.transitionToNext.copy(durationSeconds = dur)
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isDurSelected) BrandSecondary else DarkSurfaceVariant,
                                                    contentColor = if (isDurSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).testTag("transition_duration_${(dur * 10).toInt()}")
                                            ) {
                                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // Apply to all scenes button (green button)
                                    Button(
                                        onClick = {
                                            viewModel.applyTransitionToAllClips(selectedClip.transitionToNext)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("apply_transition_all_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Aplicar '${selectedClip.transitionToNext.effect.label}' a Todas as Cenas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: TEXTO & LEGENDAS COM FONTES E CORES

                    item {
                        if (selectedText != null) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Editar Texto / Legenda",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = { viewModel.deleteTextOverlay(selectedText.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    // Content Input
                                    OutlinedTextField(
                                        value = selectedText.text,
                                        onValueChange = { viewModel.updateTextOverlay(selectedText.copy(text = it)) },
                                        label = { Text("Texto da Legenda") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_edit_subtitle_text")
                                    )

                                    // FONT SELECTOR
                                    Text(
                                        text = "Opções de Fonte:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(FontStyleChoice.values()) { font ->
                                            val isSelected = selectedText.fontStyle == font
                                            Surface(
                                                color = if (isSelected) BrandPrimary.copy(alpha = 0.25f) else Color(0xFF1E1736),
                                                shape = RoundedCornerShape(8.dp),
                                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary) else null,
                                                modifier = Modifier
                                                    .clickable { viewModel.updateTextOverlay(selectedText.copy(fontStyle = font)) }
                                                    .testTag("font_select_${font.name.lowercase()}")
                                            ) {
                                                Text(
                                                    text = font.label,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    // COLOR PALETTE
                                    Text(
                                        text = "Paleta de Cores:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(VIRAL_COLOR_PALETTE) { (hex, label) ->
                                            val isSelected = selectedText.textColorHex == hex
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(hex))
                                                    .border(
                                                        if (isSelected) 2.5.dp else 1.dp,
                                                        if (isSelected) Color.White else Color.Gray,
                                                        CircleShape
                                                    )
                                                    .clickable { viewModel.updateTextOverlay(selectedText.copy(textColorHex = hex)) }
                                                    .testTag("color_select_${hex}"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = if (hex == 0xFFFFFFFF) Color.Black else Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // BACKGROUND STYLE & POSITION
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Estilo de Fundo:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                items(TextBgStyle.values()) { bg ->
                                                    val isSelected = selectedText.bgStyle == bg
                                                    Surface(
                                                        color = if (isSelected) BrandSecondary.copy(alpha = 0.2f) else Color(0xFF1E1736),
                                                        shape = RoundedCornerShape(6.dp),
                                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary) else null,
                                                        modifier = Modifier.clickable {
                                                            viewModel.updateTextOverlay(selectedText.copy(bgStyle = bg))
                                                        }
                                                    ) {
                                                        Text(
                                                            text = bg.label.split(" ").first(),
                                                            fontSize = 10.sp,
                                                            color = if (isSelected) BrandSecondary else Color.White,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Posição Vertical:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                TextVerticalPosition.values().forEach { pos ->
                                                    val isSelected = selectedText.positionY == pos
                                                    Surface(
                                                        color = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else Color(0xFF1E1736),
                                                        shape = RoundedCornerShape(6.dp),
                                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary) else null,
                                                        modifier = Modifier.clickable {
                                                            viewModel.updateTextOverlay(selectedText.copy(positionY = pos))
                                                        }
                                                    ) {
                                                        Text(
                                                            text = pos.label,
                                                            fontSize = 10.sp,
                                                            color = if (isSelected) BrandPrimary else Color.White,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // FONT SIZE SLIDER
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("Tamanho: ${selectedText.fontSizeSp}sp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Slider(
                                            value = selectedText.fontSizeSp.toFloat(),
                                            onValueChange = { viewModel.updateTextOverlay(selectedText.copy(fontSizeSp = it.toInt())) },
                                            valueRange = 12f..28f,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add new text overlay button
                        Button(
                            onClick = {
                                viewModel.addTextOverlay(
                                    text = "🔥 SUPER OFERTA RELÂMPAGO!",
                                    fontStyle = FontStyleChoice.VIRAL_IMPACT,
                                    colorHex = 0xFFFFE600,
                                    bgStyle = TextBgStyle.SOLID_BOX,
                                    position = TextVerticalPosition.BOTTOM
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_text_overlay_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Adicionar Nova Legenda no Cursor", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNavigateToScriptAssistant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("editor_open_gemini_script_assistant_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrandSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Assistente de Roteiro Gemini AI",
                                color = BrandSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                3 -> {
                    // TAB 3: ÁUDIO & TRILHA SONORA (PRESET + UPLOAD DO USUÁRIO)
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Equilíbrio de Áudio & Volume",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Música Fundo: ${(audioTrack.volume * 100).toInt()}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Slider(
                                        value = audioTrack.volume,
                                        onValueChange = { viewModel.updateAudioVolumes(it, audioTrack.voiceVolume) },
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(thumbColor = BrandSecondary, activeTrackColor = BrandSecondary)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Narração IA: ${(audioTrack.voiceVolume * 100).toInt()}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Slider(
                                        value = audioTrack.voiceVolume,
                                        onValueChange = { viewModel.updateAudioVolumes(audioTrack.volume, it) },
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(thumbColor = BrandPrimary, activeTrackColor = BrandPrimary)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Upload User Audio CTA Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1430)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
                                        Text("Fazer Upload do Seu Próprio Áudio", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Text(
                                        text = "Importe arquivos MP3/WAV ou gravações externas",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }

                                Button(
                                    onClick = { audioPickerLauncher.launch("audio/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("upload_user_audio_button")
                                ) {
                                    Text("Escolher", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Biblioteca de Músicas Virais (Pré-definidas):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // List of Preset Tracks
                    items(AudioLibrary.PRESET_TRACKS) { track ->
                        val isSelected = audioTrack.id == track.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) BrandPrimary else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectBackgroundAudio(track) }
                                .testTag("audio_track_${track.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) BrandPrimary else Color(0xFF2E264E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${track.genre} • ${track.durationSeconds.toInt()}s",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isSelected) {
                                    Surface(
                                        color = BrandPrimary,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ATIVA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 4: AVATAR IA & ENQUADRAMENTO
                    val currentAvatar = avatars.find { it.id == project?.avatarId } ?: avatars.firstOrNull()

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Apresentador Ativo no Vídeo",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (onNavigateToAvatarGallery != null) {
                                        Text(
                                            text = "Galeria Completa",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandSecondary,
                                            modifier = Modifier
                                                .clickable { onNavigateToAvatarGallery.invoke() }
                                                .testTag("editor_open_avatar_gallery_link")
                                        )
                                    }
                                }

                                if (currentAvatar != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val resId = context.resources.getIdentifier(currentAvatar.drawableResName, "drawable", context.packageName)
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, BrandPrimary, CircleShape)
                                                .background(Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (currentAvatar.customImageUri.isNotBlank()) {
                                                AsyncImage(
                                                    model = Uri.parse(currentAvatar.customImageUri),
                                                    contentDescription = currentAvatar.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else if (resId != 0) {
                                                Image(
                                                    painter = painterResource(id = resId),
                                                    contentDescription = currentAvatar.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = currentAvatar.name,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = BrandPrimary.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = currentAvatar.niche,
                                                        fontSize = 10.sp,
                                                        color = BrandPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = currentAvatar.roleTitle,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.playVideoVoice(project?.fullScriptText ?: "Aproveite esta super oferta agora!") },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(BrandSecondary)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Testar Fala",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Framing Style selector
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Estilo de Enquadramento no Vídeo 9:16",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AvatarStyle.entries.forEach { style ->
                                    val isSelected = project?.avatarStyle == style
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) BrandPrimary else DarkCardBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                if (currentAvatar != null) {
                                                    viewModel.applyGalleryAvatarToEditor(currentAvatar, style)
                                                }
                                            }
                                            .testTag("editor_avatar_style_${style.name}")
                                    ) {
                                        Text(
                                            text = when (style) {
                                                AvatarStyle.PIP_CIRCLE -> "PiP"
                                                AvatarStyle.SPLIT_SCREEN -> "Dividida"
                                                AvatarStyle.BOTTOM_CORNER -> "Canto"
                                                AvatarStyle.FULL_PRESENTER -> "Apresentador"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Switch Avatar Quick Carousel
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Trocar Apresentador (1 Toque)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(avatars) { av ->
                                    val isSelected = av.id == project?.avatarId
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) BrandPrimary else DarkCardBorder
                                        ),
                                        modifier = Modifier
                                            .width(110.dp)
                                            .clickable { viewModel.applyGalleryAvatarToEditor(av, project?.avatarStyle) }
                                            .testTag("editor_switch_avatar_${av.id}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val rId = context.resources.getIdentifier(av.drawableResName, "drawable", context.packageName)
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                                    .border(1.5.dp, if (isSelected) BrandPrimary else Color.Transparent, CircleShape)
                                                    .background(Color.Black),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (av.customImageUri.isNotBlank()) {
                                                    AsyncImage(
                                                        model = Uri.parse(av.customImageUri),
                                                        contentDescription = av.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                } else if (rId != 0) {
                                                    Image(
                                                        painter = painterResource(id = rId),
                                                        contentDescription = av.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                            }

                                            Text(
                                                text = av.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = av.niche,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Button to open full gallery
                    item {
                        if (onNavigateToAvatarGallery != null) {
                            Button(
                                onClick = { onNavigateToAvatarGallery.invoke() },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("editor_full_gallery_button")
                            ) {
                                Icon(imageVector = Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Abrir Galeria & Estúdio Completo de Avatares", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}
