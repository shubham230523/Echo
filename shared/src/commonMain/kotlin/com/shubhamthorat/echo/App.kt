package com.shubhamthorat.echo

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shubhamthorat.echo.presentation.navigation.EchoNavHost
import com.shubhamthorat.echo.presentation.theme.*

@Composable
@Preview
fun App() {
    BoxWithConstraints {
        val windowSizeClass = calculateWindowSizeClass(maxWidth)
        val adaptiveConfig = AdaptiveConfig(
            windowSizeClass = windowSizeClass,
            isMobile = windowSizeClass == WindowSizeClass.COMPACT,
            isTablet = windowSizeClass == WindowSizeClass.MEDIUM,
            isDesktop = windowSizeClass == WindowSizeClass.EXPANDED,
            screenWidth = maxWidth
        )

        EchoTheme(adaptiveConfig = adaptiveConfig) {
            EchoNavHost()
        }
    }
}
