package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

/**
 * JVM-specific implementation of [PdfProcessor] using Apache PDFBox.
 */
class JvmPdfProcessor : PdfProcessor {

    override suspend fun extractText(document: Document): AppResult<List<String>> {
        return try {
            val file = File(document.filePath)
            if (!file.exists()) {
                return AppResult.Error("File not found at ${document.filePath}")
            }

            Loader.loadPDF(file).use { pdfDocument ->
                val stripper = PDFTextStripper()
                val pages = mutableListOf<String>()
                
                for (i in 1..pdfDocument.numberOfPages) {
                    stripper.startPage = i
                    stripper.endPage = i
                    val text = stripper.getText(pdfDocument)
                    pages.add(text)
                }
                
                AppResult.Success(pages)
            }
        } catch (e: Exception) {
            AppResult.Error("Failed to extract text: ${e.message}")
        }
    }
}
