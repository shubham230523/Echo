package com.shubhamthorat.echo.server.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.*

class OpenRouterAIProvider(
    private val client: HttpClient,
    private val config: AIConfig
) : AIProvider {

    private val baseUrl = config.baseUrl ?: "https://openrouter.ai/api/v1/chat/completions"

    override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse {
        val prompt = PromptTemplates.documentStructurePrompt(request.fullText)
        val response = callAi(prompt)
        
        return try {
            JsonExtractor.extract<DocumentStructureResponse>(response)
        } catch (e: Exception) {
            DocumentStructureResponse(
                title = "Unknown",
                type = "UNKNOWN",
                language = "en",
                chapters = emptyList()
            )
        }
    }

    private suspend fun callAi(prompt: String): String {
        println("📡 Calling OpenRouter [${config.modelName}] with streaming enabled... (Prompt length: ${prompt.length})")
        
        val fullContent = StringBuilder()
        
        client.preparePost(baseUrl) {
            header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
            header("HTTP-Referer", "https://github.com/shubham230523/Echo")
            header("X-OpenRouter-Title", "Echo AI Audiobook Creator")
            contentType(ContentType.Application.Json)
            setBody(OpenAIRequest(
                model = config.modelName,
                messages = listOf(OpenAiMessage("user", prompt)),
                stream = true,
                reasoning = ReasoningConfig(enabled = true)
            ))
        }.execute { response ->
            if (!response.status.isSuccess()) {
                val errorBody = try { response.bodyAsText() } catch (e: Exception) { "Empty error body" }
                throw AIProviderException.ServiceUnavailable("OpenRouter AI failed with status ${response.status}: $errorBody")
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data: ")) {
                    val data = line.substring(6).trim()
                    if (data == "[DONE]") break
                    
                    try {
                        val chunk = JsonExtractor.json.decodeFromString<OpenAIResponse>(data)
                        val content = chunk.choices.firstOrNull()?.delta?.content
                        if (content != null) {
                            fullContent.append(content)
                        }
                    } catch (e: Exception) {
                        // Skip non-json or malformed chunks
                    }
                }
            }
        }

        println("\n✅ AI processing complete.")
        val result = fullContent.toString()
        if (result.isBlank()) {
            throw AIProviderException.ServiceUnavailable("OpenRouter returned empty response")
        }
        return result
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
            NarrationPreparationResponse(
                preparedText = request.text,
                estimatedDurationSeconds = (request.text.length / 15.0),
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

    override suspend fun transcribeAudio(request: TranscriptionRequest): TranscriptionResponse {
        throw UnsupportedOperationException("STT not implemented for OpenRouter yet.")
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
