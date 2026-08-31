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
            status = "PENDING",
            inputs = chapters,
            voiceId = voiceId,
            speed = speed
        )
        jobs[jobId] = initialStatus

        runJob(jobId)

        return jobId
    }

    private fun runJob(jobId: String) {
        val jobStatus = jobs[jobId] ?: return
        val chapters = jobStatus.inputs
        val voiceId = jobStatus.voiceId
        val speed = jobStatus.speed

        scope.launch {
            try {
                updateJobStatus(jobId) { it.copy(status = "PROCESSING", errorMessage = null) }
                
                val currentResults = jobStatus.results.toMutableList()
                val currentFailures = jobStatus.failedChapters.toMutableList()
                
                chapters.forEachIndexed { index, chapter ->
                    // Skip if already completed in a previous run
                    if (currentResults.any { it.chapterId == chapter.id && it.status == "COMPLETED" }) {
                        return@forEachIndexed
                    }

                    updateJobStatus(jobId) { 
                        it.copy(
                            currentStep = "Preparing narration",
                            currentChapterIndex = index + 1,
                            currentChapterTitle = chapter.title,
                            progress = index.toFloat() / chapters.size
                        ) 
                    }

                    try {
                        // 1. Prepare Narration
                        val prepared = narrationService.prepareNarration(chapter.originalText, "storytelling")
                        
                        updateJobStatus(jobId) { it.copy(currentStep = "Generating audio") }

                        // 2. Generate Audio
                        val generationResult = generationService.generateChapterAudio(
                            chapterId = chapter.id,
                            narrationText = prepared.preparedText,
                            voiceId = voiceId,
                            speed = speed
                        )

                        if (generationResult.status == "FAILED") {
                            throw Exception(generationResult.errorMessage ?: "TTS Failure")
                        }

                        currentResults.add(generationResult)
                        // Remove from failures if it was there
                        currentFailures.removeAll { it.chapterId == chapter.id }
                        
                        updateJobStatus(jobId) { 
                            it.copy(
                                completedChapters = currentResults.size,
                                results = currentResults,
                                failedChapters = currentFailures,
                                progress = (index + 1).toFloat() / chapters.size
                            ) 
                        }
                    } catch (e: Exception) {
                        val retryCount = (currentFailures.find { it.chapterId == chapter.id }?.retryCount ?: 0) + 1
                        val failure = FailedChapterRecord(
                            chapterId = chapter.id,
                            title = chapter.title,
                            reason = e.message ?: "Unknown",
                            retryCount = retryCount
                        )
                        currentFailures.removeAll { it.chapterId == chapter.id }
                        currentFailures.add(failure)
                        
                        updateJobStatus(jobId) { 
                            it.copy(
                                failedChapters = currentFailures,
                                status = "FAILED",
                                errorMessage = "Failed at chapter '${chapter.title}': ${e.message}"
                            ) 
                        }
                        // Stop sequential processing on error
                        return@launch
                    }
                }

                updateJobStatus(jobId) { 
                    it.copy(
                        status = "COMPLETED",
                        currentStep = "Finished",
                        progress = 1.0f
                    ) 
                }
            } catch (e: Exception) {
                updateJobStatus(jobId) { 
                    it.copy(
                        status = "FAILED",
                        errorMessage = e.message ?: "Unexpected error"
                    ) 
                }
            }
        }
    }

    fun retryJob(jobId: String): Boolean {
        val status = jobs[jobId] ?: return false
        if (status.status != "FAILED") return false
        
        runJob(jobId)
        return true
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
    val failedChapters: List<FailedChapterRecord> = emptyList(),
    val errorMessage: String? = null,
    // Store inputs for retry support
    val inputs: List<ChapterInput> = emptyList(),
    val voiceId: String = "",
    val speed: Float = 1.0f
)

data class FailedChapterRecord(
    val chapterId: String,
    val title: String,
    val reason: String,
    val retryCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
