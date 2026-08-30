package com.shubhamthorat.echo.feature.document_analysis

import com.shubhamthorat.echo.domain.model.AnalysisStage

/**
 * UI State for the Document Analysis screen.
 *
 * @property currentStage The stage currently being processed.
 * @property progress The progress percentage (0.0 to 1.0).
 * @property statusMessage A short message explaining the current stage.
 * @property isCompleted Whether the entire analysis process has finished.
 * @property error An optional error message if the analysis failed.
 */
data class DocumentAnalysisUiState(
    val currentStage: AnalysisStage = AnalysisStage.READING_DOCUMENT,
    val progress: Float = 0f,
    val statusMessage: String = "Opening the file and preparing for processing.",
    val isCompleted: Boolean = false,
    val error: String? = null
)
