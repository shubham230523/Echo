package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.HealthStatus

interface SystemRepository {
    suspend fun checkHealth(): AppResult<HealthStatus>
}
