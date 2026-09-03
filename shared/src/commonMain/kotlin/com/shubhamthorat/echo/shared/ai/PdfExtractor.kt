package com.shubhamthorat.echo.shared.ai

expect class PdfExtractor() {
    suspend fun extractText(path: String): String
}
