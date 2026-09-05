package com.shubhamthorat.echo.feature.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubhamthorat.echo.domain.model.AudioChapter
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onChapterSelected: (AudioChapter) -> Unit,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showChapters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Now Playing",
                onNavigationClick = onBackClick
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val adaptive = EchoTheme.ads
        
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (adaptive.isMobile) {
                MobilePlayerContent(
                    uiState = uiState,
                    onPlayClick = onPlayClick,
                    onPauseClick = onPauseClick,
                    onSeek = onSeek,
                    onSkipForward = onSkipForward,
                    onSkipBackward = onSkipBackward,
                    onNextChapter = onNextChapter,
                    onPreviousChapter = onPreviousChapter,
                    onSpeedSelected = onSpeedSelected,
                    showChapters = { showChapters = true }
                )
            } else {
                WidePlayerContent(
                    uiState = uiState,
                    onPlayClick = onPlayClick,
                    onPauseClick = onPauseClick,
                    onSeek = onSeek,
                    onSkipForward = onSkipForward,
                    onSkipBackward = onSkipBackward,
                    onNextChapter = onNextChapter,
                    onPreviousChapter = onPreviousChapter,
                    onSpeedSelected = onSpeedSelected,
                    showChapters = { showChapters = true }
                )
            }
        }
    }
    
    if (showChapters) {
        ModalBottomSheet(
            onDismissRequest = { showChapters = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            ChapterQueueSheet(
                chapters = uiState.chapters,
                currentChapterId = uiState.currentChapter?.chapterId,
                onChapterSelected = {
                    onChapterSelected(it)
                    showChapters = false
                }
            )
        }
    }
}

@Composable
private fun MobilePlayerContent(
    uiState: PlayerUiState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    showChapters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(EchoTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Audiobook Cover & Titles Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Surface(
                modifier = Modifier
                    .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                    .aspectRatio(1f)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(EchoTheme.spacing.medium)),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(EchoTheme.spacing.large))

            Text(
                text = uiState.audiobook?.title ?: "No Audiobook Selected",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(EchoTheme.spacing.extraSmall))
            
            Text(
                text = uiState.currentChapter?.chapterId ?: "Initializing...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Progress & Controls Section
        PlayerControls(
            uiState = uiState,
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            onSeek = onSeek,
            onSkipForward = onSkipForward,
            onSkipBackward = onSkipBackward,
            onNextChapter = onNextChapter,
            onPreviousChapter = onPreviousChapter,
            onSpeedSelected = onSpeedSelected,
            showChapters = showChapters
        )
    }
}

@Composable
private fun WidePlayerContent(
    uiState: PlayerUiState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    showChapters: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(EchoTheme.spacing.huge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EchoTheme.spacing.huge)
    ) {
        // Left Side: Audiobook Cover
        Surface(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(EchoTheme.spacing.large)),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.4f),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )
            }
        }

        // Right Side: Info & Controls
        Column(
            modifier = Modifier.weight(1.2f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = uiState.audiobook?.title ?: "No Audiobook Selected",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(EchoTheme.spacing.small))
            
            Text(
                text = uiState.currentChapter?.chapterId ?: "Initializing...",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(EchoTheme.spacing.huge))

            PlayerControls(
                uiState = uiState,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                onSeek = onSeek,
                onSkipForward = onSkipForward,
                onSkipBackward = onSkipBackward,
                onNextChapter = onNextChapter,
                onPreviousChapter = onPreviousChapter,
                onSpeedSelected = onSpeedSelected,
                showChapters = showChapters
            )
        }
    }
}

@Composable
private fun PlayerControls(
    uiState: PlayerUiState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    showChapters: () -> Unit
) {
    var showSpeedMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Progress Slider
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = uiState.progress,
                onValueChange = { onSeek((it * uiState.duration).toLong()) },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.currentPositionText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = uiState.totalDurationText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))

        // Primary Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousChapter) {
                Icon(Icons.Default.SkipPrevious, "Previous Chapter", modifier = Modifier.size(32.dp))
            }

            IconButton(onClick = onSkipBackward) {
                Icon(Icons.Default.Replay10, "Rewind 10s", modifier = Modifier.size(32.dp))
            }

            Surface(
                onClick = { if (uiState.isPlaying) onPauseClick() else onPlayClick() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            IconButton(onClick = onSkipForward) {
                Icon(Icons.Default.Forward10, "Forward 10s", modifier = Modifier.size(32.dp))
            }

            IconButton(onClick = onNextChapter) {
                Icon(Icons.Default.SkipNext, "Next Chapter", modifier = Modifier.size(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(EchoTheme.spacing.large))

        // Bottom Row: Speed & Chapters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                TextButton(
                    onClick = { showSpeedMenu = true },
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = "${uiState.playbackSpeed}x",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false }
                ) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        DropdownMenuItem(
                            text = { Text("${speed}x") },
                            onClick = {
                                onSpeedSelected(speed)
                                showSpeedMenu = false
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = showChapters,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(Icons.Default.List, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Chapters")
            }
        }
    }
}
