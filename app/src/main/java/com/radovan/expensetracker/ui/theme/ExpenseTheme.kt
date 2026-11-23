package com.radovan.expensetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppBackground = Color(0xFF101110)
val AppSurface = Color(0xFF1B1E1B)
val AppSurfaceVariant = Color(0xFF252A25)
val AppAccent = Color(0xFFB5F36B)
val AppText = Color(0xFFF4F5F2)
val AppMuted = Color(0xFF929890)
val AppRed = Color(0xFFFF7777)

private val DarkColors = darkColorScheme(
    primary = AppAccent,
    onPrimary = AppBackground,
    secondary = Color(0xFF76B9A0),
    background = AppBackground,
    onBackground = AppText,
    surface = AppSurface,
    onSurface = AppText,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppMuted,
    error = AppRed
)

@Composable
fun ExpenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
