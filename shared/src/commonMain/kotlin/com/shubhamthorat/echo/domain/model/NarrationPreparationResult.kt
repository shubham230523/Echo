package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * The outcome of a narration preparation request.
 *
 * @property chapterId The ID of the processed chapter.
 * @property narrationText The optimized text ready for speech synthesis.
 * @property changesSummary A brief description of what was changed (e.g., "Simplified complex sentences").
 */
@Serializable
data class NarrationPreparationResult(
    val chapterId: String,
    val narrationText: String,
    val changesSummary: String?
)
