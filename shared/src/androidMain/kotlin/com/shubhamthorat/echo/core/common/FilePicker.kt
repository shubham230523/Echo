package com.shubhamthorat.echo.core.common

/**
 * Android-specific implementation of FilePicker.
 * Note: Actual implementation requires hooking into Activity results.
 */
actual class FilePicker {
    actual suspend fun pickPdf(): PlatformFile? {
        // TODO: Implement PDF selection using Intent.ACTION_OPEN_DOCUMENT
        // Requires ActivityResultLauncher integration.
        return null
    }
}
