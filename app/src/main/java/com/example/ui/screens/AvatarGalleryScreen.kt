package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import coil.compose.AsyncImage
import com.example.data.local.entities.AvatarEntity
import com.example.data.model.AvatarStyle
import com.example.ui.components.AudioWaveformBars
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.TikTokCyan
import com.example.ui.viewmodel.MainViewModel

/**
 * Screen for browsing, testing, and selecting AI Avatars for product videos.
 * Features an interactive 9:16 studio stage preview, voice test simulation,
 * niche filtering, style framing options, and custom avatar creation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarGalleryScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onSelectAndProceed: ((AvatarEntity, AvatarStyle) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatars by viewModel.avatars.collectAsStateWithLifecycle()
    val selectedAvatar by viewModel.selectedGalleryAvatar.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.gallerySelectedStyle.collectAsStateWithLifecycle()
    val filterNiche by viewModel.galleryFilterNiche.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isAvatarSpeakingTest.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Estúdio & Preview, 1: Galeria Completa
    var showCreateModal by remember { mutableStateOf(false) }

    val niches = listOf(
        "Todos",
        "Vendas & Achadinhos",
        "Tecnologia & Gadgets",
        "Moda & Beleza",
        "Eletrônicos & Smart Devices",
        "Saúde & Praticidade",
        "Personalizados"
    )

    val filteredAvatars = remember(avatars, filterNiche) {
        if (filterNiche == "Todos") {
            avatars
        } else if (filterNiche == "Personalizados") {
            avatars.filter { it.isCustom }
        } else {
            avatars.filter { it.niche.contains(filterNiche, ignoreCase = true) }
        }
    }

    val currentAvatar = selectedAvatar ?: avatars.firstOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Galeria de Avatares IA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Escolha o apresentador virtual do seu produto",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("avatar_gallery_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCreateModal = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("open_create_avatar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Novo Avatar",
                            tint = BrandSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            // Confirm & Apply Floating Bar
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentAvatar != null) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Avatar Selecionado:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currentAvatar.name} • ${selectedStyle.name.replace("_", " ")}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.applyGalleryAvatarToCreation(currentAvatar, selectedStyle)
                                viewModel.applyGalleryAvatarToEditor(currentAvatar, selectedStyle)
                                onSelectAndProceed?.invoke(currentAvatar, selectedStyle) ?: onBackClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("confirm_avatar_selection_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Usar este Avatar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // View Mode Tab Row
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurface,
                contentColor = BrandPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = BrandPrimary
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Estúdio de Apresentação", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_studio_preview")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Galeria Completa (${avatars.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_all_avatars_grid")
                )
            }

            // Niche Filter Chips Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(niches) { niche ->
                    val isSelected = filterNiche == niche
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setGalleryFilterNiche(niche) },
                        label = { Text(niche, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary.copy(alpha = 0.25f),
                            selectedLabelColor = BrandPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkCardBorder,
                            selectedBorderColor = BrandPrimary,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Tab Content
            if (activeTab == 0) {
                // Interactive Studio & Preview Stage
                AvatarStudioPreviewTab(
                    avatar = currentAvatar,
                    allAvatars = filteredAvatars,
                    selectedStyle = selectedStyle,
                    isSpeaking = isSpeaking,
                    onSelectAvatar = { viewModel.selectGalleryAvatar(it) },
                    onSelectStyle = { viewModel.setGallerySelectedStyle(it) },
                    onTestVoice = { viewModel.testAvatarVoiceSample(it) },
                    onCreateNew = { showCreateModal = true }
                )
            } else {
                // Full Avatars Grid
                AvatarGridViewTab(
                    avatars = filteredAvatars,
                    selectedAvatarId = currentAvatar?.id,
                    isSpeaking = isSpeaking,
                    onSelectAvatar = {
                        viewModel.selectGalleryAvatar(it)
                        activeTab = 0
                    },
                    onTestVoice = { viewModel.testAvatarVoiceSample(it) },
                    onCreateNew = { showCreateModal = true }
                )
            }
        }
    }

    // Modal to create custom avatar
    if (showCreateModal) {
        CreateCustomAvatarBottomSheet(
            onDismiss = { showCreateModal = false },
            onCreate = { name, role, niche, personality, uri, style ->
                viewModel.createCustomAvatar(name, role, niche, personality, uri, style)
                showCreateModal = false
            }
        )
    }
}

/**
 * Tab 1: Interactive Studio Preview Stage
 * Displays the avatar placed on a 9:16 vertical video simulator with real-time framing styles and voice auditions.
 */
@Composable
private fun AvatarStudioPreviewTab(
    avatar: AvatarEntity?,
    allAvatars: List<AvatarEntity>,
    selectedStyle: AvatarStyle,
    isSpeaking: Boolean,
    onSelectAvatar: (AvatarEntity) -> Unit,
    onSelectStyle: (AvatarStyle) -> Unit,
    onTestVoice: (AvatarEntity) -> Unit,
    onCreateNew: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (avatar == null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nenhum avatar encontrado nesta categoria", color = Color.White)
                }
            }
            return
        }

        // 9:16 Interactive Stage Simulation Box
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .testTag("avatar_studio_stage_preview")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background simulated product showcase
                val bgId = context.resources.getIdentifier(
                    "showcase_smartwatch_1790132114921",
                    "drawable",
                    context.packageName
                )
                if (bgId != 0) {
                    Image(
                        painter = painterResource(id = bgId),
                        contentDescription = "Cenário de Produto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Vignette gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Stage Header Label
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) BrandSecondary else Color(0xFF10B981))
                        )
                        Text(
                            text = if (isSpeaking) "ÁUDIO AO VIVO (IA)" else "ENQUADRAMENTO: ${selectedStyle.name}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Avatar Placement according to selectedStyle
                AvatarFramingRenderer(
                    avatar = avatar,
                    style = selectedStyle,
                    isSpeaking = isSpeaking,
                    modifier = Modifier.fillMaxSize()
                )

                // Bottom Simulated Subtitle Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSpeaking) {
                            AudioWaveformBars(isPlaying = true, barCount = 4, barColor = BrandSecondary)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(12.dp))
                        }
                        Text(
                            text = if (isSpeaking)
                                "\"Aproveite o cupom de frete grátis antes que acabe!\""
                            else
                                "Voz sintética hiper-realista sincronizada",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Presenter Info Card with Audition Button
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = avatar.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = avatar.niche,
                                    fontSize = 10.sp,
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (avatar.isCustom) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandSecondary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Personalizado",
                                        fontSize = 10.sp,
                                        color = BrandSecondary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = avatar.roleTitle,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Audition Voice Button
                    Button(
                        onClick = { onTestVoice(avatar) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeaking) Color(0xFFFF5252) else BrandSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("test_avatar_voice_button")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSpeaking) "Parar" else "Ouvir Voz",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Personality summary
                Text(
                    text = avatar.personalityPrompt,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )
            }
        }

        // Style / Framing Mode Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Modo de Enquadramento no Vídeo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarStyle.entries.forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) BrandPrimary else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectStyle(style) }
                            .testTag("style_chip_${style.name}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val label = when (style) {
                                AvatarStyle.PIP_CIRCLE -> "PiP Redondo"
                                AvatarStyle.SPLIT_SCREEN -> "Tela Dividida"
                                AvatarStyle.BOTTOM_CORNER -> "Canto Inferior"
                                AvatarStyle.FULL_PRESENTER -> "Apresentador Cheio"
                            }
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Quick Avatar Switcher Carousel
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Outros Apresentadores Disponíveis",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "${allAvatars.size} opções",
                    fontSize = 12.sp,
                    color = BrandPrimary
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(allAvatars) { item ->
                    val isSelected = item.id == avatar.id
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
                            .clickable { onSelectAvatar(item) }
                            .testTag("avatar_carousel_item_${item.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AvatarPortraitBox(
                                avatar = item,
                                sizeDp = 54,
                                isSelected = isSelected
                            )

                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )

                            Text(
                                text = item.niche,
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

        Spacer(modifier = Modifier.height(10.dp))
    }
}

/**
 * Tab 2: Full Grid of Avatars
 */
@Composable
private fun AvatarGridViewTab(
    avatars: List<AvatarEntity>,
    selectedAvatarId: Long?,
    isSpeaking: Boolean,
    onSelectAvatar: (AvatarEntity) -> Unit,
    onTestVoice: (AvatarEntity) -> Unit,
    onCreateNew: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // "Create Your Own Avatar" Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(BrandPrimary, BrandSecondary))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .clickable { onCreateNew() }
                    .testTag("create_custom_avatar_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(BrandPrimary, BrandSecondary))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Criar Meu Avatar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Carregue sua foto e configure estilo único",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // List of Avatars
        items(avatars) { item ->
            val isSelected = item.id == selectedAvatarId
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) BrandPrimary else DarkCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .clickable { onSelectAvatar(item) }
                    .testTag("avatar_grid_card_${item.id}")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Portrait Box
                        AvatarPortraitBox(
                            avatar = item,
                            sizeDp = 68,
                            isSelected = isSelected
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = item.roleTitle,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Bottom Actions: Audition Voice & Select
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onTestVoice(item) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurface)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Testar Voz",
                                    tint = BrandSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BrandPrimary else BrandPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.clickable { onSelectAvatar(item) }
                            ) {
                                Text(
                                    text = if (isSelected) "Selecionado" else "Escolher",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BrandPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Selection Checkmark Badge
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Avatar Framing Renderer on the 9:16 Video Stage
 */
@Composable
private fun AvatarFramingRenderer(
    avatar: AvatarEntity,
    style: AvatarStyle,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(avatar.drawableResName, "drawable", context.packageName)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(modifier = modifier) {
        when (style) {
            AvatarStyle.PIP_CIRCLE -> {
                // Floating Circular PiP in top-left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 14.dp, top = 40.dp)
                        .size(68.dp)
                        .scale(if (isSpeaking) pulseScale else 1.0f)
                        .clip(CircleShape)
                        .border(
                            2.5.dp,
                            if (isSpeaking) BrandSecondary else BrandPrimary,
                            CircleShape
                        )
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    RenderAvatarImage(avatar, resId)
                }
            }

            AvatarStyle.BOTTOM_CORNER -> {
                // Floating presenter card in bottom-right corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 14.dp, bottom = 48.dp)
                        .size(80.dp, 100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            2.dp,
                            if (isSpeaking) BrandSecondary else Color.White,
                            RoundedCornerShape(12.dp)
                        )
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    RenderAvatarImage(avatar, resId)
                }
            }

            AvatarStyle.SPLIT_SCREEN -> {
                // Split Screen: Presenter on lower half
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.55f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .border(
                            2.dp,
                            BrandPrimary,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    RenderAvatarImage(avatar, resId)
                }
            }

            AvatarStyle.FULL_PRESENTER -> {
                // Centered Standing Presenter Simulation
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .size(100.dp, 140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            if (isSpeaking) BrandSecondary else BrandPrimary,
                            RoundedCornerShape(16.dp)
                        )
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    RenderAvatarImage(avatar, resId)
                }
            }
        }
    }
}

@Composable
private fun RenderAvatarImage(avatar: AvatarEntity, resId: Int) {
    if (avatar.customImageUri.isNotBlank()) {
        AsyncImage(
            model = Uri.parse(avatar.customImageUri),
            contentDescription = avatar.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = avatar.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun AvatarPortraitBox(
    avatar: AvatarEntity,
    sizeDp: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(avatar.drawableResName, "drawable", context.packageName)

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .border(
                2.dp,
                if (isSelected) BrandPrimary else Color.Transparent,
                CircleShape
            )
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        RenderAvatarImage(avatar, resId)
    }
}

/**
 * Bottom Sheet Modal for creating custom presenter avatar with photo and sales personality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCustomAvatarBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String, role: String, niche: String, personality: String, imageUri: String, style: AvatarStyle) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Apresentador Virtual") }
    var niche by remember { mutableStateOf("Vendas & E-commerce") }
    var personality by remember { mutableStateOf("Energético, focado em benefícios rápidos e gatilhos de urgência.") }
    var imageUri by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(AvatarStyle.PIP_CIRCLE) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri.toString()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Criar Apresentador IA Personalizado",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Photo picker button & preview
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.5.dp, BrandSecondary, CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.isNotBlank()) {
                        AsyncImage(
                            model = Uri.parse(imageUri),
                            contentDescription = "Foto Escolhida",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(28.dp))
                    }
                }

                Column {
                    Text("Foto do Apresentador", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Text("Toque para escolher uma foto da galeria", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do Apresentador") },
                placeholder = { Text("Ex: Mariana, Thiago...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Papel / Especialidade") },
                placeholder = { Text("Ex: Consultora de Beleza, Gamer...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = niche,
                onValueChange = { niche = it },
                label = { Text("Nicho de Produtos") },
                placeholder = { Text("Ex: Moda, Casa, Tecnologia...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = personality,
                onValueChange = { personality = it },
                label = { Text("Tom de Voz & Personalidade") },
                placeholder = { Text("Ex: Descontraído, acolhedor, focado em promoção...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, role, niche, personality, imageUri, selectedStyle)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_custom_avatar_button")
            ) {
                Text("Salvar Avatar na Galeria", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
