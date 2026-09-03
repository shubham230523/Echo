package com.shubhamthorat.echo.server.document

import com.shubhamthorat.echo.server.ai.*
import com.shubhamthorat.echo.server.api.dto.v1.GetChaptersResponse.ChapterDto
import com.shubhamthorat.echo.server.core.retryWithBackoff
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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
            
            // Parallel extraction could be done here if we used multiple PDFBox instances, 
            // but PDFBox document is not thread-safe. We'll extract sequentially but fast.
            for (page in 1..pageCount) {
                stripper.startPage = page
                stripper.endPage = page
                fullTextBuilder.append(stripper.getText(document))
            }

            val fullText = fullTextBuilder.toString()
            
            // Project Gutenberg / Boilerplate cleanup
            val cleanedText = cleanupText(fullText)

            // Smaller chunks for higher reliability on free AI models
            val chunkSize = 15 
            val overlap = 2
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
        println("🤖 Node: BatchAnalysisNode - Processing ${state.chunks.size} chunks with controlled parallelism")
        
        // 1. Separate Metadata Extraction (Lightweight call on first 5 pages)
        val metadataSample = state.fullText.take(15000)
        val metadata = retryWithBackoff(maxRetries = 2) {
            println("  Agent [Metadata]: Extracting book info...")
            aiProvider.analyzeDocumentStructure(DocumentStructureRequest(fullText = metadataSample))
        }

        // 2. Controlled Parallel Chapter Detection
        val aiSemaphore = Semaphore(2) // Max 2 concurrent AI requests for free tier
        
        val deferredChapters = state.chunks.map { chunk ->
            async {
                aiSemaphore.withPermit {
                    retryWithBackoff(maxRetries = 3) {
                        println("  Agent [${chunk.pageRange}]: Detecting chapters (Length: ${chunk.text.length})")
                        val response = aiProvider.detectChapters(
                            ChapterDetectionRequest(fullText = chunk.text)
                        )
                        response.chapters.map { it.copy(startIndex = it.startIndex + chunk.startIndex) }
                    }
                }
            }
        }

        val results = deferredChapters.awaitAll()
        val allChapters = results.flatten()

        state.copy(
            rawChapters = allChapters,
            title = metadata.title,
            author = metadata.author,
            type = metadata.type,
            language = metadata.language
        )
    }

    internal fun reconcileChaptersNode(state: AnalysisState): AnalysisState {
        println("🧩 Node: ReconcileChaptersNode")
        
        // Remove duplicates caused by chunk overlaps
        // We use a fuzzy similarity check on titles and proximity of start indices
        val reconciled = mutableListOf<DetectedChapter>()
        val sortedRaw = state.rawChapters.filter { it.startIndex != -1 }.sortedBy { it.startIndex }

        for (chapter in sortedRaw) {
            val last = reconciled.lastOrNull()
            if (last == null) {
                reconciled.add(chapter)
                continue
            }

            // If start indices are very close (within 1000 chars) AND titles are similar, skip
            val distance = chapter.startIndex - last.startIndex
            if (distance < 2000 && isSimilar(chapter.title, last.title)) {
                // Keep the one with higher confidence or just the first one found
                continue
            }
            
            reconciled.add(chapter)
        }

        // Map to ChapterDto and slice content
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
        // Project Gutenberg specific cleanup: Trim boilerplate
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
