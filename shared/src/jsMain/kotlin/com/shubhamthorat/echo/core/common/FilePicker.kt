package com.shubhamthorat.echo.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * JS-specific implementation of FilePicker.
 */
class JsFilePicker : FilePicker {
    override fun pickPdf() {
        // TODO: Implement
    }
}

@Composable
actual fun rememberFilePicker(onFileSelected: (PlatformFile) -> Unit): FilePicker {
    return remember { JsFilePicker() }
}
