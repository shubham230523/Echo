package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing the progress of an active audiobook generation task.
 *
 * @property generationId Unique identifier for the generation job.
 * @property status The high-level status of the process.
 * @property currentStep A descriptive name of the step currently being executed.
 * @property completedChapters The number of chapters that have been finished.
 * @property totalChapters The total number of chapters in the generation process.
 * @property progress The overall progress percentage (0.0 to 1.0).
 * @property currentChapter Title of the chapter currently being processed.
 * @property error Error message if the generation failed.
 * @property audiobookId The ID of the final audiobook if completed.
 */
@Serializable
data class GenerationProgress(
    val generationId: String,
    val status: String,
    val currentStep: String,
    val completedChapters: Int,
    val totalChapters: Int,
    val progress: Float,
    val currentChapter: String? = null,
    val error: String? = null,
    val audiobookId: String? = null
)
