package com.example.data.audio

import com.example.data.model.BackgroundAudioTrack

object AudioLibrary {

    val PRESET_TRACKS = listOf(
        BackgroundAudioTrack(
            id = "preset_phonk",
            title = "Phonk E-commerce Viral",
            genre = "Batida Acelerada (135 BPM)",
            durationSeconds = 30f,
            volume = 0.65f
        ),
        BackgroundAudioTrack(
            id = "preset_bike_phonk_244",
            title = "Phonk Brasileiro Grau 244 (Bass Boosted)",
            genre = "Bicicletas & Grau de Rua (145 BPM)",
            durationSeconds = 35f,
            volume = 0.75f
        ),
        BackgroundAudioTrack(
            id = "preset_drama_suspense",
            title = "Tensão & Drama de Mini Novela",
            genre = "Cliffhanger & Violinos (110 BPM)",
            durationSeconds = 40f,
            volume = 0.60f
        ),
        BackgroundAudioTrack(
            id = "preset_cinema_trailer",
            title = "Trailer Épico de Cinema & Séries",
            genre = "Cinematic Hits & Sub-Grave (130 BPM)",
            durationSeconds = 45f,
            volume = 0.70f
        ),
        BackgroundAudioTrack(
            id = "preset_cartoon_playful",
            title = "Desenho Animado Lúdico & Boing",
            genre = "Animação & Xilofone Cômico (125 BPM)",
            durationSeconds = 30f,
            volume = 0.65f
        ),
        BackgroundAudioTrack(
            id = "preset_story_dark_ambient",
            title = "Estórias Reais & Mistério Noturno",
            genre = "Dark Ambient & Tique-Taque (75 BPM)",
            durationSeconds = 50f,
            volume = 0.50f
        ),
        BackgroundAudioTrack(
            id = "preset_lofi",
            title = "Lo-Fi Suave Vendas",
            genre = "Chill & Aconchegante (90 BPM)",
            durationSeconds = 45f,
            volume = 0.55f
        ),
        BackgroundAudioTrack(
            id = "preset_upbeat",
            title = "Pop Comercial Upbeat",
            genre = "Moda & Beleza (128 BPM)",
            durationSeconds = 30f,
            volume = 0.60f
        ),
        BackgroundAudioTrack(
            id = "preset_tech_trap",
            title = "Tech Review Bass Booster",
            genre = "Grave Pesado (140 BPM)",
            durationSeconds = 35f,
            volume = 0.70f
        ),
        BackgroundAudioTrack(
            id = "preset_tictac",
            title = "Shopee Achadinhos Tique-Taque",
            genre = "Urgência & Desconto (120 BPM)",
            durationSeconds = 25f,
            volume = 0.60f
        ),
        BackgroundAudioTrack(
            id = "preset_acoustic",
            title = "Acústico Minimalista",
            genre = "Orgânico & Artesanal (95 BPM)",
            durationSeconds = 30f,
            volume = 0.50f
        )
    )

    fun createFromUserFile(fileName: String, uriString: String): BackgroundAudioTrack {
        return BackgroundAudioTrack(
            id = "user_audio_${System.currentTimeMillis()}",
            title = fileName.ifBlank { "Meu Áudio de Fundo" },
            genre = "Áudio do Usuário (Upload)",
            durationSeconds = 30f,
            isUserUploaded = true,
            userFileUri = uriString,
            volume = 0.70f
        )
    }
}
