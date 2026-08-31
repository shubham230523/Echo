package com.shubhamthorat.echo.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setTheme(theme: String) {
        _uiState.update { it.copy(theme = theme) }
    }

    fun setDefaultPlaybackSpeed(speed: Float) {
        _uiState.update { it.copy(defaultPlaybackSpeed = speed) }
    }

    fun clearCache() {
        // Mocking clearing cache
        _uiState.update { it.copy(storageUsage = "0.0 MB") }
    }
}
