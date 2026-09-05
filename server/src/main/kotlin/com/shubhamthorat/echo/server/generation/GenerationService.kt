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
        documentId: String,
        chapterId: String,
        narrationText: String,
        voiceId: String,
        speed: Float,
        maxRetries: Int = 3,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): ChapterGenerationStatus {
        val generationId = UUID.randomUUID().toString()
        val currentStatus = ChapterGenerationStatus(
            generationId = generationId,
            chapterId = chapterId,
            status = "PROCESSING"
        )
        generationStatus[generationId] = currentStatus

        // Sequential processing for a SINGLE chapter's chunks
        val chunks = splitTextIntoChunks(narrationText, maxChars = 2000)
        println("🧩 Splitting chapter into ${chunks.size} chunks for synthesis...")
        
        val tempAudioFiles = mutableListOf<File>()
        
        try {
            for ((index, chunk) in chunks.withContextIndex()) {
                var attempt = 0
                var lastError: Exception? = null
                var success = false

                while (attempt < maxRetries && !success) {
                    try {
                        val chunkStatus = "Synthesizing chunk ${index + 1}/${chunks.size}"
                        println("   $chunkStatus (${chunk.length} chars)...")
                        
                        val result = ttsProvider.synthesize(
                            TTSRequest(
                                text = chunk,
                                voiceId = voiceId,
                                speed = speed
                            )
                        )
                        val chunkFile = File(URI(result.audioFileUri))
                        tempAudioFiles.add(chunkFile)
                        success = true
                        
                        // Report intermediate progress within chapter
                        val chapterProgress = (index + 1).toFloat() / chunks.size
                        onProgress(chapterProgress, chunkStatus)
                        
                    } catch (e: Exception) {
                        lastError = e
                        attempt++
                        println("   ⚠️ Chunk ${index + 1} failed (Attempt $attempt): ${e.message}")
                        if (attempt < maxRetries) {
                            delay(2000L * attempt)
                        }
                    }
                }

                if (!success) {
                    throw lastError ?: Exception("Failed to synthesize chunk ${index + 1}")
                }
            }

            // Merge all chunks into one final file
            val finalAudioFile = mergeAudioFiles(tempAudioFiles, documentId, chapterId)
            println("✅ Chapter generation complete: ${finalAudioFile.absolutePath}")

            // Extract actual metadata
            val (fileSize, duration) = try {
                val af = AudioFileIO.read(finalAudioFile)
                finalAudioFile.length() to af.audioHeader.trackLength.toDouble()
            } catch (e: Exception) {
                finalAudioFile.length() to 0.0
            }

            val completedStatus = currentStatus.copy(
                status = "COMPLETED",
                audioUrl = "file:///C:/Users/shubham/.echo/output/audiobooks/$documentId/$chapterId.mp3",
                durationSeconds = duration,
                fileSizeByte = fileSize
            )
            generationStatus[generationId] = completedStatus
            return completedStatus

        } catch (e: Exception) {
            val failedStatus = currentStatus.copy(
                status = "FAILED",
                errorMessage = e.message ?: "Max retries reached"
            )
            generationStatus[generationId] = failedStatus
            return failedStatus
        } finally {
            // Cleanup individual chunk files
            tempAudioFiles.forEach { if (it.exists()) it.delete() }
        }
    }

    private fun splitTextIntoChunks(text: String, maxChars: Int): List<String> {
        if (text.length <= maxChars) return listOf(text)

        val chunks = mutableListOf<String>()
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))
        var currentChunk = StringBuilder()

        for (sentence in sentences) {
            if (currentChunk.length + sentence.length > maxChars) {
                if (currentChunk.isNotEmpty()) {
                    chunks.add(currentChunk.toString().trim())
                    currentChunk = StringBuilder()
                }
                
                // If a single sentence is longer than maxChars, split by words
                if (sentence.length > maxChars) {
                    val words = sentence.split(" ")
                    for (word in words) {
                        if (currentChunk.length + word.length > maxChars) {
                            chunks.add(currentChunk.toString().trim())
                            currentChunk = StringBuilder()
                        }
                        currentChunk.append(word).append(" ")
                    }
                } else {
                    currentChunk.append(sentence).append(" ")
                }
            } else {
                currentChunk.append(sentence).append(" ")
            }
        }

        if (currentChunk.isNotEmpty()) {
            chunks.add(currentChunk.toString().trim())
        }

        return chunks
    }

    private fun mergeAudioFiles(files: List<File>, documentId: String, chapterId: String): File {
        val rootDir = File(System.getProperty("user.home"), ".echo/output/audiobooks")
        val publicDir = File(rootDir, documentId)
        publicDir.mkdirs()
        val targetFile = File(publicDir, "${chapterId}.mp3")
        
        println("📦 Finalizing Chapter Audio: ${targetFile.absolutePath}")
        
        // Since OpenRouter returns MP3, we can concatenate the bytes directly
        targetFile.outputStream().use { output ->
            files.forEach { file ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }
        return targetFile
    }

    private fun <T> Iterable<T>.withContextIndex(): List<IndexedValue<T>> = this.withIndex().toList()

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
