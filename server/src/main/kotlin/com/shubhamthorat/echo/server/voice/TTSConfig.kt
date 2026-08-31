package com.shubhamthorat.echo.server.voice

/**
 * Types of supported TTS providers.
 */
enum class TTSProviderType {
    OPENAI,
    GOOGLE,
    ELEVENLABS,
    MOCK
}

/**
 * Configuration for the TTS system, sourced from environment variables.
 */
data class TTSConfig(
    val providerType: TTSProviderType,
    val apiKey: String?,
    val baseUrl: String?,
    val voiceModel: String
) {
    companion object {
        fun fromEnvironment(): TTSConfig {
            val typeStr = System.getenv("TTS_PROVIDER") ?: "MOCK"
            val type = try {
                TTSProviderType.valueOf(typeStr.uppercase())
            } catch (e: Exception) {
                TTSProviderType.MOCK
            }

            return TTSConfig(
                providerType = type,
                apiKey = System.getenv("TTS_API_KEY"),
                baseUrl = System.getenv("TTS_BASE_URL"),
                voiceModel = System.getenv("TTS_MODEL_NAME") ?: when (type) {
                    TTSProviderType.OPENAI -> "tts-1"
                    TTSProviderType.ELEVENLABS -> "eleven_monolingual_v1"
                    else -> "default"
                }
            )
        }
    }
}
