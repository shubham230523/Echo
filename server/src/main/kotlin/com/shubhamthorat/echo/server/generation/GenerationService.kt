package com.shubhamthorat.echo.server.generation

import com.shubhamthorat.echo.server.voice.TTSProvider
import com.shubhamthorat.echo.server.voice.TTSRequest
import com.shubhamthorat.echo.server.voice.TTSResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jaudiotagger.audio.AudioFileIO
import java.io.File
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing the generation of audiobook chapter audio.
 */
class GenerationService(
    private val ttsProvider: TTSProvider
) {
    private val generationStatus = ConcurrentHashMap<String, ChapterGenerationStatus>()
    private val lock = Mutex()

    suspend fun generateChapterAudio(
        chapterId: String,
        narrationText: String,
        voiceId: String,
        speed: Float,
        maxRetries: Int = 3
    ): ChapterGenerationStatus {
        val generationId = UUID.randomUUID().toString()
        val currentStatus = ChapterGenerationStatus(
            generationId = generationId,
            chapterId = chapterId,
            status = "PROCESSING"
        )
        generationStatus[generationId] = currentStatus

        // Lock ensures non-concurrent generation as per requirement
        lock.withLock {
            var attempt = 0
            var lastError: Exception? = null

            while (attempt < maxRetries) {
                try {
                    val result = ttsProvider.synthesize(
                        TTSRequest(
                            text = narrationText,
                            voiceId = voiceId,
                            speed = speed
                        )
                    )

                    val audioFile = File(URI(result.audioFileUri))
                    
                    // Extract actual metadata
                    val (fileSize, duration) = try {
                        val af = AudioFileIO.read(audioFile)
                        audioFile.length() to af.audioHeader.trackLength.toDouble()
                    } catch (e: Exception) {
                        // Fallback to provider result or 0
                        audioFile.length() to result.durationSeconds
                    }

                    val completedStatus = currentStatus.copy(
                        status = "COMPLETED",
                        audioUrl = result.audioFileUri,
                        durationSeconds = duration,
                        fileSizeByte = fileSize
                    )
                    generationStatus[generationId] = completedStatus
                    return completedStatus
                } catch (e: Exception) {
                    lastError = e
                    attempt++
                    if (attempt < maxRetries) {
                        delay(2000L * attempt) // Exponential-ish backoff
                    }
                }
            }

            val failedStatus = currentStatus.copy(
                status = "FAILED",
                errorMessage = lastError?.message ?: "Max retries reached"
            )
            generationStatus[generationId] = failedStatus
            return failedStatus
        }
    }

    fun getStatus(generationId: String): ChapterGenerationStatus? {
        return generationStatus[generationId]
    }
}

data class ChapterGenerationStatus(
    val generationId: String,
    val chapterId: String,
    val status: String,
    val audioUrl: String? = null,
    val durationSeconds: Double = 0.0,
    val fileSizeByte: Long = 0L,
    val errorMessage: String? = null
)
