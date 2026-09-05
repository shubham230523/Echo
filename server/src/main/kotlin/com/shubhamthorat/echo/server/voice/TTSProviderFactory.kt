package com.shubhamthorat.echo.server.voice

import io.ktor.client.*
import com.shubhamthorat.echo.shared.ai.ModelManager
import java.io.File

/**
 * Factory for creating TTSProvider instances based on configuration.
 */
class TTSProviderFactory(
    private val client: HttpClient,
    private val config: TTSConfig
) {

    fun create(modelManager: ModelManager? = null): TTSProvider {
        println("🏗️ Initializing TTS Provider: ${config.providerType} (Model: ${config.voiceModel})")
        val baseProvider = when (config.providerType) {
            TTSProviderType.OPENAI -> OpenAITTSProvider(client, config)
            TTSProviderType.OPENROUTER -> OpenRouterTTSProvider(client, config)
            TTSProviderType.DEEPGRAM -> DeepgramTTSProvider(client, config)
            TTSProviderType.LOCAL -> LocalTTSProvider(config, modelManager)
            TTSProviderType.MOCK -> MockTTSProvider()
            else -> throw UnsupportedOperationException("TTS Provider ${config.providerType} not implemented yet")
        }

        return if (config.useCache) {
            CachingTTSProvider(
                delegate = baseProvider,
                cacheDir = File(".audio_cache")
            )
        } else {
            baseProvider
        }
    }
}
