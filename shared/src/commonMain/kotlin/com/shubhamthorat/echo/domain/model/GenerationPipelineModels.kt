package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Request for Stage 1: Document Preparation.
 */
@Serializable
data class DocumentPreparationRequest(
    val documentId: String,
    val rawText: String,
    val options: PreparationOptions = PreparationOptions()
)

@Serializable
data class PreparationOptions(
    val cleanWhitespace: Boolean = true,
    val removePageNumbers: Boolean = true,
    val fixEncoding: Boolean = true
)

@Serializable
data class DocumentPreparationResult(
    val documentId: String,
    val preparedText: String,
    val structure: DocumentStructure,
    val stats: PreparationStats
)

@Serializable
data class PreparationStats(
    val characterCount: Int,
    val estimatedNarrationTimeMinutes: Int
)

/**
 * Request for Stage 3: Chapter Audio Generation.
 */
@Serializable
data class ChapterGenerationRequest(
    val chapterId: String,
    val narrationText: String,
    val voiceId: String,
    val provider: VoiceProvider
)

@Serializable
data class ChapterGenerationResult(
    val chapterId: String,
    val audioUrl: String,
    val durationSeconds: Double,
    val fileSizeByte: Long
)

/**
 * Request for Stage 4: Audio Validation.
 */
@Serializable
data class AudioValidationRequest(
    val chapterId: String,
    val audioUrl: String
)

@Serializable
data class AudioValidationResult(
    val chapterId: String,
    val isValid: Boolean,
    val issues: List<String> = emptyList()
)

/**
 * Request for Stage 5: Audiobook Finalization.
 */
@Serializable
data class AudiobookFinalizationRequest(
    val title: String,
    val author: String,
    val chapterResults: List<ChapterGenerationResult>,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class AudiobookFinalizationResult(
    val audiobook: Audiobook,
    val outputFilePath: String
)
