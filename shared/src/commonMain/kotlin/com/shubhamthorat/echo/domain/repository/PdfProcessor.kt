package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.Document

/**
 * Platform-independent abstraction for processing PDF documents.
 */
interface PdfProcessor {

    /**
     * Extracts all raw text from the given document.
     *
     * @param document The document to process.
     * @return [AppResult] containing the extracted text on success, or an error state.
     */
    suspend fun extractText(document: Document): AppResult<String>
}
