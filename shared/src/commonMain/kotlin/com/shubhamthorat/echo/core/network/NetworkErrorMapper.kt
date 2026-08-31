package com.shubhamthorat.echo.core.network

import io.ktor.client.plugins.*
import io.ktor.utils.io.errors.*

sealed class NetworkError(val errorDescription: String) : Exception(errorDescription) {
    data object NoInternet : NetworkError("No internet connection")
    data object Timeout : NetworkError("Request timed out")
    data object ServerError : NetworkError("Internal server error")
    data object Unauthorized : NetworkError("Unauthorized access")
    data class Unknown(val originalMessage: String?) : NetworkError(originalMessage ?: "Unknown network error")
}

fun Throwable.toNetworkError(): NetworkError {
    return when (this) {
        is IOException -> NetworkError.NoInternet
        is HttpRequestTimeoutException -> NetworkError.Timeout
        is ResponseException -> {
            when (response.status.value) {
                401 -> NetworkError.Unauthorized
                in 500..599 -> NetworkError.ServerError
                else -> NetworkError.Unknown(message)
            }
        }
        else -> NetworkError.Unknown(message)
    }
}
