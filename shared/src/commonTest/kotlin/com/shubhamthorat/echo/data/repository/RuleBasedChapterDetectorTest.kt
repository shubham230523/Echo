package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.ChapterDetectionRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RuleBasedChapterDetectorTest {

    private val detector = RuleBasedChapterDetector()

    @Test
    fun shouldDetectCommonChapterPatterns() = runTest {
        val text = """
            Title Page
            Some preface text that is long enough to be kept.
            
            Chapter 1
            Content of chapter one.
            
            CHAPTER 2: The Return
            Content of chapter two.
            
            Part 1: The Beginning
            Content of part one.
            
            Section 3
            Content of section three.
            
            Chapter Ten
            The end.
        """.trimIndent()

        val request = ChapterDetectionRequest("doc123", text)
        val result = detector.detectChapters(request)

        assertTrue(result is AppResult.Success)
        val chapters = result.data.chapters

        // Expected: Intro + Chapter 1 + CHAPTER 2 + Part 1 + Section 3 + Chapter Ten = 6 chapters
        assertEquals(6, chapters.size)
        
        assertEquals("Introduction", chapters[0].title)
        assertEquals("Chapter 1", chapters[1].title)
        assertEquals("CHAPTER 2: The Return", chapters[2].title)
        assertEquals("Part 1: The Beginning", chapters[3].title)
        assertEquals("Section 3", chapters[4].title)
        assertEquals("Chapter Ten", chapters[5].title)
        
        assertTrue(chapters[1].originalText.contains("Content of chapter one"))
    }

    @Test
    fun shouldFallbackToSingleChapterIfNoneDetected() = runTest {
        val text = "Just some plain text without any specific headings."
        val request = ChapterDetectionRequest("doc456", text)
        val result = detector.detectChapters(request)

        assertTrue(result is AppResult.Success)
        assertEquals(1, result.data.chapters.size)
        assertEquals("Document", result.data.chapters[0].title)
        assertEquals(text, result.data.chapters[0].originalText)
    }

    @Test
    fun shouldHandleEmptyText() = runTest {
        val request = ChapterDetectionRequest("doc789", "")
        val result = detector.detectChapters(request)

        assertTrue(result is AppResult.Success)
        assertTrue(result.data.chapters.isEmpty())
    }
}
