package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HoyoColorScheme = darkColorScheme(
    primary = HoyoGold,
    onPrimary = Color.Black,
    primaryContainer = HoyoCardHover,
    onPrimaryContainer = HoyoGoldBright,
    secondary = HoyoCyan,
    onSecondary = Color.Black,
    secondaryContainer = HoyoSurfaceDark,
    onSecondaryContainer = HoyoCyanBright,
    tertiary = HoyoPurple,
    background = HoyoDeepDark,
    onBackground = HoyoTextPrimary,
    surface = HoyoSurfaceDark,
    onSurface = HoyoTextPrimary,
    surfaceVariant = HoyoCardDark,
    onSurfaceVariant = HoyoTextSecondary,
    outline = HoyoCardBorder,
    error = HoyoRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HoyoColorScheme,
        typography = Typography,
        content = content
    )
}
