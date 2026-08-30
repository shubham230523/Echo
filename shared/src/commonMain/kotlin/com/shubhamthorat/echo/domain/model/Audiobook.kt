package com.shubhamthorat.echo.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents the current lifecycle status of an audiobook.
 */
enum class AudiobookStatus {
    DRAFT,
    PROCESSING,
    GENERATING,
    VALIDATING,
    READY,
    FAILED
}

/**
 * Core domain model representing an audiobook generated from a document.
 *
 * @property id Unique identifier for the audiobook.
 * @property documentId The ID of the source document.
 * @property title The title of the audiobook.
 * @property author The author of the audiobook.
 * @property coverImagePath Local path to the cover image, if available.
 * @property totalDurationSeconds Total duration of all chapters in seconds.
 * @property chapterCount Total number of chapters in the audiobook.
 * @property createdAt Timestamp when the audiobook record was created.
 * @property updatedAt Timestamp when the audiobook was last modified.
 * @property status Current status of the audiobook generation process.
 */
@Serializable
data class Audiobook(
    val id: String,
    val documentId: String,
    val title: String,
    val author: String,
    val coverImagePath: String?,
    val totalDurationSeconds: Int,
    val chapterCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: AudiobookStatus
)
