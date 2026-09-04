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
        
        val llmResult = try {
            JsonExtractor.extract<DocumentStructureResponse>(response)
        } catch (e: Exception) {
            null
        }

        // Heuristic Fallback for Title/Author if LLM failed or returned empty
        val text = request.fullText
        val llmTitle = llmResult?.title
        val finalTitle = if (llmTitle.isNullOrBlank()) {
            text.lines().firstOrNull { it.isNotBlank() }?.trim()?.take(100) ?: "Unknown Document"
        } else llmTitle

        val llmAuthor = llmResult?.author
        val finalAuthor = if (llmAuthor.isNullOrBlank()) {
            if (text.contains("by ", ignoreCase = true)) {
                val idx = text.indexOf("by ", ignoreCase = true)
                text.substring(idx + 3, (idx + 50).coerceAtMost(text.length)).lines().firstOrNull()?.trim()
            } else "Unknown Author"
        } else llmAuthor

        val llmChapters = llmResult?.chapters
        val finalChapters = if (llmChapters.isNullOrEmpty()) {
            println("🔍 Local LLM returned no chapters for structure. Using Regex fallback...")
            performRegexChapterDetection(text)
        } else llmChapters

        return DocumentStructureResponse(
            title = finalTitle,
            author = finalAuthor,
            type = llmResult?.type ?: "BOOK",
            language = llmResult?.language ?: "en",
            chapters = finalChapters
        )
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
            return ChapterDetectionResponse(chapters = performRegexChapterDetection(request.fullText))
        }

        return llmResult
    }

    private fun performRegexChapterDetection(text: String): List<DetectedChapter> {
        // Broadened regex to catch:
        // 1. Chapter/Section/Part 1 or I
        // 2. I. Title (Roman numeral start)
        // 3. 1. Title (Numeric start)
        val chapterRegex = Regex("(?i)^((Chapter|Section|Part)\\s+(\\d+|[IVXLC]+|ONE|TWO|THREE|FOUR|FIVE|SIX|SEVEN|EIGHT|NINE|TEN).*)|(^[IVXLC]+\\.\\s+.*)|(^\\d+\\.\\s+.*)", RegexOption.MULTILINE)
        val matches = chapterRegex.findAll(text).toList()
        
        val detected = mutableListOf<DetectedChapter>()
        
        if (matches.isEmpty()) {
            detected.add(
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
            matches.map { match ->
                val nextMatch = matches.getOrNull(matches.indexOf(match) + 1)
                val nextStart = nextMatch?.range?.first ?: text.length
                val title = match.value.trim().take(100)
                
                // Lowered threshold to 150 characters to catch short/intro chapters
                val length = nextStart - match.range.first
                if (length > 150) { 
                    detected.add(
                        DetectedChapter(
                            title = title,
                            index = detected.size + 1,
                            openingText = text.substring(match.range.first, (match.range.first + 200).coerceAtMost(text.length)).replace("\n", " "),
                            startIndex = match.range.first,
                            endIndex = nextStart,
                            confidence = 0.9f
                        )
                    )
                }
            }
        }
        
        if (detected.isEmpty() && text.length > 200) {
            detected.add(DetectedChapter("Main Content", 1, text.take(200), 0, text.length, 0.5f))
        }
        
        return detected
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        val prompt = PromptTemplates.narrationPreparationPrompt(request.text, request.style)
        val response = llmEngine.generate(prompt)
        
        val result = try {
            JsonExtractor.extract<NarrationPreparationResponse>(response)
        } catch (e: Exception) {
            null
        }

        return if (result == null || result.preparedText.isBlank()) {
            NarrationPreparationResponse(
                preparedText = request.text,
                estimatedDurationSeconds = (request.text.length / 15.0),
                notes = "Local AI fallback: Using original text."
            )
        } else {
            result
        }
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        val prompt = PromptTemplates.dialogueDetectionPrompt(request.text)
        val response = llmEngine.generate(prompt)
        
        val result = try {
            JsonExtractor.extract<DialogueDetectionResponse>(response)
        } catch (e: Exception) {
            null
        }

        return if (result == null || result.segments.isEmpty()) {
            DialogueDetectionResponse(
                segments = listOf(
                    DialogueSegment(request.text, "Narrator", false)
                )
            )
        } else {
            result
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
