package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.ScriptVariation
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.data.ai.ScriptCategory
import com.example.data.ai.ScriptTemplatePreset
import com.example.data.ai.ScriptTemplatesCatalog
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.TikTokCyan
import com.example.ui.viewmodel.MainViewModel


private val SAMPLE_PROMPTS = listOf(
    "Mini Processador USB" to "Mini processador de alimentos elétrico USB portátil, tritura alho, cebola e legumes em 5 segundos, lâminas de aço inox, fácil de lavar e bivolt.",
    "Fone Bluetooth ANC" to "Fone de ouvido sem fio Bluetooth 5.3 com cancelamento ativo de ruído, bateria de 40 horas, graves potentes e microfone HD para chamadas.",
    "Luminária Sunset RGB" to "Luminária de pôr do sol Sunset Lamp com projeção giratória 360°, 16 cores RGB controladas por controle remoto e aplicativo, ideal para vídeos.",
    "Garrafa Térmica LED" to "Garrafa térmica inteligente em inox com display digital touch indicando a temperatura em tempo real, conserva gelado por 24h e quente por 12h.",
    "Massageador Cervical" to "Massageador elétrico para pescoço e costas com aquecimento infravermelho, 8 nós de massagem shiatsu e adaptador para carro e casa."
)

private val MENTAL_TRIGGERS = listOf(
    "Urgência & Escassez" to "🔥",
    "Prova Social & Avaliação" to "⭐",
    "Dor vs. Solução" to "⚡",
    "Curiosidade & Achadinho" to "👀",
    "Custo-Benefício Extremo" to "💰"
)

private val DURATION_OPTIONS = listOf(
    15 to "15s (Viral Rápido)",
    30 to "30s (Ideal 9:16)",
    60 to "60s (Review Detalhado)"
)

private val TONE_OPTIONS = listOf(
    "Empolgado & Viral",
    "Reviewer Técnico",
    "Amigável & Conversacional",
    "Humor & Descontraído"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScriptAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scriptState by viewModel.scriptAssistantState.collectAsStateWithLifecycle()
    val isPlayingSpeech by viewModel.audioEngine.isPlaying.collectAsStateWithLifecycle()

    var activeCaptionTab by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "gemini_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .testTag("script_assistant_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Assistente de Roteiro",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BrandPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Gemini 3.5 Flash",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                            }
                        }
                    }
                    Text(
                        text = "Scripts persuasivos de alta conversão para Shopee Vídeo, TikTok e Reels",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Category Tabs: Achadinhos & Vendas | Mini Novelas Virais | Unboxing & Review
        // Category Tabs: 7 Viral Categories
        item {
            ScrollableTabRow(
                selectedTabIndex = when (scriptState.activeCategory) {
                    ScriptCategory.MINI_NOVELA -> 0
                    ScriptCategory.CINEMA_FILMS -> 1
                    ScriptCategory.ANIMATION_CARTOON -> 2
                    ScriptCategory.STORIES_MYSTERY -> 3
                    ScriptCategory.BIKES_CYCLING -> 4
                    ScriptCategory.ECOMMERCE -> 5
                    ScriptCategory.UNBOXING -> 6
                },
                containerColor = DarkSurfaceVariant,
                contentColor = BrandSecondary,
                edgePadding = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("script_category_tabs")
            ) {
                listOf(
                    ScriptCategory.MINI_NOVELA to ("Mini Novelas" to Color(0xFFFF5722)),
                    ScriptCategory.CINEMA_FILMS to ("Filmes & Séries" to Color(0xFFA855F7)),
                    ScriptCategory.ANIMATION_CARTOON to ("Desenhos Animados" to Color(0xFFFFD600)),
                    ScriptCategory.STORIES_MYSTERY to ("Estórias & Relatos" to Color(0xFF38BDF8)),
                    ScriptCategory.BIKES_CYCLING to ("Bicicletas & Grau" to Color(0xFF10B981)),
                    ScriptCategory.ECOMMERCE to ("Achadinhos & Vendas" to BrandSecondary),
                    ScriptCategory.UNBOXING to ("Unboxing & Review" to Color(0xFF00F2FE))
                ).forEach { (category, pair) ->
                    val (label, accentColor) = pair
                    val isSelected = scriptState.activeCategory == category
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectScriptCategory(category) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = when (category) {
                                        ScriptCategory.MINI_NOVELA -> Icons.Default.Movie
                                        ScriptCategory.CINEMA_FILMS -> Icons.Default.SmartDisplay
                                        ScriptCategory.ANIMATION_CARTOON -> Icons.Default.AutoAwesome
                                        ScriptCategory.STORIES_MYSTERY -> Icons.Default.Psychology
                                        ScriptCategory.BIKES_CYCLING -> Icons.Default.ElectricBolt
                                        ScriptCategory.ECOMMERCE -> Icons.Default.ShoppingBag
                                        ScriptCategory.UNBOXING -> Icons.Default.Inventory2
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = if (isSelected) accentColor else Color(0xFFAFA7CD)
                                )
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFFAFA7CD)
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_script_${category.name.lowercase()}")
                    )
                }
            }
        }

        // Dedicated Module for Selected Genre Templates (All 7 Categories)
        item {
            val templatesList = ScriptTemplatesCatalog.getTemplatesForCategory(scriptState.activeCategory)
            val activeCat = scriptState.activeCategory
            val accentColor = when (activeCat) {
                ScriptCategory.MINI_NOVELA -> Color(0xFFFF5722)
                ScriptCategory.CINEMA_FILMS -> Color(0xFFA855F7)
                ScriptCategory.ANIMATION_CARTOON -> Color(0xFFFFD600)
                ScriptCategory.STORIES_MYSTERY -> Color(0xFF38BDF8)
                ScriptCategory.BIKES_CYCLING -> Color(0xFF10B981)
                ScriptCategory.ECOMMERCE -> BrandSecondary
                ScriptCategory.UNBOXING -> Color(0xFF00F2FE)
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("genre_templates_module")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = when (activeCat) {
                                ScriptCategory.MINI_NOVELA -> Icons.Default.Movie
                                ScriptCategory.CINEMA_FILMS -> Icons.Default.SmartDisplay
                                ScriptCategory.ANIMATION_CARTOON -> Icons.Default.AutoAwesome
                                ScriptCategory.STORIES_MYSTERY -> Icons.Default.Psychology
                                ScriptCategory.BIKES_CYCLING -> Icons.Default.ElectricBolt
                                ScriptCategory.ECOMMERCE -> Icons.Default.ShoppingBag
                                ScriptCategory.UNBOXING -> Icons.Default.Inventory2
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Modelos Prontos de ${activeCat.title} (9:16)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = activeCat.subtitle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(templatesList) { template ->
                            val isSelected = scriptState.selectedTemplateId == template.id
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) BrandSecondary.copy(alpha = 0.15f) else DarkSurfaceVariant
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) accentColor else DarkCardBorder
                                ),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable { viewModel.applyScriptTemplate(template) }
                                    .testTag("template_card_${template.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = accentColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = template.badge,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selecionado",
                                                tint = BrandGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = template.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = "\"${template.retentionHook}\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp,
                                        maxLines = 3
                                    )

                                    Button(
                                        onClick = { viewModel.applyScriptTemplate(template) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) BrandGreen else BrandSecondary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_use_template_${template.id}")
                                    ) {
                                        Text(
                                            text = if (isSelected) "Template Ativo" else "Usar Este Template",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hero Card
        item {
            Card(

                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(BrandPrimary, BrandSecondary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Inteligência de Vendas 9:16",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Gera ganchos hipnóticos (0-3s), dores reais e chamadas de ação diretas.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Item Description Field
                    Text(
                        text = "Descrição ou Detalhes do Produto Físico *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = scriptState.itemDescriptionInput,
                        onValueChange = { viewModel.updateScriptAssistantDescription(it) },
                        placeholder = {
                            Text(
                                text = "Cole a descrição, materiais, vantagens ou problemas que seu item resolve...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("script_assistant_description_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        )
                    )

                    // Quick Sample Chips
                    Text(
                        text = "Exemplos rápidos para testar:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SAMPLE_PROMPTS.forEach { (label, fullText) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable {
                                    viewModel.updateScriptAssistantProductName(label)
                                    viewModel.updateScriptAssistantDescription(fullText)
                                }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BrandSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Customization Settings: Trigger, Duration, Tone
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Optional Product Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nome do Produto (Opcional)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            OutlinedTextField(
                                value = scriptState.productNameInput,
                                onValueChange = { viewModel.updateScriptAssistantProductName(it) },
                                placeholder = { Text("Ex: Fone WavePro Max", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("script_assistant_product_name_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface
                                )
                            )
                        }
                    }

                    // Mental Triggers Selector
                    Text(
                        text = "Gatilho Mental Predominante",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MENTAL_TRIGGERS.forEach { (trigger, emoji) ->
                            val isSelected = scriptState.selectedTrigger == trigger
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateScriptAssistantTrigger(trigger) },
                                label = {
                                    Text(
                                        text = "$emoji $trigger",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkSurface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("trigger_chip_${trigger.take(4)}")
                            )
                        }
                    }

                    // Duration and Tone row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Duration
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Duração",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            DURATION_OPTIONS.forEach { (sec, label) ->
                                val isSelected = scriptState.selectedDuration == sec
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) BrandPrimary.copy(alpha = 0.25f) else DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) BrandPrimary else DarkCardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable { viewModel.updateScriptAssistantDuration(sec) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Tone
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tom do Avatar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TONE_OPTIONS.forEach { tone ->
                                val isSelected = scriptState.selectedTone == tone
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) BrandSecondary.copy(alpha = 0.25f) else DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) BrandSecondary else DarkCardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable { viewModel.updateScriptAssistantTone(tone) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ElectricBolt,
                                            contentDescription = null,
                                            tint = if (isSelected) BrandSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = tone,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) BrandSecondary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Generate Button
                    Button(
                        onClick = { viewModel.generateScriptVariationsWithGemini() },
                        enabled = !scriptState.isGenerating && scriptState.itemDescriptionInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .scale(if (scriptState.isGenerating) pulseScale else 1f)
                            .testTag("generate_scripts_with_gemini_button")
                    ) {
                        if (scriptState.isGenerating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = scriptState.generationProgressStep.ifBlank { "Gerando com Gemini AI..." },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gerar Roteiros Persuasivos com Gemini",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Generated Variations Section
        if (scriptState.variations.isNotEmpty()) {
            val selectedVar = scriptState.variations.getOrNull(scriptState.selectedVariationIndex)
                ?: scriptState.variations.first()

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Roteiros Gerados (${scriptState.variations.size} Abordagens)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ShopeeOrange.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Alta Conversão",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopeeOrange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Variation Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = scriptState.selectedVariationIndex,
                    containerColor = DarkSurfaceVariant,
                    contentColor = BrandPrimary,
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    scriptState.variations.forEachIndexed { index, variation ->
                        val isSelected = scriptState.selectedVariationIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectScriptVariation(index) },
                            text = {
                                Text(
                                    text = variation.styleTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("variation_tab_$index")
                        )
                    }
                }
            }

            // Active Script Card
            item {
                ScriptCard(
                    variation = selectedVar,
                    isPlayingSpeech = isPlayingSpeech,
                    onPlaySpeech = { viewModel.previewScriptSpeech(selectedVar.fullScript) },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Roteiro Viral", selectedVar.fullScript)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Roteiro copiado com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    onApplyToWizard = {
                        viewModel.applySelectedScriptToWizard()
                        onNavigateToCreate()
                    },
                    onApplyToEditor = {
                        viewModel.applySelectedScriptToEditor()
                        onNavigateToEditor()
                    }
                )
            }

            // Platform Captions Card
            item {
                CaptionsCard(
                    variation = selectedVar,
                    activeTab = activeCaptionTab,
                    onTabSelected = { activeCaptionTab = it },
                    onCopyCaption = { caption ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Legenda Otimizada", caption)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Legenda copiada!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun ScriptCard(
    variation: ScriptVariation,
    isPlayingSpeech: Boolean,
    onPlaySpeech: () -> Unit,
    onCopy: () -> Unit,
    onApplyToWizard: () -> Unit,
    onApplyToEditor: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = variation.styleTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "~${variation.estimatedDurationSeconds} segundos • Gatilho: ${variation.triggerType}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Audio Play Button (TTS preview)
                IconButton(
                    onClick = onPlaySpeech,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isPlayingSpeech) Color(0xFFFF5252) else BrandPrimary)
                        .testTag("preview_script_audio_button")
                ) {
                    Icon(
                        imageVector = if (isPlayingSpeech) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlayingSpeech) "Parar" else "Ouvir Roteiro",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Breakdown Boxes
            ScriptStepBox(
                tag = "🪝 GANCHO DE RETENÇÃO (0-3s)",
                text = variation.hook,
                tagColor = Color(0xFFFFD54F),
                textColor = Color(0xFFFFF9C4),
                bgColor = Color(0xFF2C2405)
            )

            ScriptStepBox(
                tag = "⚠️ PROBLEMA & FRUSTRAÇÃO (3-10s)",
                text = variation.problem,
                tagColor = Color(0xFFFF8A80),
                textColor = Color(0xFFFFEBEE),
                bgColor = Color(0xFF2B1110)
            )

            ScriptStepBox(
                tag = "💡 SOLUÇÃO & DIFERENCIAIS (10-20s)",
                text = variation.solution,
                tagColor = Color(0xFF80D8FF),
                textColor = Color(0xFFE1F5FE),
                bgColor = Color(0xFF092430)
            )

            ScriptStepBox(
                tag = "🏷️ OFERTA & ESCASSEZ (20-25s)",
                text = variation.offer,
                tagColor = Color(0xFFB9F6CA),
                textColor = Color(0xFFE8F8F0),
                bgColor = Color(0xFF0B2917)
            )

            ScriptStepBox(
                tag = "🎯 CHAMADA PARA AÇÃO (25-30s)",
                text = variation.cta,
                tagColor = ShopeeOrange,
                textColor = Color(0xFFFFEBE5),
                bgColor = Color(0xFF2E150D)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_script_button")
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copiar", fontSize = 12.sp)
                }

                Button(
                    onClick = onApplyToEditor,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("apply_script_to_editor_button")
                ) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("No Editor 9:16", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onApplyToWizard,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("apply_script_to_wizard_button")
                ) {
                    Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Criar Vídeo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ScriptStepBox(
    tag: String,
    text: String,
    tagColor: Color,
    textColor: Color,
    bgColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = tag,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = tagColor
            )
            Text(
                text = text,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun CaptionsCard(
    variation: ScriptVariation,
    activeTab: Int,
    onTabSelected: (Int) -> Unit,
    onCopyCaption: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Legendas Prontas para Redes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Copywriting Otimizado",
                    fontSize = 11.sp,
                    color = BrandSecondary
                )
            }

            // Platform Tabs: TikTok, Shopee, Instagram
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("TikTok", "Shopee Vídeo", "Reels/Insta").forEachIndexed { index, name ->
                    val isSelected = activeTab == index
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) BrandPrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BrandPrimary else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabSelected(index) }
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }

            val currentCaption = when (activeTab) {
                0 -> variation.tiktokCaption.ifBlank { "${variation.hook} ${variation.cta} ${variation.recommendedHashtags}" }
                1 -> variation.shopeeCaption.ifBlank { "ACHADINHO SHOPEE! ${variation.solution} Garanta com cupom! ${variation.recommendedHashtags}" }
                else -> variation.instagramCaption.ifBlank { "${variation.hook} Comente 'QUERO' para o link! ${variation.recommendedHashtags}" }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentCaption,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = variation.recommendedHashtags,
                        fontSize = 11.sp,
                        color = TikTokCyan
                    )
                }
            }

            Button(
                onClick = { onCopyCaption("$currentCaption\n\n${variation.recommendedHashtags}") },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("copy_caption_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Copiar Legenda Formatada",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimary
                )
            }
        }
    }
}
