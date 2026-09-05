package com.shubhamthorat.echo.server.voice

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.Serializable
import java.io.File

class OpenRouterTTSProvider(
    private val client: HttpClient,
    private val config: TTSConfig
) : TTSProvider {

    private val baseUrl = config.baseUrl ?: "https://openrouter.ai/api/v1/audio/speech"

    override suspend fun synthesize(request: TTSRequest): TTSResult {
        val response = try {
            client.post(baseUrl) {
                header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
                header("HTTP-Referer", "https://github.com/shubham230523/Echo")
                header("X-OpenRouter-Title", "Echo AI Audiobook Creator")
                contentType(ContentType.Application.Json)
                setBody(OpenRouterTTSRequest(
                    model = config.voiceModel,
                    input = request.text,
                    voice = request.voiceId
                ))
            }
        } catch (e: Exception) {
            throw TTSProviderException.ServiceUnavailable("OpenRouter TTS service unreachable", e)
        }

        if (!response.status.isSuccess()) {
            val errorText = try { response.body<String>() } catch (e: Exception) { "Unknown error" }
            throw TTSProviderException.ServiceUnavailable("OpenRouter TTS failed with status ${response.status}: $errorText")
        }

        val tempFile = File.createTempFile("echo-or-tts-", ".mp3")
        response.body<io.ktor.utils.io.ByteReadChannel>().toInputStream().use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val generationId = response.headers["X-Generation-Id"]

        return TTSResult(
            audioFileUri = tempFile.toURI().toString(),
            durationSeconds = 0.0,
            format = "MP3",
            providerMetadata = mapOf(
                "provider" to "OpenRouter", 
                "model" to config.voiceModel,
                "generationId" to (generationId ?: "unknown")
            )
        )
    }
}

@Serializable
private data class OpenRouterTTSRequest(
    val model: String,
    val input: String,
    val voice: String
)
