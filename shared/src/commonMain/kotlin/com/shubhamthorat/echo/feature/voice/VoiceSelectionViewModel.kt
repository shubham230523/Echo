package com.shubhamthorat.echo.feature.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.Voice
import com.shubhamthorat.echo.domain.model.VoiceProvider
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import com.shubhamthorat.echo.domain.repository.SystemRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing voice selection.
 */
class VoiceSelectionViewModel(
    private val systemRepository: SystemRepository,
    private val currentAnalysisRepository: CurrentAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceSelectionUiState(isLoading = true))
    val uiState: StateFlow<VoiceSelectionUiState> = _uiState.asStateFlow()

    init {
        loadVoices()
    }

    private fun loadVoices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = systemRepository.getVoices()) {
                is AppResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            voices = result.data,
                            selectedVoiceId = null,
                            isLoading = false
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
                AppResult.Loading -> {}
            }
        }
    }

    fun onVoiceSelected(voiceId: String) {
        _uiState.update { it.copy(selectedVoiceId = voiceId) }
        currentAnalysisRepository.setVoiceId(voiceId)
    }

    fun onPreviewClick(voiceId: String) {
        val currentState = _uiState.value
        
        // If clicking the one currently playing/loading, stop it
        if (currentState.previewingVoiceId == voiceId) {
            _uiState.update { it.copy(previewingVoiceId = null, isPreviewLoading = false) }
            return
        }

        // Start loading preview
        viewModelScope.launch {
            _uiState.update { it.copy(previewingVoiceId = voiceId, isPreviewLoading = true) }
            
            // Simulate network/buffer delay
            delay(1000)
            
            // Start "playing"
            _uiState.update { it.copy(isPreviewLoading = false) }
            
            // Simulate playback duration
            delay(5000)
            
            // Stop if still the same voice
            if (_uiState.value.previewingVoiceId == voiceId) {
                _uiState.update { it.copy(previewingVoiceId = null) }
            }
        }
    }
}
