package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.network.toNetworkError
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.data.remote.EchoApi
import com.shubhamthorat.echo.domain.model.HealthStatus
import com.shubhamthorat.echo.domain.repository.SystemRepository

class ApiSystemRepository(
    private val api: EchoApi
) : SystemRepository {
    override suspend fun checkHealth(): AppResult<HealthStatus> {
        return try {
            val response = api.checkHealth()
            AppResult.Success(
                HealthStatus(
                    status = response.status,
                    version = response.metadata.version,
                    environment = response.metadata.environment
                )
            )
        } catch (e: Exception) {
            AppResult.Error(e.toNetworkError().message ?: "Unknown error")
        }
    }
}
