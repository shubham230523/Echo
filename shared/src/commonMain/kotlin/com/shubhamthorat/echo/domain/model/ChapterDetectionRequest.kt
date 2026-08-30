package com.shubhamthorat.echo.domain.model

/**
 * Request model for detecting chapters within a document's text.
 *
 * @property documentId The ID of the document being processed.
 * @property cleanedText The cleaned raw text of the document.
 * @property options Additional configuration for the detection process.
 */
data class ChapterDetectionRequest(
    val documentId: String,
    val cleanedText: String,
    val options: Map<String, String> = emptyMap()
)
