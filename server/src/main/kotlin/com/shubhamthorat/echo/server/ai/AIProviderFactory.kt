package com.shubhamthorat.echo.server.ai

/**
 * Factory for creating AIProvider instances based on configuration.
 */
class AIProviderFactory(private val config: AIConfig) {

    fun create(): AIProvider {
        return when (config.providerType) {
            AIProviderType.MOCK -> MockAIProvider()
            // Future implementations will be added here
            else -> throw UnsupportedOperationException("Provider ${config.providerType} not implemented yet")
        }
    }
}

/**
 * Basic mock implementation for development without active AI service.
 */
private class MockAIProvider : AIProvider {
    override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse {
        return DocumentStructureResponse(
            title = "Mock Title",
            summary = "This is a mock summary.",
            nodes = emptyList()
        )
    }

    override suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse {
        return ChapterDetectionResponse(chapters = emptyList())
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        return NarrationPreparationResponse(
            preparedText = request.text,
            estimatedDurationSeconds = 0.0
        )
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        return DialogueDetectionResponse(segments = emptyList())
    }

    override suspend fun assistPronunciation(request: PronunciationRequest): PronunciationResponse {
        return PronunciationResponse(guides = emptyList())
    }
}
