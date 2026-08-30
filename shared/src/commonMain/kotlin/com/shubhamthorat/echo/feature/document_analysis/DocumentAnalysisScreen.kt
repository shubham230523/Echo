package com.shubhamthorat.echo.feature.document_analysis

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.domain.model.AnalysisStage
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

private val AnalysisStage.displayTitle: String
    get() = when (this) {
        AnalysisStage.READING_DOCUMENT -> "Reading document"
        AnalysisStage.EXTRACTING_TEXT -> "Extracting text"
        AnalysisStage.ANALYZING_STRUCTURE -> "Understanding structure"
        AnalysisStage.DETECTING_CHAPTERS -> "Detecting chapters"
        AnalysisStage.COMPLETED -> "Analysis Complete"
    }

@Composable
fun DocumentAnalysisScreen(
    uiState: DocumentAnalysisUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Analyzing Document",
                onNavigationClick = onBackClick
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(EchoTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(EchoTheme.spacing.medium))

            // Main Progress
            CircularProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(EchoTheme.spacing.extraLarge))

            // Current Stage Detail
            AnimatedContent(
                targetState = uiState.currentStage,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                },
                label = "AnalysisStageAnimation"
            ) { stage ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stage.displayTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(EchoTheme.spacing.small))
                    Text(
                        text = uiState.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(EchoTheme.spacing.huge))

            // Stage List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(EchoTheme.spacing.medium)
            ) {
                AnalysisStage.entries.forEach { stage ->
                    if (stage != AnalysisStage.COMPLETED) {
                        StageItem(
                            title = stage.displayTitle,
                            isCompleted = stage.ordinal < uiState.currentStage.ordinal || uiState.isCompleted,
                            isActive = stage == uiState.currentStage && !uiState.isCompleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StageItem(
    title: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isActive -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            },
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(EchoTheme.spacing.medium))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isActive || isCompleted) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            },
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
