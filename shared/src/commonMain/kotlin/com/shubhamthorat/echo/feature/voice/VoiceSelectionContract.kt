package com.shubhamthorat.echo.feature.voice

import com.shubhamthorat.echo.domain.model.Voice

/**
 * UI State for the Voice Selection screen.
 *
 * @property voices List of available voices for selection.
 * @property selectedVoiceId The ID of the currently selected voice.
 * @property isLoading Whether the voice list is being loaded.
 * @property error Optional error message.
 */
data class VoiceSelectionUiState(
    val voices: List<Voice> = emptyList(),
    val selectedVoiceId: String? = null,
    val previewingVoiceId: String? = null,
    val isPreviewLoading: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val selectedVoice: Voice? = voices.find { it.id == selectedVoiceId }
    val isContinueEnabled: Boolean = selectedVoiceId != null
    val isPreviewing: Boolean = previewingVoiceId != null && !isPreviewLoading
}
