package com.shubhamthorat.echo.server.ai

import io.ktor.client.*
import com.shubhamthorat.echo.shared.ai.ModelManager
import java.io.File

/**
 * Factory for creating AIProvider instances based on configuration.
 */
class AIProviderFactory(
    private val client: HttpClient,
    private val config: AIConfig
) {

    fun create(modelManager: ModelManager? = null): AIProvider {
        println("🏗️ Initializing AI Provider: ${config.providerType} (Model: ${config.modelName})")
        val baseProvider = when (config.providerType) {
            AIProviderType.GEMINI -> OpenAICompatibleAIProvider(client, config)
            AIProviderType.OLLAMA -> OpenAICompatibleAIProvider(client, config)
            AIProviderType.OPENAI_COMPATIBLE -> OpenAICompatibleAIProvider(client, config)
            AIProviderType.OPENROUTER -> OpenRouterAIProvider(client, config)
            AIProviderType.LOCAL -> LocalAIProvider(config, modelManager)
            AIProviderType.MOCK -> MockAIProvider()
        }

        return if (config.useCache) {
            CachingAIProvider(
                delegate = baseProvider,
                cacheDir = File(".ai_cache"),
                modelName = config.modelName
            )
        } else {
            baseProvider
        }
    }
}

/**
 * High-fidelity mock implementation for development.
 * Performs heuristic analysis on text to simulate AI behavior without API costs.
 */
private class MockAIProvider : AIProvider {
    override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse {
        val text = request.fullText
        val title = text.lines().firstOrNull { it.isNotBlank() }?.take(100) ?: "Mock Document"
        val author = if (text.contains("by ", ignoreCase = true)) {
            val idx = text.indexOf("by ", ignoreCase = true)
            text.substring(idx + 3, (idx + 50).coerceAtMost(text.length)).lines().firstOrNull()?.trim()
        } else "Mock Author"

        val chapters = detectChapters(ChapterDetectionRequest(fullText = text)).chapters

        return DocumentStructureResponse(
            title = title,
            author = author,
            type = "BOOK",
            language = "en",
            chapters = chapters
        )
    }

    override suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse {
        val text = request.fullText
        val chapterRegex = Regex("(?i)^(chapter|section|part)\\s+(\\d+|[ivxlc]+).*$", RegexOption.MULTILINE)
        val matches = chapterRegex.findAll(text).toList()
        
        val detected = if (matches.isEmpty()) {
            listOf(
                DetectedChapter(
                    title = "Main Content",
                    index = 1,
                    openingText = text.take(200).replace("\n", " "),
                    startIndex = 0,
                    endIndex = text.length,
                    confidence = 0.5f
                )
            )
        } else {
            matches.mapIndexed { index, match ->
                val nextStart = if (index + 1 < matches.size) matches[index + 1].range.first else text.length
                val chapterText = text.substring(match.range.first, nextStart).trim()
                
                // Extract first 2 sentences for openingText
                val opening = chapterText.split(Regex("[.!?]\\s+"))
                    .take(2)
                    .joinToString(". ")
                    .replace("\n", " ")
                    .take(300)

                DetectedChapter(
                    title = match.value.trim(),
                    index = index + 1,
                    openingText = opening,
                    startIndex = match.range.first,
                    endIndex = nextStart,
                    confidence = 0.9f
                )
            }
        }

        return ChapterDetectionResponse(chapters = detected)
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        // Just return the text but with a "prepared" prefix to show it's working
        return NarrationPreparationResponse(
            preparedText = "[Simulated Narration Fixes] ${request.text}",
            estimatedDurationSeconds = (request.text.length / 15.0),
            notes = "Heuristic-based mock narration."
        )
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        val text = request.text
        val quoteRegex = Regex("\"([^\"]*)\"")
        val segments = mutableListOf<DialogueSegment>()
        
        var lastEnd = 0
        quoteRegex.findAll(text).forEach { match ->
            if (match.range.first > lastEnd) {
                segments.add(DialogueSegment(text.substring(lastEnd, match.range.first), "Narrator", false))
            }
            segments.add(DialogueSegment(match.groupValues[1], "Unknown Character", true))
            lastEnd = match.range.last + 1
        }
        
        if (lastEnd < text.length) {
            segments.add(DialogueSegment(text.substring(lastEnd), "Narrator", false))
        }

        return DialogueDetectionResponse(segments = segments)
    }

    override suspend fun assistPronunciation(request: PronunciationRequest): PronunciationResponse {
        return PronunciationResponse(
            guides = listOf(WordPronunciation(request.text.take(10), null, null, 1.0f))
        )
    }

    override suspend fun transcribeAudio(request: TranscriptionRequest): TranscriptionResponse {
        return TranscriptionResponse("Simulated transcription for: ${request.audioUrl}", 1.0f)
    }

    override suspend fun compareTranscription(request: ContentComparisonRequest): ContentComparisonResponse {
        return ContentComparisonResponse(1.0f, emptyList(), emptyList())
    }
}
