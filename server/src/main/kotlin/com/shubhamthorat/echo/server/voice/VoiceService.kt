package com.shubhamthorat.echo.server.voice

class VoiceService(
    private val voiceProvider: VoiceProvider
) {
    suspend fun getVoices(): List<BackendVoice> {
        val voices = voiceProvider.getAvailableVoices()
        println("🎙️ VoiceService: Fetched ${voices.size} voices from provider: ${voiceProvider.javaClass.simpleName}")
        return voices
    }
}
