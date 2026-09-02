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

                // Single Pass Stage: Get Verbatim Chapter Titles from AI
                val analysis = aiProvider.analyzeDocumentStructure(
                    DocumentStructureRequest(fullText = fullText)
                )

                // Hybrid Sequential Search: Locate headers in the actual text in order
                val detectedChapters = mutableListOf<com.shubhamthorat.echo.server.ai.DetectedChapter>()
                
                // Start searching after the likely TOC area (first 5% of text or min 2000 chars)
                var searchFromIndex = Math.min(fullText.length, Math.max(2000, (fullText.length * 0.05).toInt()))
                
                analysis.chapters.forEachIndexed { i, aiChapter ->
                    val rawTitle = aiChapter.title
                    
                    // Search strategy 1: Exact match
                    var foundIdx = fullText.indexOf(rawTitle, searchFromIndex, ignoreCase = true)
                    
                    // Search strategy 2: Just the Chapter number (e.g. "CHAPTER XXX")
                    if (foundIdx == -1 && rawTitle.contains("CHAPTER", ignoreCase = true)) {
                        val chapterNum = rawTitle.substringBefore(".").trim()
                        foundIdx = fullText.indexOf(chapterNum, searchFromIndex, ignoreCase = true)
                    }
                    
                    // Search strategy 3: Just the title part
                    if (foundIdx == -1 && rawTitle.contains(".")) {
                        val titlePart = rawTitle.substringAfter(".").trim()
                        if (titlePart.length > 5) {
                            foundIdx = fullText.indexOf(titlePart, searchFromIndex, ignoreCase = true)
                        }
                    }

                    if (foundIdx != -1) {
                        detectedChapters.add(
                            com.shubhamthorat.echo.server.ai.DetectedChapter(
                                title = rawTitle,
                                index = aiChapter.index,
                                startIndex = foundIdx,
                                confidence = 1.0f
                            )
                        )
                        searchFromIndex = foundIdx + 10 // Advance slightly
                    } else {
                        // Mark as missing but keep the slot to prevent shifting
                        detectedChapters.add(
                            com.shubhamthorat.echo.server.ai.DetectedChapter(
                                title = rawTitle,
                                index = aiChapter.index,
                                startIndex = -1,
                                confidence = 0f
                            )
                        )
                    }
                }

                // Clean Slicing: Only slice between valid start points
                val validChapters = detectedChapters.filter { it.startIndex != -1 }.sortedBy { it.startIndex }
                
                val finalChapters = detectedChapters.map { chapter ->
                    if (chapter.startIndex == -1) return@map chapter.copy(endIndex = -1)
                    
                    // Find the next valid start index in the document
                    val nextChapter = validChapters.firstOrNull { it.startIndex > chapter.startIndex }
                    val nextStart = nextChapter?.startIndex ?: fullText.length
                    chapter.copy(endIndex = nextStart)
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
                    hierarchy = finalChapters.map { chapter ->
                        val content = if (chapter.startIndex != -1) {
                            val safeStart = Math.max(0, Math.min(fullText.length, chapter.startIndex))
                            val safeEnd = Math.max(safeStart, Math.min(fullText.length, chapter.endIndex))
                            fullText.substring(safeStart, safeEnd).trim()
                        } else {
                            "[Content not found for this chapter header in the text]"
                        }
                        
                        ChapterDto(
                            id = UUID.randomUUID().toString(),
                            title = chapter.title,
                            index = chapter.index,
                            content = content,
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
