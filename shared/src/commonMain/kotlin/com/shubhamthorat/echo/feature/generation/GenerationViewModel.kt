package com.shubhamthorat.echo.feature.generation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.*
import com.shubhamthorat.echo.domain.repository.AudiobookRepository
import com.shubhamthorat.echo.domain.repository.ChapterRepository
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import com.shubhamthorat.echo.domain.repository.RemoteGenerationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

/**
 * ViewModel for managing the audiobook generation process via backend.
 */
class GenerationViewModel(
    private val remoteRepository: RemoteGenerationRepository,
    private val currentAnalysisRepository: CurrentAnalysisRepository,
    private val audiobookRepository: AudiobookRepository,
    private val chapterRepository: ChapterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerationUiState())
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startGeneration()
    }

    private fun startGeneration() {
        val document = currentAnalysisRepository.currentDocument.value
        val chapters = currentAnalysisRepository.chapters.value
        val voiceId = currentAnalysisRepository.selectedVoiceId.value

        if (document == null || chapters.isEmpty() || voiceId == null) {
            _uiState.update { it.copy(status = GenerationStatus.ERROR, error = "Invalid generation context") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(status = GenerationStatus.PREPARING_CHAPTERS, message = "Starting generation...") }
            
            val result = remoteRepository.startGeneration(
                documentId = document.id,
                voiceId = voiceId,
                chapters = chapters,
                speed = 1.0f
            )

            when (result) {
                is AppResult.Success -> {
                    startPolling(result.data)
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(status = GenerationStatus.ERROR, error = result.message) }
                }
                AppResult.Loading -> {}
            }
        }
    }

    private fun startPolling(generationId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            var pollingCount = 0
            val maxPolling = 100 // Prevent infinite loop (approx 5 mins)
            
            while (pollingCount < maxPolling) {
                val statusResult = remoteRepository.getGenerationStatus(generationId)
                
                when (statusResult) {
                    is AppResult.Success -> {
                        val progress = statusResult.data
                        updateUiWithProgress(progress)

                        if (progress.status == "COMPLETED") {
                            saveFinalAudiobook(progress.audiobookId ?: generationId)
                            _uiState.update { 
                                it.copy(
                                    status = GenerationStatus.COMPLETED,
                                    progress = 1.0f,
                                    message = "Audiobook is ready!"
                                ) 
                            }
                            break
                        } else if (progress.status == "FAILED") {
                            _uiState.update { 
                                it.copy(
                                    status = GenerationStatus.ERROR, 
                                    error = progress.error ?: "Generation failed" 
                                ) 
                            }
                            break
                        }
                    }
                    is AppResult.Error -> {
                        // Retry polling on network error unless too many failures
                        if (pollingCount % 5 == 0) {
                            _uiState.update { it.copy(message = "Connection issue, retrying...") }
                        }
                    }
                    AppResult.Loading -> {}
                }
                
                pollingCount++
                delay(3000) // Poll every 3 seconds
            }
            
            if (pollingCount >= maxPolling) {
                _uiState.update { it.copy(status = GenerationStatus.ERROR, error = "Generation timed out. Please try again.") }
            }
        }
    }

    private fun saveFinalAudiobook(audiobookId: String) {
        val document = currentAnalysisRepository.currentDocument.value ?: return
        
        viewModelScope.launch {
            audiobookRepository.insertAudiobook(
                Audiobook(
                    id = audiobookId,
                    documentId = document.id,
                    title = document.fileName.removeSuffix(".pdf"),
                    author = "AI Narrator",
                    coverImagePath = null,
                    totalDurationSeconds = 0, // Should be sum of chapters
                    chapterCount = currentAnalysisRepository.chapters.value.size,
                    createdAt = Instant.fromEpochMilliseconds(0),
                    updatedAt = Instant.fromEpochMilliseconds(0),
                    status = AudiobookStatus.READY
                )
            )
        }
    }

    private fun updateUiWithProgress(progress: GenerationProgress) {
        _uiState.update { 
            it.copy(
                status = mapBackendStatus(progress.status),
                progress = progress.progress,
                message = "${progress.currentStep}: ${progress.currentChapter ?: ""}",
                currentChapter = progress.currentChapter
            )
        }
    }

    private fun mapBackendStatus(status: String): GenerationStatus {
        return when (status) {
            "PENDING" -> GenerationStatus.IDLE
            "PROCESSING" -> GenerationStatus.GENERATING_AUDIO
            "COMPLETED" -> GenerationStatus.COMPLETED
            "FAILED" -> GenerationStatus.ERROR
            else -> GenerationStatus.IDLE
        }
    }

    fun cancelGeneration() {
        pollingJob?.cancel()
        _uiState.update { 
            it.copy(
                status = GenerationStatus.CANCELLED,
                message = "Generation cancelled."
            ) 
        }
    }
    
    fun retry() {
        _uiState.update { GenerationUiState() }
        startGeneration()
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
