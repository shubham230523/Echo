package com.shubhamthorat.echo.server.document

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.util.*

class DocumentService {

    fun analyzePdf(file: File): AnalysisResult {
        return try {
            Loader.loadPDF(file).use { document ->
                val stripper = PDFTextStripper()
                var totalChars = 0
                var totalWords = 0
                val pageCount = document.numberOfPages
                
                for (page in 1..pageCount) {
                    stripper.startPage = page
                    stripper.endPage = page
                    val pageText = stripper.getText(document)
                    totalChars += pageText.length
                    totalWords += countWords(pageText)
                }

                AnalysisResult(
                    analysisId = UUID.randomUUID().toString(),
                    fileName = file.name,
                    pageCount = pageCount,
                    totalCharacters = totalChars,
                    totalWords = totalWords,
                    status = "ANALYZED"
                )
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to analyze PDF: ${e.message}")
        }
    }

    private fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split(Regex("\\s+")).size
    }
}

data class AnalysisResult(
    val analysisId: String,
    val fileName: String,
    val pageCount: Int,
    val totalCharacters: Int,
    val totalWords: Int,
    val status: String
)
