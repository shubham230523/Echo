package com.shubhamthorat.echo.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents the status of an imported document in the system.
 */
enum class DocumentStatus {
    IMPORTED,
    ANALYZING,
    ANALYZED,
    PROCESSING,
    READY,
    FAILED
}

/**
 * Core domain model representing a document imported for audiobook creation.
 *
 * @property id Unique identifier for the document.
 * @property fileName Original name of the imported file.
 * @property filePath Local path where the document is stored.
 * @property fileSizeBytes Size of the file in bytes.
 * @property pageCount Total number of pages in the document.
 * @property importedAt Timestamp when the document was imported.
 * @property status Current processing status of the document.
 */
@Serializable
data class Document(
    val id: String,
    val fileName: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val importedAt: Instant,
    val status: DocumentStatus
)
