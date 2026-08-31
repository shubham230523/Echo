package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.GenerationProgress
import com.shubhamthorat.echo.domain.model.Chapter

interface RemoteGenerationRepository {
    suspend fun startGeneration(
        documentId: String,
        voiceId: String,
        chapters: List<Chapter>,
        speed: Float = 1.0f
    ): AppResult<String>

    suspend fun getGenerationStatus(id: String): AppResult<GenerationProgress>
}
