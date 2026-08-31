package com.shubhamthorat.echo.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.domain.model.AudiobookStatus
import com.shubhamthorat.echo.domain.model.AudioChapter
import com.shubhamthorat.echo.domain.model.AudioGenerationStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// Removed Clock import
import kotlinx.datetime.Instant
// Removed Clock import
class PlayerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    init {
        // Load mock data for initial UI state
        val mockAudiobook = Audiobook(
            id = "gatsby_1",
            documentId = "doc_1",
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            coverImagePath = null,
            totalDurationSeconds = 1800,
            chapterCount = 9,
            createdAt = Instant.fromEpochMilliseconds(0),
            updatedAt = Instant.fromEpochMilliseconds(0),
            status = AudiobookStatus.READY
        )
        
        val mockChapter = AudioChapter(
            chapterId = "chapter_1",
            audioPath = "path/to/audio.mp3",
            durationSeconds = 640.0,
            fileSizeBytes = 1024L * 1024 * 5,
            generationStatus = AudioGenerationStatus.COMPLETED
        )

        val mockChapters = listOf(
            mockChapter,
            AudioChapter(
                chapterId = "chapter_2",
                audioPath = "path/to/audio2.mp3",
                durationSeconds = 820.0,
                fileSizeBytes = 1024L * 1024 * 7,
                generationStatus = AudioGenerationStatus.COMPLETED
            ),
            AudioChapter(
                chapterId = "chapter_3",
                audioPath = "path/to/audio3.mp3",
                durationSeconds = 450.0,
                fileSizeBytes = 1024L * 1024 * 4,
                generationStatus = AudioGenerationStatus.COMPLETED
            )
        )

        _uiState.update { 
            it.copy(
                audiobook = mockAudiobook,
                currentChapter = mockChapter,
                chapters = mockChapters,
                duration = 640000L, // 640 seconds in ms
                currentPosition = 45000L // 45 seconds in ms
            )
        }
    }

    fun play() {
        if (_uiState.value.isPlaying) return
        
        _uiState.update { it.copy(isPlaying = true) }
        startPlaybackSimulation()
    }

    fun pause() {
        if (!_uiState.value.isPlaying) return
        
        _uiState.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
    }

    fun seekTo(position: Long) {
        _uiState.update { 
            it.copy(currentPosition = position.coerceIn(0, it.duration))
        }
    }

    fun skipForward() {
        seekTo(_uiState.value.currentPosition + 15000L) // +15 seconds
    }

    fun skipBackward() {
        seekTo(_uiState.value.currentPosition - 15000L) // -15 seconds
    }

    fun nextChapter() {
        // Mocking next chapter transition
        _uiState.update { 
            it.copy(
                currentPosition = 0L,
                duration = 820000L,
                currentChapter = it.currentChapter?.copy(chapterId = "chapter_2", durationSeconds = 820.0)
            )
        }
    }

    fun previousChapter() {
        // Mocking previous chapter transition
        _uiState.update { 
            it.copy(
                currentPosition = 0L,
                duration = 640000L,
                currentChapter = it.chapters.firstOrNull() ?: it.currentChapter
            )
        }
    }

    fun selectChapter(chapter: AudioChapter) {
        _uiState.update { 
            it.copy(
                currentChapter = chapter,
                currentPosition = 0L,
                duration = (chapter.durationSeconds * 1000).toLong()
            )
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _uiState.update { it.copy(playbackSpeed = speed) }
    }

    private fun startPlaybackSimulation() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_uiState.value.isPlaying && _uiState.value.currentPosition < _uiState.value.duration) {
                delay(1000)
                _uiState.update { 
                    it.copy(currentPosition = (it.currentPosition + 1000).coerceAtMost(it.duration))
                }
            }
            if (_uiState.value.currentPosition >= _uiState.value.duration) {
                _uiState.update { it.copy(isPlaying = false) }
            }
        }
    }
}
