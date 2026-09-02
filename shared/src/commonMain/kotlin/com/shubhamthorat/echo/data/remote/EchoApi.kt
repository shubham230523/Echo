package com.shubhamthorat.echo.data.remote

import com.shubhamthorat.echo.data.remote.dto.v1.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

interface EchoApi {
    suspend fun checkHealth(): HealthResponse
    suspend fun analyzeDocument(fileBytes: ByteArray, fileName: String): AnalyzeDocumentResponse
    suspend fun getVoices(): GetVoicesResponse
    suspend fun startGeneration(request: GenerateAudiobookRequest): GenerateAudiobookResponse
    suspend fun getGenerationStatus(id: String): GenerationStatusResponse
}

class KtorEchoApi(
    private val client: HttpClient
) : EchoApi {
    override suspend fun checkHealth(): HealthResponse {
        return client.get("/health").body()
    }

    override suspend fun getVoices(): GetVoicesResponse {
        return client.get("/voices").body()
    }

    override suspend fun analyzeDocument(fileBytes: ByteArray, fileName: String): AnalyzeDocumentResponse {
        return client.submitFormWithBinaryData(
            url = "/documents/analyze",
            formData = formData {
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    append(HttpHeaders.ContentType, "application/pdf")
                })
            }
        ).body<AnalyzeDocumentResponse>()
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
