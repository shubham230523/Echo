package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the core stages of the document analysis process.
 */
@Serializable
enum class AnalysisStage {
    READING_DOCUMENT,
    EXTRACTING_TEXT,
    ANALYZING_STRUCTURE,
    DETECTING_CHAPTERS,
    COMPLETED
}
