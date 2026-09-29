package com.fisherfence.maritime.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    secondary = SecondaryBlue,
    background = DarkNavyBackground,
    surface = DarkCardSurface,
    error = ErrorRed,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onError = Color.White,
    outline = BorderBlue
)

@Composable
fun FisherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
