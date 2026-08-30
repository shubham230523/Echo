package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Request to optimize chapter text for a specific narration style and language.
 *
 * @property chapter The chapter whose text needs to be prepared.
 * @property language The target language code (e.g., "en", "es").
 * @property style The desired narration style.
 */
@Serializable
data class NarrationPreparationRequest(
    val chapter: Chapter,
    val language: String,
    val style: NarrationStyle
)
