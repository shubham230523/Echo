package com.shubhamthorat.echo.server.voice

/**
 * Types of supported TTS providers.
 */
enum class TTSProviderType {
    OPENAI,
    GOOGLE,
    ELEVENLABS,
    OPENROUTER,
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
            val typeStr = System.getenv("TTS_PROVIDER") ?: "OPENROUTER"
            val type = try {
                TTSProviderType.valueOf(typeStr.uppercase())
            } catch (e: Exception) {
                TTSProviderType.OPENROUTER
            }

            val apiKey = System.getenv("TTS_API_KEY")

            return TTSConfig(
                providerType = type,
                apiKey = apiKey,
                baseUrl = System.getenv("TTS_BASE_URL"),
                voiceModel = System.getenv("TTS_MODEL_NAME") ?: when (type) {
                    TTSProviderType.OPENAI -> "tts-1"
                    TTSProviderType.ELEVENLABS -> "eleven_monolingual_v1"
                    TTSProviderType.OPENROUTER -> "deepgram/flux-tts:free"
                    else -> "default"
                }
            )
        }
    }
}
