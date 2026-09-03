package com.shubhamthorat.echo.server.document

import com.shubhamthorat.echo.server.ai.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class AnalysisWorkflowTest {

    private val aiProvider = object : AIProvider {
        override suspend fun analyzeDocumentStructure(request: DocumentStructureRequest) = TODO()
        override suspend fun detectChapters(request: ChapterDetectionRequest) = TODO()
        override suspend fun prepareNarration(request: NarrationPreparationRequest) = TODO()
        override suspend fun detectDialogue(request: DialogueDetectionRequest) = TODO()
        override suspend fun assistPronunciation(request: PronunciationRequest) = TODO()
        override suspend fun transcribeAudio(request: TranscriptionRequest) = TODO()
        override suspend fun compareTranscription(request: ContentComparisonRequest) = TODO()
    }
    
    private val workflow = AnalysisWorkflow(aiProvider)

    @Test
    fun `reconcileChaptersNode merges overlapping chapters`() {
        val state = AnalysisState(
            file = File("dummy.pdf"),
            fullText = "Chapter 1 text. Chapter 2 text. Chapter 3 text.",
            rawChapters = listOf(
                DetectedChapter(title = "Chapter 1", index = 1, startIndex = 0),
                DetectedChapter(title = "Chapter 1", index = 1, startIndex = 5), // Overlap
                DetectedChapter(title = "Chapter 2", index = 2, startIndex = 20),
                DetectedChapter(title = "Chapter 3", index = 3, startIndex = 40)
            )
        )

        val result = workflow.reconcileChaptersNode(state)
        
        assertEquals(3, result.finalChapters.size)
        assertEquals("Chapter 1", result.finalChapters[0].title)
        assertEquals("Chapter 2", result.finalChapters[1].title)
        assertEquals("Chapter 3", result.finalChapters[2].title)
        assertEquals(0L, result.finalChapters[0].byteOffset)
        assertEquals(20L, result.finalChapters[1].byteOffset)
    }
}
