package com.shubhamthorat.echo.feature.generation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.domain.model.*
import com.shubhamthorat.echo.domain.repository.AudiobookRepository
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import com.shubhamthorat.echo.domain.service.LocalAudiobookGenerator
import com.shubhamthorat.echo.shared.ai.ModelManager
import com.shubhamthorat.echo.shared.ai.ModelType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class LocalGenerationViewModel(
    private val localGenerator: LocalAudiobookGenerator,
    private val currentAnalysisRepository: CurrentAnalysisRepository,
    private val audiobookRepository: AudiobookRepository,
    private val modelManager: ModelManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerationUiState())
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    init {
        checkModelAndStart()
    }

    private fun checkModelAndStart() {
        if (!modelManager.isModelDownloaded(ModelType.TTS)) {
            _uiState.update { 
                it.copy(
                    status = GenerationStatus.ERROR, 
                    error = "TTS model not downloaded. Please go to settings to download models." 
                ) 
            }
            return
        }
        startLocalGeneration()
    }

    private fun startLocalGeneration() {
        val document = currentAnalysisRepository.currentDocument.value
        val chapters = currentAnalysisRepository.chapters.value

        if (document == null || chapters.isEmpty()) {
            _uiState.update { it.copy(status = GenerationStatus.ERROR, error = "Invalid generation context") }
            return
        }

        viewModelScope.launch {
            localGenerator.generate(chapters).collect { progress ->
                _uiState.update { 
                    it.copy(
                        status = mapLocalStatus(progress.status),
                        progress = progress.progress,
                        message = "${progress.currentStep}: ${progress.currentChapter ?: ""}",
                        currentChapter = progress.currentChapter
                    )
                }

                if (progress.status == "COMPLETED") {
                    saveFinalAudiobook(document)
                }
            }
        }
    }

    private fun mapLocalStatus(status: String): GenerationStatus = when (status) {
        "PROCESSING" -> GenerationStatus.GENERATING_AUDIO
        "COMPLETED" -> GenerationStatus.COMPLETED
        "FAILED" -> GenerationStatus.ERROR
        else -> GenerationStatus.IDLE
    }

    private fun saveFinalAudiobook(document: Document) {
        viewModelScope.launch {
            audiobookRepository.insertAudiobook(
                Audiobook(
                    id = "local_${document.id}",
                    documentId = document.id,
                    title = document.fileName.removeSuffix(".pdf"),
                    author = "Local AI Narrator",
                    coverImagePath = null,
                    totalDurationSeconds = 0,
                    chapterCount = currentAnalysisRepository.chapters.value.size,
                    createdAt = Instant.fromEpochMilliseconds(0),
                    updatedAt = Instant.fromEpochMilliseconds(0),
                    status = AudiobookStatus.READY
                )
            )
        }
    }
    
    fun downloadModel() {
        viewModelScope.launch {
            modelManager.downloadModel(ModelType.TTS).collect { progress ->
                _uiState.update { 
                    it.copy(
                        message = "Downloading TTS Model: ${(progress.progress * 100).toInt()}%",
                        progress = progress.progress
                    )
                }
                if (progress.isComplete) {
                    startLocalGeneration()
                }
            }
        }
    }
}
