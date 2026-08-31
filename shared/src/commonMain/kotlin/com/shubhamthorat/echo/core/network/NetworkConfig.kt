package com.shubhamthorat.echo.core.network

data class NetworkConfig(
    val baseUrl: String,
    val isDebug: Boolean,
    val connectTimeoutMillis: Long = 30_000L,
    val requestTimeoutMillis: Long = 30_000L
)
