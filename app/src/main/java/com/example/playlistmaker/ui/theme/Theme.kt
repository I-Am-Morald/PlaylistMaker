package com.example.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = basicWhite,
    onPrimary = basicBlack,
    onSecondary = ypBlack,
    tertiary = ypLightGray,
    onTertiary = ypTextGray,
    primaryContainer = ypBlue,
    onPrimaryContainer = ypTextGray,
    surface = basicWhite,
    onSurface = basicBlack,
    surfaceVariant = ypLightGray,
    onSurfaceVariant = ypTextGray,
    background = basicWhite,
    onBackground = basicBlack,
    secondaryContainer = ypLightGray,
    onSecondaryContainer = ypTextGray
)

private val DarkColorScheme = darkColorScheme(
    primary = ypBlack,
    onPrimary = basicWhite,
    onSecondary = basicWhite,
    tertiary = basicWhite,
    onTertiary = ypBlack,
    primaryContainer = ypBlack,
    onPrimaryContainer = basicWhite,
    surface = ypBlack,
    onSurface = basicWhite,
    surfaceVariant = ypBlack,
    onSurfaceVariant = basicWhite,
    background = ypBlack,
    onBackground = basicWhite,
    secondaryContainer = ypBlue.copy(alpha = 0.48f),
    onSecondaryContainer = ypBlue
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PlaylistMakerTypography,
        content = content
    )
}