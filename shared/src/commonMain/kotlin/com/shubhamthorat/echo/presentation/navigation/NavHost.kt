package com.shubhamthorat.echo.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shubhamthorat.echo.core.common.PlatformFile
import com.shubhamthorat.echo.core.common.rememberFilePicker
import com.shubhamthorat.echo.domain.model.AudiobookStatus
import com.shubhamthorat.echo.feature.chapters.ChaptersScreen
import com.shubhamthorat.echo.feature.chapters.ChaptersViewModel
import com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisScreen
import com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisViewModel
import com.shubhamthorat.echo.feature.import_document.ImportDocumentScreen
import com.shubhamthorat.echo.feature.library.LibraryMocks
import com.shubhamthorat.echo.feature.library.LibraryScreen
import com.shubhamthorat.echo.feature.narration.NarrationScreen
import com.shubhamthorat.echo.feature.narration.NarrationViewModel
import com.shubhamthorat.echo.feature.voice.VoiceSelectionScreen
import com.shubhamthorat.echo.feature.voice.VoiceSelectionViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main navigation host for the Echo application.
 * Defines the navigation graph and handles transitions between screens.
 */
@Composable
fun EchoNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var selectedFile by remember { mutableStateOf<PlatformFile?>(null) }

    val filePicker = rememberFilePicker { file ->
        selectedFile = file
    }

    NavHost(
        navController = navController,
        startDestination = Route.Library,
        modifier = modifier
    ) {
        composable<Route.Library> {
            LibraryScreen(
                audiobooks = LibraryMocks.sampleAudiobooks,
                onCreateAudiobookClick = {
                    selectedFile = null
                    navController.navigate(Route.ImportDocument)
                },
                onSettingsClick = {
                    navController.navigate(Route.Settings)
                },
                onAudiobookClick = { audiobook ->
                    if (audiobook.status == AudiobookStatus.READY) {
                        navController.navigate(Route.Player)
                    }
                }
            )
        }
        composable<Route.ImportDocument> {
            ImportDocumentScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSelectFile = {
                    filePicker.pickPdf()
                },
                onContinueClick = {
                    navController.navigate(Route.DocumentAnalysis)
                },
                selectedFile = selectedFile
            )
        }
        composable<Route.DocumentAnalysis> {
            val viewModel: DocumentAnalysisViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                selectedFile?.let { file ->
                    viewModel.startAnalysis(file)
                } ?: run {
                    // If no file selected (shouldn't happen with correct nav), go back
                    navController.popBackStack()
                }
            }

            LaunchedEffect(uiState.isCompleted) {
                if (uiState.isCompleted) {
                    // Small delay to let user see completion
                    delay(1000)
                    navController.navigate(Route.Chapters)
                }
            }

            DocumentAnalysisScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<Route.Chapters> {
            val viewModel: ChaptersViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            ChaptersScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = {
                    navController.navigate(Route.Narration)
                },
                onEditChapterClick = viewModel::onEditChapterClick,
                onDismissEditDialog = viewModel::onDismissEditDialog,
                onConfirmEditTitle = viewModel::onUpdateChapterTitle,
                onChapterSelect = { viewModel.toggleChapterSelection(it.id) },
                onMergeClick = viewModel::mergeSelectedChapters
            )
        }
        composable<Route.Narration> {
            val viewModel: NarrationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            NarrationScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = {
                    navController.navigate(Route.VoiceSelection)
                },
                onChapterSelected = viewModel::onChapterSelected,
                onRegenerateClick = viewModel::onRegenerateClick
            )
        }
        composable<Route.VoiceSelection> {
            val viewModel: VoiceSelectionViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            VoiceSelectionScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = {
                    navController.navigate(Route.Generation)
                },
                onVoiceSelect = viewModel::onVoiceSelected
            )
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
