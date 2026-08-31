package com.shubhamthorat.echo.feature.narration

import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.NarrationStyle

/**
 * UI State for the Narration preparation screen.
 *
 * @property chapters List of chapters available for narration preparation.
 * @property currentChapterIndex Index of the chapter currently being viewed.
 * @property narrationStyle The selected style for narration optimization.
 * @property isLoading Whether a processing operation is in progress.
 * @property error Optional error message.
 */
data class NarrationUiState(
    val chapters: List<Chapter> = emptyList(),
    val currentChapterIndex: Int = 0,
    val narrationStyle: NarrationStyle = NarrationStyle.NATURAL,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val currentChapter: Chapter?
        get() = chapters.getOrNull(currentChapterIndex)
}
