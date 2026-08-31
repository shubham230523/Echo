package com.shubhamthorat.echo.feature.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.domain.model.AudioChapter
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterQueueSheet(
    chapters: List<AudioChapter>,
    currentChapterId: String?,
    onChapterSelected: (AudioChapter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EchoTheme.spacing.medium)
    ) {
        Text(
            text = "Chapters",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = EchoTheme.spacing.medium)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.small)
        ) {
            items(chapters) { chapter ->
                val isCurrent = chapter.chapterId == currentChapterId
                
                ChapterItem(
                    chapter = chapter,
                    isCurrent = isCurrent,
                    onClick = { onChapterSelected(chapter) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(EchoTheme.spacing.large))
    }
}

@Composable
private fun ChapterItem(
    chapter: AudioChapter,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(EchoTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCurrent) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(EchoTheme.spacing.small))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Chapter ${chapter.chapterId}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = formatDuration(chapter.durationSeconds.toInt()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "${mins}:${secs.toString().padStart(2, '0')}"
}
