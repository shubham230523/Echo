package com.shubhamthorat.echo.server.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class OpenAICompatibleAIProvider(
    private val client: HttpClient,
    private val config: AIConfig
) : AIProvider {

    override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse {
        val prompt = PromptTemplates.documentStructurePrompt(request.fullText)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<DocumentStructureResponse>(response)
        } catch (e: Exception) {
            // Fallback gracefully
            DocumentStructureResponse(
                title = "Unknown",
                type = "UNKNOWN",
                language = "en",
                hierarchy = emptyList()
            )
        }
    }

    private suspend fun callAi(prompt: String): String {
        val response: OpenAIResponse = client.post(config.baseUrl ?: "https://api.openai.com/v1/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(OpenAIRequest(
                model = config.modelName,
                messages = listOf(OpenAiMessage("user", prompt))
            ))
        }.body()

        return response.choices.firstOrNull()?.message?.content 
            ?: throw AIProviderException.ServiceUnavailable("AI provider returned empty response")
    }

    override suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse {
        val prompt = PromptTemplates.chapterDetectionPrompt(request.fullText, request.structure)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<ChapterDetectionResponse>(response)
        } catch (e: Exception) {
            ChapterDetectionResponse(chapters = emptyList())
        }
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        val prompt = PromptTemplates.narrationPreparationPrompt(request.text, request.style)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<NarrationPreparationResponse>(response)
        } catch (e: Exception) {
            // Fallback to original text if AI fails
            NarrationPreparationResponse(
                preparedText = request.text,
                estimatedDurationSeconds = (request.text.length / 15.0), // Rough estimate: 15 chars/sec
                notes = "AI transformation failed, using original text."
            )
        }
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        val prompt = PromptTemplates.dialogueDetectionPrompt(request.text)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<DialogueDetectionResponse>(response)
        } catch (e: Exception) {
            DialogueDetectionResponse(
                segments = listOf(
                    DialogueSegment(request.text, "Narrator", false)
                )
            )
        }
    }

    override suspend fun assistPronunciation(request: PronunciationRequest): PronunciationResponse {
        val prompt = PromptTemplates.pronunciationPrompt(request.text)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<PronunciationResponse>(response)
        } catch (e: Exception) {
            PronunciationResponse(guides = emptyList())
        }
    }

    override suspend fun findChapterAnchors(text: String, titles: List<String>): List<ChapterAnchor> {
        val prompt = PromptTemplates.chapterSplittingPrompt(text, titles)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<List<ChapterAnchor>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun transcribeAudio(request: TranscriptionRequest): TranscriptionResponse {
        // Transcription usually requires a different API endpoint (e.g., Whisper)
        // For now, we'll throw or return empty if not fully configured for audio
        throw UnsupportedOperationException("STT not implemented for generic OpenAI compatible provider yet.")
    }

    override suspend fun compareTranscription(request: ContentComparisonRequest): ContentComparisonResponse {
        val prompt = PromptTemplates.contentComparisonPrompt(request.sourceText, request.transcription)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<ContentComparisonResponse>(response)
        } catch (e: Exception) {
            ContentComparisonResponse(
                matchScore = 0.0f,
                issues = listOf("Failed to parse comparison response: ${e.message}"),
                differences = emptyList()
            )
        }
    }
}

