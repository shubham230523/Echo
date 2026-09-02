package com.shubhamthorat.echo.feature.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.audio.AudioPlayer
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
    private val currentAnalysisRepository: CurrentAnalysisRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceSelectionUiState(isLoading = true))
    val uiState: StateFlow<VoiceSelectionUiState> = _uiState.asStateFlow()

    init {
        loadVoices()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            audioPlayer.state.collect { state ->
                if (state.isCompleted) {
                    _uiState.update { it.copy(previewingVoiceId = null) }
                }
            }
        }
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
        val voice = currentState.voices.find { it.id == voiceId }
        val previewUrl = voice?.previewAudioUrl

        // If clicking the one currently playing/loading, stop it
        if (currentState.previewingVoiceId == voiceId) {
            audioPlayer.stop()
            _uiState.update { it.copy(previewingVoiceId = null, isPreviewLoading = false) }
            return
        }

        if (previewUrl == null) return

        // Start loading preview
        viewModelScope.launch {
            _uiState.update { it.copy(previewingVoiceId = voiceId, isPreviewLoading = true) }
            
            try {
                audioPlayer.load(previewUrl)
                audioPlayer.play()
                _uiState.update { it.copy(isPreviewLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(previewingVoiceId = null, isPreviewLoading = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
