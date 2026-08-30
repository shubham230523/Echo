package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.Document

/**
 * Platform-independent abstraction for processing PDF documents.
 */
interface PdfProcessor {

    /**
     * Extracts raw text from the given document, separated by page.
     *
     * @param document The document to process.
     * @return [AppResult] containing a list of strings (one per page) on success, or an error state.
     */
    suspend fun extractText(document: Document): AppResult<List<String>>
}
