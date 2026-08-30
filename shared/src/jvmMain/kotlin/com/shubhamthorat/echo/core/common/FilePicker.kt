package com.shubhamthorat.echo.core.common

/**
 * JVM (Desktop) implementation of FilePicker.
 */
actual class FilePicker {
    actual suspend fun pickPdf(): PlatformFile? {
        // TODO: Implement PDF selection using AWT FileDialog or JFileChooser
        return null
    }
}
