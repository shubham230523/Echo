package com.shubhamthorat.echo.feature.document_analysis

/**
 * Represents the distinct stages of the document analysis process.
 */
enum class AnalysisStage(val title: String, val description: String) {
    READING("Reading document", "Opening the file and preparing for processing."),
    EXTRACTING("Extracting text", "Reading the raw content from the PDF pages."),
    UNDERSTANDING("Understanding structure", "Identifying headers, paragraphs, and metadata."),
    DETECTING("Detecting chapters", "Organizing the content into logical sections."),
    PREPARING("Preparing narration", "Optimizing text for natural AI voice generation.")
}

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
    val currentStage: AnalysisStage = AnalysisStage.READING,
    val progress: Float = 0f,
    val statusMessage: String = AnalysisStage.READING.description,
    val isCompleted: Boolean = false,
    val error: String? = null
)
