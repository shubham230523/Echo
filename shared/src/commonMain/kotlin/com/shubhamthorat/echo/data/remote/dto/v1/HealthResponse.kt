package com.shubhamthorat.echo.data.remote.dto.v1

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val service: String,
    val timestamp: String,
    val metadata: Metadata
) {
    @Serializable
    data class Metadata(
        val version: String,
        val environment: String
    )
}
