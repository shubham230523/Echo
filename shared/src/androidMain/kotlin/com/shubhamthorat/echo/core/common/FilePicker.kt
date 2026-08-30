package com.shubhamthorat.echo.core.common

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android-specific implementation of FilePicker.
 */
class AndroidFilePicker(
    private val launch: () -> Unit
) : FilePicker {
    override fun pickPdf() {
        launch()
    }
}

@Composable
actual fun rememberFilePicker(onFileSelected: (PlatformFile) -> Unit): FilePicker {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val platformFile = contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    val name = cursor.getString(nameIndex)
                    val size = cursor.getLong(sizeIndex)
                    PlatformFile(
                        name = name,
                        path = it.toString(),
                        sizeBytes = if (size > 0) size else null
                    )
                }
                platformFile?.let(onFileSelected)
            }
        }
    )

    return remember {
        AndroidFilePicker {
            launcher.launch("application/pdf")
        }
    }
}
