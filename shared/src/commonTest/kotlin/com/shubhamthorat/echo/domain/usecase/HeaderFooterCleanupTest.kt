package com.shubhamthorat.echo.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

class HeaderFooterCleanupTest {

    private val useCase = CleanDocumentTextUseCase()

    @Test
    fun shouldRemoveConsistentHeadersAcrossMultiplePages() {
        val pages = listOf(
            "Echo User Guide\nChapter 1\nContent of page 1",
            "Echo User Guide\nChapter 2\nContent of page 2",
            "Echo User Guide\nChapter 3\nContent of page 3",
            "Echo User Guide\nChapter 4\nContent of page 4"
        )
        
        // "Echo User Guide" appears as the first line in all 4 pages.
        // Threshold is max(3, 4 * 0.5) = 3.
        // It should be removed.
        
        val result = useCase(pages)
        
        // Pages are joined by \n\n
        // Page 1: "Chapter 1\nContent of page 1"
        // Page 2: "Chapter 2\nContent of page 2"
        // ...
        
        val expected = "Chapter 1\nContent of page 1\n\nChapter 2\nContent of page 2\n\nChapter 3\nContent of page 3\n\nChapter 4\nContent of page 4"
        assertEquals(expected, result)
    }

    @Test
    fun shouldRemoveConsistentFootersAcrossMultiplePages() {
        val pages = listOf(
            "Content 1\n(c) 2024 Echo Inc",
            "Content 2\n(c) 2024 Echo Inc",
            "Content 3\n(c) 2024 Echo Inc",
            "Content 4\n(c) 2024 Echo Inc"
        )
        
        val result = useCase(pages)
        
        val expected = "Content 1\n\nContent 2\n\nContent 3\n\nContent 4"
        assertEquals(expected, result)
    }

    @Test
    fun shouldNotRemoveHeadersIfThresholdNotMet() {
        val pages = listOf(
            "Header A\nContent 1",
            "Header B\nContent 2",
            "Header A\nContent 3"
        )
        
        // "Header A" appears in 2 out of 3 pages.
        // Threshold is max(3, 3 * 0.5) = 3.
        // It should NOT be removed.
        
        val result = useCase(pages)
        
        val expected = "Header A\nContent 1\n\nHeader B\nContent 2\n\nHeader A\nContent 3"
        assertEquals(expected, result)
    }

    @Test
    fun shouldBeConservativeAndKeepContentIfSmallNumberOfPages() {
        val pages = listOf(
            "Same Header\nContent 1",
            "Same Header\nContent 2"
        )
        
        // Only 2 pages. UseCase skips header/footer removal if pages < 3.
        val result = useCase(pages)
        
        val expected = "Same Header\nContent 1\n\nSame Header\nContent 2"
        assertEquals(expected, result)
    }
}
