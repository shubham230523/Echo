package com.shubhamthorat.echo.feature.chapters

import com.shubhamthorat.echo.domain.model.Chapter

/**
 * UI State for the Chapters screen.
 *
 * @property documentTitle The title of the document.
 * @property chapters The list of chapters detected in the document.
 * @property isLoading Whether the chapters are being loaded or processed.
 * @property error An optional error message.
 */
data class ChaptersUiState(
    val documentTitle: String = "",
    val chapters: List<Chapter> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val editingChapter: Chapter? = null,
    val selectedChapterIds: Set<String> = emptySet()
)
