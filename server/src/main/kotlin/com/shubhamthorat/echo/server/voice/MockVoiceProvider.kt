package com.shubhamthorat.echo.server.voice

class MockVoiceProvider : VoiceProvider {
    override suspend fun getAvailableVoices(): List<BackendVoice> {
        return listOf(
            BackendVoice("google_en_male_1", "James", "GOOGLE", "en-US", "MALE", null),
            BackendVoice("google_en_female_1", "Sarah", "GOOGLE", "en-US", "FEMALE", null),
            BackendVoice("openai_alloy", "Alloy", "OPEN_AI", "en-US", "NEUTRAL", null),
            BackendVoice("eleven_labs_adam", "Adam", "ELEVEN_LABS", "en-US", "MALE", null)
        )
    }
}
