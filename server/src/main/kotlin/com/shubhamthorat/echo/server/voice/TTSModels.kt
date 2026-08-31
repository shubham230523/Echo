package com.shubhamthorat.echo.server.voice

import kotlinx.serialization.Serializable

/**
 * Pronunciation hint for a specific word or phrase to assist the TTS engine.
 */
@Serializable
data class TTSPronunciationHint(
    val originalText: String,
    val ipa: String? = null,
    val phoneticRespelling: String? = null
)

/**
 * Request to synthesize audio from text.
 */
@Serializable
data class TTSRequest(
    val text: String,
    val voiceId: String,
    val speed: Float = 1.0f,
    val pronunciationHints: List<TTSPronunciationHint> = emptyList()
)

/**
 * Result of a successful TTS synthesis.
 */
@Serializable
data class TTSResult(
    val audioFileUri: String,
    val durationSeconds: Double,
    val format: String, // e.g., "MP3", "WAV", "OGG"
    val providerMetadata: Map<String, String> = emptyMap()
)
