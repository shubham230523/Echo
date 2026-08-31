package com.shubhamthorat.echo.server.generation

import com.shubhamthorat.echo.server.narration.NarrationService
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing the end-to-end audiobook generation job.
 */
class AudiobookGenerationService(
    private val narrationService: NarrationService,
    private val generationService: GenerationService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val jobs = ConcurrentHashMap<String, AudiobookJobStatus>()

    fun startGenerationJob(
        documentId: String,
        chapters: List<ChapterInput>,
        voiceId: String,
        speed: Float
    ): String {
        val jobId = UUID.randomUUID().toString()
        val initialStatus = AudiobookJobStatus(
            jobId = jobId,
            documentId = documentId,
            totalChapters = chapters.size,
            status = "PENDING"
        )
        jobs[jobId] = initialStatus

        scope.launch {
            try {
                updateJobStatus(jobId) { it.copy(status = "PROCESSING") }
                
                val results = mutableListOf<ChapterGenerationStatus>()
                
                chapters.forEachIndexed { index, chapter ->
                    updateJobStatus(jobId) { 
                        it.copy(
                            currentStep = "Preparing narration",
                            currentChapterIndex = index + 1,
                            currentChapterTitle = chapter.title,
                            progress = index.toFloat() / chapters.size
                        ) 
                    }

                    // 1. Prepare Narration
                    val prepared = narrationService.prepareNarration(chapter.originalText, "storytelling")
                    
                    updateJobStatus(jobId) { it.copy(currentStep = "Generating audio") }

                    // 2. Generate Audio (sequential, uses lock in generationService)
                    val generationResult = generationService.generateChapterAudio(
                        chapterId = chapter.id,
                        narrationText = prepared.preparedText,
                        voiceId = voiceId,
                        speed = speed
                    )

                    if (generationResult.status == "FAILED") {
                        throw Exception("Failed to generate audio for chapter '${chapter.title}': ${generationResult.errorMessage}")
                    }

                    results.add(generationResult)
                    
                    updateJobStatus(jobId) { 
                        it.copy(
                            completedChapters = index + 1,
                            progress = (index + 1).toFloat() / chapters.size
                        ) 
                    }
                }

                updateJobStatus(jobId) { 
                    it.copy(
                        status = "COMPLETED",
                        currentStep = "Finished",
                        progress = 1.0f,
                        results = results
                    ) 
                }
            } catch (e: Exception) {
                updateJobStatus(jobId) { 
                    it.copy(
                        status = "FAILED",
                        errorMessage = e.message ?: "Unknown error during audiobook generation"
                    ) 
                }
            }
        }

        return jobId
    }

    fun getJobStatus(jobId: String): AudiobookJobStatus? = jobs[jobId]

    private fun updateJobStatus(jobId: String, update: (AudiobookJobStatus) -> AudiobookJobStatus) {
        jobs[jobId]?.let {
            jobs[jobId] = update(it)
        }
    }
}

data class ChapterInput(
    val id: String,
    val title: String,
    val originalText: String
)

data class AudiobookJobStatus(
    val jobId: String,
    val documentId: String,
    val totalChapters: Int,
    val completedChapters: Int = 0,
    val currentChapterIndex: Int = 0,
    val currentChapterTitle: String? = null,
    val currentStep: String = "Initializing",
    val progress: Float = 0f,
    val status: String,
    val results: List<ChapterGenerationStatus> = emptyList(),
    val errorMessage: String? = null
)
