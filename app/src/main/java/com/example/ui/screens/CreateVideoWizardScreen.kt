package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.viewmodel.MainViewModel

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SmartDisplay
import coil.compose.AsyncImage
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.ui.components.ProductImagePickerPreview
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandGreenAccent
import com.example.ui.theme.BrandGreenDark

@Composable
fun CreateVideoWizardScreen(
    viewModel: MainViewModel,
    onNavigateToScriptAssistant: () -> Unit,
    onNavigateToAvatarGallery: (() -> Unit)? = null,
    onVideoCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }
    val formState by viewModel.creationForm.collectAsStateWithLifecycle()
    val avatars by viewModel.avatars.collectAsStateWithLifecycle()
    val voices by viewModel.voices.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val socialAccounts by viewModel.socialAccounts.collectAsStateWithLifecycle()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingSeconds.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.audioEngine.isPlaying.collectAsStateWithLifecycle()
    val micAmplitude by viewModel.micAmplitude.collectAsStateWithLifecycle()
    var playingVoiceId by remember { mutableStateOf<Long?>(null) }
    var customCloneVoiceName by remember { mutableStateOf("Minha Voz Clonada") }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.startVoiceRecording(context)
    }

    val stepTitles = listOf("1. Produto & Link", "2. Avatar IA", "3. Voz & Áudio", "4. Roteiro & Canais")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = "Criador de Vídeo 9:16",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Gere vídeos de alta conversão para Shopee, TikTok & Reels",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Steps Progress TabRow
        TabRow(
            selectedTabIndex = currentStep,
            containerColor = DarkSurfaceVariant,
            contentColor = BrandPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[currentStep]),
                    color = BrandSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            stepTitles.forEachIndexed { index, title ->
                Tab(
                    selected = currentStep == index,
                    onClick = { currentStep = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (currentStep == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentStep == index) BrandSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("wizard_step_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step Content inside scrollable column
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                0 -> {
                    // STEP 1: PRODUTO FÍSICO, IMAGEM & LINK DE AFILIADO

                    // Card 1: Imagem do Produto com Coil & Photo Picker
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ProductImagePickerPreview(
                                    imageUri = formState.customImageUri,
                                    onImagePicked = { uri ->
                                        viewModel.setProductCustomImageUri(uri.toString())
                                    },
                                    onRemoveImage = {
                                        viewModel.setProductCustomImageUri("")
                                    },
                                    previewHeight = 200.dp
                                )




                                    // Produtos Salvos do Catálogo do Usuário
                                    if (products.isNotEmpty()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "Seus Produtos Cadastrados (${products.size}):",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                items(products, key = { it.id }) { prod ->
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = DarkSurfaceVariant,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.6f)),
                                                        modifier = Modifier.clickable {
                                                            viewModel.loadProductIntoCreationWizard(prod)
                                                        }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            if (prod.customImageUri.isNotBlank()) {
                                                                AsyncImage(
                                                                    model = prod.customImageUri,
                                                                    contentDescription = prod.title,
                                                                    modifier = Modifier
                                                                        .size(20.dp)
                                                                        .clip(RoundedCornerShape(4.dp)),
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                            } else {
                                                                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(16.dp))
                                                            }
                                                            Text(prod.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Demonstrações rápidas
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Ou escolha uma imagem de teste:",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            item {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = DarkSurfaceVariant,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                                    modifier = Modifier.clickable {
                                                        viewModel.setProductCustomImageUri("android.resource://${context.packageName}/drawable/img_product_earbuds_1792222861982")
                                                        viewModel.updateCreationProduct(
                                                            name = "Fone Bluetooth Gamer Sem Fio",
                                                            category = "Tecnologia & Áudio",
                                                            description = "Cancelamento de ruído ativo, bateria de 36 horas e baixa latência para jogos e chamadas.",
                                                            originalPrice = "189.90",
                                                            promoPrice = "79.90",
                                                            coupon = "SOMVIP15",
                                                            features = "Display LED digital, graves potentes, à prova de respingos"
                                                        )
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                                                        Text("Fone Bluetooth Pro", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                                    }
                                                }
                                            }
                                            item {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = DarkSurfaceVariant,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                                    modifier = Modifier.clickable {
                                                        viewModel.setProductCustomImageUri("android.resource://${context.packageName}/drawable/showcase_smartwatch_1790132114921")
                                                        viewModel.updateCreationProduct(
                                                            name = "Smartwatch Ultra Pro Titânio",
                                                            category = "Tecnologia & Acessórios",
                                                            description = "Tela AMOLED 2.1 polegadas, monitoramento cardíaco, GPS e bateria 14 dias.",
                                                            originalPrice = "249.90",
                                                            promoPrice = "119.90",
                                                            coupon = "PROMO10",
                                                            features = "Resistente a impactos, faz e recebe chamadas, pulseira oceano"
                                                        )
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = ShopeeOrange, modifier = Modifier.size(16.dp))
                                                        Text("Smartwatch Ultra", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    // Card 2: Link de Afiliado (Monetização)
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Seu Link de Afiliado",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Shopee, TikTok Shop, Mercado Livre, Amazon",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = formState.affiliateLink,
                                    onValueChange = { viewModel.setProductAffiliateLink(it) },
                                    label = { Text("Link de Afiliado do Produto") },
                                    placeholder = { Text("https://shopee.com.br/universal-link/...") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_affiliate_link")
                                )

                                Text(
                                    text = "💡 Este link será colocado automaticamente na legenda, na bio e nos stickers dos vídeos.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Presets de plataformas de afiliados
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = ShopeeOrange.copy(alpha = 0.2f),
                                        modifier = Modifier.clickable {
                                            viewModel.setProductAffiliateLink("https://shopee.com.br/universal-link/produto?afiliado_id=meu_canal")
                                        }
                                    ) {
                                        Text(
                                            text = "Shopee",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShopeeOrange,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF00F2FE).copy(alpha = 0.2f),
                                        modifier = Modifier.clickable {
                                            viewModel.setProductAffiliateLink("https://vt.tiktok.com/ZS_shop_afiliado/")
                                        }
                                    ) {
                                        Text(
                                            text = "TikTok Shop",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00F2FE),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                                        modifier = Modifier.clickable {
                                            viewModel.setProductAffiliateLink("https://mercadolivre.com.br/afiliados/produto?tag=viral")
                                        }
                                    ) {
                                        Text(
                                            text = "Mercado Livre",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                        modifier = Modifier.clickable {
                                            viewModel.setProductAffiliateLink("https://amzn.to/afiliado_viral")
                                        }
                                    ) {
                                        Text(
                                            text = "Amazon",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Card 3: Dados do Produto Físico
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = ShopeeOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Dados do Produto Físico",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                OutlinedTextField(
                                    value = formState.productName,
                                    onValueChange = {
                                        viewModel.updateCreationProduct(
                                            name = it,
                                            category = formState.category,
                                            description = formState.description,
                                            originalPrice = formState.originalPriceStr,
                                            promoPrice = formState.promoPriceStr,
                                            coupon = formState.couponCode,
                                            features = formState.keyFeatures
                                        )
                                    },
                                    label = { Text("Nome do Produto") },
                                    placeholder = { Text("Ex: Smartwatch Ultra Pro Titânio") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_product_name")
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = BrandPrimary.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.initScriptAssistantForProduct(
                                                name = formState.productName,
                                                description = formState.keyFeatures.ifBlank { formState.description }
                                            )
                                            onNavigateToScriptAssistant()
                                        }
                                        .testTag("wizard_open_script_assistant_step1")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Assistente de Roteiro Gemini AI",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandPrimary
                                            )
                                            Text(
                                                text = "Gerar script de vendas persuasivo a partir da descrição",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = formState.category,
                                    onValueChange = {
                                        viewModel.updateCreationProduct(
                                            name = formState.productName,
                                            category = it,
                                            description = formState.description,
                                            originalPrice = formState.originalPriceStr,
                                            promoPrice = formState.promoPriceStr,
                                            coupon = formState.couponCode,
                                            features = formState.keyFeatures
                                        )
                                    },
                                    label = { Text("Nicho / Categoria") },
                                    placeholder = { Text("Ex: Tecnologia, Beleza, Casa & Cozinha") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = formState.originalPriceStr,
                                        onValueChange = {
                                            viewModel.updateCreationProduct(
                                                name = formState.productName,
                                                category = formState.category,
                                                description = formState.description,
                                                originalPrice = it,
                                                promoPrice = formState.promoPriceStr,
                                                coupon = formState.couponCode,
                                                features = formState.keyFeatures
                                            )
                                        },
                                        label = { Text("Preço Original (R$)") },
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = formState.promoPriceStr,
                                        onValueChange = {
                                            viewModel.updateCreationProduct(
                                                name = formState.productName,
                                                category = formState.category,
                                                description = formState.description,
                                                originalPrice = formState.originalPriceStr,
                                                promoPrice = it,
                                                coupon = formState.couponCode,
                                                features = formState.keyFeatures
                                            )
                                        },
                                        label = { Text("Preço Oferta (R$)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                OutlinedTextField(
                                    value = formState.couponCode,
                                    onValueChange = {
                                        viewModel.updateCreationProduct(
                                            name = formState.productName,
                                            category = formState.category,
                                            description = formState.description,
                                            originalPrice = formState.originalPriceStr,
                                            promoPrice = formState.promoPriceStr,
                                            coupon = it,
                                            features = formState.keyFeatures
                                        )
                                    },
                                    label = { Text("Cupom de Desconto (Opcional)") },
                                    placeholder = { Text("Ex: FRETEGRATIS ou ACHADINHO20") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = formState.keyFeatures,
                                    onValueChange = {
                                        viewModel.updateCreationProduct(
                                            name = formState.productName,
                                            category = formState.category,
                                            description = formState.description,
                                            originalPrice = formState.originalPriceStr,
                                            promoPrice = formState.promoPriceStr,
                                            coupon = formState.couponCode,
                                            features = it
                                        )
                                    },
                                    label = { Text("Principais Recursos / Benefícios") },
                                    placeholder = { Text("Ex: Bateria dura 14 dias, tela AMOLED, à prova d'água") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // STEP 2: AVATAR APRESENTADOR IA
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selecione o Apresentador Virtual",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Full Gallery Button
                        if (onNavigateToAvatarGallery != null) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Brush.linearGradient(listOf(BrandPrimary, BrandSecondary))
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToAvatarGallery.invoke() }
                                    .testTag("wizard_open_avatar_gallery_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(listOf(BrandPrimary, BrandSecondary))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartDisplay,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Abrir Galeria & Estúdio 9:16",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Ouça vozes ao vivo e teste modos de enquadramento",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = BrandSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    items(avatars) { avatar ->
                        val isSelected = formState.selectedAvatarId == avatar.id
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) BrandPrimary else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectAvatarInCreation(avatar.id) }
                                .testTag("avatar_select_${avatar.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar Photo
                                val resId = context.resources.getIdentifier(avatar.drawableResName, "drawable", context.packageName)
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (isSelected) BrandPrimary else Color.Transparent, CircleShape)
                                        .background(Color.DarkGray)
                                ) {
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
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = avatar.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            color = BrandPrimary.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = avatar.niche,
                                                fontSize = 10.sp,
                                                color = BrandPrimary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = avatar.roleTitle,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = avatar.personalityPrompt,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 2
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(BrandPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Posicionamento no Vídeo 9:16",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AvatarStyle.values().forEach { style ->
                                val isSelected = formState.avatarStyle == style
                                Surface(
                                    color = if (isSelected) BrandSecondary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandSecondary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.selectAvatarStyleInCreation(style) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = style.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) BrandSecondary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // STEP 3: VOZ & CLONAGEM DE ÁUDIO
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(BrandGreen.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RecordVoiceOver,
                                            contentDescription = null,
                                            tint = BrandGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Clonar Minha Própria Voz (10s)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Grave 10 segundos para calibrar o clone neural de voz",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = customCloneVoiceName,
                                    onValueChange = { customCloneVoiceName = it },
                                    label = { Text("Nome da Voz Clonada") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                // Teleprompter Guide text
                                Surface(
                                    color = Color(0xFF0D1814),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Texto Guia para Leitura em Voz Alta:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandGreen
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"Fala pessoal! Hoje eu trouxe esse super achadinho que testei a semana inteira. O custo-benefício é impressionante e vale cada centavo!\"",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }

                                if (isRecordingVoice) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1B0C11))
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.FiberManualRecord,
                                                    contentDescription = null,
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "Gravando amostra: ${recordingSeconds}s / 10s",
                                                    color = Color.Red,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            AudioWaveformBars(isPlaying = true, barCount = 14, barColor = Color.Red)
                                        }

                                        // Mic sensitivity level bar
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "Captação:",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(Color.DarkGray)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(fraction = micAmplitude.coerceIn(0.08f, 1f))
                                                        .height(6.dp)
                                                        .background(BrandGreen)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { viewModel.finishVoiceRecording(customCloneVoiceName) },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("finish_voice_clone_button")
                                        ) {
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Concluir e Calibrar Clone de Voz", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            val hasPermission = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED
                                            if (hasPermission) {
                                                viewModel.startVoiceRecording(context)
                                            } else {
                                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("start_voice_clone_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Gravar Amostra para Clonar Voz (10s)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Modelos de Voz Disponíveis (${voices.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(voices) { voice ->
                        val isSelected = formState.selectedVoiceId == voice.id
                        val isThisVoicePlaying = isAudioPlaying && playingVoiceId == voice.id
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BrandGreen.copy(alpha = 0.15f) else DarkSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) BrandGreen else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectVoiceInCreation(voice.id) }
                                .testTag("voice_select_${voice.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (voice.isClonedVoice) BrandGreen else BrandGreen.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (voice.isClonedVoice) Icons.Default.Mic else Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = if (voice.isClonedVoice) Color.Black else BrandGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = voice.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (voice.isClonedVoice) {
                                            Surface(
                                                color = BrandGreen.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "CLONADA POR IA",
                                                    fontSize = 9.sp,
                                                    color = BrandGreenAccent,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${voice.toneLabel} • ${voice.speed}x veloc.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "\"${voice.samplePhrase}\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 1
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (isThisVoicePlaying) {
                                            viewModel.stopVideoVoice()
                                            playingVoiceId = null
                                        } else {
                                            playingVoiceId = voice.id
                                            viewModel.playVoice(voice)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(BrandGreen.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = if (isThisVoicePlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Ouvir Amostra",
                                        tint = BrandGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // STEP 4: ROTEIRO VIRAL IA & CANAIS
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Roteiro Viral Otimizado",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Estrutura testada para alta retenção e vendas",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.initScriptAssistantForProduct(
                                                    formState.productName,
                                                    formState.keyFeatures.ifBlank { formState.description }
                                                )
                                                onNavigateToScriptAssistant()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("open_script_assistant_step4_button")
                                        ) {
                                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Assistente Gemini", fontSize = 11.sp, color = BrandSecondary)
                                        }

                                        Button(
                                            onClick = { viewModel.generateAiScript() },
                                            enabled = !formState.isGeneratingAi,
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("generate_ai_script_button")
                                        ) {
                                            if (formState.isGeneratingAi) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Rápido", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                // Hook Script
                                OutlinedTextField(
                                    value = formState.generatedHook.ifBlank { "Para tudo e não compre outro smartwatch antes de ver isso!" },
                                    onValueChange = {
                                        viewModel.updateGeneratedScript(
                                            hook = it,
                                            problem = formState.generatedProblem,
                                            solution = formState.generatedSolution,
                                            offer = formState.generatedOffer,
                                            cta = formState.generatedCta
                                        )
                                    },
                                    label = { Text("Gancho Magnético (0 a 3s)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )

                                // Problem Script
                                OutlinedTextField(
                                    value = formState.generatedProblem.ifBlank { "A maioria dos relógios baratos descarrega no mesmo dia e não aguenta suor." },
                                    onValueChange = {
                                        viewModel.updateGeneratedScript(
                                            hook = formState.generatedHook,
                                            problem = it,
                                            solution = formState.generatedSolution,
                                            offer = formState.generatedOffer,
                                            cta = formState.generatedCta
                                        )
                                    },
                                    label = { Text("Problema / Dor (3 a 10s)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )

                                // Solution Script
                                OutlinedTextField(
                                    value = formState.generatedSolution.ifBlank { "O Ultra Pro tem caixa em titânio, tela AMOLED e bateria para 14 dias seguidos." },
                                    onValueChange = {
                                        viewModel.updateGeneratedScript(
                                            hook = formState.generatedHook,
                                            problem = formState.generatedProblem,
                                            solution = it,
                                            offer = formState.generatedOffer,
                                            cta = formState.generatedCta
                                        )
                                    },
                                    label = { Text("Demonstração & Solução (10 a 22s)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )

                                // Offer & CTA Script
                                OutlinedTextField(
                                    value = formState.generatedOffer.ifBlank { "De R$ 289 por apenas R$ 139,90 com o cupom FRETEGRATIS na Shopee!" },
                                    onValueChange = {
                                        viewModel.updateGeneratedScript(
                                            hook = formState.generatedHook,
                                            problem = formState.generatedProblem,
                                            solution = formState.generatedSolution,
                                            offer = it,
                                            cta = formState.generatedCta
                                        )
                                    },
                                    label = { Text("Oferta & Cupom (22 a 28s)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )

                                OutlinedTextField(
                                    value = formState.generatedCta.ifBlank { "Clica no carrinho aqui embaixo e garante o seu antes que o lote termine!" },
                                    onValueChange = {
                                        viewModel.updateGeneratedScript(
                                            hook = formState.generatedHook,
                                            problem = formState.generatedProblem,
                                            solution = formState.generatedSolution,
                                            offer = formState.generatedOffer,
                                            cta = it
                                        )
                                    },
                                    label = { Text("Chamada para Ação (CTA 28 a 30s)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Canais de Postagem Automática Simultânea",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(SocialPlatform.values()) { platform ->
                                val isSelected = formState.targetPlatforms.contains(platform)
                                PlatformBadge(
                                    platform = platform,
                                    isSelected = isSelected,
                                    modifier = Modifier.clickable {
                                        viewModel.toggleTargetPlatform(platform)
                                    }
                                )
                            }
                        }

                        // Status das redes sociais conectadas do usuário
                        if (socialAccounts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Minhas Redes Sociais Conectadas para Publicação Direta:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                socialAccounts.filter { formState.targetPlatforms.contains(it.platform) }.forEach { acc ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10B981))
                                        )
                                        Column {
                                            Text(
                                                text = "${acc.platform.displayName}: ${acc.accountHandle}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            if (acc.profileLink.isNotBlank()) {
                                                Text(
                                                    text = "Canal Vinculado: ${acc.profileLink}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF10B981),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Navigation Footer Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = { currentStep -= 1 },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Voltar")
                }
            }

            if (currentStep < 3) {
                Button(
                    onClick = { currentStep += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wizard_next_button")
                ) {
                    Text("Avançar", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.finalizeAndBuildVideo { createdId ->
                            onVideoCreated(createdId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("render_video_button")
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gerar Vídeo & Agendar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
