package com.shubhamthorat.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    com.shubhamthorat.echo.presentation.theme.EchoTheme(darkTheme = true) {
        com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisScreen(
            uiState = com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisUiState(
                currentStage = com.shubhamthorat.echo.domain.model.AnalysisStage.ANALYZING_STRUCTURE,
                progress = 0.5f,
                statusMessage = "Identifying headers, paragraphs, and metadata."
            ),
            onBackClick = {}
        )
    }
}