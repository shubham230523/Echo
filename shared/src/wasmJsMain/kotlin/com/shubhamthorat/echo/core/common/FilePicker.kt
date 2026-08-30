package com.shubhamthorat.echo.core.common

/**
 * WasmJS implementation of FilePicker.
 */
actual class FilePicker {
    actual suspend fun pickPdf(): PlatformFile? {
        // TODO: Implement PDF selection using <input type="file">
        return null
    }
}
