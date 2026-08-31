package com.shubhamthorat.echo.feature.narration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Narration preparation screen.
 */
class NarrationViewModel(
    private val currentAnalysisRepository: CurrentAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NarrationUiState())
    val uiState: StateFlow<NarrationUiState> = _uiState.asStateFlow()

    init {
        observeChapters()
    }

    private fun observeChapters() {
        currentAnalysisRepository.chapters.onEach { chapters ->
            _uiState.update { it.copy(chapters = chapters) }
        }.launchIn(viewModelScope)
    }

    fun onChapterSelected(index: Int) {
        _uiState.update { it.copy(currentChapterIndex = index) }
    }

    fun onRegenerateClick() {
        // TODO: Implement narration text regeneration with AI
    }
}
