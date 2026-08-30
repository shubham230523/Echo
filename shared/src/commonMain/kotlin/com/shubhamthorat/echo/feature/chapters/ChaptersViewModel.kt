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
