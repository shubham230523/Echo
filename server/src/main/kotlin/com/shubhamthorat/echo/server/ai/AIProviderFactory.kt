package com.shubhamthorat.echo.server.ai

import io.ktor.client.*

/**
 * Factory for creating AIProvider instances based on configuration.
 */
class AIProviderFactory(
    private val client: HttpClient,
    private val config: AIConfig
) {

    fun create(): AIProvider {
        return when (config.providerType) {
            AIProviderType.GEMINI -> OpenAICompatibleAIProvider(client, config) // Placeholder if using compatible endpoint
            AIProviderType.OLLAMA -> OpenAICompatibleAIProvider(client, config)
            AIProviderType.OPENAI_COMPATIBLE -> OpenAICompatibleAIProvider(client, config)
            AIProviderType.OPENROUTER -> OpenRouterAIProvider(client, config)
            AIProviderType.MOCK -> MockAIProvider()
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
            author = "Mock Author",
            type = "BOOK",
            language = "en",
            hierarchy = emptyList()
        )
    }

    override suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse {
        return ChapterDetectionResponse(
            chapters = listOf(
                DetectedChapter("Chapter 1: The Beginning", 1, 0, 1000, 1.0f),
                DetectedChapter("Chapter 2: The Middle", 2, 1000, 2000, 1.0f),
                DetectedChapter("Chapter 3: The End", 3, 2000, 3000, 1.0f)
            )
        )
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        return NarrationPreparationResponse(
            preparedText = "Mocked narration friendly text: ${request.text}",
            estimatedDurationSeconds = (request.text.length / 10.0),
            notes = "This is a mock response for development."
        )
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        return DialogueDetectionResponse(
            segments = listOf(
                DialogueSegment("He looked at her and said,", "Narrator", false),
                DialogueSegment(" \"I'll be back soon.\"", "Male Character", true),
                DialogueSegment(" She nodded and whispered,", "Narrator", false),
                DialogueSegment(" \"I know.\"", "Female Character", true)
            )
        )
    }

    override suspend fun assistPronunciation(request: PronunciationRequest): PronunciationResponse {
        return PronunciationResponse(
            guides = listOf(
                WordPronunciation("Echo", "/ˈɛkoʊ/", "EH-koh", 0.99f),
                WordPronunciation("Ktor", "/keɪtɔːr/", "KAY-tor", 0.95f)
            )
        )
    }

    override suspend fun findChapterAnchors(text: String, titles: List<String>): List<ChapterAnchor> {
        return titles.mapIndexed { index, title ->
            ChapterAnchor(title, index * 1000)
        }
    }

    override suspend fun transcribeAudio(request: TranscriptionRequest): TranscriptionResponse {
        return TranscriptionResponse(
            text = "This is a mock transcription of the audio file.",
            confidence = 0.98f
        )
    }

    override suspend fun compareTranscription(request: ContentComparisonRequest): ContentComparisonResponse {
        return ContentComparisonResponse(
            matchScore = 0.95f,
            issues = emptyList(),
            differences = emptyList()
        )
    }
}
