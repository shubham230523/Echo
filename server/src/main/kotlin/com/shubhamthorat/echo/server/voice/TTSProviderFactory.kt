package com.shubhamthorat.echo.server.voice

import io.ktor.client.*

/**
 * Factory for creating TTSProvider instances based on configuration.
 */
class TTSProviderFactory(
    private val client: HttpClient,
    private val config: TTSConfig
) {

    fun create(): TTSProvider {
        return when (config.providerType) {
            TTSProviderType.OPENAI -> OpenAITTSProvider(client, config)
            TTSProviderType.OPENROUTER -> OpenRouterTTSProvider(client, config)
            TTSProviderType.MOCK -> MockTTSProvider()
            else -> throw UnsupportedOperationException("TTS Provider ${config.providerType} not implemented yet")
        }
    }
}
