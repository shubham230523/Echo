package com.shubhamthorat.echo.feature.chapters

import androidx.lifecycle.ViewModel
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.ChapterStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Chapters screen.
 */
class ChaptersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChaptersUiState())
    val uiState: StateFlow<ChaptersUiState> = _uiState.asStateFlow()

    init {
        // TODO: Load actual chapters from a repository or state holder
        loadMockChapters()
    }

    private fun loadMockChapters() {
        val mockChapters = listOf(
            Chapter(
                id = "1",
                documentId = "doc1",
                index = 1,
                title = "Introduction",
                originalText = "...",
                narrationText = "...",
                estimatedDurationSeconds = 120,
                status = ChapterStatus.READY
            ),
            Chapter(
                id = "2",
                documentId = "doc1",
                index = 2,
                title = "Chapter 1: The Beginning",
                originalText = "...",
                narrationText = "...",
                estimatedDurationSeconds = 600,
                status = ChapterStatus.READY
            ),
            Chapter(
                id = "3",
                documentId = "doc1",
                index = 3,
                title = "Chapter 2: Deep Dive",
                originalText = "...",
                narrationText = "...",
                estimatedDurationSeconds = 450,
                status = ChapterStatus.READY
            )
        )

        _uiState.value = ChaptersUiState(
            documentTitle = "Sample Document.pdf",
            chapters = mockChapters
        )
    }
}
