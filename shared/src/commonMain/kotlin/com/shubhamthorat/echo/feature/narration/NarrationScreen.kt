package com.shubhamthorat.echo.feature.narration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@Composable
fun NarrationScreen(
    uiState: NarrationUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onChapterSelected: (Int) -> Unit,
    onRegenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentChapter = uiState.currentChapter

    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Narration Preparation",
                onNavigationClick = onBackClick
            )
        },
        bottomBar = {
            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(EchoTheme.spacing.medium)
                    .height(56.dp),
                shape = RoundedCornerShape(EchoTheme.spacing.medium)
            ) {
                Text(
                    text = "Continue to Voice Selection",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chapter Selector
            if (uiState.chapters.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = uiState.currentChapterIndex,
                    edgePadding = EchoTheme.spacing.medium,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    uiState.chapters.forEachIndexed { index, _ ->
                        Tab(
                            selected = uiState.currentChapterIndex == index,
                            onClick = { onChapterSelected(index) },
                            text = {
                                Text(
                                    text = "Ch ${index + 1}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        )
                    }
                }
            }

            if (currentChapter != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(EchoTheme.spacing.medium)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.large)
                ) {
                    // Original Text Section
                    TextSection(
                        title = "Original Text",
                        content = currentChapter.originalText,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )

                    // Narration Text Section
                    Column(verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Narration Text",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            TextButton(
                                onClick = onRegenerateClick,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Regenerate")
                            }
                        }

                        TextSection(
                            title = "",
                            content = currentChapter.narrationText,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            textColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else if (!uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No chapters found.")
                }
            }
        }
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun TextSection(
    title: String,
    content: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.small)) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(EchoTheme.spacing.medium)),
            color = backgroundColor,
            shape = RoundedCornerShape(EchoTheme.spacing.medium)
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(EchoTheme.spacing.medium),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}
