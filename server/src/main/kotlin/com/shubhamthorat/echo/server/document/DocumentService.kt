package com.shubhamthorat.echo.server.document

import com.shubhamthorat.echo.server.ai.AIProvider
import com.shubhamthorat.echo.server.ai.ChapterDetectionRequest
import com.shubhamthorat.echo.server.ai.DocumentStructureRequest
import com.shubhamthorat.echo.server.ai.DocumentStructureResponse
import com.shubhamthorat.echo.server.api.dto.v1.GetChaptersResponse.ChapterDto
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.util.*

class DocumentService(private val aiProvider: AIProvider) {

    suspend fun analyzePdf(file: File): AnalysisResult {
        return try {
            Loader.loadPDF(file).use { document ->
                val stripper = PDFTextStripper()
                var totalChars = 0
                var totalWords = 0
                val pageCount = document.numberOfPages
                val fullTextBuilder = StringBuilder()
                
                for (page in 1..pageCount) {
                    stripper.startPage = page
                    stripper.endPage = page
                    val pageText = stripper.getText(document)
                    totalChars += pageText.length
                    totalWords += countWords(pageText)
                    fullTextBuilder.append(pageText)
                }

                val fullText = fullTextBuilder.toString()

                // Stage 1: AI Structure Analysis
                val structure = aiProvider.analyzeDocumentStructure(
                    DocumentStructureRequest(fullText = fullText)
                )

                // Stage 2: AI Chapter Detection
                var chapters = aiProvider.detectChapters(
                    ChapterDetectionRequest(
                        fullText = fullText,
                        structure = structure
                    )
                ).chapters

                // Stage 3: Validation & Fallback
                if (chapters.isEmpty()) {
                    chapters = performRuleBasedChapterDetection(fullText)
                }

                AnalysisResult(
                    analysisId = UUID.randomUUID().toString(),
                    fileName = file.name,
                    pageCount = pageCount,
                    totalCharacters = totalChars,
                    totalWords = totalWords,
                    title = structure.title,
                    author = structure.author,
                    documentType = structure.type,
                    language = structure.language,
                    hierarchy = chapters.map { chapter ->
                        ChapterDto(
                            id = UUID.randomUUID().toString(),
                            title = chapter.title,
                            index = chapter.index,
                            byteOffset = chapter.startIndex.toLong()
                        )
                    },
                    status = "ANALYZED"
                )
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to analyze PDF: ${e.message}")
        }
    }

    private fun performRuleBasedChapterDetection(text: String): List<com.shubhamthorat.echo.server.ai.DetectedChapter> {
        val chapterRegex = Regex("(?i)(chapter|section)\\s+(\\d+|[ivxlc]+)", RegexOption.MULTILINE)
        val matches = chapterRegex.findAll(text).toList()
        
        if (matches.isEmpty()) {
            // Last resort: treat entire document as one chapter
            return listOf(
                com.shubhamthorat.echo.server.ai.DetectedChapter(
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
            com.shubhamthorat.echo.server.ai.DetectedChapter(
                title = match.value,
                index = index + 1,
                startIndex = match.range.first,
                endIndex = nextStart,
                confidence = 0.8f
            )
        }
    }

    private fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split(Regex("\\s+")).size
    }
}

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
