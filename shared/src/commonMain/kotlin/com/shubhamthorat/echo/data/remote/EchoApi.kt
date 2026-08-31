package com.shubhamthorat.echo.data.remote

import com.shubhamthorat.echo.data.remote.dto.v1.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface EchoApi {
    suspend fun checkHealth(): HealthResponse
    suspend fun startGeneration(request: GenerateAudiobookRequest): GenerateAudiobookResponse
    suspend fun getGenerationStatus(id: String): GenerationStatusResponse
}

class KtorEchoApi(
    private val client: HttpClient
) : EchoApi {
    override suspend fun checkHealth(): HealthResponse {
        return client.get("/health").body()
    }

    override suspend fun startGeneration(request: GenerateAudiobookRequest): GenerateAudiobookResponse {
        return client.post("/generation/audiobook") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getGenerationStatus(id: String): GenerationStatusResponse {
        return client.get("/generation/$id").body()
    }
}
