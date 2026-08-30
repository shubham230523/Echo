package com.shubhamthorat.echo.feature.import_document

import com.shubhamthorat.echo.core.common.PlatformFile

/**
 * Represents the result of a file validation check.
 */
sealed interface FileValidationResult {
    data object Valid : FileValidationResult
    data class Invalid(val message: String) : FileValidationResult
}

/**
 * Validates the selected platform file.
 */
fun validateSelectedFile(file: PlatformFile?): FileValidationResult {
    if (file == null) {
        return FileValidationResult.Invalid("No file selected")
    }

    if (!file.name.lowercase().endsWith(".pdf")) {
        return FileValidationResult.Invalid("Only PDF files are supported")
    }

    if (file.sizeBytes != null && file.sizeBytes <= 0) {
        return FileValidationResult.Invalid("Selected file is empty")
    }

    return FileValidationResult.Valid
}
