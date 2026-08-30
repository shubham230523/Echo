package com.shubhamthorat.echo.feature.import_document

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.core.common.PlatformFile
import com.shubhamthorat.echo.presentation.components.EchoButton
import com.shubhamthorat.echo.presentation.components.EchoCard
import com.shubhamthorat.echo.presentation.components.EchoOutlineButton
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

/**
 * Screen for importing documents to be converted into audiobooks.
 *
 * @param onBackClick Callback for navigation back.
 * @param onSelectFile Callback when the user triggers the file selection.
 * @param onContinueClick Callback when the user wants to proceed with the selected file.
 * @param selectedFile The currently selected file, if any.
 * @param modifier Modifier for the root layout.
 */
@Composable
fun ImportDocumentScreen(
    onBackClick: () -> Unit,
    onSelectFile: () -> Unit,
    onContinueClick: () -> Unit,
    selectedFile: PlatformFile? = null,
    modifier: Modifier = Modifier
) {
    val validationResult = remember(selectedFile) { validateSelectedFile(selectedFile) }

    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Create Audiobook",
                onNavigationClick = onBackClick
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(EchoTheme.spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            if (selectedFile == null) {
                ImportIdleContent(
                    onSelectFile = onSelectFile
                )
            } else {
                FileSelectedContent(
                    file = selectedFile,
                    validationResult = validationResult,
                    onReplaceFile = onSelectFile,
                    onContinue = onContinueClick
                )
            }
        }
    }
}

@Composable
private fun ImportIdleContent(
    onSelectFile: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EchoCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clickable(onClick = onSelectFile)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))

                Text(
                    text = "Choose a PDF",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(EchoTheme.spacing.small))

                Text(
                    text = "Upload a document to transform it into an audiobook",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = EchoTheme.spacing.medium)
                )
            }
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.large))

        Text(
            text = "Supported format: PDF",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FileSelectedContent(
    file: PlatformFile,
    validationResult: FileValidationResult,
    onReplaceFile: () -> Unit,
    onContinue: () -> Unit
) {
    val isValid = validationResult is FileValidationResult.Valid

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))

        Text(
            text = file.name,
            style = MaterialTheme.typography.titleMedium,
            color = if (isValid) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        if (isValid) {
            file.sizeBytes?.let { size ->
                Text(
                    text = formatFileSize(size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = EchoTheme.spacing.extraSmall)
                )
            }
        } else if (validationResult is FileValidationResult.Invalid) {
            Text(
                text = validationResult.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = EchoTheme.spacing.extraSmall)
            )
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.huge))

        EchoButton(
            onClick = onContinue,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Continue")
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))

        EchoOutlineButton(
            onClick = onReplaceFile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Replace File")
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        "${(mb * 100).toInt() / 100.0} MB"
    } else {
        "${(kb * 100).toInt() / 100.0} KB"
    }
}
