package com.shubhamthorat.echo.server.validation

import kotlinx.serialization.Serializable

/**
 * Result of an audio quality validation check.
 *
 * @property passed Whether the audio passed all mandatory quality checks.
 * @property score A numeric quality score between 0.0 and 1.0.
 * @property issues A list of specific issues identified during validation (e.g., "Background noise", "Incorrect pronunciation").
 * @property recommendations A list of suggestions to improve the audio quality.
 */
@Serializable
data class QualityValidationResult(
    val passed: Boolean,
    val score: Float,
    val issues: List<String> = emptyList(),
    val recommendations: List<String> = emptyList()
)

/**
 * Request to validate the quality of a generated audio file.
 *
 * @property sourceText The original narration text used to generate the audio.
 * @property audioUrl The location of the generated audio file.
 * @property durationSeconds The duration of the audio in seconds.
 * @property format The audio format (e.g., "MP3").
 * @property transcription Optional transcription of the audio for verification.
 */
@Serializable
data class AudioValidationRequest(
    val sourceText: String,
    val audioUrl: String,
    val durationSeconds: Double,
    val format: String,
    val transcription: String? = null
)
