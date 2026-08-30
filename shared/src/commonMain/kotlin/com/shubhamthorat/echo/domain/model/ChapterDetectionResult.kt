package com.shubhamthorat.echo.domain.model

/**
 * Result model for the chapter detection process.
 *
 * @property chapters The list of logical chapters detected in the text.
 * @property detectionMethod Descriptive name of the detection strategy used (e.g., "RuleBased", "AI").
 * @property metadata Optional key-value pairs containing detection statistics or debug info.
 */
data class ChapterDetectionResult(
    val chapters: List<Chapter>,
    val detectionMethod: String,
    val metadata: Map<String, String> = emptyMap()
)
