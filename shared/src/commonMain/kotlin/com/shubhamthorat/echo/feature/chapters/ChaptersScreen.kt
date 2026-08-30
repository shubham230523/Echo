package com.shubhamthorat.echo.feature.chapters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@Composable
fun ChaptersScreen(
    uiState: ChaptersUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onEditChapterClick: (Chapter) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Chapters",
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
                    text = "Continue to Narration",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(EchoTheme.spacing.large)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Document Header Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(EchoTheme.spacing.medium)
                    ) {
                        Text(
                            text = uiState.documentTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(EchoTheme.spacing.extraSmall))
                        Text(
                            text = "${uiState.chapters.size} chapters detected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = EchoTheme.spacing.medium),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Chapter List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(EchoTheme.spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.medium)
                    ) {
                        items(uiState.chapters) { chapter ->
                            ChapterItem(
                                chapter = chapter,
                                onEditClick = { onEditChapterClick(chapter) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: Chapter,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EchoTheme.spacing.medium))
            .clickable(onClick = onEditClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(EchoTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier
                .padding(EchoTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chapter Number Bubble
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (chapter.index + 1).toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(EchoTheme.spacing.medium))

            // Title and Duration
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Est. duration: ${chapter.estimatedDurationSeconds / 60}m ${chapter.estimatedDurationSeconds % 60}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Edit Action
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Chapter",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
