package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the current status of the audiobook generation process.
 */
enum class GenerationStatus {
    IDLE,
    QUEUED,
    PREPARING,
    GENERATING,
    VALIDATING,
    COMPLETED,
    FAILED
}

/**
 * Domain model representing the progress of an active audiobook generation task.
 *
 * @property currentStep A descriptive name of the step currently being executed.
 * @property completedSteps The number of steps that have been finished.
 * @property totalSteps The total number of steps in the generation process.
 * @property percentage The progress expressed as a value between 0.0 and 1.0.
 * @property currentChapterTitle The title of the chapter being processed, if applicable.
 * @property message A status message to display to the user.
 */
@Serializable
data class GenerationProgress(
    val status: GenerationStatus,
    val currentStep: String,
    val completedSteps: Int,
    val totalSteps: Int,
    val percentage: Float,
    val currentChapterTitle: String? = null,
    val message: String? = null
)
