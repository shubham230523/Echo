package com.shubhamthorat.echo.core.network

data class NetworkConfig(
    val baseUrl: String,
    val isDebug: Boolean,
    val connectTimeoutMillis: Long = 60_000L,
    val requestTimeoutMillis: Long = 1_800_000L // 30 minutes
)
