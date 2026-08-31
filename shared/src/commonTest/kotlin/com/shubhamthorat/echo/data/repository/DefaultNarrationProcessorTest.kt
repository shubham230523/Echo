package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultNarrationProcessorTest {

    private val processor = DefaultNarrationProcessor()

    @Test
    fun shouldNormalizeWhitespaceWhilePreservingParagraphs() = runTest {
        val originalText = """
            This is paragraph   one.
            It has multiple   spaces.
            
            This is paragraph   two.
            It also has   extra   spaces.
        """.trimIndent()

        val chapter = Chapter(
            id = "ch1",
            documentId = "doc1",
            index = 0,
            title = "Title",
            originalText = originalText,
            narrationText = "",
            estimatedDurationSeconds = 0,
            status = ChapterStatus.PENDING
        )

        val request = NarrationPreparationRequest(
            chapter = chapter,
            language = "en",
            style = NarrationStyle.NATURAL
        )

        val result = processor.prepareNarration(request)

        assertTrue(result is AppResult.Success)
        
        val expectedText = "This is paragraph one. It has multiple spaces.\n\nThis is paragraph two. It also has extra spaces."
        assertEquals(expectedText, result.data.narrationText)
    }

    @Test
    fun shouldHandleEmptyText() = runTest {
        val chapter = Chapter(
            id = "ch2",
            documentId = "doc1",
            index = 1,
            title = "Empty",
            originalText = "   ",
            narrationText = "",
            estimatedDurationSeconds = 0,
            status = ChapterStatus.PENDING
        )

        val request = NarrationPreparationRequest(
            chapter = chapter,
            language = "en",
            style = NarrationStyle.NATURAL
        )

        val result = processor.prepareNarration(request)

        assertTrue(result is AppResult.Success)
        assertEquals("", result.data.narrationText)
        assertEquals("Empty content", result.data.changesSummary)
    }
}
