package com.shubhamthorat.echo.core.audio

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockAudioPlayer(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) : AudioPlayer {

    private val _state = MutableStateFlow(AudioPlayerState())
    override val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var progressJob: Job? = null

    override suspend fun load(uri: String) {
        _state.update { 
            it.copy(
                position = 0L,
                duration = 300_000L, // 5 minutes mock
                isCompleted = false
            ) 
        }
    }

    override fun play() {
        _state.update { it.copy(isPlaying = true) }
        startProgressUpdates()
    }

    override fun pause() {
        _state.update { it.copy(isPlaying = false) }
        stopProgressUpdates()
    }

    override fun seekTo(position: Long) {
        _state.update { it.copy(position = position) }
    }

    override fun stop() {
        _state.update { it.copy(isPlaying = false, position = 0L) }
        stopProgressUpdates()
    }

    override fun setPlaybackSpeed(speed: Float) {
        _state.update { it.copy(playbackSpeed = speed) }
    }

    override fun release() {
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                delay(1000)
                _state.update { 
                    val newPos = (it.position + (1000 * it.playbackSpeed).toLong()).coerceAtMost(it.duration)
                    if (newPos >= it.duration) {
                        stopProgressUpdates()
                        it.copy(position = newPos, isPlaying = false, isCompleted = true)
                    } else {
                        it.copy(position = newPos)
                    }
                }
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }
}
