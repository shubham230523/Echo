package com.shubhamthorat.echo.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * WasmJS-specific implementation of FilePicker.
 */
class WasmJsFilePicker : FilePicker {
    override fun pickPdf() {
        // TODO: Implement
    }
}

@Composable
actual fun rememberFilePicker(onFileSelected: (PlatformFile) -> Unit): FilePicker {
    return remember { WasmJsFilePicker() }
}
