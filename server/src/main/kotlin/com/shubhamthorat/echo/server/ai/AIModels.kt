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
    val summary: String,
    val nodes: List<StructureNode>
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
    val fullText: String
)

@Serializable
data class ChapterDetectionResponse(
    val chapters: List<DetectedChapter>
)

@Serializable
data class DetectedChapter(
    val title: String,
    val content: String,
    val index: Int
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
    val words: List<String>,
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
    val audioUrl: String? = null
)
