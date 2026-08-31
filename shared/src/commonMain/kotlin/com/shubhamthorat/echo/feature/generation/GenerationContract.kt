package com.shubhamthorat.echo.feature.generation

/**
 * Stages of the audiobook generation pipeline.
 */
enum class GenerationStage(val displayName: String) {
    PREPARING_CHAPTERS("Preparing chapters"),
    PREPARING_NARRATION("Preparing narration"),
    GENERATING_AUDIO("Generating Audio"),
    VALIDATING_AUDIO("Validating Audio"),
    FINALIZING_AUDIOBOOK("Finalizing Audiobook")
}

/**
 * UI State for the Audiobook Generation screen.
 *
 * @property currentStage The current stage in the generation pipeline.
 * @property progress Overall progress as a float between 0.0 and 1.0.
 * @property currentChapter The name of the chapter currently being processed.
 * @property isCompleted Whether the generation process is finished.
 * @property isCancelled Whether the generation process was cancelled.
 */
data class GenerationUiState(
    val currentStage: GenerationStage = GenerationStage.PREPARING_CHAPTERS,
    val progress: Float = 0f,
    val currentChapter: String? = null,
    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false
) {
    val progressPercentage: Int = (progress * 100).toInt()
}
