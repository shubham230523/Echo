package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the provider of the AI voice.
 */
enum class VoiceProvider {
    SYSTEM,
    GOOGLE,
    OPEN_AI,
    ELEVEN_LABS,
    AZURE,
    CUSTOM
}

/**
 * Domain model representing an AI voice available for narration.
 *
 * @property id Unique identifier for the voice.
 * @property name Display name of the voice.
 * @property description A brief description of the voice characteristics.
 * @property language The language code (e.g., "en-US") the voice supports.
 * @property gender The gender of the voice (e.g., "MALE", "FEMALE", "NEUTRAL").
 * @property previewAudioUrl URL to a short audio clip demonstrating the voice.
 * @property provider The TTS provider offering this voice.
 * @property isAvailable Whether the voice is currently available for use.
 */
@Serializable
data class Voice(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val gender: String?,
    val previewAudioUrl: String?,
    val provider: VoiceProvider,
    val isAvailable: Boolean
)
