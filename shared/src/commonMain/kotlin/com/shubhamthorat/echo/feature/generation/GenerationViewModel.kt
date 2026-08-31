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
            updateStage(GenerationStage.PREPARING_CHAPTERS, 0.05f)
            delay(1500)
            
            // Stage 2: Preparing Narration
            updateStage(GenerationStage.PREPARING_NARRATION, 0.15f)
            delay(2000)
            
            // Stage 3: Generating Audio (simulating multiple chapters)
            val chapters = listOf("Introduction", "Chapter 1: The Beginning", "Chapter 2: The Middle", "Chapter 3: The End")
            chapters.forEachIndexed { index, chapterTitle ->
                val baseProgress = 0.2f
                val chapterProgress = (index.toFloat() / chapters.size) * 0.6f
                
                updateStage(GenerationStage.GENERATING_AUDIO, baseProgress + chapterProgress, chapterTitle)
                
                // Simulate intra-chapter progress
                repeat(5) { step ->
                    delay(800)
                    val stepProgress = (step + 1).toFloat() / 5 * (0.6f / chapters.size)
                    _uiState.update { it.copy(progress = baseProgress + chapterProgress + stepProgress) }
                }
            }
            
            // Stage 4: Validating Audio
            updateStage(GenerationStage.VALIDATING_AUDIO, 0.85f, null)
            delay(2500)
            
            // Stage 5: Finalizing Audiobook
            updateStage(GenerationStage.FINALIZING_AUDIOBOOK, 0.95f)
            delay(1500)
            
            // Completed
            _uiState.update { it.copy(progress = 1.0f, isCompleted = true) }
        }
    }

    private fun updateStage(stage: GenerationStage, progress: Float, chapter: String? = null) {
        _uiState.update { 
            it.copy(
                currentStage = stage,
                progress = progress,
                currentChapter = chapter
            )
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        _uiState.update { it.copy(isCancelled = true) }
    }
    
    fun retry() {
        _uiState.update { GenerationUiState() }
        startSimulatedGeneration()
    }
}
