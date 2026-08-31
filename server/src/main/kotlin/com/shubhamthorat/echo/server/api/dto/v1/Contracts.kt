package com.shubhamthorat.echo.server.api.dto.v1

import kotlinx.serialization.Serializable

/**
 * Version 1 API Contracts for Echo Backend.
 */

// --- Document Analysis ---

@Serializable
data class AnalyzeDocumentRequest(
    val fileUrl: String,
    val mimeType: String = "application/pdf",
    val options: AnalysisOptions = AnalyzeDocumentRequest.AnalysisOptions()
) {
    @Serializable
    data class AnalysisOptions(
        val detectChapters: Boolean = true,
        val extractMetadata: Boolean = true
    )
}

@Serializable
data class AnalyzeDocumentResponse(
    val analysisId: String,
    val fileName: String,
    val pageCount: Int,
    val totalCharacters: Int,
    val totalWords: Int,
    val status: String
)

// --- Chapter Detection ---

@Serializable
data class GetChaptersRequest(
    val documentId: String
)

@Serializable
data class GetChaptersResponse(
    val documentId: String,
    val chapters: List<ChapterDto>
) {
    @Serializable
    data class ChapterDto(
        val id: String,
        val title: String,
        val index: Int,
        val byteOffset: Long? = null
    )
}

// --- Narration Preparation ---

@Serializable
data class PrepareNarrationRequest(
    val text: String,
    val style: String = "neutral"
)

@Serializable
data class PrepareNarrationResponse(
    val chapterId: String,
    val narrationText: String,
    val estimatedDurationSeconds: Double
)

// --- Dialogue Detection ---

@Serializable
data class DetectDialogueRequest(
    val text: String
)

// --- Pronunciation Assistance ---

@Serializable
data class AssistPronunciationRequest(
    val text: String
)

// --- Voice Management ---

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

// --- Audiobook Generation ---

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

// --- Generation Status ---

@Serializable
data class GenerationStatusResponse(
    val generationId: String,
    val status: String,
    val progress: Float,
    val message: String,
    val audiobookId: String? = null
)

// --- Audiobook Details ---

@Serializable
data class AudiobookDetailResponse(
    val audiobookId: String,
    val title: String,
    val author: String,
    val totalDurationSeconds: Double,
    val createdAt: String,
    val chapters: List<AudioChapterDto>
) {
    @Serializable
    data class AudioChapterDto(
        val id: String,
        val title: String,
        val index: Int,
        val durationSeconds: Double,
        val audioUrl: String
    )
}
