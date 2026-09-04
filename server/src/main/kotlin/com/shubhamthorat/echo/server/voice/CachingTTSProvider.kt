package com.shubhamthorat.echo.server.voice

import java.io.File
import java.security.MessageDigest
import java.util.*

/**
 * A decorator for [TTSProvider] that caches audio files locally to save API costs.
 */
class CachingTTSProvider(
    private val delegate: TTSProvider,
    private val cacheDir: File
) : TTSProvider, VoiceProvider {

    override suspend fun getAvailableVoices(): List<BackendVoice> {
        return if (delegate is VoiceProvider) {
            delegate.getAvailableVoices()
        } else {
            emptyList()
        }
    }

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    override suspend fun synthesize(request: TTSRequest): TTSResult {
        val hash = sha256("${request.voiceId}|${request.speed}|${request.text}")
        val cacheFile = File(cacheDir, "$hash.mp3")
        val metaFile = File(cacheDir, "$hash.json")

        if (cacheFile.exists()) {
            println("🟢 AUDIO CACHE HIT: ${cacheFile.name}")
            // Duration and format should ideally be cached too, but 0.0 is a safe fallback for now
            return TTSResult(
                audioFileUri = cacheFile.toURI().toString(),
                durationSeconds = 0.0, 
                format = "MP3"
            )
        }

        val providerName = delegate.javaClass.simpleName
        println("📡 AUDIO CACHE MISS -> Calling $providerName...")
        val result = delegate.synthesize(request)
        
        try {
            val sourceFile = File(java.net.URI(result.audioFileUri))
            sourceFile.copyTo(cacheFile, overwrite = true)
            println("✅ Cached audio to: ${cacheFile.absolutePath}")
        } catch (e: Exception) {
            println("⚠️ Failed to cache audio: ${e.message}")
        }

        return result.copy(audioFileUri = cacheFile.toURI().toString())
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
