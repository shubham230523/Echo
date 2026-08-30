package com.shubhamthorat.echo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.domain.model.AudiobookStatus
import com.shubhamthorat.echo.presentation.theme.EchoTheme

/**
 * A reusable list item component for displaying audiobook summaries.
 *
 * @param audiobook The audiobook data to display.
 * @param onClick Callback when the item is clicked.
 * @param modifier Modifier for the root card.
 */
@Composable
fun AudiobookListItem(
    audiobook: Audiobook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EchoCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(EchoTheme.spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Icon or placeholder text could be added here
            }

            Spacer(modifier = Modifier.width(EchoTheme.spacing.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = audiobook.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = audiobook.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(EchoTheme.spacing.extraSmall))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${audiobook.chapterCount} Chapters",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDuration(audiobook.totalDurationSeconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(EchoTheme.spacing.small))

            // Status Badge
            Text(
                text = audiobook.status.name,
                style = MaterialTheme.typography.labelSmall,
                color = getStatusColor(audiobook.status)
            )
        }
    }
}

@Composable
private fun getStatusColor(status: AudiobookStatus) = when (status) {
    AudiobookStatus.READY -> MaterialTheme.colorScheme.primary
    AudiobookStatus.FAILED -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.secondary
}

private fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
