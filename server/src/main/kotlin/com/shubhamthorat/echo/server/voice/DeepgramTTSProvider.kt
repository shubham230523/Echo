package com.shubhamthorat.echo.server.voice

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.Serializable
import java.io.File

class DeepgramTTSProvider(
    private val client: HttpClient,
    private val config: TTSConfig
) : TTSProvider, VoiceProvider {

    private val baseUrl = config.baseUrl ?: "https://api.deepgram.com/v1/speak"

    override suspend fun synthesize(request: TTSRequest): TTSResult {
        // Deepgram uses the 'model' parameter for the voice selection
        // Ensure request.voiceId is used if provided, otherwise fallback to config
        val voiceModel = if (request.voiceId.isNotBlank() && request.voiceId != "default") {
            request.voiceId
        } else {
            config.voiceModel
        }
        
        println("📡 Deepgram API Call: $voiceModel | Text: ${request.text.take(30)}...")
        
        if (config.apiKey.isNullOrBlank() || config.apiKey == "tts-api-key") {
            println("❌ Deepgram API Key is missing or default! Cannot synthesize.")
            throw TTSProviderException.InvalidRequest("Deepgram API Key is not configured in local.properties")
        }

        val response = try {
            client.post("$baseUrl?model=$voiceModel") {
                header("Authorization", "Token ${config.apiKey}")
                contentType(ContentType.Application.Json)
                setBody(DeepgramTTSRequest(text = request.text))
            }
        } catch (e: Exception) {
            println("❌ Deepgram Connection Error: ${e.message}")
            throw TTSProviderException.ServiceUnavailable("Deepgram TTS service unreachable", e)
        }

        if (!response.status.isSuccess()) {
            val errorText = try { response.body<String>() } catch (e: Exception) { "Unknown error" }
            println("❌ Deepgram API Error (${response.status}): $errorText")
            throw TTSProviderException.ServiceUnavailable("Deepgram TTS failed with status ${response.status}: $errorText")
        }

        println("✅ Deepgram Synthesis Successful. Streaming to temp file...")
        val tempFile = File.createTempFile("echo-deepgram-tts-", ".mp3")
        response.body<io.ktor.utils.io.ByteReadChannel>().toInputStream().use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return TTSResult(
            audioFileUri = tempFile.toURI().toString(),
            durationSeconds = 0.0, 
            format = "MP3",
            providerMetadata = mapOf(
                "provider" to "Deepgram",
                "model" to voiceModel
            )
        )
    }

    override suspend fun getAvailableVoices(): List<BackendVoice> {
        return listOf(
            BackendVoice("aura-asteria-en", "Asteria (Female - US)", "DEEPGRAM", "en-US", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565353/aura/asteria_docs_venw0r.mp3"),
            BackendVoice("aura-luna-en", "Luna (Female - US)", "DEEPGRAM", "en-US", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565351/aura/luna_docs_clom0e.mp3"),
            BackendVoice("aura-stella-en", "Stella (Female - US)", "DEEPGRAM", "en-US", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565349/aura/stella_docs_xh5jbv.mp3"),
            BackendVoice("aura-athena-en", "Athena (Female - UK)", "DEEPGRAM", "en-GB", "FEMALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565350/aura/athena_docs_sample.mp3"),
            BackendVoice("aura-hera-en", "Hera (Female - US)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("aura-orion-en", "Orion (Male - US)", "DEEPGRAM", "en-US", "MALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565346/aura/orion_docs_aljv1q.mp3"),
            BackendVoice("aura-arcas-en", "Arcas (Male - US)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("aura-perseus-en", "Perseus (Male - US)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("aura-angus-en", "Angus (Male - IE)", "DEEPGRAM", "en-IE", "MALE", null),
            BackendVoice("aura-orpheus-en", "Orpheus (Male - US)", "DEEPGRAM", "en-US", "MALE", "https://res.cloudinary.com/deepgram/video/upload/v1709565350/aura/orpheus_docs_zdlpcf.mp3"),
            BackendVoice("aura-helios-en", "Helios (Male - UK)", "DEEPGRAM", "en-GB", "MALE", null),
            BackendVoice("aura-zeus-en", "Zeus (Male - US)", "DEEPGRAM", "en-US", "MALE", null)
        )
    }
}

@Serializable
private data class DeepgramTTSRequest(val text: String)
