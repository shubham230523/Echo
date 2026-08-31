package com.shubhamthorat.echo.core.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AndroidAudioPlayer(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) : AudioPlayer {

    private val _state = MutableStateFlow(AudioPlayerState())
    override val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressUpdates()
            } else {
                stopProgressUpdates()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _state.update { it.copy(duration = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L) }
            } else if (playbackState == Player.STATE_ENDED) {
                _state.update { it.copy(isCompleted = true, isPlaying = false) }
                stopProgressUpdates()
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            _state.update { it.copy(playbackSpeed = playbackParameters.speed) }
        }
    }

    override suspend fun load(uri: String) {
        withContext(Dispatchers.Main) {
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(context).build().apply {
                    addListener(playerListener)
                }
            }

            val mediaItem = MediaItem.fromUri(Uri.parse(uri))
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                _state.update { 
                    it.copy(
                        position = 0L,
                        isCompleted = false,
                        duration = 0L // Reset duration until loaded
                    ) 
                }
            }
        }
    }

    override fun play() {
        scope.launch(Dispatchers.Main) {
            exoPlayer?.play()
        }
    }

    override fun pause() {
        scope.launch(Dispatchers.Main) {
            exoPlayer?.pause()
        }
    }

    override fun seekTo(position: Long) {
        scope.launch(Dispatchers.Main) {
            exoPlayer?.seekTo(position)
            _state.update { it.copy(position = position) }
        }
    }

    override fun stop() {
        scope.launch(Dispatchers.Main) {
            exoPlayer?.stop()
            _state.update { it.copy(isPlaying = false, position = 0L) }
            stopProgressUpdates()
        }
    }

    override fun setPlaybackSpeed(speed: Float) {
        scope.launch(Dispatchers.Main) {
            exoPlayer?.playbackParameters = PlaybackParameters(speed)
        }
    }

    override fun release() {
        scope.launch(Dispatchers.Main) {
            stopProgressUpdates()
            exoPlayer?.removeListener(playerListener)
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    _state.update { it.copy(position = player.currentPosition) }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }
}
