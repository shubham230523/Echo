package com.shubhamthorat.echo.server.ai

class PronunciationService(private val aiProvider: AIProvider) {

    suspend fun assistPronunciation(text: String): PronunciationResponse {
        if (text.isBlank()) {
            throw IllegalArgumentException("Text cannot be empty")
        }

        return aiProvider.assistPronunciation(PronunciationRequest(text))
    }
}
