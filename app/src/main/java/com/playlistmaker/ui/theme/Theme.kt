package com.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalAppColors = staticCompositionLocalOf { LightAppColors }

private val LightColorScheme = lightColorScheme(
    primary = YpBlue,
    onPrimary = White,
    background = White,
    onBackground = YpBlack,
    surface = White,
    onSurface = YpBlack,
    surfaceContainerHigh = White,
    onSurfaceVariant = YpBlack,
)

private val DarkColorScheme = darkColorScheme(
    primary = YpBlue,
    onPrimary = White,
    background = YpBlack,
    onBackground = White,
    surface = YpBlack,
    onSurface = White,
    surfaceContainerHigh = YpBlack,
    onSurfaceVariant = White,
)

private val SelectionColors = TextSelectionColors(
    handleColor = YpBlue,
    backgroundColor = YpBlue.copy(alpha = 0.4f),
)

/**
 * Тёмная тема определяется конфигурацией активити: AppCompatDelegate.setDefaultNightMode()
 * подменяет uiMode, поэтому isSystemInDarkTheme() отражает выбор пользователя в настройках.
 */
@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    ) {
        CompositionLocalProvider(
            LocalAppColors provides if (darkTheme) DarkAppColors else LightAppColors,
            LocalTextSelectionColors provides SelectionColors,
            content = content,
        )
    }
}

object PlaylistMakerTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}
