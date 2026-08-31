package com.shubhamthorat.echo.feature.player

/**
 * UI State for the Audiobook Player screen.
 *
 * @property audiobookTitle The title of the audiobook being played.
 * @property chapterTitle The title of the current chapter.
 * @property coverUrl The URL or local path to the audiobook cover image.
 * @property isPlaying Whether audio is currently playing.
 * @property currentPositionSeconds The current playback position in seconds.
 * @property totalDurationSeconds The total duration of the current chapter in seconds.
 * @property hasNextChapter Whether there is a next chapter available.
 * @property hasPreviousChapter Whether there is a previous chapter available.
 */
data class PlayerUiState(
    val audiobookTitle: String = "",
    val chapterTitle: String = "",
    val coverUrl: String? = null,
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Float = 0f,
    val totalDurationSeconds: Float = 0f,
    val hasNextChapter: Boolean = false,
    val hasPreviousChapter: Boolean = false
) {
    val progress: Float = if (totalDurationSeconds > 0) currentPositionSeconds / totalDurationSeconds else 0f
    
    val currentPositionText: String = formatDuration(currentPositionSeconds.toInt())
    val totalDurationText: String = formatDuration(totalDurationSeconds.toInt())

    private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "${mins}:${secs.toString().padStart(2, '0')}"
    }
}
