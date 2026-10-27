package com.example.data.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Log entry representation for in-app Analytics visual monitoring and debugging
 */
data class AnalyticsLogEntry(
    val eventName: String,
    val category: String,
    val params: Map<String, Any>,
    val timestampFormatted: String,
    val timestampEpoch: Long = System.currentTimeMillis()
)

/**
 * Centralized Firebase Analytics service for ViralCraft AI.
 * Rastreia:
 * 1. Geração de Vídeo (Início, Conclusão, Duração, Plataformas, Segmentos)
 * 2. Compartilhamento e Publicação em Redes Sociais (Shopee, TikTok, Instagram, Facebook)
 * 3. Uso da Ferramenta de Clonagem de Voz (Gravação, Calibração, Testes de Reprodução)
 */
object AppAnalytics {
    private const val TAG = "AppAnalytics"

    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var isInitialized = false

    // Realtime state flow of recent events for user monitoring in UI
    private val _recentEvents = MutableStateFlow<List<AnalyticsLogEntry>>(emptyList())
    val recentEvents: StateFlow<List<AnalyticsLogEntry>> = _recentEvents.asStateFlow()

    fun init(context: Context) {
        if (isInitialized) return
        val appContext = context.applicationContext
        try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                // Initialize default FirebaseApp with fallback configuration if google-services.json is not present
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:187364080080:android:a89c4728f3214891")
                    .setProjectId("viralcraft-ai")
                    .setApiKey("AIzaSyB-analytics-prod-key")
                    .build()
                FirebaseApp.initializeApp(appContext, options)
                Log.i(TAG, "FirebaseApp inicializado com sucesso para Analytics")
            }
            firebaseAnalytics = FirebaseAnalytics.getInstance(appContext).apply {
                setAnalyticsCollectionEnabled(true)
            }
            isInitialized = true
            Log.i(TAG, "FirebaseAnalytics pronto e ativo")

            // Log session start
            logEvent(
                name = "app_analytics_initialized",
                category = "SISTEMA",
                params = mapOf(
                    "app_name" to "ViralCraft AI",
                    "status" to "active"
                )
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Aviso ao inicializar FirebaseAnalytics: ${e.message}")
            isInitialized = true
        }
    }

    private fun logEvent(name: String, category: String, params: Map<String, Any>) {
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val entry = AnalyticsLogEntry(
            eventName = name,
            category = category,
            params = params,
            timestampFormatted = timeStr
        )
        // Store last 40 events for UI inspection
        _recentEvents.value = (listOf(entry) + _recentEvents.value).take(40)
        Log.d(TAG, "⚡ [Firebase Analytics | $category] $name -> $params")

        try {
            val bundle = Bundle().apply {
                params.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Float -> putFloat(key, value)
                        is Boolean -> putBoolean(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
            firebaseAnalytics?.logEvent(name, bundle)
        } catch (e: Throwable) {
            Log.w(TAG, "Falha ao registrar evento $name no Firebase: ${e.message}")
        }
    }

    // ==========================================
    // 1. EVENTOS DE GERAÇÃO DE VÍDEO
    // ==========================================

    fun trackVideoGenerationStarted(productTitle: String, category: String, durationSec: Int, platformCount: Int) {
        logEvent(
            name = "video_generation_started",
            category = "GERAÇÃO_VÍDEO",
            params = mapOf(
                "product_title" to productTitle.take(60),
                "category" to category,
                "duration_seconds" to durationSec,
                "target_platform_count" to platformCount
            )
        )
    }

    fun trackVideoGenerationCompleted(
        videoProjectId: Long,
        title: String,
        platforms: List<String>,
        durationSec: Int,
        avatarId: Long,
        voiceId: Long,
        fileSizeBytes: Long
    ) {
        logEvent(
            name = "video_generation_completed",
            category = "GERAÇÃO_VÍDEO",
            params = mapOf(
                "video_id" to videoProjectId,
                "video_title" to title.take(60),
                "platforms" to platforms.joinToString(","),
                "platforms_count" to platforms.size,
                "duration_seconds" to durationSec,
                "avatar_id" to avatarId,
                "voice_id" to voiceId,
                "size_kb" to (fileSizeBytes / 1024)
            )
        )
    }

    fun trackVideoEditorExport(
        videoProjectId: Long,
        title: String,
        clipCount: Int,
        textOverlayCount: Int,
        audioTrackName: String
    ) {
        logEvent(
            name = "video_editor_export",
            category = "GERAÇÃO_VÍDEO",
            params = mapOf(
                "video_id" to videoProjectId,
                "title" to title.take(60),
                "clips_count" to clipCount,
                "overlays_count" to textOverlayCount,
                "audio_track" to audioTrackName
            )
        )
    }

    // ==========================================
    // 2. EVENTOS DE COMPARTILHAMENTO NAS REDES SOCIAIS
    // ==========================================

    fun trackSocialPublishDirect(
        platform: String,
        scheduleId: Long,
        productTitle: String,
        hasCustomProfileLink: Boolean,
        success: Boolean
    ) {
        logEvent(
            name = "social_publish_direct",
            category = "REDES_SOCIAIS",
            params = mapOf(
                "platform" to platform,
                "schedule_id" to scheduleId,
                "product_title" to productTitle.take(60),
                "has_profile_link" to hasCustomProfileLink,
                "status" to if (success) "success" else "failure"
            )
        )
    }

    fun trackSocialShareNative(
        platform: String,
        scheduleId: Long,
        captionLength: Int,
        hasProfileLink: Boolean
    ) {
        logEvent(
            name = "social_share_native",
            category = "REDES_SOCIAIS",
            params = mapOf(
                "platform" to platform,
                "schedule_id" to scheduleId,
                "caption_chars" to captionLength,
                "has_profile_link" to hasProfileLink,
                "method" to "native_intent_share"
            )
        )
    }

    fun trackSocialLinksConfigured(
        tiktokSet: Boolean,
        shopeeSet: Boolean,
        instagramSet: Boolean,
        facebookSet: Boolean
    ) {
        val totalConfigured = listOf(tiktokSet, shopeeSet, instagramSet, facebookSet).count { it }
        logEvent(
            name = "social_links_configured",
            category = "REDES_SOCIAIS",
            params = mapOf(
                "total_links" to totalConfigured,
                "tiktok_linked" to tiktokSet,
                "shopee_linked" to shopeeSet,
                "instagram_linked" to instagramSet,
                "facebook_linked" to facebookSet
            )
        )
    }

    // ==========================================
    // 3. EVENTOS DE CLONAGEM DE VOZ
    // ==========================================

    fun trackVoiceCloneRecordingStarted() {
        logEvent(
            name = "voice_clone_recording_started",
            category = "CLONAGEM_VOZ",
            params = mapOf(
                "max_sample_seconds" to 10,
                "audio_format" to "wav_16khz"
            )
        )
    }

    fun trackVoiceCloneCompleted(
        voiceName: String,
        sampleDurationSec: Int,
        emotion: String,
        audioSize: Long
    ) {
        logEvent(
            name = "voice_clone_completed",
            category = "CLONAGEM_VOZ",
            params = mapOf(
                "voice_name" to voiceName.take(50),
                "sample_duration_seconds" to sampleDurationSec,
                "emotion" to emotion,
                "audio_size_bytes" to audioSize,
                "calibration_status" to "success"
            )
        )
    }

    fun trackVoicePlayback(voiceName: String, isCloned: Boolean) {
        logEvent(
            name = "voice_tested_playback",
            category = "CLONAGEM_VOZ",
            params = mapOf(
                "voice_name" to voiceName.take(50),
                "is_cloned" to isCloned
            )
        )
    }

    fun trackVoiceSelected(voiceId: Long, voiceName: String, isCloned: Boolean) {
        logEvent(
            name = "voice_selected_for_video",
            category = "CLONAGEM_VOZ",
            params = mapOf(
                "voice_id" to voiceId,
                "voice_name" to voiceName.take(50),
                "is_cloned" to isCloned
            )
        )
    }
}
