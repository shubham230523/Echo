package com.shubhamthorat.echo.data.repository

import android.content.Context
import android.net.Uri
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * Android-specific implementation of [PdfProcessor] using iText 7.
 */
class AndroidPdfProcessor(private val context: Context) : PdfProcessor {

    override suspend fun extractText(document: Document): AppResult<List<String>> = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        var pdfDocument: PdfDocument? = null
        
        try {
            val uri = Uri.parse(document.filePath)
            inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext AppResult.Error("Could not open input stream")

            val reader = PdfReader(inputStream)
            pdfDocument = PdfDocument(reader)
            
            val pages = mutableListOf<String>()
            val numberOfPages = pdfDocument.numberOfPages
            
            for (i in 1..numberOfPages) {
                val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i))
                pages.add(pageText)
            }

            if (pages.isEmpty() || pages.all { it.isBlank() }) {
                AppResult.Error("No text content found")
            } else {
                AppResult.Success(pages)
            }
        } catch (e: Exception) {
            AppResult.Error(
                message = "Failed to extract text from ${document.fileName}",
                throwable = e
            )
        } finally {
            try {
                pdfDocument?.close()
                inputStream?.close()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
