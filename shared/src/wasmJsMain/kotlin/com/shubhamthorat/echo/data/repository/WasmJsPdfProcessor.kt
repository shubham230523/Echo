package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.repository.PdfProcessor

class WasmJsPdfProcessor : PdfProcessor {
    override suspend fun extractText(document: Document): AppResult<List<String>> {
        return AppResult.Success(listOf("Mocked text for Web"))
    }
}
