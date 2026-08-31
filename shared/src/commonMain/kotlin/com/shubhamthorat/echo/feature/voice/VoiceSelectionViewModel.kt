package com.shubhamthorat.echo.feature.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.domain.model.Voice
import com.shubhamthorat.echo.domain.model.VoiceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing voice selection.
 */
class VoiceSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceSelectionUiState(isLoading = true))
    val uiState: StateFlow<VoiceSelectionUiState> = _uiState.asStateFlow()

    init {
        loadVoices()
    }

    private fun loadVoices() {
        val mockVoices = listOf(
            Voice(
                id = "google_en_male_1",
                name = "James",
                description = "Clear, warm and authoritative male voice.",
                language = "en-US",
                gender = "MALE",
                previewAudioUrl = null,
                provider = VoiceProvider.GOOGLE,
                isAvailable = true
            ),
            Voice(
                id = "google_en_female_1",
                name = "Sarah",
                description = "Soft, professional and engaging female voice.",
                language = "en-US",
                gender = "FEMALE",
                previewAudioUrl = null,
                provider = VoiceProvider.GOOGLE,
                isAvailable = true
            ),
            Voice(
                id = "openai_alloy",
                name = "Alloy",
                description = "Versatile, balanced and neutral voice.",
                language = "en-US",
                gender = "NEUTRAL",
                previewAudioUrl = null,
                provider = VoiceProvider.OPEN_AI,
                isAvailable = true
            ),
            Voice(
                id = "eleven_labs_adam",
                name = "Adam",
                description = "Deep, resonant and narrative storytelling voice.",
                language = "en-US",
                gender = "MALE",
                previewAudioUrl = null,
                provider = VoiceProvider.ELEVEN_LABS,
                isAvailable = true
            )
        )

        _uiState.update { 
            it.copy(
                voices = mockVoices,
                selectedVoiceId = null, // Ensure no auto-selection for MVP validation
                isLoading = false
            )
        }
    }

    fun onVoiceSelected(voiceId: String) {
        _uiState.update { it.copy(selectedVoiceId = voiceId) }
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
