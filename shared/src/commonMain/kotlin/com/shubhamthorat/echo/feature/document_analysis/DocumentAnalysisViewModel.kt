package com.shubhamthorat.echo.feature.document_analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.domain.model.AnalysisStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the document analysis process.
 * Currently implements a simulated analysis for UI demonstration.
 */
class DocumentAnalysisViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentAnalysisUiState())
    val uiState: StateFlow<DocumentAnalysisUiState> = _uiState.asStateFlow()

    init {
        startSimulatedAnalysis()
    }

    /**
     * Simulates the multi-stage document analysis process.
     */
    private fun startSimulatedAnalysis() {
        viewModelScope.launch {
            val stages = listOf(
                AnalysisStage.READING_DOCUMENT to "Opening the file and preparing for processing.",
                AnalysisStage.EXTRACTING_TEXT to "Reading the raw content from the PDF pages.",
                AnalysisStage.ANALYZING_STRUCTURE to "Identifying headers, paragraphs, and metadata.",
                AnalysisStage.DETECTING_CHAPTERS to "Organizing the content into logical sections."
            )
            
            stages.forEachIndexed { index, (stage, message) ->
                // Update state for current stage
                _uiState.update { 
                    it.copy(
                        currentStage = stage,
                        progress = index.toFloat() / (stages.size + 1),
                        statusMessage = message
                    )
                }

                // Simulate work
                delay(1500)
            }

            // Mark as completed
            _uiState.update { 
                it.copy(
                    currentStage = AnalysisStage.COMPLETED,
                    statusMessage = "Analysis complete!",
                    progress = 1f,
                    isCompleted = true
                )
            }
        }
    }
}
