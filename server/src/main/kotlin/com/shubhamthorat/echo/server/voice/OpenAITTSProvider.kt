package com.shubhamthorat.echo.server.voice

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

class OpenAITTSProvider(
    private val client: HttpClient,
    private val config: TTSConfig
) : TTSProvider {

    override suspend fun synthesize(request: TTSRequest): TTSResult {
        val response = try {
            client.post(config.baseUrl ?: "https://api.openai.com/v1/audio/speech") {
                header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
                contentType(ContentType.Application.Json)
                setBody(OpenAITTSRequest(
                    model = config.voiceModel,
                    input = request.text,
                    voice = request.voiceId,
                    speed = request.speed
                ))
            }
        } catch (e: Exception) {
            throw TTSProviderException.ServiceUnavailable("OpenAI TTS service unreachable", e)
        }

        if (!response.status.isSuccess()) {
            throw when (response.status) {
                HttpStatusCode.TooManyRequests -> TTSProviderException.RateLimitExceeded()
                HttpStatusCode.NotFound -> TTSProviderException.VoiceNotFound(request.voiceId)
                else -> TTSProviderException.ServiceUnavailable("OpenAI TTS failed with status ${response.status}")
            }
        }

        val tempFile = File.createTempFile("echo-tts-", ".mp3")
        response.body<io.ktor.utils.io.ByteReadChannel>().toInputStream().use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return TTSResult(
            audioFileUri = tempFile.toURI().toString(),
            durationSeconds = 0.0, // OpenAI doesn't return duration in the body, would need metadata extraction
            format = "MP3",
            providerMetadata = mapOf("provider" to "OpenAI", "model" to config.voiceModel)
        )
    }
}

@Serializable
private data class OpenAITTSRequest(
    val model: String,
    val input: String,
    val voice: String,
    val speed: Float = 1.0f
)
