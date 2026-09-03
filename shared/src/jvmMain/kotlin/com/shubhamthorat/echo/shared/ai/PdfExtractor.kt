package com.shubhamthorat.echo.shared.ai

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

actual class PdfExtractor actual constructor() {
    actual suspend fun extractText(path: String): String {
        return try {
            val file = File(path)
            val document = Loader.loadPDF(file)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            text
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
