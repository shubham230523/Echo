package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the processing status of a specific chapter.
 */
enum class ChapterStatus {
    PENDING,
    PROCESSING,
    READY,
    FAILED
}

/**
 * Core domain model representing a chapter within a document.
 *
 * @property id Unique identifier for the chapter.
 * @property documentId The ID of the document this chapter belongs to.
 * @property index The position of the chapter in the document.
 * @property title The title of the chapter.
 * @property originalText The raw text extracted from the document.
 * @property narrationText The cleaned or modified text optimized for narration.
 * @property estimatedDurationSeconds The calculated duration of the audio in seconds.
 * @property status Current narration status of the chapter.
 */
@Serializable
data class Chapter(
    val id: String,
    val documentId: String,
    val index: Int,
    val title: String,
    val originalText: String,
    val narrationText: String,
    val estimatedDurationSeconds: Int,
    val status: ChapterStatus
)
