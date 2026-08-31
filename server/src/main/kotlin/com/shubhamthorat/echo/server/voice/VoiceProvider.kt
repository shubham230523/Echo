package com.shubhamthorat.echo.server.voice

/**
 * Abstraction for TTS voice providers.
 */
interface VoiceProvider {
    suspend fun getAvailableVoices(): List<BackendVoice>
}

data class BackendVoice(
    val id: String,
    val name: String,
    val provider: String,
    val language: String,
    val gender: String,
    val previewUrl: String?
)
