package com.shubhamthorat.echo.server.voice

import java.io.File
import java.util.*

/**
 * High-fidelity mock implementation for TTS.
 * Generates actual dummy MP3 files to ensure downstream processing works.
 */
class MockTTSProvider : TTSProvider {
    override suspend fun synthesize(request: TTSRequest): TTSResult {
        println("🛠️ MOCK TTS: Synthesizing ${request.text.take(50)}...")
        
        val tempDir = File("temp/mock_audio")
        tempDir.mkdirs()
        
        val fileName = "mock_${UUID.randomUUID()}.mp3"
        val mockFile = File(tempDir, fileName)
        
        // Generate a very small, valid-ish dummy file if it doesn't exist
        // In a real scenario, we might copy a tiny silent mp3 from resources
        if (!mockFile.exists()) {
            mockFile.writeBytes(ByteArray(1024)) // 1KB of junk data as a placeholder
        }

        return TTSResult(
            audioFileUri = mockFile.toURI().toString(),
            durationSeconds = (request.text.length / 15.0).coerceAtLeast(1.0),
            format = "MP3",
            providerMetadata = mapOf("engine" to "MockEngine", "version" to "2.0")
        )
    }
}
