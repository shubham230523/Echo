package com.shubhamthorat.echo.server.voice

import java.util.*

class MockTTSProvider : TTSProvider {
    override suspend fun synthesize(request: TTSRequest): TTSResult {
        // Return a mock result for development
        return TTSResult(
            audioFileUri = "file:///mock/audio/${UUID.randomUUID()}.mp3",
            durationSeconds = (request.text.length / 15.0),
            format = "MP3",
            providerMetadata = mapOf("engine" to "MockEngine", "version" to "1.0")
        )
    }
}
