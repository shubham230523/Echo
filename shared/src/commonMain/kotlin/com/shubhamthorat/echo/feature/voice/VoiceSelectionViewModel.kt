package com.shubhamthorat.echo.feature.voice

import androidx.lifecycle.ViewModel
import com.shubhamthorat.echo.domain.model.Voice
import com.shubhamthorat.echo.domain.model.VoiceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
                selectedVoiceId = mockVoices.firstOrNull()?.id,
                isLoading = false
            )
        }
    }

    fun onVoiceSelected(voiceId: String) {
        _uiState.update { it.copy(selectedVoiceId = voiceId) }
    }
}
