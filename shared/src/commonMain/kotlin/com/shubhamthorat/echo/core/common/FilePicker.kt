package com.shubhamthorat.echo.core.common

import kotlinx.serialization.Serializable

/**
 * Metadata for a file selected from the host platform.
 *
 * @property name The name of the file (including extension).
 * @property path The platform-specific path or URI reference to the file.
 * @property sizeBytes The size of the file in bytes, if available.
 */
@Serializable
data class PlatformFile(
    val name: String,
    val path: String,
    val sizeBytes: Long? = null
)

/**
 * Platform-specific abstraction for picking a PDF file.
 */
expect class FilePicker {
    /**
     * Launches the platform-specific file picker to select a PDF.
     * @return [PlatformFile] if a file was selected, null otherwise.
     */
    suspend fun pickPdf(): PlatformFile?
}
