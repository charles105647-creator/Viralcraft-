package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiScriptService
import com.example.data.ai.ScriptVariation
import com.example.data.audio.AudioVoiceEngine
import com.example.data.local.entities.AvatarEntity
import com.example.data.local.entities.PostScheduleEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SocialAccountEntity
import com.example.data.local.entities.VideoProjectEntity
import com.example.data.local.entities.VoiceEntity
import com.example.data.audio.AudioLibrary
import com.example.data.model.AvatarStyle
import com.example.data.model.BackgroundAudioTrack
import com.example.data.model.FontStyleChoice
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.TextBgStyle
import com.example.data.model.TextOverlay
import com.example.data.model.TextVerticalPosition
import com.example.data.model.VideoClip
import com.example.data.model.VideoStatus
import com.example.data.model.VoiceEmotion
import com.example.data.publisher.SocialPublisher
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.local.entities.ProjectMetadataEntity
import com.example.data.local.LocalAssetManager
import com.example.data.analytics.AppAnalytics
import com.example.data.analytics.AnalyticsLogEntry
import com.example.data.ai.ScriptCategory
import com.example.data.ai.ScriptTemplatePreset
import com.example.data.ai.ScriptTemplatesCatalog
import com.example.data.model.ClipTransition
import com.example.data.model.TransitionEffect
import com.example.data.model.VideoExportQuality


data class VideoCreationFormState(
    val productName: String = "",
    val category: String = "Tecnologia & Acessórios",
    val description: String = "",
    val originalPriceStr: String = "249.90",
    val promoPriceStr: String = "119.90",
    val couponCode: String = "PROMO10",
    val keyFeatures: String = "Bateria longa duração, design ergonômico, acabamento premium",
    val targetAudience: String = "Público em busca de custo-benefício",
    val customImageUri: String = "",
    val affiliateLink: String = "https://shopee.com.br/afiliado/produto",
    val selectedAvatarId: Long = 1,
    val selectedVoiceId: Long = 1,
    val avatarStyle: AvatarStyle = AvatarStyle.PIP_CIRCLE,
    val targetPlatforms: Set<SocialPlatform> = setOf(
        SocialPlatform.SHOPEE,
        SocialPlatform.TIKTOK,
        SocialPlatform.INSTAGRAM,
        SocialPlatform.FACEBOOK
    ),
    val backgroundMusic: String = "Viral Beat Phonk Lo-Fi",
    val isGeneratingAi: Boolean = false,
    val generatedHook: String = "",
    val generatedProblem: String = "",
    val generatedSolution: String = "",
    val generatedOffer: String = "",
    val generatedCta: String = "",
    val generatedFullScript: String = "",
    val videoDuration: Int = 30
)

data class ScriptAssistantUiState(
    val isGenerating: Boolean = false,
    val activeCategory: ScriptCategory = ScriptCategory.ECOMMERCE,
    val selectedTemplateId: String? = null,
    val itemDescriptionInput: String = "Mini Processador de Alimentos Elétrico USB portátil, tritura alho, cebola e legumes em 5 segundos, lâminas de aço inox, fácil de lavar e bivolt.",
    val productNameInput: String = "Mini Processador Turbo USB",
    val selectedTrigger: String = "Urgência & Escassez",
    val selectedDuration: Int = 30,
    val selectedTone: String = "Empolgado & Viral",
    val variations: List<ScriptVariation> = emptyList(),
    val selectedVariationIndex: Int = 0,
    val generationProgressStep: String = "",
    val errorMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)
    val audioEngine = AudioVoiceEngine(application)

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val avatars: StateFlow<List<AvatarEntity>> = repository.allAvatars
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voices: StateFlow<List<VoiceEntity>> = repository.allVoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videoProjects: StateFlow<List<VideoProjectEntity>> = repository.allVideoProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<PostScheduleEntity>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val socialAccounts: StateFlow<List<SocialAccountEntity>> = repository.allSocialAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectMetadataList: StateFlow<List<ProjectMetadataEntity>> = repository.allProjectMetadata
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently active video in player
    private val _currentPlayingVideo = MutableStateFlow<VideoProjectEntity?>(null)
    val currentPlayingVideo: StateFlow<VideoProjectEntity?> = _currentPlayingVideo.asStateFlow()

    // Player active platform overlay preview
    private val _playerPreviewPlatform = MutableStateFlow(SocialPlatform.SHOPEE)
    val playerPreviewPlatform: StateFlow<SocialPlatform> = _playerPreviewPlatform.asStateFlow()

    // Creation Wizard state
    private val _creationForm = MutableStateFlow(VideoCreationFormState())
    val creationForm: StateFlow<VideoCreationFormState> = _creationForm.asStateFlow()

    // Voice recording simulation state
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Realtime Firebase Analytics events feed
    val analyticsEvents: StateFlow<List<AnalyticsLogEntry>> = AppAnalytics.recentEvents

    private var recordingJob: Job? = null

    // --- Video Editor State ---
    private val _editorVideoProject = MutableStateFlow<VideoProjectEntity?>(null)
    val editorVideoProject: StateFlow<VideoProjectEntity?> = _editorVideoProject.asStateFlow()

    private val _editorClips = MutableStateFlow<List<VideoClip>>(emptyList())
    val editorClips: StateFlow<List<VideoClip>> = _editorClips.asStateFlow()

    private val _editorTextOverlays = MutableStateFlow<List<TextOverlay>>(emptyList())
    val editorTextOverlays: StateFlow<List<TextOverlay>> = _editorTextOverlays.asStateFlow()

    private val _editorAudioTrack = MutableStateFlow<BackgroundAudioTrack>(AudioLibrary.PRESET_TRACKS.first())
    val editorAudioTrack: StateFlow<BackgroundAudioTrack> = _editorAudioTrack.asStateFlow()

    private val _editorPlayhead = MutableStateFlow(0f)
    val editorPlayhead: StateFlow<Float> = _editorPlayhead.asStateFlow()

    private val _isEditorPlaying = MutableStateFlow(false)
    val isEditorPlaying: StateFlow<Boolean> = _isEditorPlaying.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _selectedTextId = MutableStateFlow<String?>(null)
    val selectedTextId: StateFlow<String?> = _selectedTextId.asStateFlow()

    private val _editorExportQuality = MutableStateFlow(VideoExportQuality.FULL_HD_1080P)
    val editorExportQuality: StateFlow<VideoExportQuality> = _editorExportQuality.asStateFlow()

    private var editorPlayJob: Job? = null


    // --- Real-Time Preview State ---
    private val _previewPlaybackSpeed = MutableStateFlow(1.0f)
    val previewPlaybackSpeed: StateFlow<Float> = _previewPlaybackSpeed.asStateFlow()

    private val _previewPlatformGuide = MutableStateFlow<SocialPlatform?>(null)
    val previewPlatformGuide: StateFlow<SocialPlatform?> = _previewPlatformGuide.asStateFlow()

    private val _previewShowSafeZones = MutableStateFlow(false)
    val previewShowSafeZones: StateFlow<Boolean> = _previewShowSafeZones.asStateFlow()

    private val _previewShowAvatarPiP = MutableStateFlow(true)
    val previewShowAvatarPiP: StateFlow<Boolean> = _previewShowAvatarPiP.asStateFlow()

    private val _isFullscreenPreview = MutableStateFlow(false)
    val isFullscreenPreview: StateFlow<Boolean> = _isFullscreenPreview.asStateFlow()

    // --- Script Assistant (Gemini) State ---
    private val _scriptAssistantState = MutableStateFlow(ScriptAssistantUiState())
    val scriptAssistantState: StateFlow<ScriptAssistantUiState> = _scriptAssistantState.asStateFlow()

    // --- Avatar Gallery & Selection State ---
    private val _selectedGalleryAvatar = MutableStateFlow<AvatarEntity?>(null)
    val selectedGalleryAvatar: StateFlow<AvatarEntity?> = _selectedGalleryAvatar.asStateFlow()

    private val _gallerySelectedStyle = MutableStateFlow(AvatarStyle.PIP_CIRCLE)
    val gallerySelectedStyle: StateFlow<AvatarStyle> = _gallerySelectedStyle.asStateFlow()

    private val _galleryFilterNiche = MutableStateFlow("Todos")
    val galleryFilterNiche: StateFlow<String> = _galleryFilterNiche.asStateFlow()

    private val _isAvatarSpeakingTest = MutableStateFlow(false)
    val isAvatarSpeakingTest: StateFlow<Boolean> = _isAvatarSpeakingTest.asStateFlow()

    init {
        // Automatically select first video project for player and editor when ready & seed viral genre projects
        viewModelScope.launch {
            videoProjects.collect { list ->
                if (list.isNotEmpty()) {
                    if (_currentPlayingVideo.value == null) {
                        _currentPlayingVideo.value = list.first()
                    }
                    if (_editorVideoProject.value == null) {
                        loadProjectIntoEditor(list.first())
                    }
                    if (list.size < 4) {
                        val hasNovela = list.any { it.coverImageRes.contains("drama") }
                        val hasCinema = list.any { it.coverImageRes.contains("cinema") }
                        val hasCartoon = list.any { it.coverImageRes.contains("cartoon") }
                        val hasBike = list.any { it.coverImageRes.contains("bike") }
                        val hasStory = list.any { it.coverImageRes.contains("story") }

                        if (!hasNovela) {
                            repository.insertVideoProject(
                                VideoProjectEntity(
                                    title = "Mini Novela: O Bilhete Secreto",
                                    productId = 1,
                                    avatarId = 3,
                                    voiceId = 2,
                                    aspectRatio = "9:16",
                                    avatarStyle = AvatarStyle.PIP_CIRCLE,
                                    hookScript = "Abri a encomenda que chegou no nome do meu marido e encontrei um bilhete que mudou tudo...",
                                    problemScript = "Nenhum vizinho quis falar, mas a data marcada no papel era para hoje às 19h no centro.",
                                    solutionScript = "Quando cheguei ao local com as mãos tremendo, todas as luzes se acenderam na festa surpresa!",
                                    offerScript = "O amor supera qualquer desconfiança quando há transparência e carinho.",
                                    ctaScript = "Curte e comenta se você também teria ficado desconfiada com esse bilhete!",
                                    fullScriptText = "Abri a encomenda que chegou no nome do meu marido e encontrei um bilhete que mudou tudo... Quando cheguei lá era minha festa surpresa!",
                                    durationSeconds = 28,
                                    backgroundMusic = "Tensão & Drama de Mini Novela",
                                    status = VideoStatus.READY,
                                    coverImageRes = "img_genre_drama_1793133540012",
                                    estimatedViews = 89400,
                                    estimatedSales = 312
                                )
                            )
                        }
                        if (!hasCinema) {
                            repository.insertVideoProject(
                                VideoProjectEntity(
                                    title = "Cinema Recap: Neon Reckoning",
                                    productId = 1,
                                    avatarId = 4,
                                    voiceId = 3,
                                    aspectRatio = "9:16",
                                    avatarStyle = AvatarStyle.BOTTOM_CORNER,
                                    hookScript = "Se você assistiu esse filme e não entendeu nada do final, olha o detalhe aos 5 minutos!",
                                    problemScript = "O diretor escondeu a verdadeira identidade do soldado cibernético no reflexo do espelho.",
                                    solutionScript = "Na batalha final em Neo-Tóquio, a mesma cicatriz prova que ele era o irmão do imperador.",
                                    offerScript = "Uma das maiores obras-primas da ficção científica pós-apocalíptica dos últimos 10 anos.",
                                    ctaScript = "Já assistiu esse filme no streaming? Deixa sua nota de 1 a 10 nos comentários!",
                                    fullScriptText = "Se você assistiu esse filme e não entendeu nada do final, olha o detalhe aos 5 minutos! Na batalha final tudo faz sentido.",
                                    durationSeconds = 30,
                                    backgroundMusic = "Trailer Épico de Cinema & Séries",
                                    status = VideoStatus.READY,
                                    coverImageRes = "img_genre_cinema_1793133552336",
                                    estimatedViews = 142000,
                                    estimatedSales = 540
                                )
                            )
                        }
                        if (!hasCartoon) {
                            repository.insertVideoProject(
                                VideoProjectEntity(
                                    title = "Desenho: A Raposa Inventora",
                                    productId = 1,
                                    avatarId = 2,
                                    voiceId = 2,
                                    aspectRatio = "9:16",
                                    avatarStyle = AvatarStyle.PIP_CIRCLE,
                                    hookScript = "A pequena raposa achou uma engrenagem brilhante que volta 10 segundos no tempo!",
                                    problemScript = "Tudo parecia uma grande brincadeira, até que um feitiço começou a engolir a oficina do ferreiro.",
                                    solutionScript = "Usando sua chave de fenda mágica e a ajuda dos amigos da floresta, ela consertou o cristal.",
                                    offerScript = "Uma aventura divertida com lição de coragem e amizade para toda a família.",
                                    ctaScript = "Comenta 'PARTE 2' para ver o próximo episódio da Raposa e o Cristal Mágico!",
                                    fullScriptText = "A pequena raposa achou uma engrenagem brilhante que volta 10 segundos no tempo! Ela salvou a oficina mágica.",
                                    durationSeconds = 25,
                                    backgroundMusic = "Desenho Animado Lúdico & Boing",
                                    status = VideoStatus.READY,
                                    coverImageRes = "img_genre_cartoon_1793133564699",
                                    estimatedViews = 67300,
                                    estimatedSales = 198
                                )
                            )
                        }
                        if (!hasBike) {
                            repository.insertVideoProject(
                                VideoProjectEntity(
                                    title = "Grau 244 & Bike: O Ponto de Equilíbrio",
                                    productId = 1,
                                    avatarId = 1,
                                    voiceId = 1,
                                    aspectRatio = "9:16",
                                    avatarStyle = AvatarStyle.BOTTOM_CORNER,
                                    hookScript = "Se sua bike tá caindo pro lado na hora do grau, você tá errando essa regulagem no freio traseiro!",
                                    problemScript = "Muita gente puxa no braço com medo de cair de costas e esquece de dosar a pinça hidráulica.",
                                    solutionScript = "Com o dedo leve no manete e o pneu traseiro em 28 libras, você raspa a rabeta com segurança!",
                                    offerScript = "Tutorial completo de manobras de rua e customização de bike para quem ama grau.",
                                    ctaScript = "Marca aquele seu amigo que vive ralando o joelho tentando empinar a bike!",
                                    fullScriptText = "Se sua bike tá caindo pro lado na hora do grau, você tá errando essa regulagem no freio traseiro! Dedo no manete e equilíbrio.",
                                    durationSeconds = 27,
                                    backgroundMusic = "Phonk Brasileiro Grau 244 (Bass Boosted)",
                                    status = VideoStatus.READY,
                                    coverImageRes = "img_genre_bike_1793133576441",
                                    estimatedViews = 210500,
                                    estimatedSales = 890
                                )
                            )
                        }
                        if (!hasStory) {
                            repository.insertVideoProject(
                                VideoProjectEntity(
                                    title = "Estórias Reais: O Quarto Trancado",
                                    productId = 1,
                                    avatarId = 5,
                                    voiceId = 3,
                                    aspectRatio = "9:16",
                                    avatarStyle = AvatarStyle.PIP_CIRCLE,
                                    hookScript = "A única regra do proprietário era nunca, jamais abrir a terceira porta do corredor...",
                                    problemScript = "Toda madrugada exatamente às 3h15 ouvia-se um barulho de páginas sendo folheadas lá dentro.",
                                    solutionScript = "Quando finalmente girei a chave antiga, encontrei um cofre com o diário original de 1940!",
                                    offerScript = "Relato anônimo real que intrigou milhões de pessoas na internet.",
                                    ctaScript = "Você teria coragem de abrir a porta trancada de madrugada? Deixe nos comentários!",
                                    fullScriptText = "A única regra do proprietário era nunca, jamais abrir a terceira porta do corredor... Encontrei o diário misterioso de 1940.",
                                    durationSeconds = 30,
                                    backgroundMusic = "Estórias Reais & Mistério Noturno",
                                    status = VideoStatus.READY,
                                    coverImageRes = "img_genre_story_1793133588372",
                                    estimatedViews = 95200,
                                    estimatedSales = 340
                                )
                            )
                        }
                    }
                }
            }
        }

        // Seed additional avatars if not present and select default
        viewModelScope.launch {
            avatars.collect { list ->
                if (list.isNotEmpty()) {
                    if (_selectedGalleryAvatar.value == null) {
                        _selectedGalleryAvatar.value = list.first()
                        _gallerySelectedStyle.value = list.first().defaultStyle
                    }
                    if (list.size < 5) {
                        val hasCamila = list.any { it.name == "Camila" }
                        val hasGabriel = list.any { it.name == "Gabriel" }
                        val toAdd = mutableListOf<AvatarEntity>()
                        if (!hasCamila) {
                            toAdd.add(
                                AvatarEntity(
                                    name = "Camila",
                                    roleTitle = "Criadora UGC Viral",
                                    niche = "Moda, Beleza & Casa",
                                    drawableResName = "img_avatar_camila_1790159354092",
                                    defaultStyle = AvatarStyle.PIP_CIRCLE,
                                    personalityPrompt = "Autêntica, carismática e espontânea. Perfeita para vídeos no estilo 'comprinhas da Shopee' e unboxing."
                                )
                            )
                        }
                        if (!hasGabriel) {
                            toAdd.add(
                                AvatarEntity(
                                    name = "Gabriel",
                                    roleTitle = "Tech & Inovação",
                                    niche = "Eletrônicos & Smart Devices",
                                    drawableResName = "img_avatar_gabriel_1790159373843",
                                    defaultStyle = AvatarStyle.BOTTOM_CORNER,
                                    personalityPrompt = "Entusiasta de novidades tecnológicas, fala com convicção e destaca diferenciais competitivos."
                                )
                            )
                        }
                        if (toAdd.isNotEmpty()) {
                            repository.insertAvatars(toAdd)
                        }
                    }
                }
            }
        }
    }

    fun selectGalleryAvatar(avatar: AvatarEntity) {
        _selectedGalleryAvatar.value = avatar
        _gallerySelectedStyle.value = avatar.defaultStyle
        audioEngine.stop()
        _isAvatarSpeakingTest.value = false
    }

    fun setGallerySelectedStyle(style: AvatarStyle) {
        _gallerySelectedStyle.value = style
    }

    fun setGalleryFilterNiche(niche: String) {
        _galleryFilterNiche.value = niche
    }

    fun testAvatarVoiceSample(avatar: AvatarEntity, customSpeech: String? = null) {
        val sampleText = customSpeech ?: when (avatar.name) {
            "Sophia" -> "Oi, gente! Olha só que achadinho incrível eu encontrei hoje com frete grátis na Shopee! Clica no link e aproveita o cupom antes que acabe!"
            "Lucas" -> "Fala, pessoal! Testei esse produto por uma semana e a bateria realmente impressiona. Excelente custo-benefício para quem busca tecnologia!"
            "Camila" -> "Meninas, sério, vocês não têm noção da qualidade desse produto! Chegou super rápido e já virou o meu queridinho indispensável!"
            "Gabriel" -> "Design premium, acabamento impecável e funções avançadas de última geração. Sem dúvidas a melhor escolha da categoria!"
            "Elena" -> "Se você quer transformar a sua rotina com praticidade e elegância, essa é a recomendação perfeita para o seu dia a dia!"
            else -> "Olá! Eu sou ${avatar.name}, seu apresentador virtual gerado por inteligência artificial para vídeos de alta conversão!"
        }

        val pitch = when (avatar.name) {
            "Sophia" -> 1.1f
            "Lucas" -> 0.9f
            "Camila" -> 1.15f
            "Gabriel" -> 0.85f
            "Elena" -> 1.05f
            else -> 1.0f
        }

        audioEngine.stop()
        _isAvatarSpeakingTest.value = true
        audioEngine.speak(sampleText, pitch = pitch, speed = 1.15f)

        viewModelScope.launch {
            delay(4500)
            _isAvatarSpeakingTest.value = false
        }
    }

    fun applyGalleryAvatarToCreation(avatar: AvatarEntity, style: AvatarStyle? = null) {
        selectAvatarInCreation(avatar.id)
        if (style != null) {
            selectAvatarStyleInCreation(style)
        }
        _userMessage.value = "Avatar ${avatar.name} selecionado para o novo vídeo!"
    }

    fun applyGalleryAvatarToEditor(avatar: AvatarEntity, style: AvatarStyle? = null) {
        val current = _editorVideoProject.value ?: return
        val updated = current.copy(
            avatarId = avatar.id,
            avatarStyle = style ?: avatar.defaultStyle
        )
        _editorVideoProject.value = updated
        viewModelScope.launch {
            repository.updateVideoProject(updated)
            _userMessage.value = "Avatar ${avatar.name} aplicado ao projeto atual!"
        }
    }

    fun createCustomAvatar(
        name: String,
        role: String,
        niche: String,
        personality: String,
        imageUri: String,
        defaultStyle: AvatarStyle = AvatarStyle.PIP_CIRCLE
    ) {
        if (name.isBlank()) {
            _userMessage.value = "Informe o nome do avatar"
            return
        }
        viewModelScope.launch {
            val newAvatar = AvatarEntity(
                name = name.trim(),
                roleTitle = role.ifBlank { "Criador de Conteúdo IA" },
                niche = niche.ifBlank { "Geral & E-commerce" },
                drawableResName = "avatar_sophia_1790132087493",
                customImageUri = imageUri,
                defaultStyle = defaultStyle,
                personalityPrompt = personality.ifBlank { "Apresentador persuasivo focado em engajamento e vendas." },
                isCustom = true
            )
            val newId = repository.insertAvatar(newAvatar)
            val created = newAvatar.copy(id = newId)
            _selectedGalleryAvatar.value = created
            _userMessage.value = "Avatar personalizado ${created.name} criado com sucesso!"
        }
    }

    fun selectVideoForPlayback(video: VideoProjectEntity) {
        audioEngine.stop()
        _currentPlayingVideo.value = video
    }

    fun setPlayerPreviewPlatform(platform: SocialPlatform) {
        _playerPreviewPlatform.value = platform
    }

    fun playVideoVoice(text: String, pitch: Float = 1.0f, speed: Float = 1.15f) {
        audioEngine.speak(text, pitch, speed)
    }

    fun stopVideoVoice() {
        audioEngine.stop()
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- Video Creation Form Handlers ---
    fun updateCreationProduct(
        name: String,
        category: String,
        description: String,
        originalPrice: String,
        promoPrice: String,
        coupon: String,
        features: String,
        customImageUri: String? = null,
        affiliateLink: String? = null
    ) {
        _creationForm.value = _creationForm.value.copy(
            productName = name,
            category = category,
            description = description,
            originalPriceStr = originalPrice,
            promoPriceStr = promoPrice,
            couponCode = coupon,
            keyFeatures = features,
            customImageUri = customImageUri ?: _creationForm.value.customImageUri,
            affiliateLink = affiliateLink ?: _creationForm.value.affiliateLink
        )
    }

    fun setProductCustomImageUri(uriString: String) {
        _creationForm.value = _creationForm.value.copy(customImageUri = uriString)
    }

    fun setProductAffiliateLink(link: String) {
        _creationForm.value = _creationForm.value.copy(affiliateLink = link)
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.insertProduct(product)
                _userMessage.value = "Produto '${product.title}' cadastrado com sucesso!"
            } else {
                repository.updateProduct(product)
                _userMessage.value = "Produto '${product.title}' atualizado com sucesso!"
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _userMessage.value = "Produto '${product.title}' removido do catálogo."
        }
    }

    fun loadProductIntoCreationWizard(product: ProductEntity) {
        val imgUri = if (product.customImageUri.isNotBlank()) {
            product.customImageUri
        } else if (product.imageDrawableRes.isNotBlank()) {
            "android.resource://com.aistudio.productvideocreator.wskd/drawable/${product.imageDrawableRes}"
        } else {
            ""
        }

        _creationForm.value = _creationForm.value.copy(
            productName = product.title,
            category = product.category,
            description = product.description,
            originalPriceStr = String.format(java.util.Locale.US, "%.2f", product.originalPrice),
            promoPriceStr = String.format(java.util.Locale.US, "%.2f", product.promoPrice),
            couponCode = product.couponCode,
            keyFeatures = product.keyFeatures,
            customImageUri = if (imgUri.isNotBlank()) imgUri else _creationForm.value.customImageUri,
            affiliateLink = if (product.productLink.isNotBlank()) product.productLink else _creationForm.value.affiliateLink
        )
        _userMessage.value = "Produto '${product.title}' carregado no Criador 9:16!"
    }

    fun saveSocialAccount(account: SocialAccountEntity) {
        viewModelScope.launch {
            repository.saveSocialAccount(account)
            _userMessage.value = "Conta @${account.accountHandle} atualizada em ${account.platform.displayName}!"
        }
    }

    fun saveAllSocialLinks(
        tiktokLink: String,
        shopeeLink: String,
        instagramLink: String,
        facebookLink: String
    ) {
        viewModelScope.launch {
            val list = socialAccounts.value
            val platformsWithLinks = listOf(
                SocialPlatform.TIKTOK to tiktokLink,
                SocialPlatform.SHOPEE to shopeeLink,
                SocialPlatform.INSTAGRAM to instagramLink,
                SocialPlatform.FACEBOOK to facebookLink
            )

            for ((platform, link) in platformsWithLinks) {
                val existing = list.find { it.platform == platform } ?: SocialAccountEntity(
                    platform = platform,
                    accountHandle = when (platform) {
                        SocialPlatform.TIKTOK -> "@viralcraft_brasil"
                        SocialPlatform.SHOPEE -> "@shopee.achadinhos.br"
                        SocialPlatform.INSTAGRAM -> "@achadinhos_reels_br"
                        SocialPlatform.FACEBOOK -> "@loja.viral.achados"
                        else -> "@meuperfil"
                    },
                    accountName = platform.displayName
                )
                val updated = existing.copy(
                    profileLink = link.trim(),
                    isConnected = true,
                    lastSyncEpoch = System.currentTimeMillis()
                )
                repository.saveSocialAccount(updated)
            }
            AppAnalytics.trackSocialLinksConfigured(
                tiktokSet = tiktokLink.isNotBlank(),
                shopeeSet = shopeeLink.isNotBlank(),
                instagramSet = instagramLink.isNotBlank(),
                facebookSet = facebookLink.isNotBlank()
            )
            _userMessage.value = "✅ Links das redes sociais salvos e vinculados à publicação direta!"
        }
    }

    fun selectAvatarInCreation(avatarId: Long) {
        _creationForm.value = _creationForm.value.copy(selectedAvatarId = avatarId)
    }

    fun selectAvatarStyleInCreation(style: AvatarStyle) {
        _creationForm.value = _creationForm.value.copy(avatarStyle = style)
    }

    fun selectVoiceInCreation(voiceId: Long) {
        _creationForm.value = _creationForm.value.copy(selectedVoiceId = voiceId)
        val selected = voices.value.find { it.id == voiceId }
        if (selected != null) {
            AppAnalytics.trackVoiceSelected(voiceId, selected.name, selected.isClonedVoice)
        }
    }

    fun toggleTargetPlatform(platform: SocialPlatform) {
        val current = _creationForm.value.targetPlatforms.toMutableSet()
        if (current.contains(platform)) {
            if (current.size > 1) current.remove(platform)
        } else {
            current.add(platform)
        }
        _creationForm.value = _creationForm.value.copy(targetPlatforms = current)
    }

    fun selectBackgroundMusic(music: String) {
        _creationForm.value = _creationForm.value.copy(backgroundMusic = music)
    }

    fun updateGeneratedScript(hook: String, problem: String, solution: String, offer: String, cta: String) {
        val full = "$hook $problem $solution $offer $cta"
        _creationForm.value = _creationForm.value.copy(
            generatedHook = hook,
            generatedProblem = problem,
            generatedSolution = solution,
            generatedOffer = offer,
            generatedCta = cta,
            generatedFullScript = full
        )
    }

    fun generateAiScript() {
        viewModelScope.launch {
            _creationForm.value = _creationForm.value.copy(isGeneratingAi = true)
            val form = _creationForm.value
            val originalPrice = form.originalPriceStr.toDoubleOrNull() ?: 249.90
            val promoPrice = form.promoPriceStr.toDoubleOrNull() ?: 119.90

            val avatar = avatars.value.find { it.id == form.selectedAvatarId }
            val avatarTone = avatar?.personalityPrompt ?: "Energético e focado em conversão de vendas"

            val scriptResult = GeminiScriptService.generateEcommerceScript(
                productName = form.productName.ifBlank { "Smartwatch Ultra Pro" },
                category = form.category,
                keyFeatures = form.keyFeatures,
                originalPrice = originalPrice,
                promoPrice = promoPrice,
                coupon = form.couponCode,
                targetAudience = form.targetAudience,
                avatarTone = avatarTone
            )

            _creationForm.value = _creationForm.value.copy(
                isGeneratingAi = false,
                generatedHook = scriptResult.hook,
                generatedProblem = scriptResult.problem,
                generatedSolution = scriptResult.solution,
                generatedOffer = scriptResult.offer,
                generatedCta = scriptResult.cta,
                generatedFullScript = scriptResult.fullScript,
                videoDuration = 30
            )
            _userMessage.value = "Roteiro viral 9:16 gerado com sucesso pela IA!"
        }
    }

    fun loadGenrePresetIntoCreation(category: ScriptCategory, template: ScriptTemplatePreset, context: Context) {
        val coverRes = when (category) {
            ScriptCategory.MINI_NOVELA -> "img_genre_drama_1793133540012"
            ScriptCategory.CINEMA_FILMS -> "img_genre_cinema_1793133552336"
            ScriptCategory.ANIMATION_CARTOON -> "img_genre_cartoon_1793133564699"
            ScriptCategory.STORIES_MYSTERY -> "img_genre_story_1793133588372"
            ScriptCategory.BIKES_CYCLING -> "img_genre_bike_1793133576441"
            ScriptCategory.ECOMMERCE -> "showcase_smartwatch_1790132114921"
            ScriptCategory.UNBOXING -> "img_product_earbuds_1792222861982"
        }
        val full = "${template.retentionHook} ${template.plotSetup} ${template.sceneStructure.joinToString(" ")}"
        _creationForm.value = _creationForm.value.copy(
            productName = template.title,
            category = category.title,
            keyFeatures = template.mentalTrigger,
            targetAudience = template.defaultTone,
            customImageUri = "android.resource://${context.packageName}/drawable/$coverRes",
            generatedHook = template.retentionHook,
            generatedProblem = template.plotSetup,
            generatedSolution = template.sceneStructure.joinToString("\n"),
            generatedOffer = template.mentalTrigger,
            generatedCta = "Comente se você quer a Parte 2 e compartilhe!",
            generatedFullScript = full,
            videoDuration = 30
        )
        _userMessage.value = "🚀 Nicho '${category.title}' carregado com sucesso no Estúdio!"
    }

    fun generateGenreAiScript(category: ScriptCategory, topicTitle: String) {
        viewModelScope.launch {
            _creationForm.value = _creationForm.value.copy(isGeneratingAi = true)
            val form = _creationForm.value
            val scriptResult = GeminiScriptService.generateGenreScript(
                category = category,
                topicTitle = topicTitle.ifBlank { form.productName },
                details = form.keyFeatures,
                tone = form.targetAudience
            )
            _creationForm.value = _creationForm.value.copy(
                isGeneratingAi = false,
                productName = scriptResult.title,
                generatedHook = scriptResult.hook,
                generatedProblem = scriptResult.problem,
                generatedSolution = scriptResult.solution,
                generatedOffer = scriptResult.offer,
                generatedCta = scriptResult.cta,
                generatedFullScript = scriptResult.fullScript,
                videoDuration = 30
            )
            _userMessage.value = "Roteiro viral para ${category.title} gerado com sucesso!"
        }
    }

    fun finalizeAndBuildVideo(onFinished: (Long) -> Unit) {
        viewModelScope.launch {
            val form = _creationForm.value
            val originalPrice = form.originalPriceStr.toDoubleOrNull() ?: 199.90
            val promoPrice = form.promoPriceStr.toDoubleOrNull() ?: 99.90

            // Track video generation started in Firebase Analytics
            AppAnalytics.trackVideoGenerationStarted(
                productTitle = form.productName.ifBlank { "Produto Físico Shopee/TikTok" },
                category = form.category,
                durationSec = form.videoDuration,
                platformCount = form.targetPlatforms.size
            )

            // 1. Create or ensure Product
            val product = ProductEntity(
                title = form.productName.ifBlank { "Produto Físico Shopee/TikTok" },
                category = form.category,
                description = form.description.ifBlank { "Produto em destaque com super oferta." },
                originalPrice = originalPrice,
                promoPrice = promoPrice,
                couponCode = form.couponCode,
                keyFeatures = form.keyFeatures,
                targetAudience = form.targetAudience,
                imageDrawableRes = "showcase_smartwatch_1790132114921",
                customImageUri = form.customImageUri,
                productLink = form.affiliateLink
            )
            val productId = repository.insertProduct(product)

            // 2. Generate local assets on disk & create Video Project
            val draftTitle = "${product.title} - 9:16 Viral"
            val fullScript = form.generatedFullScript.ifBlank { "Olha só esse achadinho imperdível com desconto agora!" }
            val platformsCsv = form.targetPlatforms.joinToString(",") { it.name }
            val now = System.currentTimeMillis()
            val tempAssetId = now % 100000

            val assetBundle = LocalAssetManager.generateLocalAssetsForProject(
                context = getApplication(),
                projectId = tempAssetId,
                draftName = draftTitle,
                scriptText = fullScript,
                durationSeconds = form.videoDuration
            )

            val newProject = VideoProjectEntity(
                title = draftTitle,
                productId = productId,
                avatarId = form.selectedAvatarId,
                voiceId = form.selectedVoiceId,
                avatarStyle = form.avatarStyle,
                hookScript = form.generatedHook.ifBlank { "Olha só esse achadinho imperdível!" },
                problemScript = form.generatedProblem.ifBlank { "Você não aguenta mais pagar caro em produtos que não duram." },
                solutionScript = form.generatedSolution.ifBlank { "Essa solução resolve seu problema em instantes." },
                offerScript = form.generatedOffer.ifBlank { "Com super desconto e cupom ativo agora!" },
                ctaScript = form.generatedCta.ifBlank { "Clica no link do carrinho e garanta o seu!" },
                fullScriptText = fullScript,
                targetPlatformsCsv = platformsCsv,
                durationSeconds = form.videoDuration,
                backgroundMusic = form.backgroundMusic,
                status = VideoStatus.READY,
                coverImageRes = "showcase_smartwatch_1790132114921",
                videoDraftName = draftTitle,
                creationTimestamp = now,
                videoFilePath = assetBundle.videoFilePath,
                thumbnailFilePath = assetBundle.thumbnailFilePath,
                audioFilePath = assetBundle.audioFilePath,
                generatedAssetPaths = assetBundle.allAssetPaths,
                fileSizeBytes = assetBundle.totalSizeBytes,
                customImageUri = form.customImageUri,
                affiliateLink = form.affiliateLink
            )
            val newVideoId = repository.insertVideoProject(newProject)

            // Track video generation completed in Firebase Analytics
            AppAnalytics.trackVideoGenerationCompleted(
                videoProjectId = newVideoId,
                title = draftTitle,
                platforms = form.targetPlatforms.map { it.name },
                durationSec = form.videoDuration,
                avatarId = form.selectedAvatarId,
                voiceId = form.selectedVoiceId,
                fileSizeBytes = assetBundle.totalSizeBytes
            )

            // Save dedicated ProjectMetadataEntity in Room
            val metadataEntity = ProjectMetadataEntity(
                videoDraftName = draftTitle,
                creationTimestamp = now,
                lastModifiedTimestamp = now,
                videoFilePath = assetBundle.videoFilePath,
                thumbnailFilePath = assetBundle.thumbnailFilePath,
                audioFilePath = assetBundle.audioFilePath,
                generatedAssetPaths = assetBundle.allAssetPaths,
                durationSeconds = form.videoDuration,
                aspectRatio = "9:16",
                resolution = "1080x1920",
                fileSizeBytes = assetBundle.totalSizeBytes,
                status = "READY",
                notes = "Vídeo gerado automaticamente com ${form.targetPlatforms.size} plataformas configuradas."
            )
            repository.insertProjectMetadata(metadataEntity)

            // 3. Automatically schedule posts for connected platforms
            var offset = 0L
            form.targetPlatforms.forEach { platform ->
                val account = repository.getAccountByPlatform(platform)
                val socialLink = account?.profileLink.orEmpty()
                val linkSuffix = if (socialLink.isNotBlank()) " | Canal Oficial: $socialLink" else ""
                val caption = when (platform) {
                    SocialPlatform.SHOPEE -> "🔥 SUPER ACHADINHO SHOPEE: ${product.title} de R$ ${String.format("%.2f", originalPrice)} por R$ ${String.format("%.2f", promoPrice)}! Use o cupom ${product.couponCode}! 🛒$linkSuffix"
                    SocialPlatform.TIKTOK -> "Você precisa ver isso antes de acabar! 😱 ${product.title} com desconto exclusivo no TikTok Shop!$linkSuffix #achadinhos #review"
                    SocialPlatform.INSTAGRAM -> "Dica top para economizar hoje! ✨ ${product.title}! Comenta 'QUERO' para receber o cupom VIP!$linkSuffix 🚀"
                    SocialPlatform.FACEBOOK -> "Oferta especial por tempo limitado: ${product.title} com frete grátis! Clique e confira.$linkSuffix"
                    SocialPlatform.YOUTUBE -> "Incrível: ${product.title} em super promoção! Link com desconto nos comentários e na descrição!$linkSuffix ⚡"
                    SocialPlatform.KWAI -> "Olha esse achadinho: ${product.title} com preço imperdível! Corre pro link antes que acabe!$linkSuffix 🔥"
                }
                val hashtags = "#shopee #tiktokshop #achadinhos #reels #viral #promocao"
                val sched = PostScheduleEntity(
                    videoProjectId = newVideoId,
                    platform = platform,
                    caption = caption,
                    hashtags = hashtags,
                    scheduledTimeEpoch = now + offset,
                    status = PostStatus.QUEUED,
                    logs = if (socialLink.isNotBlank()) "Agendado e vinculado ao canal oficial ($socialLink)" else "Agendado automaticamente para postagem na ${platform.displayName}"
                )
                repository.insertSchedule(sched)
                offset += 3600000L // 1 hour spaced
            }

            // Select new video for player
            val created = repository.getVideoProject(newVideoId)
            if (created != null) {
                _currentPlayingVideo.value = created
            }

            _userMessage.value = "Vídeo 9:16 gerado e adicionado à fila de postagem automática!"
            onFinished(newVideoId)
        }
    }

    // --- Voice Cloning Handlers ---
    private var lastRecordedAudioFile: java.io.File? = null
    val micAmplitude: StateFlow<Float> = audioEngine.micAmplitude

    fun startVoiceRecording(context: Context? = null) {
        _isRecordingVoice.value = true
        _recordingSeconds.value = 0
        recordingJob?.cancel()

        // Track voice clone recording started in Firebase Analytics
        AppAnalytics.trackVoiceCloneRecordingStarted()

        val appCtx = context ?: getApplication<Application>()
        try {
            val audioFile = java.io.File(appCtx.cacheDir, "voice_clone_${System.currentTimeMillis()}.wav")
            lastRecordedAudioFile = audioFile
            audioEngine.startRecording(audioFile)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error initiating voice recording", e)
        }

        recordingJob = viewModelScope.launch {
            while (_recordingSeconds.value < 10 && _isRecordingVoice.value) {
                delay(1000)
                if (_isRecordingVoice.value) {
                    _recordingSeconds.value += 1
                }
            }
            if (_isRecordingVoice.value) {
                finishVoiceRecording()
            }
        }
    }

    fun finishVoiceRecording(customVoiceName: String = "Minha Voz Clonada") {
        recordingJob?.cancel()
        _isRecordingVoice.value = false
        val recordedFile = audioEngine.stopRecording() ?: lastRecordedAudioFile
        val duration = _recordingSeconds.value.coerceAtLeast(3)
        viewModelScope.launch {
            _userMessage.value = "Calibrando perfil neural e modelo acústico..."
            val audioPath = if (recordedFile != null && recordedFile.exists()) recordedFile.absolutePath else ""
            val cloned = repository.cloneVoiceFromSample(
                name = customVoiceName.ifBlank { "Minha Voz Clonada" },
                sampleText = "Fala pessoal! Hoje eu trouxe esse super achadinho que testei a semana inteira. O custo-benefício é impressionante e vale cada centavo!",
                sampleDurationSec = duration,
                emotion = VoiceEmotion.ENTHUSIASTIC,
                sampleAudioPath = audioPath
            )
            // Auto-select this newly cloned voice in creation wizard
            _creationForm.value = _creationForm.value.copy(selectedVoiceId = cloned.id)

            // Track voice clone completed in Firebase Analytics
            AppAnalytics.trackVoiceCloneCompleted(
                voiceName = cloned.name,
                sampleDurationSec = duration,
                emotion = cloned.emotion.name,
                audioSize = recordedFile?.length() ?: 0L
            )

            _userMessage.value = "✅ Voz '${cloned.name}' clonada e selecionada com sucesso!"

            // Immediate audio playback so user hears the calibrated clone
            playVoice(cloned)
        }
    }

    fun playVoice(voice: VoiceEntity) {
        // Track voice playback test in Firebase Analytics
        AppAnalytics.trackVoicePlayback(voice.name, voice.isClonedVoice)

        audioEngine.stop()
        if (voice.sampleRecordingUri.isNotBlank()) {
            val file = java.io.File(voice.sampleRecordingUri)
            if (file.exists() && file.length() > 44L) {
                val played = audioEngine.playAudioFile(voice.sampleRecordingUri)
                if (played) return
            }
        }
        audioEngine.speak(voice.samplePhrase, voice.pitch, voice.speed)
    }

    fun deleteVoice(voice: VoiceEntity) {
        viewModelScope.launch {
            repository.deleteVoice(voice)
            if (_creationForm.value.selectedVoiceId == voice.id) {
                val fallbackVoice = voices.value.firstOrNull { it.id != voice.id }
                if (fallbackVoice != null) {
                    _creationForm.value = _creationForm.value.copy(selectedVoiceId = fallbackVoice.id)
                }
            }
            _userMessage.value = "Voz '${voice.name}' removida."
        }
    }

    // --- Automatic Publishing Actions ---
    fun triggerImmediatePublish(schedule: PostScheduleEntity, context: Context) {
        viewModelScope.launch {
            // Update to posting
            repository.updateSchedule(schedule.copy(status = PostStatus.POSTING))
            _userMessage.value = "Conectando à API da ${schedule.platform.displayName}..."

            val project = repository.getVideoProject(schedule.videoProjectId)
            val account = repository.getAccountByPlatform(schedule.platform)
            val profileLink = account?.profileLink.orEmpty()

            val result = SocialPublisher.executeDirectPublish(
                platform = schedule.platform,
                caption = schedule.caption,
                hashtags = schedule.hashtags,
                productTitle = project?.title ?: "Produto Físico",
                videoTitle = project?.title ?: "Vídeo 9:16",
                socialProfileLink = profileLink
            )

            // Track direct publishing in Firebase Analytics
            AppAnalytics.trackSocialPublishDirect(
                platform = schedule.platform.name,
                scheduleId = schedule.id,
                productTitle = project?.title ?: "Produto Físico",
                hasCustomProfileLink = profileLink.isNotBlank(),
                success = result.success
            )

            if (result.success) {
                repository.updateSchedule(
                    schedule.copy(
                        status = PostStatus.PUBLISHED,
                        externalPostId = result.externalId,
                        publishedUrl = result.liveUrl,
                        logs = result.logMessage
                    )
                )
                _userMessage.value = "✅ Publicado com sucesso no ${schedule.platform.displayName}! Link: ${result.liveUrl}"
            } else {
                repository.updateSchedule(
                    schedule.copy(
                        status = PostStatus.FAILED,
                        errorMessage = "Falha temporária de rede"
                    )
                )
                _userMessage.value = "Erro ao publicar na ${schedule.platform.displayName}"
            }
        }
    }

    fun shareToNativeApp(schedule: PostScheduleEntity, context: Context) {
        viewModelScope.launch {
            val account = repository.getAccountByPlatform(schedule.platform)
            val profileLink = account?.profileLink.orEmpty()

            // Track native share in Firebase Analytics
            AppAnalytics.trackSocialShareNative(
                platform = schedule.platform.name,
                scheduleId = schedule.id,
                captionLength = schedule.caption.length,
                hasProfileLink = profileLink.isNotBlank()
            )

            SocialPublisher.shareToDeviceApp(
                context = context,
                platform = schedule.platform,
                caption = schedule.caption,
                hashtags = schedule.hashtags,
                socialProfileLink = profileLink
            )
        }
    }

    fun toggleSocialAccountAutoPost(account: SocialAccountEntity) {
        viewModelScope.launch {
            val updated = account.copy(autoPublishEnabled = !account.autoPublishEnabled)
            repository.updateSocialAccount(updated)
            _userMessage.value = if (updated.autoPublishEnabled) {
                "Auto-post ativado para ${account.platform.displayName}"
            } else {
                "Auto-post pausado para ${account.platform.displayName}"
            }
        }
    }

    // --- Video Editor Actions ---

    fun loadProjectIntoEditorById(projectId: Long) {
        val proj = videoProjects.value.find { it.id == projectId }
            ?: projectMetadataList.value.find { it.id == projectId }?.let { meta ->
                videoProjects.value.find { it.title == meta.videoDraftName }
            }
        if (proj != null) {
            loadProjectIntoEditor(proj)
        } else {
            videoProjects.value.firstOrNull()?.let { loadProjectIntoEditor(it) }
        }
    }

    fun loadProjectIntoEditor(video: VideoProjectEntity) {
        _editorVideoProject.value = video

        val coverRes = video.coverImageRes.ifBlank { "showcase_smartwatch_1790132114921" }

        // Set up modular video clips representing 9:16 viral segments
        _editorClips.value = listOf(
            VideoClip(
                title = "1. Gancho Visual (Hook)",
                drawableRes = coverRes,
                startTrimSeconds = 0f,
                endTrimSeconds = 4f,
                originalDurationSeconds = 6f
            ),
            VideoClip(
                title = "2. Apresentação do Produto",
                drawableRes = coverRes,
                startTrimSeconds = 0f,
                endTrimSeconds = 8f,
                originalDurationSeconds = 10f
            ),
            VideoClip(
                title = "3. Oferta & Desconto Especial",
                drawableRes = coverRes,
                startTrimSeconds = 0f,
                endTrimSeconds = 8f,
                originalDurationSeconds = 10f
            ),
            VideoClip(
                title = "4. Chamada para Ação (CTA)",
                drawableRes = coverRes,
                startTrimSeconds = 0f,
                endTrimSeconds = 6f,
                originalDurationSeconds = 8f
            )
        )
        _selectedClipId.value = _editorClips.value.firstOrNull()?.id

        // Set up initial customizable text overlays with viral styles & colors
        _editorTextOverlays.value = listOf(
            TextOverlay(
                text = video.hookScript.ifBlank { "🔥 OLHA ESSE ACHADINHO!" },
                startTimeSeconds = 0f,
                endTimeSeconds = 4f,
                fontStyle = FontStyleChoice.VIRAL_IMPACT,
                textColorHex = 0xFFFFE600, // TikTok Neon Yellow
                bgStyle = TextBgStyle.SOLID_BOX,
                fontSizeSp = 18,
                positionY = TextVerticalPosition.BOTTOM
            ),
            TextOverlay(
                text = "⚡ OFERTA RELÂMPAGO SHOPEE",
                startTimeSeconds = 4f,
                endTimeSeconds = 12f,
                fontStyle = FontStyleChoice.NEON_DISPLAY,
                textColorHex = 0xFFFF5722, // Shopee Coral
                bgStyle = TextBgStyle.PILL_BADGE,
                fontSizeSp = 15,
                positionY = TextVerticalPosition.TOP
            ),
            TextOverlay(
                text = video.offerScript.ifBlank { "💥 DE R$ 249 POR R$ 119 COM FRETE GRÁTIS!" },
                startTimeSeconds = 12f,
                endTimeSeconds = 20f,
                fontStyle = FontStyleChoice.VIRAL_IMPACT,
                textColorHex = 0xFF00F2FE, // Electric Cyan
                bgStyle = TextBgStyle.SOLID_BOX,
                fontSizeSp = 17,
                positionY = TextVerticalPosition.BOTTOM
            ),
            TextOverlay(
                text = video.ctaScript.ifBlank { "🛒 CLICA NO LINK ABAIXO E GARANTA O SEU!" },
                startTimeSeconds = 20f,
                endTimeSeconds = 26f,
                fontStyle = FontStyleChoice.MODERN_SANS,
                textColorHex = 0xFFFFFFFF, // Pure White
                bgStyle = TextBgStyle.OUTLINE,
                fontSizeSp = 16,
                positionY = TextVerticalPosition.BOTTOM
            )
        )
        _selectedTextId.value = _editorTextOverlays.value.firstOrNull()?.id

        // Select background audio matching project or preset
        val matchingAudio = AudioLibrary.PRESET_TRACKS.find {
            it.title.contains(video.backgroundMusic, ignoreCase = true)
        } ?: AudioLibrary.PRESET_TRACKS.first()
        _editorAudioTrack.value = matchingAudio

        _editorPlayhead.value = 0f
    }

    fun getTotalEditorDuration(): Float {
        return _editorClips.value.sumOf { it.trimmedDurationSeconds.toDouble() }.toFloat().coerceAtLeast(1f)
    }

    fun setEditorPlayhead(position: Float) {
        _editorPlayhead.value = position.coerceIn(0f, getTotalEditorDuration())
    }

    fun setPlaybackSpeed(speed: Float) {
        _previewPlaybackSpeed.value = speed
    }

    fun setPreviewPlatformGuide(platform: SocialPlatform?) {
        _previewPlatformGuide.value = platform
    }

    fun togglePreviewSafeZones() {
        _previewShowSafeZones.value = !_previewShowSafeZones.value
    }

    fun togglePreviewAvatarPiP() {
        _previewShowAvatarPiP.value = !_previewShowAvatarPiP.value
    }

    fun toggleFullscreenPreview(open: Boolean) {
        _isFullscreenPreview.value = open
    }

    fun stepEditorPlayhead(deltaSeconds: Float) {
        val total = getTotalEditorDuration()
        _editorPlayhead.value = (_editorPlayhead.value + deltaSeconds).coerceIn(0f, total)
    }

    fun jumpToNextCut() {
        val clips = _editorClips.value
        var accumulated = 0f
        val current = _editorPlayhead.value
        for (clip in clips) {
            accumulated += clip.trimmedDurationSeconds
            if (accumulated > current + 0.15f) {
                _editorPlayhead.value = accumulated.coerceAtMost(getTotalEditorDuration())
                return
            }
        }
    }

    fun jumpToPreviousCut() {
        val clips = _editorClips.value
        var accumulated = 0f
        val current = _editorPlayhead.value
        var targetCut = 0f
        for (clip in clips) {
            if (accumulated < current - 0.2f) {
                targetCut = accumulated
            }
            accumulated += clip.trimmedDurationSeconds
        }
        _editorPlayhead.value = targetCut
    }

    fun jumpToNextSubtitle() {
        val overlays = _editorTextOverlays.value.sortedBy { it.startTimeSeconds }
        val current = _editorPlayhead.value
        val next = overlays.firstOrNull { it.startTimeSeconds > current + 0.1f }
        if (next != null) {
            _editorPlayhead.value = next.startTimeSeconds
        }
    }

    fun jumpToPreviousSubtitle() {
        val overlays = _editorTextOverlays.value.sortedBy { it.startTimeSeconds }
        val current = _editorPlayhead.value
        val prev = overlays.lastOrNull { it.startTimeSeconds < current - 0.2f }
        if (prev != null) {
            _editorPlayhead.value = prev.startTimeSeconds
        } else {
            _editorPlayhead.value = 0f
        }
    }

    fun toggleEditorPlayback() {
        if (_isEditorPlaying.value) {
            _isEditorPlaying.value = false
            editorPlayJob?.cancel()
            audioEngine.stop()
        } else {
            _isEditorPlaying.value = true
            editorPlayJob?.cancel()
            val total = getTotalEditorDuration()

            // Play voice for the active text overlay at current playhead using active voice characteristics
            val currentVoice = voices.value.find { it.id == _creationForm.value.selectedVoiceId }
            val activePitch = currentVoice?.pitch ?: 1.0f
            val baseVoiceSpeed = currentVoice?.speed ?: 1.15f

            val activeText = _editorTextOverlays.value.find {
                _editorPlayhead.value >= it.startTimeSeconds && _editorPlayhead.value <= it.endTimeSeconds
            }
            if (activeText != null) {
                audioEngine.speak(activeText.text, pitch = activePitch, speed = baseVoiceSpeed * _previewPlaybackSpeed.value)
            }

            editorPlayJob = viewModelScope.launch {
                var lastSpokenTextId: String? = activeText?.id
                while (_isEditorPlaying.value) {
                    val speed = _previewPlaybackSpeed.value.coerceIn(0.5f, 2.0f)
                    val delayMs = (100 / speed).toLong().coerceIn(30, 200)
                    delay(delayMs)
                    val step = 0.1f * speed
                    val next = _editorPlayhead.value + step

                    // Speak if new subtitle starts
                    val currentSub = _editorTextOverlays.value.find {
                        next >= it.startTimeSeconds && next <= it.endTimeSeconds
                    }
                    if (currentSub != null && currentSub.id != lastSpokenTextId) {
                        lastSpokenTextId = currentSub.id
                        audioEngine.speak(currentSub.text, pitch = activePitch, speed = baseVoiceSpeed * speed)
                    }

                    if (next >= total) {
                        _editorPlayhead.value = 0f
                        _isEditorPlaying.value = false
                        audioEngine.stop()
                        break
                    } else {
                        _editorPlayhead.value = next
                    }
                }
            }
        }
    }

    fun selectClipForEditing(clipId: String?) {
        _selectedClipId.value = clipId
    }

    fun updateClipTrim(clipId: String, startTrim: Float, endTrim: Float) {
        _editorClips.value = _editorClips.value.map { clip ->
            if (clip.id == clipId) {
                val validStart = startTrim.coerceIn(0f, clip.originalDurationSeconds - 0.5f)
                val validEnd = endTrim.coerceIn(validStart + 0.5f, clip.originalDurationSeconds)
                clip.copy(startTrimSeconds = validStart, endTrimSeconds = validEnd)
            } else {
                clip
            }
        }
        val maxDuration = getTotalEditorDuration()
        if (_editorPlayhead.value > maxDuration) {
            _editorPlayhead.value = maxDuration
        }
    }

    fun splitClipAtPlayhead() {
        val currentPlayhead = _editorPlayhead.value
        val clips = _editorClips.value.toMutableList()
        if (clips.isEmpty()) return

        var accumulated = 0f
        var targetIndex = -1
        var offsetInsideClip = 0f

        for (i in clips.indices) {
            val clipDuration = clips[i].trimmedDurationSeconds
            if (currentPlayhead >= accumulated && currentPlayhead <= accumulated + clipDuration) {
                targetIndex = i
                offsetInsideClip = (currentPlayhead - accumulated) * clips[i].speed
                break
            }
            accumulated += clipDuration
        }

        if (targetIndex != -1) {
            val targetClip = clips[targetIndex]
            val splitPoint = targetClip.startTrimSeconds + offsetInsideClip

            if (splitPoint - targetClip.startTrimSeconds >= 0.5f && targetClip.endTrimSeconds - splitPoint >= 0.5f) {
                val clipA = targetClip.copy(
                    title = "${targetClip.title} (A)",
                    endTrimSeconds = splitPoint
                )
                val clipB = targetClip.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    title = "${targetClip.title} (B)",
                    startTrimSeconds = splitPoint
                )
                clips.removeAt(targetIndex)
                clips.add(targetIndex, clipB)
                clips.add(targetIndex, clipA)

                _editorClips.value = clips
                _selectedClipId.value = clipB.id
                _userMessage.value = "Clipe dividido com sucesso em 2 partes no cursor!"
            } else {
                _userMessage.value = "Mova o cursor para uma posição com pelo menos 0.5s de distância do início/fim."
            }
        }
    }

    fun deleteClip(clipId: String) {
        if (_editorClips.value.size <= 1) {
            _userMessage.value = "O vídeo precisa ter pelo menos um clipe."
            return
        }
        _editorClips.value = _editorClips.value.filter { it.id != clipId }
        _selectedClipId.value = _editorClips.value.firstOrNull()?.id
        _userMessage.value = "Clipe removido da linha do tempo."
    }

    fun duplicateClip(clipId: String) {
        val clip = _editorClips.value.find { it.id == clipId } ?: return
        val duplicated = clip.copy(
            id = java.util.UUID.randomUUID().toString(),
            title = "${clip.title} (Cópia)"
        )
        val list = _editorClips.value.toMutableList()
        val index = list.indexOfFirst { it.id == clipId }
        if (index != -1) {
            list.add(index + 1, duplicated)
        } else {
            list.add(duplicated)
        }
        _editorClips.value = list
        _selectedClipId.value = duplicated.id
        _userMessage.value = "Clipe duplicado na linha do tempo."
    }

    fun addClip(title: String, duration: Float = 6f) {
        val coverRes = _editorVideoProject.value?.coverImageRes ?: "showcase_smartwatch_1790132114921"
        val newClip = VideoClip(
            title = title.ifBlank { "Novo Clipe 9:16" },
            drawableRes = coverRes,
            startTrimSeconds = 0f,
            endTrimSeconds = duration,
            originalDurationSeconds = duration
        )
        _editorClips.value = _editorClips.value + newClip
        _selectedClipId.value = newClip.id
        _userMessage.value = "Novo clipe adicionado à linha do tempo."
    }

    // --- Transition Effects ---

    fun updateClipTransition(clipId: String, transition: ClipTransition) {
        _editorClips.value = _editorClips.value.map { clip ->
            if (clip.id == clipId) {
                clip.copy(transitionToNext = transition)
            } else {
                clip
            }
        }
        _userMessage.value = "Transição '${transition.effect.label}' (${transition.durationSeconds}s) configurada!"
    }

    fun applyTransitionToAllClips(transition: ClipTransition) {
        _editorClips.value = _editorClips.value.map { clip ->
            clip.copy(transitionToNext = transition)
        }
        _userMessage.value = "Transição '${transition.effect.label}' aplicada a todas as cenas!"
    }

    fun setEditorExportQuality(quality: VideoExportQuality) {
        _editorExportQuality.value = quality
        _userMessage.value = "Qualidade de exportação: ${quality.label} (${quality.resolution})"
    }

    // --- Text Overlays (Subtitles & Stickers) ---


    fun selectTextOverlay(id: String?) {
        _selectedTextId.value = id
    }

    fun addTextOverlay(
        text: String,
        fontStyle: FontStyleChoice = FontStyleChoice.VIRAL_IMPACT,
        colorHex: Long = 0xFFFFE600,
        bgStyle: TextBgStyle = TextBgStyle.SOLID_BOX,
        position: TextVerticalPosition = TextVerticalPosition.BOTTOM,
        duration: Float = 4f
    ) {
        val start = _editorPlayhead.value
        val end = (start + duration).coerceAtMost(getTotalEditorDuration())
        val newOverlay = TextOverlay(
            text = text.ifBlank { "Novo Texto de Destaque" },
            startTimeSeconds = start,
            endTimeSeconds = end.coerceAtLeast(start + 1f),
            fontStyle = fontStyle,
            textColorHex = colorHex,
            bgStyle = bgStyle,
            fontSizeSp = 18,
            positionY = position
        )
        _editorTextOverlays.value = _editorTextOverlays.value + newOverlay
        _selectedTextId.value = newOverlay.id
        _userMessage.value = "Legenda/Texto adicionado no ponto ${String.format("%.1f", start)}s!"
    }

    fun updateTextOverlay(updated: TextOverlay) {
        _editorTextOverlays.value = _editorTextOverlays.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun deleteTextOverlay(id: String) {
        _editorTextOverlays.value = _editorTextOverlays.value.filter { it.id != id }
        if (_selectedTextId.value == id) {
            _selectedTextId.value = _editorTextOverlays.value.firstOrNull()?.id
        }
        _userMessage.value = "Legenda removida."
    }

    fun applyViralBadgeToEditor(badgeText: String, colorHex: Long = 0xFFFFE600) {
        val total = getTotalEditorDuration()
        val badgeOverlay = TextOverlay(
            text = badgeText,
            startTimeSeconds = 0f,
            endTimeSeconds = total,
            fontStyle = FontStyleChoice.VIRAL_IMPACT,
            textColorHex = colorHex,
            bgStyle = TextBgStyle.SOLID_BOX,
            fontSizeSp = 18,
            positionY = TextVerticalPosition.TOP
        )
        _editorTextOverlays.value = listOf(badgeOverlay) + _editorTextOverlays.value.filter { it.positionY != TextVerticalPosition.TOP }
        _selectedTextId.value = badgeOverlay.id
        _userMessage.value = "Selo de Retenção '$badgeText' aplicado ao vídeo todo!"
    }

    fun apply3sHookToEditor(hookText: String) {
        val hookOverlay = TextOverlay(
            text = hookText,
            startTimeSeconds = 0f,
            endTimeSeconds = 3.5f,
            fontStyle = FontStyleChoice.VIRAL_IMPACT,
            textColorHex = 0xFFFFE600,
            bgStyle = TextBgStyle.SOLID_BOX,
            fontSizeSp = 20,
            positionY = TextVerticalPosition.BOTTOM
        )
        _editorTextOverlays.value = listOf(hookOverlay) + _editorTextOverlays.value.filter { it.startTimeSeconds > 3.5f }
        _selectedTextId.value = hookOverlay.id
        _userMessage.value = "Gancho magnético de 3 segundos aplicado na abertura!"
    }

    // --- Background Audio & Music ---

    fun selectBackgroundAudio(track: BackgroundAudioTrack) {
        _editorAudioTrack.value = track
        _userMessage.value = "Música de fundo alterada para: ${track.title}"
    }

    fun updateAudioVolumes(bgVolume: Float, voiceVolume: Float) {
        _editorAudioTrack.value = _editorAudioTrack.value.copy(
            volume = bgVolume.coerceIn(0f, 1f),
            voiceVolume = voiceVolume.coerceIn(0f, 1f)
        )
    }

    fun saveEditorToProject(quality: VideoExportQuality = _editorExportQuality.value) {
        val currentProject = _editorVideoProject.value ?: return
        viewModelScope.launch {
            val totalSeconds = getTotalEditorDuration().toInt()
            val textList = _editorTextOverlays.value.sortedBy { it.startTimeSeconds }
            val fullScript = textList.joinToString(" ") { it.text }.ifBlank { currentProject.fullScriptText }
            val now = System.currentTimeMillis()

            // Generate/update physical local assets on device with selected export quality (1080p vs 4K)
            val assetBundle = LocalAssetManager.generateLocalAssetsForProject(
                context = getApplication(),
                projectId = currentProject.id,
                draftName = currentProject.title,
                scriptText = fullScript,
                durationSeconds = totalSeconds,
                resolution = quality.resolution,
                bitrateMbps = quality.bitrateMbps
            )

            val updatedProject = currentProject.copy(
                durationSeconds = totalSeconds,
                backgroundMusic = _editorAudioTrack.value.title,
                fullScriptText = fullScript,
                videoDraftName = currentProject.title,
                videoFilePath = assetBundle.videoFilePath,
                thumbnailFilePath = assetBundle.thumbnailFilePath,
                audioFilePath = assetBundle.audioFilePath,
                generatedAssetPaths = assetBundle.allAssetPaths,
                fileSizeBytes = assetBundle.totalSizeBytes
            )

            repository.updateVideoProject(updatedProject)

            // Save/update project metadata in Room
            val metadata = ProjectMetadataEntity(
                videoDraftName = currentProject.title,
                creationTimestamp = currentProject.creationTimestamp,
                lastModifiedTimestamp = now,
                videoFilePath = assetBundle.videoFilePath,
                thumbnailFilePath = assetBundle.thumbnailFilePath,
                audioFilePath = assetBundle.audioFilePath,
                generatedAssetPaths = assetBundle.allAssetPaths,
                durationSeconds = totalSeconds,
                aspectRatio = currentProject.aspectRatio,
                resolution = quality.resolution,
                fileSizeBytes = assetBundle.totalSizeBytes,
                status = "DRAFT",
                notes = "Exportado em ${quality.label} (${quality.resolution}) com ${textList.size} legendas, transições ativas e faixa '${_editorAudioTrack.value.title}'."
            )
            repository.insertProjectMetadata(metadata)

            // Track editor export in Firebase Analytics
            AppAnalytics.trackVideoEditorExport(
                videoProjectId = currentProject.id,
                title = currentProject.title,
                clipCount = _editorClips.value.size,
                textOverlayCount = textList.size,
                audioTrackName = _editorAudioTrack.value.title
            )

            _editorVideoProject.value = updatedProject
            _currentPlayingVideo.value = updatedProject
            _userMessage.value = "Vídeo renderizado e salvo em ${quality.label} (${quality.resolution}) com sucesso!"
        }
    }


    fun deleteProjectMetadata(metadata: ProjectMetadataEntity) {
        viewModelScope.launch {
            repository.deleteProjectMetadata(metadata)
            LocalAssetManager.deleteProjectAssets(getApplication(), metadata.id)
            _userMessage.value = "Rascunho '${metadata.videoDraftName}' removido do banco Room!"
        }
    }

    // --- Assistente de Roteiro Gemini ---

    fun selectScriptCategory(category: ScriptCategory) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            activeCategory = category,
            selectedTemplateId = null
        )
    }

    fun applyScriptTemplate(template: ScriptTemplatePreset) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            activeCategory = template.category,
            selectedTemplateId = template.id,
            productNameInput = template.title,
            itemDescriptionInput = "${template.retentionHook}\n\nEnredo: ${template.plotSetup}\n\nEstrutura:\n${template.sceneStructure.joinToString("\n")}",
            selectedTrigger = template.mentalTrigger,
            selectedTone = template.defaultTone
        )
        _userMessage.value = "Template '${template.title}' carregado! Clique em 'Gerar Roteiro'."
    }

    fun updateScriptAssistantDescription(description: String) {

        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            itemDescriptionInput = description
        )
    }

    fun updateScriptAssistantProductName(name: String) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            productNameInput = name
        )
    }

    fun updateScriptAssistantTrigger(trigger: String) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            selectedTrigger = trigger
        )
    }

    fun updateScriptAssistantDuration(duration: Int) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            selectedDuration = duration
        )
    }

    fun updateScriptAssistantTone(tone: String) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            selectedTone = tone
        )
    }

    fun selectScriptVariation(index: Int) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            selectedVariationIndex = index
        )
    }

    fun initScriptAssistantForProduct(name: String, description: String) {
        _scriptAssistantState.value = _scriptAssistantState.value.copy(
            productNameInput = name.ifBlank { _scriptAssistantState.value.productNameInput },
            itemDescriptionInput = description.ifBlank { _scriptAssistantState.value.itemDescriptionInput }
        )
    }

    fun generateScriptVariationsWithGemini() {
        val state = _scriptAssistantState.value
        val desc = state.itemDescriptionInput.trim()
        if (desc.isBlank()) {
            _userMessage.value = "Por favor, insira a descrição ou características do item."
            return
        }

        viewModelScope.launch {
            _scriptAssistantState.value = _scriptAssistantState.value.copy(
                isGenerating = true,
                errorMessage = null,
                generationProgressStep = "Conectando ao modelo Gemini 3.5 Flash..."
            )

            delay(300)
            _scriptAssistantState.value = _scriptAssistantState.value.copy(
                generationProgressStep = "Analisando pontos fortes e apelo de venda do item..."
            )

            delay(350)
            _scriptAssistantState.value = _scriptAssistantState.value.copy(
                generationProgressStep = "Criando ganchos virais e gatilhos de ${state.selectedTrigger}..."
            )

            try {
                val results = GeminiScriptService.generatePersuasiveSalesScriptVariations(
                    productDescription = desc,
                    productName = state.productNameInput,
                    mentalTrigger = state.selectedTrigger,
                    durationSeconds = state.selectedDuration,
                    tone = state.selectedTone
                )

                _scriptAssistantState.value = _scriptAssistantState.value.copy(
                    isGenerating = false,
                    variations = results,
                    selectedVariationIndex = 0,
                    generationProgressStep = ""
                )
                _userMessage.value = "Roteiros persuasivos gerados com sucesso pelo Gemini!"
            } catch (e: Exception) {
                _scriptAssistantState.value = _scriptAssistantState.value.copy(
                    isGenerating = false,
                    errorMessage = e.message ?: "Falha ao gerar roteiro",
                    generationProgressStep = ""
                )
                _userMessage.value = "Erro na geração: ${e.message}"
            }
        }
    }

    fun applySelectedScriptToWizard() {
        val state = _scriptAssistantState.value
        val currentVar = state.variations.getOrNull(state.selectedVariationIndex) ?: return

        _creationForm.value = _creationForm.value.copy(
            productName = state.productNameInput.ifBlank { _creationForm.value.productName },
            description = state.itemDescriptionInput.ifBlank { _creationForm.value.description },
            generatedHook = currentVar.hook,
            generatedProblem = currentVar.problem,
            generatedSolution = currentVar.solution,
            generatedOffer = currentVar.offer,
            generatedCta = currentVar.cta,
            generatedFullScript = currentVar.fullScript,
            videoDuration = currentVar.estimatedDurationSeconds
        )
        _userMessage.value = "Roteiro aplicado ao Criador de Vídeos!"
    }

    fun applySelectedScriptToEditor() {
        val state = _scriptAssistantState.value
        val currentVar = state.variations.getOrNull(state.selectedVariationIndex) ?: return

        val totalDuration = currentVar.estimatedDurationSeconds.toFloat()
        val quarter = totalDuration / 4f

        val updatedOverlays = listOf(
            TextOverlay(
                text = currentVar.hook,
                startTimeSeconds = 0f,
                endTimeSeconds = quarter,
                positionY = TextVerticalPosition.TOP,
                fontStyle = FontStyleChoice.VIRAL_IMPACT,
                textColorHex = 0xFFFFD54FL,
                bgStyle = TextBgStyle.SOLID_BOX
            ),
            TextOverlay(
                text = currentVar.problem,
                startTimeSeconds = quarter,
                endTimeSeconds = quarter * 2f,
                positionY = TextVerticalPosition.BOTTOM,
                fontStyle = FontStyleChoice.DEFAULT_BOLD,
                textColorHex = 0xFFFFFFFFL,
                bgStyle = TextBgStyle.SOLID_BOX
            ),
            TextOverlay(
                text = currentVar.solution,
                startTimeSeconds = quarter * 2f,
                endTimeSeconds = quarter * 3f,
                positionY = TextVerticalPosition.CENTER,
                fontStyle = FontStyleChoice.NEON_DISPLAY,
                textColorHex = 0xFF00E5FFL,
                bgStyle = TextBgStyle.OUTLINE
            ),
            TextOverlay(
                text = "${currentVar.offer} ${currentVar.cta}",
                startTimeSeconds = quarter * 3f,
                endTimeSeconds = totalDuration,
                positionY = TextVerticalPosition.BOTTOM,
                fontStyle = FontStyleChoice.VIRAL_IMPACT,
                textColorHex = 0xFFFF5722L,
                bgStyle = TextBgStyle.PILL_BADGE
            )
        )

        _editorTextOverlays.value = updatedOverlays
        _selectedTextId.value = updatedOverlays.firstOrNull()?.id

        val project = _editorVideoProject.value
        if (project != null) {
            viewModelScope.launch {
                val updated = project.copy(
                    hookScript = currentVar.hook,
                    problemScript = currentVar.problem,
                    solutionScript = currentVar.solution,
                    offerScript = currentVar.offer,
                    ctaScript = currentVar.cta,
                    fullScriptText = currentVar.fullScript
                )
                repository.updateVideoProject(updated)
                _editorVideoProject.value = updated
                _currentPlayingVideo.value = updated
            }
        }

        _userMessage.value = "Roteiro aplicado nas legendas e faixas do Editor!"
    }

    fun previewScriptSpeech(scriptText: String) {
        if (audioEngine.isPlaying.value) {
            audioEngine.stop()
        } else {
            audioEngine.speak(scriptText)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
        editorPlayJob?.cancel()
    }
}
