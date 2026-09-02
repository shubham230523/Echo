package com.shubhamthorat.echo.server.document

import com.shubhamthorat.echo.server.ai.AIProvider
import com.shubhamthorat.echo.server.ai.ChapterDetectionRequest
import com.shubhamthorat.echo.server.ai.DocumentStructureRequest
import com.shubhamthorat.echo.server.ai.DocumentStructureResponse
import com.shubhamthorat.echo.server.api.dto.v1.GetChaptersResponse.ChapterDto
import kotlinx.serialization.Serializable
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

                var fullText = fullTextBuilder.toString()

                // Project Gutenberg specific cleanup: Trim boilerplate
                val startMarkers = listOf("*** START OF", "PROJECT GUTENBERG EBOOK")
                val endMarkers = listOf("*** END OF", "END OF THE PROJECT GUTENBERG EBOOK")
                
                var actualStart = 0
                for (marker in startMarkers) {
                    val idx = fullText.indexOf(marker, ignoreCase = true)
                    if (idx != -1) {
                        val lineEnd = fullText.indexOf("\n", idx)
                        if (lineEnd != -1) {
                            actualStart = lineEnd
                            break
                        }
                    }
                }
                
                var actualEnd = fullText.length
                for (marker in endMarkers) {
                    val idx = fullText.lastIndexOf(marker, ignoreCase = true)
                    if (idx != -1 && idx > (fullText.length / 2)) {
                        actualEnd = idx
                        break
                    }
                }
                
                fullText = fullText.substring(actualStart, actualEnd).trim()

                // Single Pass Stage: Complete AI Analysis (Metadata + Chapters + Offsets)
                val analysis = aiProvider.analyzeDocumentStructure(
                    DocumentStructureRequest(fullText = fullText)
                )

                var detectedChapters = analysis.chapters

                // Fallback & Validation
                if (detectedChapters.isEmpty()) {
                    detectedChapters = performRuleBasedChapterDetection(fullText)
                }

                AnalysisResult(
                    analysisId = UUID.randomUUID().toString(),
                    fileName = file.name,
                    pageCount = pageCount,
                    totalCharacters = totalChars,
                    totalWords = totalWords,
                    title = analysis.title,
                    author = analysis.author,
                    documentType = analysis.type,
                    language = analysis.language,
                    hierarchy = detectedChapters.map { chapter ->
                        val safeStart = Math.max(0, Math.min(fullText.length, chapter.startIndex))
                        val safeEnd = Math.max(safeStart, Math.min(fullText.length, chapter.endIndex))
                        ChapterDto(
                            id = UUID.randomUUID().toString(),
                            title = chapter.title,
                            index = chapter.index,
                            content = fullText.substring(safeStart, safeEnd).trim(),
                            byteOffset = safeStart.toLong()
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
        // More specific regex that looks for typical book chapter starts
        // Also look for Roman Numerals (I, II, III...)
        val chapterRegex = Regex("(?i)^(chapter|section|part)\\s+(\\d+|[ivxlc]+).*$", RegexOption.MULTILINE)
        
        // Filter out sections that look like legal boilerplate (e.g. from Project Gutenberg)
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
