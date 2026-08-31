package com.shubhamthorat.echo.data.remote.dto.v1

import kotlinx.serialization.Serializable

@Serializable
data class GenerateAudiobookRequest(
    val documentId: String,
    val voiceId: String,
    val chapters: List<ChapterRequest>,
    val title: String? = null,
    val author: String? = null,
    val speed: Float = 1.0f
) {
    @Serializable
    data class ChapterRequest(
        val id: String,
        val title: String,
        val text: String
    )
}

@Serializable
data class GenerateAudiobookResponse(
    val generationId: String,
    val status: String,
    val queuePosition: Int
)

@Serializable
data class GenerationStatusResponse(
    val generationId: String,
    val status: String,
    val progress: Float,
    val currentStep: String,
    val currentChapter: String? = null,
    val completedChapters: Int,
    val totalChapters: Int,
    val error: String? = null,
    val audiobookId: String? = null
)
