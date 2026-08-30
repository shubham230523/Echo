package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Metadata and summary information resulting from the analysis of a document.
 *
 * @property document The original document that was analyzed.
 * @property title The detected title of the book or document.
 * @property author The detected author.
 * @property extractedTextLength Total number of characters extracted.
 * @property detectedChapterCount Number of logical chapters found.
 * @property language Detected language code.
 */
@Serializable
data class DocumentAnalysisResult(
    val document: Document,
    val title: String?,
    val author: String?,
    val extractedTextLength: Int,
    val detectedChapterCount: Int,
    val language: String?
)
