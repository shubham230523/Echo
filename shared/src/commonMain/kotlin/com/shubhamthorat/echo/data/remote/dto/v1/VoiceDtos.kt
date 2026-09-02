package com.shubhamthorat.echo.data.remote.dto.v1

import kotlinx.serialization.Serializable

@Serializable
data class GetVoicesResponse(
    val voices: List<VoiceDto>
) {
    @Serializable
    data class VoiceDto(
        val id: String,
        val name: String,
        val provider: String,
        val language: String,
        val gender: String,
        val previewUrl: String?
    )
}
