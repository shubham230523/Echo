package com.shubhamthorat.echo.shared.ai

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor

actual class PdfExtractor actual constructor() {
    actual suspend fun extractText(path: String): String {
        return try {
            val reader = PdfReader(path)
            val pdfDoc = PdfDocument(reader)
            val stringBuilder = StringBuilder()
            for (i in 1..pdfDoc.numberOfPages) {
                stringBuilder.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)))
            }
            pdfDoc.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
