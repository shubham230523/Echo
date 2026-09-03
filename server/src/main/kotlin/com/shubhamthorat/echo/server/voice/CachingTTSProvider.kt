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
) : TTSProvider {

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
            // In a real app, we might store duration/format in the metaFile
            return TTSResult(
                audioFileUri = cacheFile.toURI().toString(),
                durationSeconds = 0.0, // Should be extracted from file or cached meta
                format = "MP3"
            )
        }

        println("🌐 AUDIO CACHE MISS: Calling remote TTS provider...")
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
