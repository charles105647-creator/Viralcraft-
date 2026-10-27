package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AvatarEntity
import com.example.data.local.entities.PostScheduleEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.SocialAccountEntity
import com.example.data.local.entities.VideoProjectEntity
import com.example.data.local.entities.VoiceEntity
import com.example.data.model.AvatarStyle
import com.example.data.model.PostStatus
import com.example.data.model.SocialPlatform
import com.example.data.model.VideoStatus
import com.example.data.model.VoiceEmotion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val database: AppDatabase) {

    val allProducts: Flow<List<ProductEntity>> = database.productDao().getAllProducts()
    val allAvatars: Flow<List<AvatarEntity>> = database.avatarDao().getAllAvatars()
    val allVoices: Flow<List<VoiceEntity>> = database.voiceDao().getAllVoices()
    val allVideoProjects: Flow<List<VideoProjectEntity>> = database.videoProjectDao().getAllVideoProjects()
    val allSchedules: Flow<List<PostScheduleEntity>> = database.postScheduleDao().getAllSchedules()
    val allSocialAccounts: Flow<List<SocialAccountEntity>> = database.socialAccountDao().getAllAccounts()
    val videoCount: Flow<Int> = database.videoProjectDao().getVideoCount()

    val projectMetadataRepository = ProjectMetadataRepository(database.projectMetadataDao())
    val allProjectMetadata: Flow<List<com.example.data.local.entities.ProjectMetadataEntity>> = projectMetadataRepository.allProjectMetadata

    suspend fun getProjectMetadata(id: Long) = projectMetadataRepository.getProjectMetadataById(id)
    suspend fun insertProjectMetadata(metadata: com.example.data.local.entities.ProjectMetadataEntity) = projectMetadataRepository.insertProjectMetadata(metadata)
    suspend fun updateProjectMetadata(metadata: com.example.data.local.entities.ProjectMetadataEntity) = projectMetadataRepository.updateProjectMetadata(metadata)
    suspend fun deleteProjectMetadata(metadata: com.example.data.local.entities.ProjectMetadataEntity) = projectMetadataRepository.deleteProjectMetadata(metadata)
    fun searchProjectMetadata(query: String) = projectMetadataRepository.searchByDraftName(query)

    suspend fun getProduct(id: Long): ProductEntity? = withContext(Dispatchers.IO) {
        database.productDao().getProductById(id)
    }

    suspend fun getAvatar(id: Long): AvatarEntity? = withContext(Dispatchers.IO) {
        database.avatarDao().getAvatarById(id)
    }

    suspend fun getVoice(id: Long): VoiceEntity? = withContext(Dispatchers.IO) {
        database.voiceDao().getVoiceById(id)
    }

    suspend fun getVideoProject(id: Long): VideoProjectEntity? = withContext(Dispatchers.IO) {
        database.videoProjectDao().getVideoProjectById(id)
    }

    suspend fun insertProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        database.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        database.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        database.productDao().deleteProduct(product)
    }

    suspend fun insertAvatar(avatar: AvatarEntity): Long = withContext(Dispatchers.IO) {
        database.avatarDao().insertAvatar(avatar)
    }

    suspend fun insertAvatars(avatars: List<AvatarEntity>) = withContext(Dispatchers.IO) {
        database.avatarDao().insertAvatars(avatars)
    }

    suspend fun insertVoice(voice: VoiceEntity): Long = withContext(Dispatchers.IO) {
        database.voiceDao().insertVoice(voice)
    }

    suspend fun insertVideoProject(project: VideoProjectEntity): Long = withContext(Dispatchers.IO) {
        database.videoProjectDao().insertVideoProject(project)
    }

    suspend fun updateVideoProject(project: VideoProjectEntity) = withContext(Dispatchers.IO) {
        database.videoProjectDao().updateVideoProject(project)
    }

    suspend fun deleteVideoProject(project: VideoProjectEntity) = withContext(Dispatchers.IO) {
        database.videoProjectDao().deleteVideoProject(project)
    }

    suspend fun insertSchedule(schedule: PostScheduleEntity): Long = withContext(Dispatchers.IO) {
        database.postScheduleDao().insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: PostScheduleEntity) = withContext(Dispatchers.IO) {
        database.postScheduleDao().updateSchedule(schedule)
    }

    suspend fun updateSocialAccount(account: SocialAccountEntity) = withContext(Dispatchers.IO) {
        database.socialAccountDao().updateAccount(account)
    }

    suspend fun saveSocialAccount(account: SocialAccountEntity) = withContext(Dispatchers.IO) {
        database.socialAccountDao().insertOrUpdateAccount(account)
    }

    suspend fun getAccountByPlatform(platform: SocialPlatform): SocialAccountEntity? = withContext(Dispatchers.IO) {
        database.socialAccountDao().getAccountByPlatform(platform)
    }

    // Voice Cloning Engine:
    suspend fun cloneVoiceFromSample(
        name: String,
        sampleText: String,
        sampleDurationSec: Int,
        emotion: VoiceEmotion,
        sampleAudioPath: String = ""
    ): VoiceEntity = withContext(Dispatchers.IO) {
        delay(900) // Simulates neural audio feature extraction & acoustic profile embedding
        val clonedVoice = VoiceEntity(
            name = name.ifBlank { "Minha Voz Clonada" },
            toneLabel = "Voz Clonada por IA",
            samplePhrase = sampleText.ifBlank { "Fala pessoal! Hoje eu trouxe esse super achadinho que testei a semana inteira. O custo-benefício é impressionante!" },
            emotion = emotion,
            pitch = 1.0f,
            speed = 1.15f,
            isClonedVoice = true,
            sampleRecordingUri = sampleAudioPath,
            cloneStatus = "Clonagem Concluída com Sucesso",
            audioDurationSeconds = sampleDurationSec
        )
        val newId = database.voiceDao().insertVoice(clonedVoice)
        clonedVoice.copy(id = newId)
    }

    suspend fun deleteVoice(voice: VoiceEntity) = withContext(Dispatchers.IO) {
        database.voiceDao().deleteVoice(voice)
    }

    // Direct Automatic Social Post Dispatcher:
    suspend fun publishDirectly(scheduleId: Long): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Find schedule
            var scheduleToUpdate: PostScheduleEntity? = null
            // Query all or update directly
            // Delay simulating network API call to TikTok/Shopee/Meta APIs
            delay(1500)
            Result.success("Postagem realizada com sucesso!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val repo = AppRepository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
