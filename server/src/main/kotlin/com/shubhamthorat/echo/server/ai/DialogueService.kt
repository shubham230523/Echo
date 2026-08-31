package com.shubhamthorat.echo.server.ai

class DialogueService(private val aiProvider: AIProvider) {

    suspend fun detectDialogue(text: String): DialogueDetectionResponse {
        if (text.isBlank()) {
            throw IllegalArgumentException("Text cannot be empty")
        }

        // We might want to chunk text here if it's too large for a single AI call
        // For now, we'll assume it fits or is already chunked (e.g., at chapter level)
        return aiProvider.detectDialogue(DialogueDetectionRequest(text))
    }
}
