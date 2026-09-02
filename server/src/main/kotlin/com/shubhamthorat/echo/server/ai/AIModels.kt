package com.shubhamthorat.echo.server.ai

import kotlinx.serialization.Serializable

/**
 * --- Document Structure Analysis ---
 */
@Serializable
data class DocumentStructureRequest(
    val fullText: String,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class DocumentStructureResponse(
    val title: String,
    val author: String? = null,
    val type: String, // e.g., "BOOK", "ARTICLE", "PAPER"
    val language: String,
    val chapters: List<DetectedChapter> = emptyList()
)

@Serializable
data class TocEntry(
    val title: String,
    val level: Int
)

@Serializable
data class ChapterAnchor(
    val title: String,
    val startIndex: Int
)

@Serializable
data class ChapterAnchorsResponse(
    val anchors: List<ChapterAnchor>
)

@Serializable
data class StructureNode(
    val type: String, // e.g., "PART", "CHAPTER", "SECTION"
    val title: String,
    val startIndex: Int,
    val endIndex: Int,
    val children: List<StructureNode> = emptyList()
)

/**
 * --- Chapter Detection ---
 */
@Serializable
data class ChapterDetectionRequest(
    val fullText: String,
    val structure: DocumentStructureResponse? = null
)

@Serializable
data class ChapterDetectionResponse(
    val chapters: List<DetectedChapter>
)

@Serializable
data class DetectedChapter(
    val title: String,
    val index: Int,
    val startIndex: Int,
    val endIndex: Int,
    val confidence: Float
)

/**
 * --- Narration Preparation ---
 */
@Serializable
data class NarrationPreparationRequest(
    val text: String,
    val style: String, // e.g., "storytelling", "formal", "conversational"
    val targetFormat: String = "SSML"
)

@Serializable
data class NarrationPreparationResponse(
    val preparedText: String,
    val estimatedDurationSeconds: Double,
    val notes: String? = null
)

/**
 * --- Dialogue Detection ---
 */
@Serializable
data class DialogueDetectionRequest(
    val text: String
)

@Serializable
data class DialogueDetectionResponse(
    val segments: List<DialogueSegment>
)

@Serializable
data class DialogueSegment(
    val text: String,
    val speaker: String?, // e.g., "Narrator", "Character Name"
    val isDialogue: Boolean
)

/**
 * --- Pronunciation Assistance ---
 */
@Serializable
data class PronunciationRequest(
    val text: String,
    val context: String? = null
)

@Serializable
data class PronunciationResponse(
    val guides: List<WordPronunciation>
)

@Serializable
data class WordPronunciation(
    val word: String,
    val ipa: String?,
    val phoneticRespelling: String?,
    val confidence: Float,
    val audioUrl: String? = null
)

/**
 * --- Transcription and Validation ---
 */
@Serializable
data class TranscriptionRequest(
    val audioUrl: String,
    val language: String? = null
)

@Serializable
data class TranscriptionResponse(
    val text: String,
    val confidence: Float
)

@Serializable
data class ContentComparisonRequest(
    val sourceText: String,
    val transcription: String
)

@Serializable
data class ContentComparisonResponse(
    val matchScore: Float,
    val issues: List<String>,
    val differences: List<ContentDifference>
)

@Serializable
data class ContentDifference(
    val type: String, // e.g., "MISSING", "MODIFIED", "ADDED"
    val description: String,
    val severity: String // e.g., "LOW", "MEDIUM", "HIGH"
)
