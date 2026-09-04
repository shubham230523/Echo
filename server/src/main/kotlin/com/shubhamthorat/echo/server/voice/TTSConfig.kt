package com.shubhamthorat.echo.server.voice

/**
 * Types of supported TTS providers.
 */
enum class TTSProviderType {
    OPENAI,
    GOOGLE,
    ELEVENLABS,
    OPENROUTER,
    DEEPGRAM,
    LOCAL,
    MOCK
}

/**
 * Configuration for the TTS system, sourced from environment variables.
 */
data class TTSConfig(
    val providerType: TTSProviderType,
    val apiKey: String?,
    val baseUrl: String?,
    val voiceModel: String,
    val useCache: Boolean = true
) {
    companion object {
        fun fromEnvironment(getProperty: (String) -> String? = { System.getenv(it) }): TTSConfig {
            val typeStr = getProperty("TTS_PROVIDER") ?: "OPENROUTER"
            val type = try {
                TTSProviderType.valueOf(typeStr.uppercase())
            } catch (e: Exception) {
                TTSProviderType.OPENROUTER
            }

            val apiKey = getProperty("TTS_API_KEY")
            
            // Simulation check: if keys are exactly the dummy ones and not using LOCAL, force MOCK
            val isDummyKey = apiKey == "tts-api-key" || apiKey == "dummy"
            val finalType = if (isDummyKey && type != TTSProviderType.LOCAL) TTSProviderType.MOCK else type
            
            // Bypass caching when using real keys
            val useCache = if (!isDummyKey) {
                getProperty("TTS_USE_CACHE")?.toBoolean() ?: false 
            } else {
                getProperty("TTS_USE_CACHE")?.toBoolean() ?: true
            }

            return TTSConfig(
                providerType = finalType,
                apiKey = apiKey,
                baseUrl = getProperty("TTS_BASE_URL"),
                voiceModel = getProperty("TTS_MODEL_NAME") ?: when (finalType) {
                    TTSProviderType.OPENAI -> "tts-1"
                    TTSProviderType.ELEVENLABS -> "eleven_monolingual_v1"
                    TTSProviderType.OPENROUTER -> "deepgram/flux-tts:free"
                    TTSProviderType.DEEPGRAM -> "aura-asteria-en"
                    TTSProviderType.LOCAL -> "local-vits"
                    else -> "default"
                },
                useCache = useCache
            )
        }
    }
}
