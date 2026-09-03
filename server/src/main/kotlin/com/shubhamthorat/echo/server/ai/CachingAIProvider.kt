package com.shubhamthorat.echo.server.ai

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest

/**
 * A decorator for [AIProvider] that caches responses locally to save API costs.
 */
class CachingAIProvider(
    private val delegate: AIProvider,
    private val cacheDir: File,
    private val modelName: String
) : AIProvider {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse {
        return getOrFetch("analyze_structure", request.fullText) {
            delegate.analyzeDocumentStructure(request)
        }
    }

    override suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse {
        return getOrFetch("detect_chapters", request.fullText) {
            delegate.detectChapters(request)
        }
    }

    override suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse {
        return getOrFetch("prepare_narration", request.text) {
            delegate.prepareNarration(request)
        }
    }

    override suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse {
        return getOrFetch("detect_dialogue", request.text) {
            delegate.detectDialogue(request)
        }
    }

    override suspend fun assistPronunciation(request: PronunciationRequest): PronunciationResponse {
        return getOrFetch("assist_pronunciation", request.text) {
            delegate.assistPronunciation(request)
        }
    }

    override suspend fun transcribeAudio(request: TranscriptionRequest): TranscriptionResponse {
        return delegate.transcribeAudio(request) // Don't cache transcription (files are large)
    }

    override suspend fun compareTranscription(request: ContentComparisonRequest): ContentComparisonResponse {
        return delegate.compareTranscription(request)
    }

    private suspend inline fun <reified T> getOrFetch(
        category: String,
        content: String,
        crossinline fetch: suspend () -> T
    ): T {
        val hash = sha256("$modelName|$content")
        val cacheFile = File(cacheDir, "${category}_$hash.json")

        if (cacheFile.exists()) {
            println("♻️ AI CACHE HIT [$category]: ${cacheFile.name}")
            return json.decodeFromString<T>(cacheFile.readText())
        }

        println("🌐 AI CACHE MISS [$category]: Calling remote provider...")
        val response = fetch()
        
        try {
            cacheFile.writeText(json.encodeToString(response))
        } catch (e: Exception) {
            println("⚠️ Failed to write AI cache: ${e.message}")
        }
        
        return response
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
