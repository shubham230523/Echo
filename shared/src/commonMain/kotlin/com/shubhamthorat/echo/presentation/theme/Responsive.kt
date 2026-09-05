package com.shubhamthorat.echo.presentation.theme

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-level window size classes for responsive design.
 */
enum class WindowSizeClass {
    COMPACT, // Mobile
    MEDIUM,  // Tablet / Small Laptop
    EXPANDED // Desktop / Large Monitor
}

/**
 * Holder for adaptive UI values based on current screen size.
 */
@Immutable
data class AdaptiveConfig(
    val windowSizeClass: WindowSizeClass = WindowSizeClass.COMPACT,
    val isMobile: Boolean = true,
    val isTablet: Boolean = false,
    val isDesktop: Boolean = false,
    val screenWidth: Dp = 0.dp
)

val LocalAdaptiveConfig = staticCompositionLocalOf { AdaptiveConfig() }

/**
 * Calculates the WindowSizeClass based on available width.
 */
fun calculateWindowSizeClass(width: Dp): WindowSizeClass {
    return when {
        width < 600.dp -> WindowSizeClass.COMPACT
        width < 1200.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

/**
 * A container that limits its width on larger screens to maintain readability.
 */
@Composable
fun ResponsiveContent(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 800.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val adaptive = LocalAdaptiveConfig.current
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = if (adaptive.isMobile) {
                Modifier.fillMaxSize()
            } else {
                Modifier.fillMaxHeight().widthIn(max = maxWidth)
            },
            content = content
        )
    }
}
