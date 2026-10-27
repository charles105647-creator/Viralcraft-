package com.example.data.model

import java.util.UUID

enum class FontStyleChoice(val label: String, val previewName: String) {
    DEFAULT_BOLD("Padrão Bold", "Roboto Bold"),
    MODERN_SANS("Modern Sans", "Montserrat"),
    VIRAL_IMPACT("Impact Viral", "Impact"),
    ELEGANT_SERIF("Elegante Serif", "Playfair"),
    NEON_DISPLAY("Neon Cyber", "Orbitron")
}

enum class TextBgStyle(val label: String) {
    SOLID_BOX("Caixa Sólida"),
    PILL_BADGE("Emblema Arredondado"),
    OUTLINE("Contorno Neon"),
    NONE("Sem Fundo (Sombra)")
}

enum class TextVerticalPosition(val label: String) {
    TOP("Topo"),
    CENTER("Centro"),
    BOTTOM("Rodapé")
}

enum class TransitionEffect(
    val label: String,
    val description: String,
    val iconName: String
) {
    NONE("Corte Seco", "Sem transição entre cenas", "none"),
    CROSS_FADE("Cross Fade", "Dissolvência suave e cinematográfica", "fade"),
    SLIDE_LEFT("Slide Esquerda", "Deslize rápido para a esquerda", "slide_left"),
    SLIDE_RIGHT("Slide Direita", "Deslize dinâmico para a direita", "slide_right"),
    ZOOM_IN("Punch Zoom", "Zoom veloz de impacto para alta retenção", "zoom_in"),
    ZOOM_OUT("Zoom Out", "Afastamento dramático de câmera", "zoom_out"),
    WIPE_UP("Wipe Vertical", "Transição vertical estilo Stories/Reels", "wipe_up"),
    FLASH_WHITE("Flash Branco", "Clarão rápido estilizado de impacto", "flash_white")
}

data class ClipTransition(
    val effect: TransitionEffect = TransitionEffect.CROSS_FADE,
    val durationSeconds: Float = 0.5f
)

enum class VideoExportQuality(
    val label: String,
    val resolution: String,
    val width: Int,
    val height: Int,
    val fps: Int,
    val bitrateMbps: Int,
    val estimatedMbPerMin: Int,
    val description: String
) {
    FULL_HD_1080P(
        label = "1080p Full HD",
        resolution = "1080x1920",
        width = 1080,
        height = 1920,
        fps = 60,
        bitrateMbps = 14,
        estimatedMbPerMin = 105,
        description = "Recomendado para TikTok, Reels e Shopee Vídeo. Processamento veloz e alta compatibilidade."
    ),
    ULTRA_HD_4K(
        label = "4K Ultra HD",
        resolution = "2160x3840",
        width = 2160,
        height = 3840,
        fps = 60,
        bitrateMbps = 38,
        estimatedMbPerMin = 285,
        description = "Qualidade cinematográfica máxima, nitidez impecável para telas grandes e monetização premium."
    )
}

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val drawableRes: String = "showcase_smartwatch_1790132114921",
    val startTrimSeconds: Float = 0f,
    val endTrimSeconds: Float = 10f,
    val originalDurationSeconds: Float = 10f,
    val speed: Float = 1.0f,
    val transitionToNext: ClipTransition = ClipTransition()
) {
    val trimmedDurationSeconds: Float
        get() = ((endTrimSeconds - startTrimSeconds).coerceAtLeast(0.5f)) / speed
}

data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val startTimeSeconds: Float = 0f,
    val endTimeSeconds: Float = 5f,
    val fontStyle: FontStyleChoice = FontStyleChoice.VIRAL_IMPACT,
    val textColorHex: Long = 0xFFFFE600, // TikTok Viral Yellow default
    val bgStyle: TextBgStyle = TextBgStyle.SOLID_BOX,
    val fontSizeSp: Int = 18,
    val positionY: TextVerticalPosition = TextVerticalPosition.BOTTOM
)

data class BackgroundAudioTrack(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val genre: String,
    val durationSeconds: Float = 30f,
    val isUserUploaded: Boolean = false,
    val userFileUri: String? = null,
    val volume: Float = 0.65f, // 0 to 1
    val voiceVolume: Float = 1.0f
)

