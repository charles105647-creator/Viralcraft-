package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.LocalAssetManager
import com.example.data.local.entities.ProjectMetadataEntity
import com.example.data.local.entities.VideoProjectEntity
import com.example.data.model.AvatarStyle
import com.example.data.model.VideoStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProjectMetadataRoomTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveProjectMetadata() = runBlocking {
        val now = System.currentTimeMillis()
        val generatedPaths = listOf(
            "/data/user/0/com.example/files/draft_42/render_42_9x16.mp4",
            "/data/user/0/com.example/files/draft_42/thumb_42.jpg",
            "/data/user/0/com.example/files/draft_42/voice_42.wav",
            "/data/user/0/com.example/files/draft_42/subtitles_42.srt"
        )

        val metadata = ProjectMetadataEntity(
            videoDraftName = "Smartwatch Promo 9:16 Viral Draft",
            creationTimestamp = now,
            lastModifiedTimestamp = now,
            videoFilePath = generatedPaths[0],
            thumbnailFilePath = generatedPaths[1],
            audioFilePath = generatedPaths[2],
            generatedAssetPaths = generatedPaths,
            durationSeconds = 30,
            aspectRatio = "9:16",
            resolution = "1080x1920",
            fileSizeBytes = 1048576L,
            status = "DRAFT",
            notes = "Test draft for Room metadata persistence"
        )

        val insertedId = database.projectMetadataDao().insertProjectMetadata(metadata)
        assertTrue(insertedId > 0)

        // Retrieve by ID
        val retrieved = database.projectMetadataDao().getProjectMetadataById(insertedId)
        assertNotNull(retrieved)
        assertEquals("Smartwatch Promo 9:16 Viral Draft", retrieved?.videoDraftName)
        assertEquals(now, retrieved?.creationTimestamp)
        assertEquals(generatedPaths[0], retrieved?.videoFilePath)
        assertEquals(generatedPaths[1], retrieved?.thumbnailFilePath)
        assertEquals(generatedPaths[2], retrieved?.audioFilePath)
        assertEquals(4, retrieved?.generatedAssetPaths?.size)
        assertEquals(1048576L, retrieved?.fileSizeBytes)

        // Query Flow list
        val all = database.projectMetadataDao().getAllProjectMetadata().first()
        assertEquals(1, all.size)
        assertEquals("Smartwatch Promo 9:16 Viral Draft", all[0].videoDraftName)

        // Search by draft name query
        val searchResult = database.projectMetadataDao().searchByDraftName("Promo").first()
        assertEquals(1, searchResult.size)
    }

    @Test
    fun testVideoProjectWithMetadataAndAssetPaths() = runBlocking {
        val now = System.currentTimeMillis()
        val videoProject = VideoProjectEntity(
            title = "Mini Processador Turbo Draft",
            productId = 1L,
            avatarId = 1L,
            voiceId = 1L,
            hookScript = "Olha esse achadinho!",
            problemScript = "Cansado de picar cebola?",
            solutionScript = "Esse mini processador tritura em 5s",
            offerScript = "Por apenas 39,90",
            ctaScript = "Clica no link",
            fullScriptText = "Olha esse achadinho imperdível!",
            videoDraftName = "Mini Processador Turbo Draft",
            creationTimestamp = now,
            videoFilePath = "/storage/emulated/0/movies/draft_1.mp4",
            thumbnailFilePath = "/storage/emulated/0/movies/thumb_1.jpg",
            audioFilePath = "/storage/emulated/0/movies/audio_1.wav",
            generatedAssetPaths = listOf("/storage/emulated/0/movies/draft_1.mp4"),
            fileSizeBytes = 524288L,
            avatarStyle = AvatarStyle.PIP_CIRCLE,
            status = VideoStatus.READY
        )

        val id = database.videoProjectDao().insertVideoProject(videoProject)
        assertTrue(id > 0)

        val retrieved = database.videoProjectDao().getVideoProjectById(id)
        assertNotNull(retrieved)
        assertEquals("Mini Processador Turbo Draft", retrieved?.videoDraftName)
        assertEquals(now, retrieved?.creationTimestamp)
        assertEquals("/storage/emulated/0/movies/draft_1.mp4", retrieved?.videoFilePath)
        assertEquals(524288L, retrieved?.fileSizeBytes)

        // Update local asset paths
        val updatedAssets = listOf(
            "/new/path/video.mp4",
            "/new/path/thumb.jpg",
            "/new/path/voice.wav"
        )
        database.videoProjectDao().updateLocalAssetPaths(
            id = id,
            videoPath = updatedAssets[0],
            thumbPath = updatedAssets[1],
            audioPath = updatedAssets[2],
            assetPaths = updatedAssets,
            fileSize = 999999L
        )

        val updated = database.videoProjectDao().getVideoProjectById(id)
        assertEquals("/new/path/video.mp4", updated?.videoFilePath)
        assertEquals(3, updated?.generatedAssetPaths?.size)
        assertEquals(999999L, updated?.fileSizeBytes)
    }

    @Test
    fun testLocalAssetManagerGeneratesFilesOnDisk() {
        val draftName = "Fone Noise Cancelling Draft"
        val script = "Melhor fone do momento com cancelamento ativo de ruído!"

        val bundle = LocalAssetManager.generateLocalAssetsForProject(
            context = context,
            projectId = 99L,
            draftName = draftName,
            scriptText = script,
            durationSeconds = 25
        )

        assertNotNull(bundle.videoFilePath)
        assertNotNull(bundle.thumbnailFilePath)
        assertNotNull(bundle.audioFilePath)
        assertTrue(bundle.allAssetPaths.isNotEmpty())

        val videoFile = File(bundle.videoFilePath)
        val thumbFile = File(bundle.thumbnailFilePath)
        val audioFile = File(bundle.audioFilePath)

        assertTrue("Video file should exist on local storage", videoFile.exists())
        assertTrue("Thumb file should exist on local storage", thumbFile.exists())
        assertTrue("Audio file should exist on local storage", audioFile.exists())
        assertTrue("Total bytes should be greater than 0", bundle.totalSizeBytes > 0)
    }
}
