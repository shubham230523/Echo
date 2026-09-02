package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.network.toNetworkError
import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.data.remote.EchoApi
import com.shubhamthorat.echo.domain.model.*
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

    override suspend fun getVoices(): AppResult<List<Voice>> {
        return try {
            val response = api.getVoices()
            AppResult.Success(
                response.voices.map { dto ->
                    Voice(
                        id = dto.id,
                        name = dto.name,
                        description = "", // Backend doesn't provide this yet
                        language = dto.language,
                        gender = dto.gender,
                        previewAudioUrl = dto.previewUrl,
                        provider = mapProvider(dto.provider),
                        isAvailable = true
                    )
                }
            )
        } catch (e: Exception) {
            AppResult.Error(e.toNetworkError().message ?: "Failed to fetch voices")
        }
    }

    private fun mapProvider(provider: String): VoiceProvider {
        return when (provider.uppercase()) {
            "GOOGLE" -> VoiceProvider.GOOGLE
            "OPEN_AI", "OPENAI" -> VoiceProvider.OPEN_AI
            "ELEVEN_LABS", "ELEVENLABS" -> VoiceProvider.ELEVEN_LABS
            "DEEPGRAM" -> VoiceProvider.DEEPGRAM
            else -> VoiceProvider.MOCK
        }
    }
}
