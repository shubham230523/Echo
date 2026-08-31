package com.shubhamthorat.echo.data.remote

import com.shubhamthorat.echo.data.remote.dto.v1.HealthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

interface EchoApi {
    suspend fun checkHealth(): HealthResponse
}

class KtorEchoApi(
    private val client: HttpClient
) : EchoApi {
    override suspend fun checkHealth(): HealthResponse {
        return client.get("/health").body()
    }
}
