package com.shubhamthorat.echo.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Main navigation host for the Echo application.
 * Defines the navigation graph and handles transitions between screens.
 */
@Composable
fun EchoNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Library,
        modifier = modifier
    ) {
        composable<Route.Library> {
            PlaceholderScreen("Library")
        }
        composable<Route.ImportDocument> {
            PlaceholderScreen("Import Document")
        }
        composable<Route.DocumentAnalysis> {
            PlaceholderScreen("Document Analysis")
        }
        composable<Route.Chapters> {
            PlaceholderScreen("Chapters")
        }
        composable<Route.Narration> {
            PlaceholderScreen("Narration")
        }
        composable<Route.VoiceSelection> {
            PlaceholderScreen("Voice Selection")
        }
        composable<Route.Generation> {
            PlaceholderScreen("Generation")
        }
        composable<Route.Player> {
            PlaceholderScreen("Player")
        }
        composable<Route.Settings> {
            PlaceholderScreen("Settings")
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Screen: $name")
    }
}
