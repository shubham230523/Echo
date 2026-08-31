package com.shubhamthorat.echo.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.domain.repository.AudiobookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val audiobookRepository: AudiobookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observeAudiobooks()
    }

    private fun observeAudiobooks() {
        audiobookRepository.observeAllAudiobooks()
            .onEach { audiobooks ->
                _uiState.update { 
                    it.copy(
                        audiobooks = audiobooks,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .catch { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load library"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        observeAudiobooks()
    }
}
