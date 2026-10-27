package com.example.data.local

import android.content.Context
import java.io.File
import java.io.FileOutputStream

data class GeneratedAssetBundle(
    val videoFilePath: String,
    val thumbnailFilePath: String,
    val audioFilePath: String,
    val allAssetPaths: List<String>,
    val totalSizeBytes: Long
)

object LocalAssetManager {

    private const val PROJECTS_DIR_NAME = "viralcraft_projects"

    fun getProjectDirectory(context: Context, projectId: Long): File {
        val baseDir = File(context.filesDir, PROJECTS_DIR_NAME)
        val projectDir = File(baseDir, "draft_$projectId")
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
        return projectDir
    }

    /**
     * Generates and writes local asset files for a video draft into the app's internal storage.
     * Returns a bundle containing the absolute file paths to all created assets.
     */
    fun generateLocalAssetsForProject(
        context: Context,
        projectId: Long,
        draftName: String,
        scriptText: String,
        durationSeconds: Int = 30,
        resolution: String = "1080x1920",
        bitrateMbps: Int = 14
    ): GeneratedAssetBundle {
        val dir = getProjectDirectory(context, projectId)

        // 1. Video MP4 render simulation file
        val videoFile = File(dir, "render_${projectId}_9x16.mp4")
        FileOutputStream(videoFile).use { out ->
            val header = "VIRALCRAFT_MP4_CONTAINER_V1\nDraft: $draftName\nResolution: $resolution\nFPS: 60\nBitrate: ${bitrateMbps}Mbps\nDuration: ${durationSeconds}s\n"
            out.write(header.toByteArray(Charsets.UTF_8))
            // Write binary payload proportional to resolution (4K generates larger binary payload)
            val bufferSize = if (resolution.contains("2160") || resolution.contains("4K")) 32768 else 16384
            val repeatCount = if (resolution.contains("2160") || resolution.contains("4K")) 16 else 8
            val buffer = ByteArray(bufferSize) { (it % 256).toByte() }
            repeat(repeatCount) { out.write(buffer) }
        }


        // 2. Thumbnail JPG file
        val thumbFile = File(dir, "thumb_${projectId}_cover.jpg")
        if (!thumbFile.exists()) {
            FileOutputStream(thumbFile).use { out ->
                val thumbHeader = "JPEG_SIMULATION_THUMBNAIL_DRAFT_$projectId\nTitle: $draftName\n"
                out.write(thumbHeader.toByteArray(Charsets.UTF_8))
                val buffer = ByteArray(4096) { (it % 255).toByte() }
                repeat(4) { out.write(buffer) }
            }
        }

        // 3. Audio WAV voice narration file
        val audioFile = File(dir, "voice_${projectId}_narration.wav")
        if (!audioFile.exists()) {
            FileOutputStream(audioFile).use { out ->
                val audioHeader = "RIFF_WAV_SIMULATION_AUDIO\nScript: $scriptText\n"
                out.write(audioHeader.toByteArray(Charsets.UTF_8))
                val buffer = ByteArray(8192) { (it % 128).toByte() }
                repeat(4) { out.write(buffer) }
            }
        }

        // 4. Subtitles SRT file
        val srtFile = File(dir, "subtitles_${projectId}.srt")
        FileOutputStream(srtFile).use { out ->
            val srtContent = buildString {
                append("1\n00:00:00,000 --> 00:00:05,000\n${scriptText.take(60)}\n\n")
                append("2\n00:00:05,000 --> 00:00:15,000\n${scriptText.drop(60).take(80)}\n\n")
                append("3\n00:00:15,000 --> 00:00:30,000\nClica no link e aproveite a oferta!\n")
            }
            out.write(srtContent.toByteArray(Charsets.UTF_8))
        }

        // 5. Manifest JSON
        val manifestFile = File(dir, "manifest_${projectId}.json")
        FileOutputStream(manifestFile).use { out ->
            val json = """
            {
              "projectId": $projectId,
              "draftName": "$draftName",
              "durationSeconds": $durationSeconds,
              "aspectRatio": "9:16",
              "resolution": "$resolution",
              "bitrateMbps": $bitrateMbps,
              "files": {
                "video": "${videoFile.name}",
                "thumbnail": "${thumbFile.name}",
                "audio": "${audioFile.name}",
                "subtitles": "${srtFile.name}"
              }
            }
            """.trimIndent()
            out.write(json.toByteArray(Charsets.UTF_8))
        }

        val allAssetPaths = listOf(
            videoFile.absolutePath,
            thumbFile.absolutePath,
            audioFile.absolutePath,
            srtFile.absolutePath,
            manifestFile.absolutePath
        )

        val totalBytes = allAssetPaths.map { File(it).length() }.sum()

        return GeneratedAssetBundle(
            videoFilePath = videoFile.absolutePath,
            thumbnailFilePath = thumbFile.absolutePath,
            audioFilePath = audioFile.absolutePath,
            allAssetPaths = allAssetPaths,
            totalSizeBytes = totalBytes
        )
    }

    /**
     * Deletes all local assets associated with a project.
     */
    fun deleteProjectAssets(context: Context, projectId: Long): Boolean {
        val dir = getProjectDirectory(context, projectId)
        return dir.deleteRecursively()
    }
}
