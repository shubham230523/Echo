package com.shubhamthorat.echo.server.voice

class VoiceService(
    private val voiceProvider: VoiceProvider
) {
    suspend fun getVoices(): List<BackendVoice> {
        return voiceProvider.getAvailableVoices()
    }
}
