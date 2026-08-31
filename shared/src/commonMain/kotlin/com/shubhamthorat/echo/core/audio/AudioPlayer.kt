package com.shubhamthorat.echo.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * High-level state of the audio player.
 *
 * @property position Current playback position in milliseconds.
 * @property duration Total duration of the loaded audio in milliseconds.
 * @property isPlaying Whether audio is currently playing.
 * @property isCompleted Whether playback has finished reaching the end.
 * @property playbackSpeed The current playback speed multiplier.
 */
data class AudioPlayerState(
    val position: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false,
    val isCompleted: Boolean = false,
    val playbackSpeed: Float = 1.0f
)

/**
 * Platform-independent audio player interface for Echo.
 * Provides basic playback controls and state observation.
 */
interface AudioPlayer {

    /**
     * Observable state of the player.
     */
    val state: StateFlow<AudioPlayerState>

    /**
     * Loads audio from a URI or local path.
     */
    suspend fun load(uri: String)

    /**
     * Starts or resumes playback.
     */
    fun play()

    /**
     * Pauses playback.
     */
    fun pause()

    /**
     * Seeks to a specific position in milliseconds.
     */
    fun seekTo(position: Long)

    /**
     * Stops playback and resets state.
     */
    fun stop()

    /**
     * Sets the playback speed (e.g., 1.5f).
     */
    fun setPlaybackSpeed(speed: Float)

    /**
     * Releases resources used by the player.
     */
    fun release()
}
