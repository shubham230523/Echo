package com.shubhamthorat.echo.feature.import_document

import com.shubhamthorat.echo.core.common.PlatformFile

/**
 * UI State for the Import Document screen.
 *
 * @property selectedFile The currently selected file.
 * @property isImporting Whether the document is being persisted.
 * @property error Optional error message.
 * @property isSuccess Whether the import was successful.
 * @property importedDocumentId The ID of the imported document.
 */
data class ImportDocumentUiState(
    val selectedFile: PlatformFile? = null,
    val isImporting: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val importedDocumentId: String? = null
)
