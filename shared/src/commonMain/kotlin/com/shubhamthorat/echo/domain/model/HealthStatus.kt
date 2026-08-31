package com.shubhamthorat.echo.domain.model

data class HealthStatus(
    val status: String,
    val version: String,
    val environment: String
)
