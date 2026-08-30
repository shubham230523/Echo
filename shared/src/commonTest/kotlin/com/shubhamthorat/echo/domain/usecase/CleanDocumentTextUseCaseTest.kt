package com.shubhamthorat.echo.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

class CleanDocumentTextUseCaseTest {

    private val useCase = CleanDocumentTextUseCase()

    @Test
    fun shouldNormalizeRepeatedSpaces() {
        val input = "This  is    a  test."
        val expected = "This is a test."
        assertEquals(expected, useCase(input))
    }

    @Test
    fun shouldRemoveExcessiveBlankLines() {
        val input = "Line 1\n\n\n\nLine 2\n\n\nLine 3"
        val expected = "Line 1\n\nLine 2\n\nLine 3"
        assertEquals(expected, useCase(input))
    }

    @Test
    fun shouldTrimLeadingAndTrailingWhitespaces() {
        val input = "   Leading and trailing   "
        val expected = "Leading and trailing"
        assertEquals(expected, useCase(input))
    }

    @Test
    fun shouldHandleEmptyInput() {
        assertEquals("", useCase(""))
        assertEquals("", useCase("   "))
        assertEquals("", useCase("\n\n"))
    }

    @Test
    fun shouldPreserveSingleBlankLines() {
        val input = "Line 1\n\nLine 2"
        val expected = "Line 1\n\nLine 2"
        assertEquals(expected, useCase(input))
    }
}
