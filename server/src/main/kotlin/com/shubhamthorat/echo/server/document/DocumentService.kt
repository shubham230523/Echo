package com.shubhamthorat.echo.server.document

import com.shubhamthorat.echo.server.ai.*
import com.shubhamthorat.echo.server.api.dto.v1.GetChaptersResponse.ChapterDto
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

/**
 * Service for document processing and analysis.
 * Now uses [AnalysisWorkflow] for parallel, resilient processing.
 */
class DocumentService(private val aiProvider: AIProvider) {

    private val workflow = AnalysisWorkflow(aiProvider)

    /**
     * Analyzes a PDF document using a parallelized workflow.
     */
    suspend fun analyzePdf(file: File): AnalysisResult {
        return try {
            workflow.execute(file)
        } catch (e: Exception) {
            println("❌ Analysis failed: ${e.message}")
            throw IllegalArgumentException("Failed to analyze PDF: ${e.message}")
        }
    }

    /**
     * Fallback or simplified detection if needed.
     */
    fun performRuleBasedChapterDetection(text: String): List<DetectedChapter> {
        val chapterRegex = Regex("(?i)^(chapter|section|part)\\s+(\\d+|[ivxlc]+).*$", RegexOption.MULTILINE)
        
        val matches = chapterRegex.findAll(text)
            .filter { match ->
                val linesAround = text.substring(
                    Math.max(0, match.range.first - 100),
                    Math.min(text.length, match.range.last + 100)
                )
                !linesAround.contains("Project Gutenberg", ignoreCase = true) &&
                !linesAround.contains("License", ignoreCase = true)
            }
            .toList()
        
        if (matches.isEmpty()) {
            return listOf(
                DetectedChapter(
                    title = "Main Content",
                    index = 1,
                    startIndex = 0,
                    endIndex = text.length,
                    confidence = 0.5f
                )
            )
        }

        return matches.mapIndexed { index, match ->
            val nextStart = if (index + 1 < matches.size) matches[index + 1].range.first else text.length
            DetectedChapter(
                title = match.value,
                index = index + 1,
                startIndex = match.range.first,
                endIndex = nextStart,
                confidence = 0.8f
            )
        }
    }
}

@Serializable
data class AnalysisResult(
    val analysisId: String,
    val fileName: String,
    val pageCount: Int,
    val totalCharacters: Int,
    val totalWords: Int,
    val title: String,
    val author: String?,
    val documentType: String,
    val language: String,
    val hierarchy: List<ChapterDto>,
    val status: String
)
