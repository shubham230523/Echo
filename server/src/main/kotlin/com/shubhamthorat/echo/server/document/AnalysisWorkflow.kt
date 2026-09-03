package com.shubhamthorat.echo.server.document

import com.shubhamthorat.echo.server.ai.*
import com.shubhamthorat.echo.server.api.dto.v1.GetChaptersResponse.ChapterDto
import com.shubhamthorat.echo.server.core.retryWithBackoff
import kotlinx.coroutines.*
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.util.*

/**
 * Shared state for the document analysis workflow.
 */
data class AnalysisState(
    val file: File,
    val pageCount: Int = 0,
    val fullText: String = "",
    val chunks: List<TextChunk> = emptyList(),
    val rawChapters: List<DetectedChapter> = emptyList(),
    val finalChapters: List<ChapterDto> = emptyList(),
    val title: String = "",
    val author: String? = null,
    val type: String = "BOOK",
    val language: String = "en"
)

data class TextChunk(
    val startIndex: Int,
    val endIndex: Int,
    val text: String,
    val pageRange: IntRange
)

class AnalysisWorkflow(private val aiProvider: AIProvider) {

    suspend fun execute(file: File): AnalysisResult = coroutineScope {
        var state = AnalysisState(file = file)

        // 1. Extract Pages Node
        state = extractPagesNode(state)

        // 2. Batch Analysis Node (Parallel AI Agents)
        state = batchAnalysisNode(state)

        // 3. Reconcile Chapters Node
        state = reconcileChaptersNode(state)

        AnalysisResult(
            analysisId = UUID.randomUUID().toString(),
            fileName = file.name,
            pageCount = state.pageCount,
            totalCharacters = state.fullText.length,
            totalWords = state.fullText.split(Regex("\\s+")).size,
            title = state.title,
            author = state.author,
            documentType = state.type,
            language = state.language,
            hierarchy = state.finalChapters,
            status = "ANALYZED"
        )
    }

    private suspend fun extractPagesNode(state: AnalysisState): AnalysisState = withContext(Dispatchers.IO) {
        println("📖 Node: ExtractPagesNode")
        Loader.loadPDF(state.file).use { document ->
            val stripper = PDFTextStripper()
            val fullTextBuilder = StringBuilder()
            val pageCount = document.numberOfPages
            
            for (page in 1..pageCount) {
                stripper.startPage = page
                stripper.endPage = page
                fullTextBuilder.append(stripper.getText(document))
            }

            val fullText = fullTextBuilder.toString()
            
            // Project Gutenberg / Boilerplate cleanup
            val cleanedText = cleanupText(fullText)

            // Reverting to b2fb240 parameters: 50 pages per chunk with 5 page overlap
            val chunkSize = 50
            val overlap = 5
            val chunks = mutableListOf<TextChunk>()
            
            val charsPerPage = if (pageCount > 0) cleanedText.length / pageCount else cleanedText.length
            val charsPerChunk = chunkSize * charsPerPage
            val charsOverlap = overlap * charsPerPage

            var currentStart = 0
            while (currentStart < cleanedText.length) {
                val currentEnd = (currentStart + charsPerChunk).coerceAtMost(cleanedText.length)
                chunks.add(
                    TextChunk(
                        startIndex = currentStart,
                        endIndex = currentEnd,
                        text = cleanedText.substring(currentStart, currentEnd),
                        pageRange = (currentStart / charsPerPage)..(currentEnd / charsPerPage)
                    )
                )
                if (currentEnd >= cleanedText.length) break
                currentStart = currentEnd - charsOverlap
                if (currentStart < 0) currentStart = 0
            }

            state.copy(
                pageCount = pageCount,
                fullText = cleanedText,
                chunks = chunks
            )
        }
    }

    private suspend fun batchAnalysisNode(state: AnalysisState): AnalysisState = coroutineScope {
        println("🤖 Node: BatchAnalysisNode - Processing ${state.chunks.size} chunks in parallel")
        
        val deferredChapters = state.chunks.mapIndexed { index, chunk ->
            async {
                retryWithBackoff(maxRetries = 3) {
                    println("  Agent $index: Analyzing pages ${chunk.pageRange}")
                    // Reverting to b2fb240 logic: First chunk also gets the overall book metadata
                    if (index == 0) {
                        val response = aiProvider.analyzeDocumentStructure(
                            DocumentStructureRequest(fullText = chunk.text)
                        )
                        // Adjust offsets back to global space
                        val adjusted = response.chapters.map { it.copy(startIndex = it.startIndex + chunk.startIndex) }
                        Triple(adjusted, response.title, response.author)
                    } else {
                        val response = aiProvider.detectChapters(
                            ChapterDetectionRequest(fullText = chunk.text)
                        )
                        val adjusted = response.chapters.map { it.copy(startIndex = it.startIndex + chunk.startIndex) }
                        Triple(adjusted, null, null)
                    }
                }
            }
        }

        val results = deferredChapters.awaitAll()
        val allChapters = results.flatMap { it.first }
        val title = results.firstOrNull { it.second != null }?.second ?: "Unknown Title"
        val author = results.firstOrNull { it.third != null }?.third

        state.copy(
            rawChapters = allChapters,
            title = title,
            author = author
        )
    }

    internal fun reconcileChaptersNode(state: AnalysisState): AnalysisState {
        println("🧩 Node: ReconcileChaptersNode")
        
        val reconciled = mutableListOf<DetectedChapter>()
        val sortedRaw = state.rawChapters.filter { it.startIndex != -1 }.sortedBy { it.startIndex }

        for (chapter in sortedRaw) {
            val last = reconciled.lastOrNull()
            if (last == null) {
                reconciled.add(chapter)
                continue
            }

            val distance = chapter.startIndex - last.startIndex
            if (distance < 2000 && isSimilar(chapter.title, last.title)) {
                continue
            }
            
            reconciled.add(chapter)
        }

        val finalChapters = reconciled.mapIndexed { index, chapter ->
            val nextStart = reconciled.getOrNull(index + 1)?.startIndex ?: state.fullText.length
            val content = state.fullText.substring(chapter.startIndex, nextStart).trim()
            
            ChapterDto(
                id = UUID.randomUUID().toString(),
                title = chapter.title,
                index = index + 1,
                content = content,
                byteOffset = chapter.startIndex.toLong()
            )
        }

        return state.copy(finalChapters = finalChapters)
    }

    private fun cleanupText(text: String): String {
        val startMarkers = listOf("*** START OF", "PROJECT GUTENBERG EBOOK")
        val endMarkers = listOf("*** END OF", "END OF THE PROJECT GUTENBERG EBOOK")
        
        var actualStart = 0
        for (marker in startMarkers) {
            val idx = text.indexOf(marker, ignoreCase = true)
            if (idx != -1) {
                val lineEnd = text.indexOf("\n", idx)
                if (lineEnd != -1) {
                    actualStart = lineEnd
                    break
                }
            }
        }
        
        var actualEnd = text.length
        for (marker in endMarkers) {
            val idx = text.lastIndexOf(marker, ignoreCase = true)
            if (idx != -1 && idx > (text.length / 2)) {
                actualEnd = idx
                break
            }
        }
        
        return text.substring(actualStart, actualEnd).trim()
    }

    private fun isSimilar(t1: String, t2: String): Boolean {
        val n1 = t1.lowercase().replace(Regex("[^a-z0-9]"), "")
        val n2 = t2.lowercase().replace(Regex("[^a-z0-9]"), "")
        return n1 == n2 || n1.contains(n2) || n2.contains(n1)
    }
}
