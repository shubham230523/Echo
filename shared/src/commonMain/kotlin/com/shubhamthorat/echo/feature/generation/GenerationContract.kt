package com.shubhamthorat.echo.feature.generation

/**
 * Status of the audiobook generation process.
 */
enum class GenerationStatus {
    IDLE,
    PREPARING_CHAPTERS,
    PREPARING_NARRATION,
    GENERATING_AUDIO,
    VALIDATING_AUDIO,
    FINALIZING_AUDIOBOOK,
    COMPLETED,
    CANCELLED,
    ERROR
}

/**
 * UI State for the Audiobook Generation screen.
 *
 * @property status The current status in the generation pipeline.
 * @property progress Overall progress as a float between 0.0 and 1.0.
 * @property currentChapter The name of the chapter currently being processed.
 * @property message A descriptive message for the current status.
 * @property error Optional error message if generation fails.
 */
data class GenerationUiState(
    val status: GenerationStatus = GenerationStatus.IDLE,
    val progress: Float = 0f,
    val currentChapter: String? = null,
    val storagePath: String? = null,
    val message: String = "",
    val error: String? = null
) {
    val progressPercentage: Int = (progress * 100).toInt()
    val isCompleted: Boolean = status == GenerationStatus.COMPLETED
    val isCancelled: Boolean = status == GenerationStatus.CANCELLED
}
