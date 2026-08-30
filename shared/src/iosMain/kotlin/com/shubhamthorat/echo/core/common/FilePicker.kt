package com.shubhamthorat.echo.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * iOS-specific implementation of FilePicker.
 */
class IosFilePicker : FilePicker {
    override fun pickPdf() {
        // TODO: Implement
    }
}

@Composable
actual fun rememberFilePicker(onFileSelected: (PlatformFile) -> Unit): FilePicker {
    return remember { IosFilePicker() }
}
