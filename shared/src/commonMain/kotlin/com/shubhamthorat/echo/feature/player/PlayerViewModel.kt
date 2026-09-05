package com.shubhamthorat.echo.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.domain.model.*
import com.shubhamthorat.echo.domain.repository.AudiobookRepository
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
// Removed Clock import
class PlayerViewModel(
    private val currentAnalysisRepository: CurrentAnalysisRepository,
    private val audiobookRepository: AudiobookRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        loadCurrentAudiobook()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            audioPlayer.state.collect { state ->
                _uiState.update { 
                    it.copy(
                        currentPosition = state.position,
                        duration = state.duration,
                        isPlaying = state.isPlaying,
                        playbackSpeed = state.playbackSpeed
                    )
                }
            }
        }
    }

    private fun loadCurrentAudiobook() {
        val doc = currentAnalysisRepository.currentDocument.value ?: return
        
        viewModelScope.launch {
            // Try to find the generated audiobook for this document
            val allBooks = audiobookRepository.observeAllAudiobooks().firstOrNull()
            val audiobook = allBooks?.find { it.documentId == doc.id } ?: return@launch
            
            // Map chapters to AudioChapters
            val chapters = currentAnalysisRepository.chapters.value.map { 
                AudioChapter(
                    chapterId = it.id,
                    audioPath = "http://localhost:8080/generation/audio/${doc.id}/${it.id}.mp3",
                    durationSeconds = it.estimatedDurationSeconds.toDouble(),
                    generationStatus = AudioGenerationStatus.COMPLETED
                )
            }

            _uiState.update { 
                it.copy(
                    audiobook = audiobook,
                    chapters = chapters,
                    currentChapter = chapters.firstOrNull(),
                    duration = chapters.firstOrNull()?.durationSeconds?.toLong()?.times(1000) ?: 0L
                )
            }
            
            // Auto-load first chapter
            chapters.firstOrNull()?.audioPath?.let { audioPlayer.load(it) }
        }
    }

    fun play() {
        audioPlayer.play()
    }

    fun pause() {
        audioPlayer.pause()
    }

    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
    }

    fun skipForward() {
        val newPos = _uiState.value.currentPosition + 15000L
        audioPlayer.seekTo(newPos)
    }

    fun skipBackward() {
        val newPos = _uiState.value.currentPosition - 15000L
        audioPlayer.seekTo(newPos)
    }

    fun nextChapter() {
        val currentIndex = _uiState.value.chapters.indexOf(_uiState.value.currentChapter)
        if (currentIndex != -1 && currentIndex < _uiState.value.chapters.size - 1) {
            selectChapter(_uiState.value.chapters[currentIndex + 1])
        }
    }

    fun previousChapter() {
        val currentIndex = _uiState.value.chapters.indexOf(_uiState.value.currentChapter)
        if (currentIndex > 0) {
            selectChapter(_uiState.value.chapters[currentIndex - 1])
        }
    }

    fun selectChapter(chapter: AudioChapter) {
        _uiState.update { it.copy(currentChapter = chapter) }
        viewModelScope.launch {
            chapter.audioPath?.let { audioPlayer.load(it) }
            audioPlayer.play()
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
    }
}
