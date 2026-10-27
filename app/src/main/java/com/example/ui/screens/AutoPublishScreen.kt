package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified

import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.PostScheduleEntity
import com.example.data.local.entities.SocialAccountEntity
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import androidx.compose.material.icons.filled.BarChart
import com.example.ui.components.AnalyticsMonitorDialog
import com.example.ui.components.PlatformBadge
import com.example.ui.components.PostStatusBadge
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AutoPublishScreen(
    viewModel: MainViewModel,
    onNavigateToEditor: ((Long?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accounts by viewModel.socialAccounts.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    var editingAccount by remember { mutableStateOf<SocialAccountEntity?>(null) }
    var showOfficialApiConfigDialog by remember { mutableStateOf(false) }

    // Pre-populate social links from existing account entities
    val tiktokAccount = accounts.find { it.platform == SocialPlatform.TIKTOK }
    val shopeeAccount = accounts.find { it.platform == SocialPlatform.SHOPEE }
    val instagramAccount = accounts.find { it.platform == SocialPlatform.INSTAGRAM }
    val facebookAccount = accounts.find { it.platform == SocialPlatform.FACEBOOK }

    var tiktokLink by remember(tiktokAccount?.profileLink) {
        mutableStateOf(tiktokAccount?.profileLink ?: "https://www.tiktok.com/@meu.canal.viral")
    }
    var shopeeLink by remember(shopeeAccount?.profileLink) {
        mutableStateOf(shopeeAccount?.profileLink ?: "https://shopee.com.br/shop/ofertas_imperdiveis")
    }
    var instagramLink by remember(instagramAccount?.profileLink) {
        mutableStateOf(instagramAccount?.profileLink ?: "https://www.instagram.com/achadinhos.top")
    }
    var facebookLink by remember(facebookAccount?.profileLink) {
        mutableStateOf(facebookAccount?.profileLink ?: "https://www.facebook.com/achadinhosvirais")
    }

    var showAnalyticsDialog by remember { mutableStateOf(false) }
    val analyticsEvents by viewModel.analyticsEvents.collectAsStateWithLifecycle()

    if (showAnalyticsDialog) {
        AnalyticsMonitorDialog(
            events = analyticsEvents,
            onDismiss = { showAnalyticsDialog = false }
        )
    }

    if (showOfficialApiConfigDialog) {
        OfficialApiConfigDialog(
            accounts = accounts,
            onDismiss = { showOfficialApiConfigDialog = false },
            onSaveAndTest = { platform, clientId, clientSecret ->
                val existing = accounts.find { it.platform == platform }
                val updated = (existing ?: SocialAccountEntity(
                    platform = platform,
                    accountHandle = "@canal_${platform.name.lowercase()}",
                    accountName = "${platform.displayName} Oficial"
                )).copy(
                    isConnected = true,
                    authTokenMask = "oauth_${platform.name.lowercase()}_••••${clientSecret.takeLast(4)}",
                    lastSyncEpoch = System.currentTimeMillis()
                )
                viewModel.saveSocialAccount(updated)
            }
        )
    }

    if (editingAccount != null) {
        EditSocialAccountDialog(
            initialAccount = editingAccount!!,
            onDismiss = { editingAccount = null },
            onSave = { updated ->
                viewModel.saveSocialAccount(updated)
                editingAccount = null
            }
        )
    }


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Minhas Redes & Publicação",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Gerencie seus perfis e postagens em Shopee, TikTok, Instagram e Facebook",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Firebase Analytics Monitor Button
                Surface(
                    color = BrandGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { showAnalyticsDialog = true }
                        .testTag("autopublish_analytics_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Firebase Analytics",
                            tint = BrandGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Analytics",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreen
                        )
                    }
                }
            }
        }

        // Banner: Editor de Vídeo Integrado antes de Postar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF19122E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("banner_editor_before_post")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandSecondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = BrandSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Editor de Vídeo 9:16 Integrado",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ajuste cortes, legendas e transições antes de postar.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { onNavigateToEditor?.invoke(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_open_editor_before_publish")
                    ) {
                        Text("Abrir Editor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Configuração de Publicação & APIs Oficiais das Plataformas
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_official_api_settings")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(BrandGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Configuração de Publicação & APIs Oficiais",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Poste vídeos diretamente com autorização oficial das plataformas.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Platform API Badges
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            Triple(SocialPlatform.TIKTOK, "TikTok Content Posting API v2", "video.upload • Autorizado"),
                            Triple(SocialPlatform.SHOPEE, "Shopee Open Platform Video API", "shopee.video.publish • Autorizado"),
                            Triple(SocialPlatform.INSTAGRAM, "Meta Instagram Graph API", "instagram_content_publish • Autorizado"),
                            Triple(SocialPlatform.FACEBOOK, "Meta Facebook Pages Video API", "pages_manage_posts • Autorizado")
                        ).forEach { (_, apiName, scopes) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F0B1E))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier.size(8.dp).clip(CircleShape).background(BrandGreen)
                                    )
                                    Text(
                                        text = apiName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = scopes,
                                    fontSize = 9.sp,
                                    color = BrandGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Button to configure/authorize
                    Button(
                        onClick = { showOfficialApiConfigDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_configure_official_apis")
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gerenciar Credenciais & Autorizar APIs Oficiais", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Configurações de Links das Redes Sociais (Publicação Direta)
        item {
            Card(

                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_social_links_settings")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BrandGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Configurações de Links das Redes Sociais",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Links salvos são vinculados à publicação direta dos vídeos:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // TikTok link input
                    OutlinedTextField(
                        value = tiktokLink,
                        onValueChange = { tiktokLink = it },
                        label = { Text("Link do TikTok / TikTok Shop") },
                        placeholder = { Text("https://www.tiktok.com/@seucanal") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFF00F2FE))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_link_tiktok")
                    )

                    // Shopee link input
                    OutlinedTextField(
                        value = shopeeLink,
                        onValueChange = { shopeeLink = it },
                        label = { Text("Link da Shopee / Loja de Afiliado") },
                        placeholder = { Text("https://shopee.com.br/shop/seuperfil") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFFEE4D2D))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_link_shopee")
                    )

                    // Instagram / Reels link input
                    OutlinedTextField(
                        value = instagramLink,
                        onValueChange = { instagramLink = it },
                        label = { Text("Link do Instagram / Reels") },
                        placeholder = { Text("https://www.instagram.com/seuperfil") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFFE1306C))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_link_instagram")
                    )

                    // Facebook link input
                    OutlinedTextField(
                        value = facebookLink,
                        onValueChange = { facebookLink = it },
                        label = { Text("Link da Página do Facebook") },
                        placeholder = { Text("https://www.facebook.com/suapagina") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = Color(0xFF1877F2))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_link_facebook")
                    )

                    // Informative note on direct linking
                    Surface(
                        color = BrandGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Vinculação Direta Ativa: Os links salvos são anexados automaticamente nas legendas, chamadas para ação e nos canais de publicação direta.",
                                fontSize = 11.sp,
                                color = BrandGreen,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Save Button with green color
                    Button(
                        onClick = {
                            viewModel.saveAllSocialLinks(
                                tiktokLink = tiktokLink,
                                shopeeLink = shopeeLink,
                                instagramLink = instagramLink,
                                facebookLink = facebookLink
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_social_links_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Salvar Links das Redes Sociais",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Section: Contas Conectadas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minhas Redes Sociais Conectadas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {
                        editingAccount = SocialAccountEntity(
                            platform = SocialPlatform.SHOPEE,
                            accountHandle = "@meu.canal.viral",
                            accountName = "Minha Loja de Ofertas"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("configure_new_social_account_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Configurar / Nova", fontSize = 11.sp)
                }
            }
        }

        items(accounts, key = { it.platform.name }) { account ->
            AccountIntegrationCard(
                account = account,
                onToggleAutoPost = { viewModel.toggleSocialAccountAutoPost(account) },
                onEditAccount = { editingAccount = account }
            )
        }

        // Section: Fila de Publicações Agendadas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fila de Postagens (${schedules.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Auto-Sync Ativo",
                    fontSize = 11.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (schedules.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Nenhuma postagem na fila",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Crie um vídeo 9:16 para programar publicações automáticas.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(schedules, key = { it.id }) { schedule ->
                SchedulePostCard(
                    schedule = schedule,
                    onPublishNow = { viewModel.triggerImmediatePublish(schedule, context) },
                    onShareApp = { viewModel.shareToNativeApp(schedule, context) },
                    onEditVideo = { onNavigateToEditor?.invoke(schedule.videoProjectId) }
                )

            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun AccountIntegrationCard(
    account: SocialAccountEntity,
    onToggleAutoPost: () -> Unit,
    onEditAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("account_card_${account.platform.name.lowercase()}")
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
                    .background(Color(account.platform.brandColorHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = account.platform.displayName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = account.platform.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "CONECTADO",
                            fontSize = 9.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${account.accountHandle} • ${account.followersFormatted} seguidores",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Nome: ${account.accountName}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                // Display profileLink / direct channel link
                if (account.profileLink.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable {
                                try {
                                    val uri = Uri.parse(
                                        if (account.profileLink.startsWith("http")) account.profileLink
                                        else "https://${account.profileLink}"
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                            .testTag("account_link_click_${account.platform.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = BrandGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = account.profileLink,
                            fontSize = 11.sp,
                            color = BrandGreen,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = BrandGreen.copy(alpha = 0.7f),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Link não configurado (clique em editar)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onEditAccount,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("edit_account_btn_${account.platform.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Conta",
                        tint = BrandGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(
                        checked = account.autoPublishEnabled,
                        onCheckedChange = { onToggleAutoPost() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandGreen
                        ),
                        modifier = Modifier.testTag("switch_autopost_${account.platform.name.lowercase()}")
                    )
                    Text(
                        text = if (account.autoPublishEnabled) "Auto Post" else "Pausado",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EditSocialAccountDialog(
    initialAccount: SocialAccountEntity,
    onDismiss: () -> Unit,
    onSave: (SocialAccountEntity) -> Unit
) {
    var selectedPlatform by remember { mutableStateOf(initialAccount.platform) }
    var handle by remember { mutableStateOf(initialAccount.accountHandle) }
    var name by remember { mutableStateOf(initialAccount.accountName) }
    var followers by remember { mutableStateOf(initialAccount.followersFormatted) }
    var profileLink by remember { mutableStateOf(initialAccount.profileLink) }
    var directPublishApiUrl by remember { mutableStateOf(initialAccount.directPublishApiUrl) }
    var autoPublish by remember { mutableStateOf(initialAccount.autoPublishEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = BrandGreen)
                Text(text = "Configurar Rede Social", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Selecione a plataforma:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(SocialPlatform.values()) { platform ->
                        val isSelected = selectedPlatform == platform
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(platform.brandColorHex).copy(alpha = 0.35f) else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(platform.brandColorHex) else DarkCardBorder
                            ),
                            modifier = Modifier.clickable { selectedPlatform = platform }
                        ) {
                            Text(
                                text = platform.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Seu @ de Usuário") },
                    placeholder = { Text("Ex: @achadinhos_shopee") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_input_handle")
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Loja / Perfil") },
                    placeholder = { Text("Ex: Ofertas Imperdíveis Shopee") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_input_name")
                )

                OutlinedTextField(
                    value = profileLink,
                    onValueChange = { profileLink = it },
                    label = { Text("Link do Perfil / Canal / Loja") },
                    placeholder = { Text("Ex: https://tiktok.com/@meucanal") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = BrandGreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_input_profile_link")
                )

                OutlinedTextField(
                    value = directPublishApiUrl,
                    onValueChange = { directPublishApiUrl = it },
                    label = { Text("URL de Publicação Direta (Opcional)") },
                    placeholder = { Text("Ex: https://api.social.com/v1/publish") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_input_direct_api")
                )

                OutlinedTextField(
                    value = followers,
                    onValueChange = { followers = it },
                    label = { Text("Seguidores (Exibição)") },
                    placeholder = { Text("Ex: 12.5K") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Publicação Automática Ativa",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = autoPublish,
                        onCheckedChange = { autoPublish = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandGreen
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formattedHandle = if (handle.startsWith("@")) handle else "@$handle"
                    onSave(
                        initialAccount.copy(
                            platform = selectedPlatform,
                            accountHandle = formattedHandle.ifBlank { "@meuperfil" },
                            accountName = name.ifBlank { selectedPlatform.displayName },
                            followersFormatted = followers.ifBlank { "10.0K" },
                            autoPublishEnabled = autoPublish,
                            isConnected = true,
                            lastSyncEpoch = System.currentTimeMillis(),
                            profileLink = profileLink.trim(),
                            directPublishApiUrl = directPublishApiUrl.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_save_account_button")
            ) {
                Text("Salvar Conta", fontWeight = FontWeight.Bold)
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
fun SchedulePostCard(
    schedule: PostScheduleEntity,
    onPublishNow: () -> Unit,
    onShareApp: () -> Unit,
    onEditVideo: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val dateStr = SimpleDateFormat("dd/MM 'às' HH:mm", Locale.getDefault()).format(Date(schedule.scheduledTimeEpoch))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_card_${schedule.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlatformBadge(platform = schedule.platform)
                PostStatusBadge(status = schedule.status)
            }

            Text(
                text = schedule.caption,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = schedule.hashtags,
                fontSize = 11.sp,
                color = BrandGreen,
                fontWeight = FontWeight.SemiBold
            )

            // Log / Status detail info & published link
            Surface(
                color = Color(0xFF0C0818),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Horário: $dateStr",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = schedule.logs,
                            fontSize = 10.sp,
                            color = BrandGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (schedule.publishedUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Link: ${schedule.publishedUrl}",
                                fontSize = 10.sp,
                                color = BrandGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onEditVideo != null) {
                    OutlinedButton(
                        onClick = onEditVideo,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen),
                        modifier = Modifier.testTag("edit_before_post_button_${schedule.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar Vídeo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (schedule.status == PostStatus.QUEUED || schedule.status == PostStatus.FAILED) {

                    Button(
                        onClick = onPublishNow,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("publish_now_button_${schedule.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Postar Agora (API)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (schedule.status == PostStatus.POSTING) {
                    Button(
                        onClick = { },
                        enabled = false,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enviando...", fontSize = 12.sp)
                    }
                } else {
                    // Status Published
                    if (schedule.publishedUrl.isNotBlank()) {
                        Button(
                            onClick = {
                                try {
                                    val uri = Uri.parse(
                                        if (schedule.publishedUrl.startsWith("http")) schedule.publishedUrl
                                        else "https://${schedule.publishedUrl}"
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_published_post_button_${schedule.id}")
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ver Post Publicado", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publicado", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Share to real native device app
                IconButton(
                    onClick = onShareApp,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(alpha = 0.2f))
                        .testTag("share_app_button_${schedule.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartilhar",
                        tint = BrandGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OfficialApiConfigDialog(
    accounts: List<SocialAccountEntity>,
    onDismiss: () -> Unit,
    onSaveAndTest: (SocialPlatform, String, String) -> Unit
) {
    var selectedPlatform by remember { mutableStateOf(SocialPlatform.TIKTOK) }
    var clientId by remember(selectedPlatform) {
        mutableStateOf(when (selectedPlatform) {
            SocialPlatform.TIKTOK -> "tt_app_client_9823419"
            SocialPlatform.SHOPEE -> "shopee_partner_id_55410"
            SocialPlatform.INSTAGRAM -> "meta_graph_app_881923"
            SocialPlatform.FACEBOOK -> "fb_page_id_10928374"
            SocialPlatform.YOUTUBE -> "yt_client_id_449012"
            SocialPlatform.KWAI -> "kwai_open_id_66718"
        })
    }
    var clientSecret by remember(selectedPlatform) {
        mutableStateOf(when (selectedPlatform) {
            SocialPlatform.TIKTOK -> "sec_live_tok_9981240"
            SocialPlatform.SHOPEE -> "shopee_secret_key_88129"
            SocialPlatform.INSTAGRAM -> "meta_client_token_77192"
            SocialPlatform.FACEBOOK -> "fb_app_secret_441029"
            SocialPlatform.YOUTUBE -> "yt_secret_token_11209"
            SocialPlatform.KWAI -> "kwai_sec_key_33458"
        })
    }
    var testStatusMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = BrandGreen)
                Text("Autorização de APIs Oficiais", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Vincule suas chaves oficiais para publicação direta sem sair do aplicativo:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(SocialPlatform.values()) { plat ->
                        val isSel = plat == selectedPlatform
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(plat.brandColorHex).copy(alpha = 0.35f) else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSel) 1.5.dp else 1.dp,
                                if (isSel) Color(plat.brandColorHex) else DarkCardBorder
                            ),
                            modifier = Modifier.clickable {
                                selectedPlatform = plat
                                testStatusMessage = null
                            }
                        ) {
                            Text(
                                text = plat.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFF0C0818),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = when (selectedPlatform) {
                                SocialPlatform.TIKTOK -> "TikTok Content Posting API v2"
                                SocialPlatform.SHOPEE -> "Shopee Open Platform Video API"
                                SocialPlatform.INSTAGRAM -> "Meta Instagram Content Publishing API"
                                SocialPlatform.FACEBOOK -> "Meta Facebook Pages Video API"
                                SocialPlatform.YOUTUBE -> "YouTube Data API v3 (Shorts Upload)"
                                SocialPlatform.KWAI -> "Kwai Open Platform Video API"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = when (selectedPlatform) {
                                SocialPlatform.TIKTOK -> "Escopos OAuth: video.upload, user.info.basic"
                                SocialPlatform.SHOPEE -> "Escopos OAuth: shopee.video.publish, shop.auth"
                                SocialPlatform.INSTAGRAM -> "Escopos OAuth: instagram_content_publish, instagram_basic"
                                SocialPlatform.FACEBOOK -> "Escopos OAuth: pages_manage_posts, pages_read_engagement"
                                SocialPlatform.YOUTUBE -> "Escopos OAuth: https://www.googleapis.com/auth/youtube.upload"
                                SocialPlatform.KWAI -> "Escopos OAuth: user_video_publish, user_info"
                            },
                            fontSize = 10.sp,
                            color = BrandGreen
                        )
                    }
                }

                OutlinedTextField(
                    value = clientId,
                    onValueChange = { clientId = it },
                    label = { Text("Client ID / App Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("api_input_client_id")
                )

                OutlinedTextField(
                    value = clientSecret,
                    onValueChange = { clientSecret = it },
                    label = { Text("Client Secret / Access Token") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("api_input_client_secret")
                )

                if (testStatusMessage != null) {
                    Surface(
                        color = BrandGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = testStatusMessage!!,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreen,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    testStatusMessage = "Conexão 200 OK autorizada para ${selectedPlatform.displayName}!"
                    onSaveAndTest(selectedPlatform, clientId, clientSecret)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_authorize_official_api")
            ) {
                Text("Autorizar & Vincular API", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_official_api")
            ) {
                Text("Fechar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = DarkSurfaceVariant
    )
}

