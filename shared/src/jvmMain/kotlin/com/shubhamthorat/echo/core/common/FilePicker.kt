package com.shubhamthorat.echo.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

/**
 * JVM-specific implementation of FilePicker using AWT FileDialog.
 */
class JvmFilePicker(
    private val onFileSelected: (PlatformFile) -> Unit
) : FilePicker {
    override fun pickPdf() {
        // Run on AWT Event Dispatch Thread to avoid issues with some platforms
        java.awt.EventQueue.invokeLater {
            val dialog = FileDialog(null as Frame?, "Select PDF", FileDialog.LOAD).apply {
                setFilenameFilter { _, name -> name.endsWith(".pdf", ignoreCase = true) }
                isVisible = true
            }

            val fileName = dialog.file
            val directory = dialog.directory

            if (fileName != null && directory != null) {
                val pickedFile = File(directory, fileName)
                onFileSelected(
                    PlatformFile(
                        name = pickedFile.name,
                        path = pickedFile.absolutePath,
                        sizeBytes = pickedFile.length()
                    )
                )
            }
        }
    }
}

@Composable
actual fun rememberFilePicker(onFileSelected: (PlatformFile) -> Unit): FilePicker {
    return remember { JvmFilePicker(onFileSelected) }
}
