package com.shubhamthorat.echo.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EchoWarmAccent,
    onPrimary = EchoSoftBlack,
    primaryContainer = EchoWarmAccentDark,
    onPrimaryContainer = EchoTextPrimary,
    secondary = EchoWarmAccentLight,
    onSecondary = EchoSoftBlack,
    background = EchoDeepBlack,
    onBackground = EchoTextPrimary,
    surface = EchoSoftBlack,
    onSurface = EchoTextPrimary,
    surfaceVariant = EchoGray,
    onSurfaceVariant = EchoTextSecondary,
    outline = EchoGray
)

// Minimal light scheme, though we focus on dark mode first
private val LightColorScheme = lightColorScheme(
    primary = EchoWarmAccent,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = EchoDeepBlack
)

@Composable
fun EchoTheme(
    darkTheme: Boolean = true, // Default to true for Dark Mode First
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalSpacing provides EchoSpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = EchoTypography,
            shapes = EchoShapes,
            content = content
        )
    }
}

/**
 * Convenience object to access theme values
 */
object EchoTheme {
    val spacing: EchoSpacing
        @Composable
        get() = LocalSpacing.current
}
