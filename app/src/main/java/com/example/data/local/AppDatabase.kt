package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AvatarDao
import com.example.data.local.dao.PostScheduleDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.ProjectMetadataDao
import com.example.data.local.dao.SocialAccountDao
import com.example.data.local.dao.VideoProjectDao
import com.example.data.local.dao.VoiceDao
import com.example.data.local.entities.AvatarEntity
import com.example.data.local.entities.PostScheduleEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectMetadataEntity
import com.example.data.local.entities.SocialAccountEntity
import com.example.data.local.entities.VideoProjectEntity
import com.example.data.local.entities.VoiceEntity
import com.example.data.model.AvatarStyle
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.VideoStatus
import com.example.data.model.VoiceEmotion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromAvatarStyle(value: AvatarStyle): String = value.name

    @TypeConverter
    fun toAvatarStyle(value: String): AvatarStyle = runCatching { AvatarStyle.valueOf(value) }.getOrDefault(AvatarStyle.PIP_CIRCLE)

    @TypeConverter
    fun fromVoiceEmotion(value: VoiceEmotion): String = value.name

    @TypeConverter
    fun toVoiceEmotion(value: String): VoiceEmotion = runCatching { VoiceEmotion.valueOf(value) }.getOrDefault(VoiceEmotion.ENTHUSIASTIC)

    @TypeConverter
    fun fromVideoStatus(value: VideoStatus): String = value.name

    @TypeConverter
    fun toVideoStatus(value: String): VideoStatus = runCatching { VideoStatus.valueOf(value) }.getOrDefault(VideoStatus.READY)

    @TypeConverter
    fun fromPostStatus(value: PostStatus): String = value.name

    @TypeConverter
    fun toPostStatus(value: String): PostStatus = runCatching { PostStatus.valueOf(value) }.getOrDefault(PostStatus.QUEUED)

    @TypeConverter
    fun fromSocialPlatform(value: SocialPlatform): String = value.name

    @TypeConverter
    fun toSocialPlatform(value: String): SocialPlatform = runCatching { SocialPlatform.valueOf(value) }.getOrDefault(SocialPlatform.SHOPEE)

    @TypeConverter
    fun fromStringList(value: List<String>?): String = value?.joinToString("|||") ?: ""

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }
}

@Database(
    entities = [
        ProductEntity::class,
        AvatarEntity::class,
        VoiceEntity::class,
        VideoProjectEntity::class,
        PostScheduleEntity::class,
        SocialAccountEntity::class,
        ProjectMetadataEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun avatarDao(): AvatarDao
    abstract fun voiceDao(): VoiceDao
    abstract fun videoProjectDao(): VideoProjectDao
    abstract fun postScheduleDao(): PostScheduleDao
    abstract fun socialAccountDao(): SocialAccountDao
    abstract fun projectMetadataDao(): ProjectMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE social_accounts ADD COLUMN profileLink TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE social_accounts ADD COLUMN directPublishApiUrl TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "viralcraft_studio.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(appContext), appContext)
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(database: AppDatabase, context: Context) {
            // Seed Products
            val p1 = ProductEntity(
                id = 1,
                title = "Smartwatch Ultra Pro Titânio",
                category = "Tecnologia & Acessórios",
                description = "Smartwatch de titânio aeroespacial com tela AMOLED infinita de 2.1 pol, 14 dias de bateria e resistência à água 50m.",
                originalPrice = 289.90,
                promoPrice = 139.90,
                couponCode = "FRETEGRATIS",
                keyFeatures = "Tela AMOLED 2.1\", Bateria 14 dias, Faz ligações Bluetooth, Monitor cardíaco e oxímetro",
                targetAudience = "Jovens e adultos interessados em fitness e tecnologia",
                imageDrawableRes = "showcase_smartwatch_1790132114921",
                productLink = "https://shopee.com.br/smartwatch-ultra-titanio"
            )
            val p2 = ProductEntity(
                id = 2,
                title = "Fone Sem Fio Noise-Cancelling Max Pro",
                category = "Áudio & Gadgets",
                description = "Fones com cancelamento ativo de ruído ANC híbrido, graves profundos e até 40 horas de reprodução contínua.",
                originalPrice = 349.00,
                promoPrice = 169.90,
                couponCode = "ACHADINHO20",
                keyFeatures = "Cancelamento Ativo Ruído (ANC), Graves Hi-Fi 360°, Bateria 40h com estojo, Microfones HD",
                targetAudience = "Estudantes, gamers e pessoas que trabalham ouvindo música",
                imageDrawableRes = "showcase_smartwatch_1790132114921",
                productLink = "https://shopee.com.br/fone-bluetooth-anc"
            )
            val p3 = ProductEntity(
                id = 3,
                title = "Mini Processador Elétrico Turbo 3 Lâminas",
                category = "Casa & Cozinha",
                description = "Triturador elétrico portátil recarregável via USB para alho, temperos, carnes e legumes em menos de 5 segundos.",
                originalPrice = 99.90,
                promoPrice = 39.90,
                couponCode = "COZINHA10",
                keyFeatures = "Sem fio USB, Lâminas aço inox triplas, Desmontável e lavável, Tritura em 5 segundos",
                targetAudience = "Donas de casa e quem busca praticidade na cozinha",
                imageDrawableRes = "showcase_smartwatch_1790132114921",
                productLink = "https://shopee.com.br/mini-processador-usb"
            )
            database.productDao().insertProducts(listOf(p1, p2, p3))

            // Seed Avatars
            val a1 = AvatarEntity(
                id = 1,
                name = "Sophia",
                roleTitle = "Apresentadora E-commerce",
                niche = "Vendas & Achadinhos",
                drawableResName = "avatar_sophia_1790132087493",
                defaultStyle = AvatarStyle.PIP_CIRCLE,
                personalityPrompt = "Energética, expressiva, foca em gatilhos de urgência, escassez e apelo visual do produto."
            )
            val a2 = AvatarEntity(
                id = 2,
                name = "Lucas",
                roleTitle = "Especialista em Gadgets",
                niche = "Tecnologia & Reviews",
                drawableResName = "avatar_lucas_1790132104257",
                defaultStyle = AvatarStyle.SPLIT_SCREEN,
                personalityPrompt = "Didático, confiável, foca nas especificações técnicas explicadas de forma simples e custo-benefício."
            )
            val a3 = AvatarEntity(
                id = 3,
                name = "Camila",
                roleTitle = "Criadora UGC Viral",
                niche = "Moda, Beleza & Casa",
                drawableResName = "img_avatar_camila_1790159354092",
                defaultStyle = AvatarStyle.PIP_CIRCLE,
                personalityPrompt = "Autêntica, carismática e espontânea. Perfeita para vídeos no estilo 'comprinhas da Shopee' e unboxing."
            )
            val a4 = AvatarEntity(
                id = 4,
                name = "Gabriel",
                roleTitle = "Tech & Inovação",
                niche = "Eletrônicos & Smart Devices",
                drawableResName = "img_avatar_gabriel_1790159373843",
                defaultStyle = AvatarStyle.BOTTOM_CORNER,
                personalityPrompt = "Entusiasta de novidades tecnológicas, fala com convicção e destaca diferenciais competitivos."
            )
            val a5 = AvatarEntity(
                id = 5,
                name = "Elena",
                roleTitle = "Consultora de Bem-estar",
                niche = "Saúde & Praticidade",
                drawableResName = "avatar_sophia_1790132087493",
                defaultStyle = AvatarStyle.FULL_PRESENTER,
                personalityPrompt = "Acolhedora, tom de conselho de amiga que descobriu a melhor oferta e facilidade para o dia a dia."
            )
            database.avatarDao().insertAvatars(listOf(a1, a2, a3, a4, a5))

            // Seed Voices
            val v1 = VoiceEntity(
                id = 1,
                name = "Minha Voz Clonada (Charles - IA)",
                toneLabel = "Voz Personalizada Clonada",
                samplePhrase = "Fala galera! Testei esse produto aqui durante uma semana inteira e o resultado me surpreendeu demais.",
                emotion = VoiceEmotion.ENTHUSIASTIC,
                pitch = 1.0f,
                speed = 1.15f,
                isClonedVoice = true,
                cloneStatus = "Clonagem 100% Calibrada (10s de áudio)"
            )
            val v2 = VoiceEntity(
                id = 2,
                name = "Sophia - E-commerce Conversão",
                toneLabel = "Voz Feminina Vibrante BR",
                samplePhrase = "Para tudo o que você está fazendo! Olha só esse achadinho que acabou de baixar pela metade do preço!",
                emotion = VoiceEmotion.ENTHUSIASTIC,
                pitch = 1.05f,
                speed = 1.18f,
                isClonedVoice = false
            )
            val v3 = VoiceEntity(
                id = 3,
                name = "Lucas - Review Dinâmico",
                toneLabel = "Voz Masculina Confiança BR",
                samplePhrase = "Se você quer a melhor qualidade sem pagar o preço de marca famosa, essa é a escolha certa.",
                emotion = VoiceEmotion.CONFIDENT,
                pitch = 0.95f,
                speed = 1.12f,
                isClonedVoice = false
            )
            val v4 = VoiceEntity(
                id = 4,
                name = "Locutor de Ofertas Explosivas",
                toneLabel = "Voz Varejo Urgência",
                samplePhrase = "Atenção! Queima de estoque por tempo limitadíssimo, aproveite o frete grátis antes que acabe!",
                emotion = VoiceEmotion.PERSUASIVE,
                pitch = 0.90f,
                speed = 1.22f,
                isClonedVoice = false
            )
            database.voiceDao().insertVoices(listOf(v1, v2, v3, v4))

            // Seed Social Accounts
            val accShopee = SocialAccountEntity(
                platform = SocialPlatform.SHOPEE,
                accountHandle = "@shopee.achadinhos.br",
                accountName = "Achadinhos Shopee Oficial",
                isConnected = true,
                autoPublishEnabled = true,
                followersFormatted = "342.5K",
                profileLink = "https://shopee.com.br/shop/achadinhos.br",
                directPublishApiUrl = "https://partner.shopeemobile.com/api/v2/video/upload"
            )
            val accTiktok = SocialAccountEntity(
                platform = SocialPlatform.TIKTOK,
                accountHandle = "@viralcraft_brasil",
                accountName = "ViralCraft Shop TikTok",
                isConnected = true,
                autoPublishEnabled = true,
                followersFormatted = "189.2K",
                profileLink = "https://www.tiktok.com/@viralcraft_brasil",
                directPublishApiUrl = "https://open.tiktokapis.com/v2/post/publish/video/init/"
            )
            val accInstagram = SocialAccountEntity(
                platform = SocialPlatform.INSTAGRAM,
                accountHandle = "@achadinhos_reels_br",
                accountName = "Achadinhos & Cupons Reels",
                isConnected = true,
                autoPublishEnabled = true,
                followersFormatted = "215.8K",
                profileLink = "https://www.instagram.com/achadinhos_reels_br",
                directPublishApiUrl = "https://graph.facebook.com/v19.0/me/media"
            )
            val accFacebook = SocialAccountEntity(
                platform = SocialPlatform.FACEBOOK,
                accountHandle = "@loja.viral.achados",
                accountName = "Página Viral Ofertas BR",
                isConnected = true,
                autoPublishEnabled = true,
                followersFormatted = "94.1K",
                profileLink = "https://www.facebook.com/loja.viral.achados",
                directPublishApiUrl = "https://graph.facebook.com/v19.0/page/videos"
            )
            database.socialAccountDao().insertAccounts(listOf(accShopee, accTiktok, accInstagram, accFacebook))

            // Generate real local assets in internal storage
            val draft1Name = "Smartwatch Ultra Pro - Queima 52% OFF"
            val script1 = "Se você ainda paga mais de 500 reais em smartwatch, para tudo e olha essa promoção! A maioria dos relógios baratos trava, a bateria não dura 1 dia e nem atende chamadas. O Ultra Pro Titânio tem tela AMOLED fluida, bateria pra 14 dias e faz chamadas direto no pulso. Hoje ele tá saindo de R$ 289 por apenas R$ 139,90 com o cupom FRETEGRATIS! Clica no botão do carrinho aqui embaixo agora antes que o lote promocional se esgote!"
            val assets1 = LocalAssetManager.generateLocalAssetsForProject(
                context = context,
                projectId = 1L,
                draftName = draft1Name,
                scriptText = script1,
                durationSeconds = 28
            )

            val now = System.currentTimeMillis()
            val vp1 = VideoProjectEntity(
                id = 1,
                title = draft1Name,
                productId = 1,
                avatarId = 1,
                voiceId = 1,
                aspectRatio = "9:16",
                avatarStyle = AvatarStyle.PIP_CIRCLE,
                hookScript = "Se você ainda paga mais de 500 reais em smartwatch, para tudo e olha essa promoção!",
                problemScript = "A maioria dos relógios baratos trava, a bateria não dura 1 dia e nem atende chamadas.",
                solutionScript = "O Ultra Pro Titânio tem tela AMOLED fluida, bateria pra 14 dias e faz chamadas direto no pulso.",
                offerScript = "Hoje ele tá saindo de R$ 289 por apenas R$ 139,90 com o cupom FRETEGRATIS!",
                ctaScript = "Clica no botão do carrinho aqui embaixo agora antes que o lote promocional se esgote!",
                fullScriptText = script1,
                targetPlatformsCsv = "SHOPEE,TIKTOK,INSTAGRAM,FACEBOOK",
                durationSeconds = 28,
                backgroundMusic = "Viral Beat Phonk Lo-Fi",
                status = VideoStatus.READY,
                coverImageRes = "showcase_smartwatch_1790132114921",
                estimatedViews = 45200,
                estimatedSales = 127,
                createdAtEpoch = now - 86400000L,
                videoDraftName = draft1Name,
                creationTimestamp = now - 86400000L,
                videoFilePath = assets1.videoFilePath,
                thumbnailFilePath = assets1.thumbnailFilePath,
                audioFilePath = assets1.audioFilePath,
                generatedAssetPaths = assets1.allAssetPaths,
                fileSizeBytes = assets1.totalSizeBytes
            )
            database.videoProjectDao().insertVideoProject(vp1)

            // Seed ProjectMetadataEntity for draft 1
            val meta1 = ProjectMetadataEntity(
                id = 1,
                videoDraftName = draft1Name,
                creationTimestamp = now - 86400000L,
                lastModifiedTimestamp = now - 43200000L,
                videoFilePath = assets1.videoFilePath,
                thumbnailFilePath = assets1.thumbnailFilePath,
                audioFilePath = assets1.audioFilePath,
                generatedAssetPaths = assets1.allAssetPaths,
                durationSeconds = 28,
                aspectRatio = "9:16",
                resolution = "1080x1920",
                fileSizeBytes = assets1.totalSizeBytes,
                status = "READY",
                notes = "Roteiro validado para tráfego pago e orgânico com avatar PiP e cupom de frete grátis."
            )
            database.projectMetadataDao().insertProjectMetadata(meta1)

            // Seed second ProjectMetadata draft
            val draft2Name = "Mini Processador Turbo USB - UGC Viral"
            val script2 = "Tritura alho, temperos e carne em menos de 5 segundos sem fazer sujeira na pia! Bateria USB recarregável de longa duração."
            val assets2 = LocalAssetManager.generateLocalAssetsForProject(
                context = context,
                projectId = 2L,
                draftName = draft2Name,
                scriptText = script2,
                durationSeconds = 20
            )
            val meta2 = ProjectMetadataEntity(
                id = 2,
                videoDraftName = draft2Name,
                creationTimestamp = now - 18000000L,
                lastModifiedTimestamp = now - 3600000L,
                videoFilePath = assets2.videoFilePath,
                thumbnailFilePath = assets2.thumbnailFilePath,
                audioFilePath = assets2.audioFilePath,
                generatedAssetPaths = assets2.allAssetPaths,
                durationSeconds = 20,
                aspectRatio = "9:16",
                resolution = "1080x1920",
                fileSizeBytes = assets2.totalSizeBytes,
                status = "DRAFT",
                notes = "Rascunho de vídeo UGC focado em donas de casa e praticidade na cozinha."
            )
            database.projectMetadataDao().insertProjectMetadata(meta2)

            // Seed scheduled posts
            val schedShopee = PostScheduleEntity(
                id = 1,
                videoProjectId = 1,
                platform = SocialPlatform.SHOPEE,
                caption = "🚨 QUEIMA DE ESTOQUE! Smartwatch Ultra Titânio com 52% de desconto + Cupom FRETEGRATIS! Corre pro carrinho! 🔥",
                hashtags = "#shopee #achadinhosshopee #smartwatch #ofertas #fretegratis",
                scheduledTimeEpoch = now - 3600000,
                status = PostStatus.PUBLISHED,
                externalPostId = "shp_vid_994218",
                publishedUrl = "https://shopee.com.br/video/play/994218",
                logs = "Publicado automaticamente no Shopee Vídeo às 18:30"
            )
            val schedTiktok = PostScheduleEntity(
                id = 2,
                videoProjectId = 1,
                platform = SocialPlatform.TIKTOK,
                caption = "Ele faz TUDO isso por menos de R$ 140?! 😱 Testei o smartwatch mais vendido do momento! Link na bio / vitrine!",
                hashtags = "#tiktokmademebuyit #achadinhos #reviewtech #smartwatchbrasil #tech",
                scheduledTimeEpoch = now + 7200000,
                status = PostStatus.QUEUED,
                logs = "Agendado para o melhor horário de pico do TikTok (21:15)"
            )
            val schedInstagram = PostScheduleEntity(
                id = 3,
                videoProjectId = 1,
                platform = SocialPlatform.INSTAGRAM,
                caption = "O melhor custo-benefício de 2026! Quem quiser o cupom exclusivo de 52% OFF comenta 'EU QUERO' que envio no direct!",
                hashtags = "#reelsviral #dicas #tecnologia #smartwatch #achadinho",
                scheduledTimeEpoch = now + 14400000,
                status = PostStatus.QUEUED,
                logs = "Agendado para postagem automática no Instagram Reels"
            )
            database.postScheduleDao().insertSchedules(listOf(schedShopee, schedTiktok, schedInstagram))
        }
    }
}
