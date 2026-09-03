package com.shubhamthorat.echo.shared.ai

import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class PdfExtractionTest {

    @Test
    fun testReferenceBookExtraction() = runTest {
        val extractor = PdfExtractor()
        val path = "../server/reference_book.pdf"
        val file = File(path)
        
        if (file.exists()) {
            val text = extractor.extractText(file.absolutePath)
            assertTrue(text.isNotEmpty(), "Extracted text should not be empty")
            println("Extracted ${text.length} characters from reference_book.pdf")
        } else {
            println("reference_book.pdf not found at $path")
        }
    }
}
