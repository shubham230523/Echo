package com.shubhamthorat.echo.feature.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.presentation.components.AudiobookListItem
import com.shubhamthorat.echo.presentation.components.EchoButton
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onCreateAudiobookClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAudiobookClick: (Audiobook) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Echo",
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.audiobooks.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onCreateAudiobookClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Audiobook")
                }
            }
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    LibraryErrorContent(
                        message = uiState.error,
                        onRetry = onRetryClick
                    )
                }
                uiState.isEmpty -> {
                    LibraryEmptyContent(
                        onCreateAudiobookClick = onCreateAudiobookClick
                    )
                }
                else -> {
                    LibraryListContent(
                        audiobooks = uiState.audiobooks,
                        onAudiobookClick = onAudiobookClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(EchoTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun LibraryEmptyContent(
    onCreateAudiobookClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(EchoTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(EchoTheme.spacing.large))

        Text(
            text = "Welcome to Echo",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Transform your documents into premium audiobooks.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = EchoTheme.spacing.small)
        )

        Spacer(modifier = Modifier.weight(1f))

        LibraryEmptyState(
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        EchoButton(
            onClick = onCreateAudiobookClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Audiobook")
        }

        Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))
    }
}

@Composable
private fun LibraryListContent(
    audiobooks: List<Audiobook>,
    onAudiobookClick: (Audiobook) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(EchoTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.medium)
    ) {
        item {
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(audiobooks) { audiobook ->
            AudiobookListItem(
                audiobook = audiobook,
                onClick = { onAudiobookClick(audiobook) }
            )
        }
    }
}

@Composable
private fun LibraryEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LibraryMusic,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))
        
        Text(
            text = "No audiobooks yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Start by importing your first document.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = EchoTheme.spacing.extraSmall)
        )
    }
}
