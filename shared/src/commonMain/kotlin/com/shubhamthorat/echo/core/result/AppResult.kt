package com.shubhamthorat.echo.core.result

/**
 * A sealed interface representing the result of an operation.
 * It can be in one of three states: Success, Error, or Loading.
 *
 * @param T The type of data returned in case of success.
 */
sealed interface AppResult<out T> {

    /**
     * Represents a successful operation with the resulting data.
     */
    data class Success<out T>(val data: T) : AppResult<T>

    /**
     * Represents a failed operation with an error message and an optional exception.
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : AppResult<Nothing>

    /**
     * Represents that an operation is currently in progress.
     */
    data object Loading : AppResult<Nothing>
}
