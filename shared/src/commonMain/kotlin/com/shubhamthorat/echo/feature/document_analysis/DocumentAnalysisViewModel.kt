package com.shubhamthorat.echo.feature.document_analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.common.PlatformFile
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.AnalysisStage
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.model.DocumentStatus
import com.shubhamthorat.echo.domain.model.ChapterDetectionRequest
import com.shubhamthorat.echo.domain.repository.ChapterDetector
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import com.shubhamthorat.echo.domain.usecase.CleanDocumentTextUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * ViewModel for managing the document analysis process.
 */
class DocumentAnalysisViewModel(
    private val pdfProcessor: PdfProcessor,
    private val cleanDocumentTextUseCase: CleanDocumentTextUseCase,
    private val chapterDetector: ChapterDetector,
    private val currentAnalysisRepository: CurrentAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentAnalysisUiState())
    val uiState: StateFlow<DocumentAnalysisUiState> = _uiState.asStateFlow()

    /**
     * Starts the analysis process for the selected file.
     */
    fun startAnalysis(file: PlatformFile) {
        viewModelScope.launch {
            // 1. Reading stage
            _uiState.update { 
                it.copy(
                    currentStage = AnalysisStage.READING_DOCUMENT,
                    progress = 0.2f,
                    statusMessage = "Opening ${file.name}..."
                )
            }
            delay(1000)

            // 2. Extraction stage
            _uiState.update { 
                it.copy(
                    currentStage = AnalysisStage.EXTRACTING_TEXT,
                    progress = 0.4f,
                    statusMessage = "Extracting text from PDF..."
                )
            }

            // Create a domain Document object for the processor
            val document = Document(
                id = "temp_id",
                fileName = file.name,
                filePath = file.path,
                fileSizeBytes = file.sizeBytes ?: 0L,
                pageCount = 0,
                importedAt = Instant.fromEpochMilliseconds(0), // Temporary fallback
                status = DocumentStatus.ANALYZING
            )

            val result = pdfProcessor.extractText(document)

            when (result) {
                is AppResult.Success -> {
                    // 3. Cleanup stage
                    val cleanedText = cleanDocumentTextUseCase(result.data)
                    
                    // 4. Chapter detection stage
                    _uiState.update { 
                        it.copy(
                            currentStage = AnalysisStage.DETECTING_CHAPTERS,
                            progress = 0.8f,
                            statusMessage = "Identifying chapters..."
                        )
                    }

                    val detectionResult = chapterDetector.detectChapters(
                        ChapterDetectionRequest(
                            documentId = document.id,
                            cleanedText = cleanedText
                        )
                    )

                    when (detectionResult) {
                        is AppResult.Success -> {
                            currentAnalysisRepository.setAnalysisResult(
                                document = document,
                                chapters = detectionResult.data.chapters
                            )
                            
                            _uiState.update { 
                                it.copy(
                                    currentStage = AnalysisStage.COMPLETED,
                                    progress = 1.0f,
                                    statusMessage = "Analysis complete: ${detectionResult.data.chapters.size} chapters found.",
                                    isCompleted = true
                                )
                            }
                        }
                        is AppResult.Error -> {
                            _uiState.update { 
                                it.copy(
                                    error = detectionResult.message,
                                    statusMessage = "Chapter detection failed: ${detectionResult.message}"
                                )
                            }
                        }
                        AppResult.Loading -> {}
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.message,
                            statusMessage = "Extraction failed: ${result.message}"
                        )
                    }
                }
                AppResult.Loading -> {
                    // Handled by stage updates
                }
            }
        }
    }
}
