package com.shubhamthorat.echo.server.generation

import com.shubhamthorat.echo.server.core.retryWithBackoff
import com.shubhamthorat.echo.server.narration.NarrationService
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Service for managing the end-to-end audiobook generation job.
 * Optimized with parallel processing and resilient retry logic.
 */
class AudiobookGenerationService(
    private val narrationService: NarrationService,
    private val generationService: GenerationService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val jobs = ConcurrentHashMap<String, AudiobookJobStatus>()
    private val concurrencyLimit = 3 // Restoring to b2fb240 state

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
        
        val completedCount = AtomicInteger(jobStatus.completedChapters)
        val currentResults = Collections.synchronizedList(jobStatus.results.toMutableList())
        val currentFailures = Collections.synchronizedList(jobStatus.failedChapters.toMutableList())

        scope.launch {
            try {
                updateJobStatus(jobId) { it.copy(status = "PROCESSING", errorMessage = null) }
                
                val semaphore = Semaphore(concurrencyLimit)
                
                val tasks = chapters.map { chapter ->
                    async {
                        // Skip if already completed
                        if (currentResults.any { it.chapterId == chapter.id && it.status == "COMPLETED" }) {
                            return@async
                        }

                        semaphore.withPermit {
                            try {
                                retryWithBackoff(maxRetries = 3) {
                                    println("🔊 Processing Chapter: ${chapter.title}")
                                    
                                    // 1. Prepare Narration
                                    val prepared = narrationService.prepareNarration(chapter.originalText, "storytelling")
                                    
                                    // 2. Generate Audio
                                    val generationResult = generationService.generateChapterAudio(
                                        documentId = jobStatus.documentId,
                                        chapterId = chapter.id,
                                        narrationText = prepared.preparedText,
                                        voiceId = voiceId,
                                        speed = speed,
                                        onProgress = { chapterProgress, step ->
                                            updateJobStatus(jobId) { 
                                                // Calculate fine-grained total progress
                                                // We use the already completed chapters + partial progress of this one
                                                // Note: Since 3 chapters run in parallel, this is an approximation
                                                val baseProgress = completedCount.get().toFloat() / chapters.size
                                                val addedProgress = (chapterProgress / chapters.size)
                                                
                                                it.copy(
                                                    currentChapterTitle = chapter.title,
                                                    currentStep = step,
                                                    progress = (baseProgress + addedProgress).coerceAtMost(0.99f)
                                                )
                                            }
                                        }
                                    )

                                    if (generationResult.status == "FAILED") {
                                        throw Exception(generationResult.errorMessage ?: "TTS Failure")
                                    }

                                    currentResults.add(generationResult)
                                    currentFailures.removeAll { it.chapterId == chapter.id }
                                    
                                    val done = completedCount.incrementAndGet()
                                    updateJobStatus(jobId) { 
                                        it.copy(
                                            completedChapters = done,
                                            results = currentResults.toList(),
                                            failedChapters = currentFailures.toList(),
                                            progress = done.toFloat() / chapters.size,
                                            currentChapterTitle = chapter.title
                                        ) 
                                    }
                                }
                            } catch (e: Exception) {
                                val retryCount = (currentFailures.find { it.chapterId == chapter.id }?.retryCount ?: 0) + 1
                                currentFailures.add(FailedChapterRecord(
                                    chapterId = chapter.id,
                                    title = chapter.title,
                                    reason = e.message ?: "Unknown",
                                    retryCount = retryCount
                                ))
                                updateJobStatus(jobId) { it.copy(failedChapters = currentFailures.toList()) }
                            }
                        }
                    }
                }

                tasks.awaitAll()

                val finalStatus = if (currentFailures.isNotEmpty()) "PARTIALLY_COMPLETED" else "COMPLETED"
                updateJobStatus(jobId) { 
                    it.copy(
                        status = finalStatus,
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
        // Allow retry if failed or partially completed
        if (status.status != "FAILED" && status.status != "PARTIALLY_COMPLETED") return false
        
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
    val storagePath: String? = null,
    val results: List<ChapterGenerationStatus> = emptyList(),
    val failedChapters: List<FailedChapterRecord> = emptyList(),
    val errorMessage: String? = null,
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
