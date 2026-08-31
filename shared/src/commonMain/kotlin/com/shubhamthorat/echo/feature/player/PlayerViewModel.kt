package com.shubhamthorat.echo.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            audiobookTitle = "The Great Gatsby",
            chapterTitle = "Chapter 1: The Introduction",
            isPlaying = false,
            currentPositionSeconds = 45f,
            totalDurationSeconds = 640f,
            hasNextChapter = true,
            hasPreviousChapter = false
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    fun togglePlayPause() {
        val newState = !_uiState.value.isPlaying
        _uiState.update { it.copy(isPlaying = newState) }
        
        if (newState) {
            startPlaybackSimulation()
        } else {
            playbackJob?.cancel()
        }
    }

    private fun startPlaybackSimulation() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_uiState.value.isPlaying && _uiState.value.currentPositionSeconds < _uiState.value.totalDurationSeconds) {
                delay(1000)
                _uiState.update { 
                    it.copy(currentPositionSeconds = (it.currentPositionSeconds + 1).coerceAtMost(it.totalDurationSeconds))
                }
            }
            if (_uiState.value.currentPositionSeconds >= _uiState.value.totalDurationSeconds) {
                _uiState.update { it.copy(isPlaying = false) }
            }
        }
    }

    fun seekTo(position: Float) {
        _uiState.update { it.copy(currentPositionSeconds = position) }
    }

    fun skipForward() {
        _uiState.update { 
            it.copy(currentPositionSeconds = (it.currentPositionSeconds + 15).coerceAtMost(it.totalDurationSeconds))
        }
    }

    fun skipBackward() {
        _uiState.update { 
            it.copy(currentPositionSeconds = (it.currentPositionSeconds - 15).coerceAtLeast(0f))
        }
    }

    fun nextChapter() {
        _uiState.update { 
            it.copy(
                chapterTitle = "Chapter 2: East Egg",
                currentPositionSeconds = 0f,
                totalDurationSeconds = 820f,
                hasPreviousChapter = true,
                hasNextChapter = true
            )
        }
    }

    fun previousChapter() {
        _uiState.update { 
            it.copy(
                chapterTitle = "Chapter 1: The Introduction",
                currentPositionSeconds = 0f,
                totalDurationSeconds = 640f,
                hasPreviousChapter = false,
                hasNextChapter = true
            )
        }
    }
}
