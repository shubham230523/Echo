package com.shubhamthorat.echo.server.ai

import com.shubhamthorat.echo.shared.ai.LlmEngine
import com.shubhamthorat.echo.shared.ai.ModelManager
import com.shubhamthorat.echo.shared.ai.ModelType
import com.shubhamthorat.echo.shared.ai.SherpaLlmEngine
import java.io.File

/**
 * AI Provider that uses a local Sherpa-ONNX model.
 */
class LocalAIProvider(
    private val config: AIConfig,
    private val modelManager: ModelManager? = null
) : AIProvider {

    private val llmEngine: LlmEngine by lazy {
        // 1. Try manual path from config
        val configPath = if (config.modelName != "local-qwen" && config.modelName.isNotBlank() && File(config.modelName).exists()) {
            config.modelName
        } else null

        // 2. Try ModelManager (auto-downloaded by app)
        val managerPath = modelManager?.getModelPath(ModelType.LLM)
        
        // 3. Fallback to default user home location
        val userHomePath = File(System.getProperty("user.home"), ".echo/models/llama.onnx").absolutePath
        
        val finalModelPath = configPath ?: managerPath ?: userHomePath
        val modelFile = File(finalModelPath)
        
        // Find tokenizer in the same directory as the model
        val modelDir = modelFile.parentFile ?: File(System.getProperty("user.home"), ".echo/models")
        var tokensFile = File(modelDir, "tokenizer.model")
        if (!tokensFile.exists()) {
            tokensFile = File(modelDir, "tokens.txt") // Fallback
        }
        
        println("🤖 Initializing Local LLM Engine:")
        println("   Model: ${modelFile.absolutePath} (Exists: ${modelFile.exists()})")
        println("   Tokens: ${tokensFile.absolutePath} (Exists: ${tokensFile.exists()})")
        
        SherpaLlmEngine(
            modelPath = modelFile.absolutePath,
            tokensPath = tokensFile.absolutePath
        )
    }

    override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse {
        val prompt = PromptTemplates.documentStructurePrompt(request.fullText)
        val response = llmEngine.generate(prompt)
        
        return try {
            JsonExtractor.extract<DocumentStructureResponse>(response)
        } catch (e: Exception) {
            DocumentStructureResponse(
                title = "Unknown (Local)",
                type = "UNKNOWN",
                language = "en",
                chapters = emptyList()
            )
        }
    }

    override suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse {
        val prompt = PromptTemplates.chapterDetectionPrompt(request.fullText, request.structure)
        val response = llmEngine.generate(prompt)
        
        val llmResult = try {
            JsonExtractor.extract<ChapterDetectionResponse>(response)
        } catch (e: Exception) {
            ChapterDetectionResponse(chapters = emptyList())
        }

        // Robust Local Fallback: If LLM failed or returned no chapters, use Regex detection
        if (llmResult.chapters.isEmpty()) {
            println("🔍 Local LLM returned no chapters. Using Regex fallback...")
            val text = request.fullText
            val chapterRegex = Regex("(?i)^(Chapter|Section|Part)\\s+(\\d+|[IVXLC]+).*", RegexOption.MULTILINE)
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
                    val title = match.value.trim()
                    
                    DetectedChapter(
                        title = title,
                        index = index + 1,
                        openingText = text.substring(match.range.first, (match.range.first + 200).coerceAtMost(text.length)).replace("\n", " "),
                        startIndex = match.range.first,
                        endIndex = nextStart,
                        confidence = 0.9f
                    )
                }
            }
            return ChapterDetectionResponse(chapters = detected)
        }

        return llmResult
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        val prompt = PromptTemplates.narrationPreparationPrompt(request.text, request.style)
        val response = llmEngine.generate(prompt)
        
        return try {
            JsonExtractor.extract<NarrationPreparationResponse>(response)
        } catch (e: Exception) {
            NarrationPreparationResponse(
                preparedText = request.text,
                estimatedDurationSeconds = (request.text.length / 15.0),
                notes = "Local AI transformation failed, using original text."
            )
        }
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        val prompt = PromptTemplates.dialogueDetectionPrompt(request.text)
        val response = llmEngine.generate(prompt)
        
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
        val response = llmEngine.generate(prompt)
        
        return try {
            JsonExtractor.extract<PronunciationResponse>(response)
        } catch (e: Exception) {
            PronunciationResponse(guides = emptyList())
        }
    }

    override suspend fun transcribeAudio(request: TranscriptionRequest): TranscriptionResponse {
        throw UnsupportedOperationException("STT not implemented for Local AI yet.")
    }

    override suspend fun compareTranscription(request: ContentComparisonRequest): ContentComparisonResponse {
        val prompt = PromptTemplates.contentComparisonPrompt(request.sourceText, request.transcription)
        val response = llmEngine.generate(prompt)
        
        return try {
            JsonExtractor.extract<ContentComparisonResponse>(response)
        } catch (e: Exception) {
            ContentComparisonResponse(
                matchScore = 0.0f,
                issues = listOf("Failed to parse local comparison response: ${e.message}"),
                differences = emptyList()
            )
        }
    }
}
