package com.example.data.model

enum class SocialPlatform(
    val displayName: String,
    val brandColorHex: Long,
    val iconDesc: String,
    val maxVideoSeconds: Int,
    val recommendedRatio: String
) {
    SHOPEE("Shopee Vídeo", 0xFFEE4D2D, "Carrinho Laranja", 60, "9:16"),
    TIKTOK("TikTok Shop", 0xFF00F2FE, "TikTok Viral", 90, "9:16"),
    INSTAGRAM("Instagram Reels", 0xFFE1306C, "Reels IG", 90, "9:16"),
    FACEBOOK("Facebook Reels", 0xFF1877F2, "Reels FB", 60, "9:16"),
    YOUTUBE("YouTube Shorts", 0xFFFF0000, "Shorts YT", 60, "9:16"),
    KWAI("Kwai Vídeo", 0xFFFF7700, "Kwai Vídeo", 60, "9:16")
}

enum class AvatarStyle(val label: String, val description: String) {
    PIP_CIRCLE("Burbulha PIP", "Avatar circular no canto com anel dinâmico"),
    SPLIT_SCREEN("Tela Dividida", "Avatar em cima e vídeo do produto embaixo"),
    FULL_PRESENTER("Apresentador Cheio", "Apresentador em foco com produto flutuante"),
    BOTTOM_CORNER("Canto Inferior", "Avatar estilizado apresentando a oferta")
}

enum class VoiceEmotion(val label: String, val description: String) {
    ENTHUSIASTIC("Super Empolgado", "Ideal para promoções relâmpago e queima de estoque"),
    NATURAL("Conversa Amigável", "Parece uma recomendação sincera de amigo"),
    CONFIDENT("Autoridade & Seguro", "Ideal para eletrônicos e produtos premium"),
    PERSUASIVE("Gatilho de Urgência", "Foco em escassez e cupons limitados"),
    STORYTELLING("Storytelling Suave", "Para moda, beleza e autocuidado")
}

enum class VideoStatus(val label: String) {
    DRAFT("Rascunho"),
    GENERATING("Gerando IA..."),
    READY("Pronto"),
    SCHEDULED("Agendado"),
    PUBLISHED("Publicado")
}

enum class PostStatus(val label: String) {
    IDLE("Pendente"),
    QUEUED("Na Fila"),
    POSTING("Publicando..."),
    PUBLISHED("Postado"),
    FAILED("Erro")
}
