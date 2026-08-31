package com.shubhamthorat.echo.feature.settings

/**
 * UI State for the Settings screen.
 *
 * @property theme The current theme preference (e.g., "System", "Light", "Dark").
 * @property defaultPlaybackSpeed The default playback speed for the player.
 * @property storageUsage The amount of storage used by the app (mocked).
 * @property version The current version of the Echo app.
 */
data class SettingsUiState(
    val theme: String = "System",
    val defaultPlaybackSpeed: Float = 1.0f,
    val storageUsage: String = "45.2 MB",
    val version: String = "1.0.0 (Alpha)"
)
