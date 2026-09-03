package com.shubhamthorat.echo.feature.document_analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.common.PlatformFile
import com.shubhamthorat.echo.core.common.getPlatformFileSystem
import com.shubhamthorat.echo.data.remote.EchoApi
import com.shubhamthorat.echo.domain.model.*
import com.shubhamthorat.echo.domain.repository.ChapterRepository
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import com.shubhamthorat.echo.shared.ai.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

/**
 * ViewModel for managing the document analysis process.
 */
class DocumentAnalysisViewModel(
    private val chapterRepository: ChapterRepository,
    private val currentAnalysisRepository: CurrentAnalysisRepository,
    private val echoApi: EchoApi,
    private val documentAnalyzer: DocumentAnalyzer,
    private val modelManager: ModelManager
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
                    progress = 0.1f,
                    statusMessage = "Reading ${file.name}..."
                )
            }
            
            val fileBytes = try {
                getPlatformFileSystem().readBytes(file.path)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to read file: ${e.message}") }
                return@launch
            }

            // 2. Upload & Deep Analysis stage
            _uiState.update { 
                it.copy(
                    currentStage = AnalysisStage.EXTRACTING_TEXT,
                    progress = 0.3f,
                    statusMessage = "Uploading for deep AI analysis..."
                )
            }

            try {
                val response = echoApi.analyzeDocument(fileBytes, file.name)
                
                _uiState.update { 
                    it.copy(
                        currentStage = AnalysisStage.DETECTING_CHAPTERS,
                        progress = 0.7f,
                        statusMessage = "Processing ${response.hierarchy.size} chapters..."
                    )
                }

                val document = Document(
                    id = response.analysisId,
                    fileName = response.fileName,
                    filePath = file.path,
                    fileSizeBytes = response.totalCharacters.toLong(), // Use total chars as a proxy or keep original
                    pageCount = response.pageCount,
                    importedAt = Instant.fromEpochMilliseconds(0),
                    status = DocumentStatus.ANALYZED
                )

                val domainChapters = response.hierarchy.map { dto ->
                    Chapter(
                        id = dto.id,
                        documentId = response.analysisId,
                        index = dto.index,
                        title = dto.title,
                        originalText = dto.content ?: "",
                        narrationText = dto.content ?: "",
                        estimatedDurationSeconds = estimateDuration(dto.content ?: ""),
                        status = ChapterStatus.PENDING
                    )
                }

                // Persist detected chapters
                chapterRepository.deleteChaptersByDocumentId(document.id)
                chapterRepository.insertChapters(domainChapters)

                currentAnalysisRepository.setAnalysisResult(
                    document = document,
                    chapters = domainChapters
                )
                
                _uiState.update { 
                    it.copy(
                        currentStage = AnalysisStage.COMPLETED,
                        progress = 1.0f,
                        statusMessage = "Analysis complete: ${domainChapters.size} chapters found.",
                        isCompleted = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Unknown analysis error",
                        statusMessage = "Analysis failed: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    /**
     * Starts local document analysis using on-device models.
     */
    fun startLocalAnalysis(file: PlatformFile) {
        viewModelScope.launch {
            if (!modelManager.isModelDownloaded(ModelType.EMBEDDING) || !modelManager.isModelDownloaded(ModelType.LLM)) {
                _uiState.update { it.copy(error = "Required AI models are not downloaded.") }
                return@launch
            }

            _uiState.update { 
                it.copy(
                    currentStage = AnalysisStage.EXTRACTING_TEXT,
                    progress = 0.1f,
                    statusMessage = "Analyzing document locally..."
                )
            }

            documentAnalyzer.ingestDocument(file.path).collect { progress ->
                _uiState.update { 
                    it.copy(
                        progress = progress,
                        statusMessage = "Ingesting document: ${(progress * 100).toInt()}%"
                    )
                }
            }

            // For local analysis, we might need a way to detect chapters locally too
            // Using a simple rule-based approach for now
            val document = Document(
                id = "local_${file.name}",
                fileName = file.name,
                filePath = file.path,
                fileSizeBytes = file.sizeBytes ?: 0L,
                pageCount = 0,
                importedAt = Instant.fromEpochMilliseconds(0),
                status = DocumentStatus.ANALYZED
            )
            
            // Dummy chapter for local analysis if detection is not implemented
            val chapters = listOf(
                Chapter(
                    id = "ch_1",
                    documentId = document.id,
                    index = 0,
                    title = "Full Document",
                    originalText = "Content analyzed locally",
                    narrationText = "Content analyzed locally",
                    estimatedDurationSeconds = 0,
                    status = ChapterStatus.PENDING
                )
            )

            currentAnalysisRepository.setAnalysisResult(document, chapters)
            
            _uiState.update { 
                it.copy(
                    currentStage = AnalysisStage.COMPLETED,
                    progress = 1.0f,
                    statusMessage = "Local analysis complete.",
                    isCompleted = true
                )
            }
        }
    }

    private fun estimateDuration(text: String): Int {
        val wordCount = text.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        return (wordCount / 2.1).toInt()
    }
}
