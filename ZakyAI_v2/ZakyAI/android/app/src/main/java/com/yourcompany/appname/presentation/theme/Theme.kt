package com.yourcompany.appname.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ألوان الثيمات المختلفة (داكن / فاتح / نيون / أزرق / أرجواني)
val BackgroundDark = Color(0xFF0D0D0D)
val SurfaceDark = Color(0xFF1A1A1A)
val PrimaryBlue = Color(0xFF3D5AFE)
val PrimaryPurple = Color(0xFF9C27B0)
val NeonGreen = Color(0xFF39FF14)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryPurple,
    background = BackgroundDark,
    surface = SurfaceDark
)

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryPurple
)

@Composable
fun ZakyAITheme(themeName: String = "dark", content: @Composable () -> Unit) {
    val colors = when (themeName) {
        "light" -> LightColors
        "neon" -> DarkColors.copy(primary = NeonGreen)
        "blue" -> DarkColors.copy(primary = PrimaryBlue)
        "purple" -> DarkColors.copy(primary = PrimaryPurple)
        else -> DarkColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
