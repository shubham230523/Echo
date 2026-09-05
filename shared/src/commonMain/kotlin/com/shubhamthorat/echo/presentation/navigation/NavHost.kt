package com.shubhamthorat.echo.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.shubhamthorat.echo.feature.import_document.ImportDocumentViewModel
import com.shubhamthorat.echo.feature.library.LibraryScreen
import com.shubhamthorat.echo.feature.library.LibraryViewModel
import com.shubhamthorat.echo.feature.narration.NarrationScreen
import com.shubhamthorat.echo.feature.narration.NarrationViewModel
import com.shubhamthorat.echo.feature.voice.VoiceSelectionScreen
import com.shubhamthorat.echo.feature.voice.VoiceSelectionViewModel
import com.shubhamthorat.echo.feature.generation.GenerationScreen
import com.shubhamthorat.echo.feature.generation.GenerationViewModel
import com.shubhamthorat.echo.feature.player.PlayerScreen
import com.shubhamthorat.echo.feature.player.PlayerViewModel
import com.shubhamthorat.echo.feature.settings.SettingsScreen
import com.shubhamthorat.echo.feature.settings.SettingsViewModel
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
            val viewModel: LibraryViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LibraryScreen(
                uiState = uiState,
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
                },
                onRetryClick = viewModel::retry
            )
        }
        composable<Route.ImportDocument> {
            val viewModel: ImportDocumentViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    navController.navigate(Route.DocumentAnalysis)
                }
            }

            ImportDocumentScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onSelectFileClick = {
                    filePicker.pickPdf()
                },
                onContinueClick = {
                    viewModel.onContinue()
                }
            )

            // Sync picker results to ViewModel
            LaunchedEffect(selectedFile) {
                selectedFile?.let { viewModel.onFileSelected(it) }
            }
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
                onVoiceSelect = viewModel::onVoiceSelected,
                onPreviewClick = viewModel::onPreviewClick
            )
        }
        composable<Route.Generation> {
            val viewModel: GenerationViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            GenerationScreen(
                uiState = uiState,
                onCancelClick = {
                    viewModel.cancelGeneration()
                    navController.popBackStack()
                },
                onFinishClick = {
                    navController.navigate(Route.Library) {
                        popUpTo(Route.Library) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Player> {
            val viewModel: PlayerViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            PlayerScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onPlayClick = viewModel::play,
                onPauseClick = viewModel::pause,
                onSeek = viewModel::seekTo,
                onSkipForward = viewModel::skipForward,
                onSkipBackward = viewModel::skipBackward,
                onNextChapter = viewModel::nextChapter,
                onPreviousChapter = viewModel::previousChapter,
                onChapterSelected = viewModel::selectChapter,
                onSpeedSelected = viewModel::setPlaybackSpeed
            )
        }
        composable<Route.Settings> {
            val viewModel: SettingsViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            SettingsScreen(
                uiState = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onThemeClick = {
                    // TODO: Show theme selection dialog
                },
                onSpeedSelected = viewModel::setDefaultPlaybackSpeed,
                onClearCacheClick = viewModel::clearCache
            )
        }
    }
}
