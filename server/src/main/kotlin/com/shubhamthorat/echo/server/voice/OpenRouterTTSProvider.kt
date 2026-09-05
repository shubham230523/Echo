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
) : TTSProvider, VoiceProvider {

    override suspend fun getAvailableVoices(): List<BackendVoice> {
        return listOf(
            BackendVoice("flux-alexis-en", "Alexis (Warm)", "DEEPGRAM", "en-US", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565353/aura/asteria_docs_venw0r.mp3"),
            BackendVoice("flux-bree-en", "Bree (Clear)", "DEEPGRAM", "en-US", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565351/aura/luna_docs_clom0e.mp3"),
            BackendVoice("flux-athena-en", "Athena (Natural)", "DEEPGRAM", "en-GB", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565350/aura/athena_docs_sample.mp3"),
            BackendVoice("flux-orion-en", "Orion (Deep)", "DEEPGRAM", "en-US", "MALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565346/aura/orion_docs_aljv1q.mp3"),
            BackendVoice("flux-orpheus-en", "Orpheus (Rich)", "DEEPGRAM", "en-US", "MALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565350/aura/orpheus_docs_zdlpcf.mp3"),
            BackendVoice("flux-stella-en", "Stella (Bright)", "DEEPGRAM", "en-US", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565349/aura/stella_docs_xh5jbv.mp3")
        )
    }

    private val baseUrl = config.baseUrl ?: "https://openrouter.ai/api/v1/audio/speech"

    override suspend fun synthesize(request: TTSRequest): TTSResult {
        // For OpenRouter, 'model' is the provider engine (e.g., deepgram/flux-tts)
        // and 'voice' is the specific voice ID (e.g., flux-alexis-en)
        val voiceToUse = if (request.voiceId.isNotBlank() && request.voiceId != "default") {
            request.voiceId
        } else {
            null // Let the model use its default voice
        }

        println("📡 OpenRouter TTS API Call: ${config.voiceModel} | Voice: $voiceToUse | Text: ${request.text.take(30)}...")

        val response = try {
            client.post(baseUrl) {
                header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
                header("HTTP-Referer", "https://github.com/shubham230523/Echo")
                header("X-OpenRouter-Title", "Echo AI Audiobook Creator")
                contentType(ContentType.Application.Json)
                setBody(OpenRouterTTSRequest(
                    model = config.voiceModel,
                    input = request.text,
                    voice = voiceToUse
                ))
            }
        } catch (e: Exception) {
            println("❌ OpenRouter Connection Error: ${e.message}")
            throw TTSProviderException.ServiceUnavailable("OpenRouter TTS service unreachable", e)
        }

        if (!response.status.isSuccess()) {
            val errorText = try { response.body<String>() } catch (e: Exception) { "Unknown error" }
            println("❌ OpenRouter API Error (${response.status}): $errorText")
            throw TTSProviderException.ServiceUnavailable("OpenRouter TTS failed with status ${response.status}: $errorText")
        }

        println("✅ OpenRouter Synthesis Successful. Streaming to temp file...")
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
    val voice: String? = null
)
