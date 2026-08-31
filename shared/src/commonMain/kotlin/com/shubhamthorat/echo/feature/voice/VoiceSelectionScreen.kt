package com.shubhamthorat.echo.feature.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.domain.model.Voice
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@Composable
fun VoiceSelectionScreen(
    uiState: VoiceSelectionUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onVoiceSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Choose Voice",
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
                shape = RoundedCornerShape(EchoTheme.spacing.medium),
                enabled = uiState.selectedVoiceId != null
            ) {
                Text(
                    text = "Continue to Generation",
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(EchoTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.medium)
                ) {
                    item {
                        Text(
                            text = "Select a voice that best fits your content style.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = EchoTheme.spacing.medium)
                        )
                    }

                    items(uiState.voices) { voice ->
                        VoiceItem(
                            voice = voice,
                            isSelected = uiState.selectedVoiceId == voice.id,
                            onSelect = { onVoiceSelect(voice.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceItem(
    voice: Voice,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EchoTheme.spacing.medium))
            .clickable(onClick = onSelect),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        shape = RoundedCornerShape(EchoTheme.spacing.medium),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Row(
            modifier = Modifier.padding(EchoTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = voice.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(EchoTheme.spacing.small))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = voice.provider.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                Text(
                    text = voice.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(EchoTheme.spacing.extraSmall))
                
                Text(
                    text = "${voice.language} • ${voice.gender ?: "Neutral"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            CircleShape
                        )
                )
            }
        }
    }
}
