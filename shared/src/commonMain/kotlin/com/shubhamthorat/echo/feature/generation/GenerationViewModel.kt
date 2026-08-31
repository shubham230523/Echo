package com.shubhamthorat.echo.feature.generation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the simulated audiobook generation process.
 */
class GenerationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GenerationUiState())
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    private var generationJob: Job? = null

    init {
        startSimulatedGeneration()
    }

    private fun startSimulatedGeneration() {
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            // Stage 1: Preparing Chapters
            updateState(GenerationStatus.PREPARING_CHAPTERS, 0.05f, "Organizing document structure...")
            delay(1500)
            
            // Stage 2: Preparing Narration
            updateState(GenerationStatus.PREPARING_NARRATION, 0.15f, "Applying narration styles...")
            delay(2000)
            
            // Stage 3: Generating Audio (simulating multiple chapters)
            val chapters = listOf("Introduction", "Chapter 1: The Beginning", "Chapter 2: The Middle", "Chapter 3: The End")
            chapters.forEachIndexed { index, chapterTitle ->
                val baseProgress = 0.2f
                val chapterProgress = (index.toFloat() / chapters.size) * 0.6f
                
                updateState(
                    GenerationStatus.GENERATING_AUDIO, 
                    baseProgress + chapterProgress, 
                    "Converting text to speech...",
                    chapterTitle
                )
                
                // Simulate intra-chapter progress
                repeat(5) { step ->
                    delay(800)
                    val stepProgress = (step + 1).toFloat() / 5 * (0.6f / chapters.size)
                    _uiState.update { it.copy(progress = baseProgress + chapterProgress + stepProgress) }
                }
            }
            
            // Stage 4: Validating Audio
            updateState(GenerationStatus.VALIDATING_AUDIO, 0.85f, "Checking audio quality and consistency...", null)
            delay(2500)
            
            // Stage 5: Finalizing Audiobook
            updateState(GenerationStatus.FINALIZING_AUDIOBOOK, 0.95f, "Packaging your audiobook...")
            delay(1500)
            
            // Completed
            _uiState.update { 
                it.copy(
                    status = GenerationStatus.COMPLETED,
                    progress = 1.0f,
                    message = "Audiobook is ready!"
                ) 
            }
        }
    }

    private fun updateState(status: GenerationStatus, progress: Float, message: String, chapter: String? = null) {
        _uiState.update { 
            it.copy(
                status = status,
                progress = progress,
                message = message,
                currentChapter = chapter
            )
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        _uiState.update { 
            it.copy(
                status = GenerationStatus.CANCELLED,
                message = "Generation cancelled."
            ) 
        }
    }
    
    fun retry() {
        _uiState.update { GenerationUiState() }
        startSimulatedGeneration()
    }
}
