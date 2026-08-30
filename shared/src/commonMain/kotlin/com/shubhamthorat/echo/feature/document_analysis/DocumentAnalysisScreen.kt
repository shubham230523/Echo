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
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme
import kotlinx.coroutines.delay

private val STAGES = listOf(
    "Reading document" to "Opening the file and preparing for processing.",
    "Extracting text" to "Reading the raw content from the PDF pages.",
    "Understanding structure" to "Identifying headers, paragraphs, and metadata.",
    "Detecting chapters" to "Organizing the content into logical sections.",
    "Preparing narration" to "Optimizing text for natural AI voice generation."
)

@Composable
fun DocumentAnalysisScreen(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStageIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }

    // Fake progress logic for demonstration
    LaunchedEffect(Unit) {
        while (currentStageIndex < STAGES.size) {
            delay(1500) // Simulate work for each stage
            if (progress < 1f) {
                progress += 0.2f
            }
            if (currentStageIndex < STAGES.size - 1) {
                currentStageIndex++
            } else {
                // Last stage complete
                delay(1000)
                onComplete()
                break
            }
        }
    }

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
                progress = { (currentStageIndex + 1).toFloat() / STAGES.size },
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(EchoTheme.spacing.extraLarge))

            // Current Stage Detail
            AnimatedContent(
                targetState = currentStageIndex,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                }
            ) { index ->
                val (title, description) = STAGES[index]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(EchoTheme.spacing.small))
                    Text(
                        text = description,
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
                STAGES.forEachIndexed { index, (title, _) ->
                    StageItem(
                        title = title,
                        isCompleted = index < currentStageIndex,
                        isActive = index == currentStageIndex
                    )
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
