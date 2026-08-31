package com.shubhamthorat.echo.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shubhamthorat.echo.presentation.components.EchoTopBar
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onClearCacheClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            EchoTopBar(
                title = "Settings",
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
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance Section
            SettingsSectionHeader("Appearance")
            SettingsListItem(
                title = "Theme",
                subtitle = uiState.theme,
                icon = Icons.Default.Palette,
                onClick = onThemeClick
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = EchoTheme.spacing.medium))

            // Playback Section
            SettingsSectionHeader("Playback")
            PlaybackSpeedSetting(
                currentSpeed = uiState.defaultPlaybackSpeed,
                onSpeedSelected = onSpeedSelected
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = EchoTheme.spacing.medium))

            // Storage Section
            SettingsSectionHeader("Storage")
            SettingsListItem(
                title = "Clear Cache",
                subtitle = "Used: ${uiState.storageUsage}",
                icon = Icons.Default.DeleteSweep,
                onClick = onClearCacheClick
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = EchoTheme.spacing.medium))

            // About Section
            SettingsSectionHeader("About")
            SettingsListItem(
                title = "Echo Version",
                subtitle = uiState.version,
                icon = Icons.Default.Info,
                onClick = {} // Static info
            )

            Spacer(modifier = Modifier.height(EchoTheme.spacing.large))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            start = EchoTheme.spacing.medium,
            top = EchoTheme.spacing.large,
            bottom = EchoTheme.spacing.small
        )
    )
}

@Composable
private fun SettingsListItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(EchoTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(EchoTheme.spacing.medium))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PlaybackSpeedSetting(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Surface(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(EchoTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(EchoTheme.spacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Default Playback Speed",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${currentSpeed}x",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Box {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        DropdownMenuItem(
                            text = { Text("${speed}x") },
                            onClick = {
                                onSpeedSelected(speed)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
