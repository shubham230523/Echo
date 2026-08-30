package com.shubhamthorat.echo.presentation

/**
 * A generic UI state representation to be used by feature ViewModels.
 * It enforces a consistent pattern for handling Loading, Success, and Error states.
 *
 * @param T The type of the content data for the Success state.
 */
sealed interface UiState<out T> {

    /**
     * Represents the state where data is being loaded.
     */
    data object Loading : UiState<Nothing>

    /**
     * Represents the state where data has been successfully loaded.
     * @property data The content data.
     */
    data class Success<out T>(val data: T) : UiState<T>

    /**
     * Represents the state where an error has occurred.
     * @property message A user-readable error message.
     */
    data class Error(val message: String) : UiState<Nothing>
}
