package com.shubhamthorat.echo.feature.player

import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.domain.model.AudioChapter

/**
 * UI State for the Audiobook Player screen.
 *
 * @property audiobook The audiobook currently being played.
 * @property currentChapter The chapter currently being played.
 * @property currentPosition The current playback position in milliseconds.
 * @property duration The total duration of the current chapter in milliseconds.
 * @property isPlaying Whether audio is currently playing.
 * @property playbackSpeed The current playback speed (e.g., 1.0f).
 */
data class PlayerUiState(
    val audiobook: Audiobook? = null,
    val currentChapter: AudioChapter? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false,
    val playbackSpeed: Float = 1.0f
) {
    val progress: Float = if (duration > 0) currentPosition.toFloat() / duration else 0f
    
    val currentPositionText: String = formatDuration(currentPosition / 1000)
    val totalDurationText: String = formatDuration(duration / 1000)

    private fun formatDuration(seconds: Long): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "${mins}:${secs.toString().padStart(2, '0')}"
    }
}
