package com.shubhamthorat.echo.server.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

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
                messages = listOf(Message("user", prompt))
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
}

@Serializable
private data class OpenAIRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
private data class Message(
    val role: String,
    val content: String
)

@Serializable
private data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
private data class Choice(
    val message: Message
)
