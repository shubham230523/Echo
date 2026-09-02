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
                val startMarker = "*** START OF"
                val endMarker = "*** END OF"
                val startIdx = fullText.indexOf(startMarker)
                val endIdx = fullText.lastIndexOf(endMarker)
                
                if (startIdx != -1) {
                    // Find the end of the line containing startMarker
                    val lineEnd = fullText.indexOf("\n", startIdx)
                    if (lineEnd != -1) {
                        fullText = fullText.substring(lineEnd).trim()
                    }
                }
                
                if (endIdx != -1 && endIdx > (fullText.length / 2)) {
                    fullText = fullText.substring(0, endIdx).trim()
                }

                // Stage 1: AI Structure Analysis (including TOC)
                val structure = aiProvider.analyzeDocumentStructure(
                    DocumentStructureRequest(fullText = fullText)
                )

                // Stage 2: Robust Chapter Detection
                var detectedChapters = if (structure.tableOfContents.isNotEmpty()) {
                    val titles = structure.tableOfContents.map { it.title }
                    val anchors = aiProvider.findChapterAnchors(fullText, titles)
                    
                    if (anchors.isNotEmpty()) {
                        // Sort anchors by position to ensure order
                        val sortedAnchors = anchors.sortedBy { it.startIndex }
                        
                        sortedAnchors.mapIndexed { index, anchor ->
                            val nextStart = if (index + 1 < sortedAnchors.size) sortedAnchors[index + 1].startIndex else fullText.length
                            com.shubhamthorat.echo.server.ai.DetectedChapter(
                                title = anchor.title,
                                index = index + 1,
                                startIndex = anchor.startIndex,
                                endIndex = nextStart,
                                confidence = 0.9f
                            )
                        }
                    } else {
                        // Fallback to existing logic if anchors failed
                        aiProvider.detectChapters(
                            ChapterDetectionRequest(fullText = fullText, structure = structure)
                        ).chapters
                    }
                } else {
                    aiProvider.detectChapters(
                        ChapterDetectionRequest(fullText = fullText, structure = structure)
                    ).chapters
                }

                // Stage 3: Validation & Rule-based Fallback
                if (detectedChapters.isEmpty()) {
                    detectedChapters = performRuleBasedChapterDetection(fullText)
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
            // ...
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
