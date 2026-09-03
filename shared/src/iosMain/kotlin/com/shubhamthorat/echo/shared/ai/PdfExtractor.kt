package com.shubhamthorat.echo.shared.ai

import platform.Foundation.*
import platform.PDFKit.*

actual class PdfExtractor actual constructor() {
    actual suspend fun extractText(path: String): String {
        val url = NSURL.fileURLWithPath(path)
        val document = PDFDocument(uRL = url)
        return document?.string() ?: ""
    }
}
