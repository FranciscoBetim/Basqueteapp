package com.brs.basqueteapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange = Color(0xFFFF6D28)
val OrangeDark = Color(0xFFC24E13)
val Ink = Color(0xFF0F1720)
val Slate = Color(0xFF1C2733)
val SlateLight = Color(0xFF27333F)
val Cloud = Color(0xFFEFF3F7)

private val DarkColors = darkColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = OrangeDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF4DA3FF),
    background = Ink,
    onBackground = Cloud,
    surface = Slate,
    onSurface = Cloud,
    surfaceVariant = SlateLight,
    onSurfaceVariant = Color(0xFFB9C4CF),
    error = Color(0xFFFF5A5A),
    outline = Color(0xFF3A4854)
)

private val LightColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC9),
    onPrimaryContainer = OrangeDark,
    secondary = Color(0xFF1E6FD9),
    background = Cloud,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE3E9EF),
    onSurfaceVariant = Color(0xFF44515D),
    error = Color(0xFFD32F2F),
    outline = Color(0xFFBFCAD4)
)

@Composable
fun BasqueteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
