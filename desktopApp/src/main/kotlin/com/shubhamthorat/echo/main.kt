package com.shubhamthorat.echo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.shubhamthorat.echo.core.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Echo",
        ) {
            App()
        }
    }
}
