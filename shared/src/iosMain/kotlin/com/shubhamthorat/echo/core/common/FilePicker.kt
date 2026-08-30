package com.shubhamthorat.echo.core.common

/**
 * iOS-specific implementation of FilePicker.
 */
actual class FilePicker {
    actual suspend fun pickPdf(): PlatformFile? {
        // TODO: Implement PDF selection using UIDocumentPickerViewController
        return null
    }
}
