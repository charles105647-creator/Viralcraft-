package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SocialAccountEntity
import com.example.data.model.SocialPlatform
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandGreenAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ShopeeOrange
import java.util.Locale

@Composable
fun ProductImagePickerPreview(
    imageUri: String,
    onImagePicked: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier,
    titleText: String = "Imagem do Produto (Galeria do Celular)",
    subtitleText: String = "Selecione e verifique a foto do produto antes de gerar o vídeo 9:16",
    previewHeight: Dp = 190.dp
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImagePicked(uri)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint = BrandSecondary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = titleText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitleText,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (imageUri.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_image_preview_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(previewHeight)
                        .background(Color.Black)
                        .testTag("product_image_preview_area"),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Prévia da imagem do produto selecionada",
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("product_image_preview"),
                        contentScale = ContentScale.Fit
                    )

                    // Overlay Badge: Verificação visual da foto
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .testTag("product_image_verified_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Imagem Verificada & Pronta",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Badge de proporção 9:16
                    Surface(
                        color = BrandPrimary.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "Formato 9:16 Otimizado",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_change_product_image")
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trocar Imagem", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onRemoveImage,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("button_remove_product_image")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remover", fontSize = 12.sp)
                }
            }
        } else {
            // Estado vazio: Botão interativo para acionar o seletor da galeria
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BrandPrimary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .testTag("upload_product_photo_button")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = BrandSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Selecionar Foto do Produto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Abra a galeria do celular para escolher fotos reais do produto",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("button_launch_gallery_picker")
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir Galeria do Aparelho", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddOrEditProductDialog(
    initialProduct: ProductEntity? = null,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialProduct?.title ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "Tecnologia & Acessórios") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var originalPriceStr by remember {
        mutableStateOf(
            if (initialProduct != null) String.format(Locale.US, "%.2f", initialProduct.originalPrice) else "199.90"
        )
    }
    var promoPriceStr by remember {
        mutableStateOf(
            if (initialProduct != null) String.format(Locale.US, "%.2f", initialProduct.promoPrice) else "89.90"
        )
    }
    var couponCode by remember { mutableStateOf(initialProduct?.couponCode ?: "PROMO10") }
    var keyFeatures by remember { mutableStateOf(initialProduct?.keyFeatures ?: "Alta qualidade, garantia nacional e pronta entrega") }
    var customImageUri by remember { mutableStateOf(initialProduct?.customImageUri ?: "") }
    var affiliateLink by remember { mutableStateOf(initialProduct?.productLink ?: "https://shopee.com.br/afiliado/produto") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = BrandSecondary
                )
                Text(
                    text = if (initialProduct == null) "Cadastrar Produto" else "Editar Produto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. SEÇÃO DE IMAGEM DO PRODUTO (COIL + PHOTO PICKER)
                ProductImagePickerPreview(
                    imageUri = customImageUri,
                    onImagePicked = { uri -> customImageUri = uri.toString() },
                    onRemoveImage = { customImageUri = "" },
                    previewHeight = 160.dp
                )

                // 2. SEÇÃO DE LINK DE AFILIADO
                Text(
                    text = "🔗 Meu Link de Afiliado:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = affiliateLink,
                    onValueChange = { affiliateLink = it },
                    label = { Text("Link de Afiliado") },
                    placeholder = { Text("https://shopee.com.br/universal-link/...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFF10B981))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_input_affiliate_link"),
                    singleLine = true
                )

                // Atalhos de Plataformas de Afiliado
                Text(
                    text = "Atalhos rápidos de afiliados:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ShopeeOrange.copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            affiliateLink = "https://shopee.com.br/universal-link/produto?afiliado_id=meu_canal"
                        }
                    ) {
                        Text(
                            text = "Shopee",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopeeOrange,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF00F2FE).copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            affiliateLink = "https://vt.tiktok.com/ZS_shop_afiliado/"
                        }
                    ) {
                        Text(
                            text = "TikTok Shop",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F2FE),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFE600).copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            affiliateLink = "https://mercadolivre.com/sec/afiliado_produto"
                        }
                    ) {
                        Text(
                            text = "Mercado Livre",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF9900).copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            affiliateLink = "https://amzn.to/afiliado_produto"
                        }
                    ) {
                        Text(
                            text = "Amazon",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9900),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // 3. DADOS BÁSICOS DO PRODUTO
                Text(
                    text = "🏷️ Dados do Produto:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome do Produto") },
                    placeholder = { Text("Ex: Fone Bluetooth Sem Fio Pro") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria") },
                    placeholder = { Text("Ex: Tecnologia, Moda, Cozinha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = originalPriceStr,
                        onValueChange = { originalPriceStr = it },
                        label = { Text("Preço De (R$)") },
                        placeholder = { Text("199.90") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = promoPriceStr,
                        onValueChange = { promoPriceStr = it },
                        label = { Text("Preço Por (R$)") },
                        placeholder = { Text("89.90") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = couponCode,
                    onValueChange = { couponCode = it },
                    label = { Text("Cupom de Desconto (Opcional)") },
                    placeholder = { Text("Ex: PROMO10") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = ShopeeOrange)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = keyFeatures,
                    onValueChange = { keyFeatures = it },
                    label = { Text("Principais Benefícios / Destaques") },
                    placeholder = { Text("Ex: Bateria longa, à prova d'água, frete grátis") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val orig = originalPriceStr.toDoubleOrNull() ?: 100.0
                    val promo = promoPriceStr.toDoubleOrNull() ?: 50.0
                    val discount = if (orig > 0 && promo < orig) (((orig - promo) / orig) * 100).toInt() else 0

                    val product = (initialProduct ?: ProductEntity(
                        title = title.ifBlank { "Produto Novo" },
                        category = category.ifBlank { "Geral" },
                        description = description.ifBlank { "Oferta com cupom promocional e envio rápido." },
                        originalPrice = orig,
                        promoPrice = promo,
                        discountPercent = discount,
                        couponCode = couponCode,
                        keyFeatures = keyFeatures.ifBlank { "Alta qualidade e garantia" },
                        customImageUri = customImageUri,
                        productLink = affiliateLink.ifBlank { "https://shopee.com.br" }
                    )).copy(
                        title = title.ifBlank { "Produto Novo" },
                        category = category.ifBlank { "Geral" },
                        description = description.ifBlank { "Oferta com cupom promocional e envio rápido." },
                        originalPrice = orig,
                        promoPrice = promo,
                        discountPercent = discount,
                        couponCode = couponCode,
                        keyFeatures = keyFeatures.ifBlank { "Alta qualidade e garantia" },
                        customImageUri = customImageUri,
                        productLink = affiliateLink.ifBlank { "https://shopee.com.br" }
                    )
                    onSave(product)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White)
            ) {
                Text(if (initialProduct == null) "Salvar Produto" else "Salvar Alterações", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ProductCatalogCard(
    product: ProductEntity,
    onCreateVideo: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier
            .width(240.dp)
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Imagem do Produto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black)
            ) {
                if (product.customImageUri.isNotBlank()) {
                    AsyncImage(
                        model = product.customImageUri,
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val resId = context.resources.getIdentifier(product.imageDrawableRes, "drawable", context.packageName)
                    if (resId != 0) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = product.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        }
                    }
                }

                // Badge de Desconto
                if (product.discountPercent > 0) {
                    Surface(
                        color = Color(0xFFEF4444),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Título do Produto
            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Link de Afiliado Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = when {
                        product.productLink.contains("shopee", ignoreCase = true) -> "Shopee Afiliado"
                        product.productLink.contains("tiktok", ignoreCase = true) -> "TikTok Shop"
                        product.productLink.contains("mercadolivre", ignoreCase = true) -> "Mercado Livre"
                        product.productLink.contains("amzn", ignoreCase = true) || product.productLink.contains("amazon", ignoreCase = true) -> "Amazon Afiliado"
                        else -> "Link de Afiliado Ativo"
                    },
                    fontSize = 10.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Preço
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "R$ ${String.format(Locale.US, "%.2f", product.originalPrice)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                )
                Text(
                    text = "R$ ${String.format(Locale.US, "%.2f", product.promoPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF10B981)
                )
            }

            // Botão Principal: Criar Vídeo 9:16
            Button(
                onClick = onCreateVideo,
                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Criar Vídeo 9:16", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Botões de Ação Secundária (Excluir)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir produto", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
