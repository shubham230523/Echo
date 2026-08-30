package com.shubhamthorat.echo.feature.chapters

import androidx.lifecycle.ViewModel
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.ChapterStatus
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.lifecycle.viewModelScope

/**
 * ViewModel for the Chapters screen.
 */
class ChaptersViewModel(
    private val currentAnalysisRepository: CurrentAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChaptersUiState(isLoading = true))
    val uiState: StateFlow<ChaptersUiState> = _uiState.asStateFlow()

    init {
        observeAnalysisResult()
    }

    fun onEditChapterClick(chapter: Chapter) {
        _uiState.value = _uiState.value.copy(editingChapter = chapter)
    }

    fun onDismissEditDialog() {
        _uiState.value = _uiState.value.copy(editingChapter = null)
    }

    fun onUpdateChapterTitle(newTitle: String) {
        val currentEditing = _uiState.value.editingChapter ?: return
        if (newTitle.isBlank()) return

        val updatedChapters = _uiState.value.chapters.map { chapter ->
            if (chapter.id == currentEditing.id) {
                chapter.copy(title = newTitle)
            } else {
                chapter
            }
        }

        _uiState.value = _uiState.value.copy(
            chapters = updatedChapters,
            editingChapter = null
        )
    }

    private fun observeAnalysisResult() {
        currentAnalysisRepository.currentDocument.onEach { document ->
            _uiState.value = _uiState.value.copy(
                documentTitle = document?.fileName ?: "Unknown Document"
            )
        }.launchIn(viewModelScope)

        currentAnalysisRepository.chapters.onEach { chapters ->
            _uiState.value = _uiState.value.copy(
                chapters = chapters,
                isLoading = false
            )
            
            if (chapters.isEmpty() && _uiState.value.documentTitle != "") {
                _uiState.value = _uiState.value.copy(
                    error = "No chapters could be detected."
                )
            }
        }.launchIn(viewModelScope)
    }
}
