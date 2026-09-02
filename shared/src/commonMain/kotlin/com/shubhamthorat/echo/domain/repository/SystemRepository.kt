package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.HealthStatus
import com.shubhamthorat.echo.domain.model.Voice

interface SystemRepository {
    suspend fun checkHealth(): AppResult<HealthStatus>
    suspend fun getVoices(): AppResult<List<Voice>>
}
