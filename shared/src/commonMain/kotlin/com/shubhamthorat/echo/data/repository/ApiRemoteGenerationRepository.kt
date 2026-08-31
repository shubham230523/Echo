package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.network.toNetworkError
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.data.remote.EchoApi
import com.shubhamthorat.echo.data.remote.dto.v1.GenerateAudiobookRequest
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.GenerationProgress
import com.shubhamthorat.echo.domain.repository.RemoteGenerationRepository

class ApiRemoteGenerationRepository(
    private val api: EchoApi
) : RemoteGenerationRepository {

    override suspend fun startGeneration(
        documentId: String,
        voiceId: String,
        chapters: List<Chapter>,
        speed: Float
    ): AppResult<String> {
        return try {
            val response = api.startGeneration(
                GenerateAudiobookRequest(
                    documentId = documentId,
                    voiceId = voiceId,
                    chapters = chapters.map { 
                        GenerateAudiobookRequest.ChapterRequest(
                            id = it.id,
                            title = it.title,
                            text = it.originalText
                        )
                    },
                    speed = speed
                )
            )
            AppResult.Success(response.generationId)
        } catch (e: Exception) {
            AppResult.Error(e.toNetworkError().errorDescription)
        }
    }

    override suspend fun getGenerationStatus(id: String): AppResult<GenerationProgress> {
        return try {
            val response = api.getGenerationStatus(id)
            AppResult.Success(
                GenerationProgress(
                    generationId = response.generationId,
                    status = response.status,
                    progress = response.progress,
                    currentStep = response.currentStep,
                    currentChapter = response.currentChapter,
                    completedChapters = response.completedChapters,
                    totalChapters = response.totalChapters,
                    error = response.error,
                    audiobookId = response.audiobookId
                )
            )
        } catch (e: Exception) {
            AppResult.Error(e.toNetworkError().errorDescription)
        }
    }
}
