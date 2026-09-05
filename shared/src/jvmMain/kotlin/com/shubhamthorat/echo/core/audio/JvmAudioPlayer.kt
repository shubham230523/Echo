package com.shubhamthorat.echo.core.audio

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.awt.Desktop
import java.io.File
import java.net.URI

class JvmAudioPlayer(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) : AudioPlayer {

    private val _state = MutableStateFlow(AudioPlayerState())
    override val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var currentUri: String? = null
    private var playbackJob: Job? = null
    private var progressJob: Job? = null

    override suspend fun load(uri: String) {
        currentUri = uri
        _state.update { 
            it.copy(
                position = 0L,
                duration = 5000L, // 5 seconds placeholder
                isCompleted = false
            ) 
        }
        
        // Auto-open in system player for Desktop experience
        try {
            val file = if (uri.startsWith("file:")) {
                File(URI(uri))
            } else if (uri.startsWith("http")) {
                null
            } else {
                File(uri)
            }

            if (file != null && file.exists()) {
                println("🔈 Opening file in system media player: ${file.absolutePath}")
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file)
                } else {
                    val os = System.getProperty("os.name").lowercase()
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler \"${file.absolutePath}\"")
                    }
                }
            } else if (uri.startsWith("http")) {
                println("🔈 Opening URL in system browser/player: $uri")
                Desktop.getDesktop().browse(URI(uri))
            }
        } catch (e: Exception) {
            println("⚠️ Could not open system player: ${e.message}")
        }
    }

    override fun play() {
        _state.update { it.copy(isPlaying = true) }
        startProgressUpdates()
    }

    override fun pause() {
        _state.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
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
