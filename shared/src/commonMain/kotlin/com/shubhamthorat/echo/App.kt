package com.shubhamthorat.echo

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shubhamthorat.echo.presentation.navigation.EchoNavHost
import com.shubhamthorat.echo.presentation.theme.EchoTheme

@Composable
@Preview
fun App() {
    EchoTheme {
        EchoNavHost()
    }
}