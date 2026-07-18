package com.yourcompany.appname.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// الألوان الأساسية للثيم الداكن (الافتراضي)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF2B5CE6),
    secondary = Color(0xFF8B5CF6),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1A1A1A),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF2B5CE6),
    secondary = Color(0xFF8B5CF6),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
)

// ألوان الثيمات الإضافية (نيون/أزرق/أرجواني) - أمثلة يمكن توسيعها
private val NeonColors = darkColorScheme(
    primary = Color(0xFF39FF14),
    secondary = Color(0xFFFF00FF),
    background = Color(0xFF000000),
    surface = Color(0xFF111111),
)

@Composable
fun ZakyAITheme(themeName: String = "dark", content: @Composable () -> Unit) {
    val colors = when (themeName) {
        "light" -> LightColors
        "neon" -> NeonColors
        "blue" -> DarkColors.copy(primary = Color(0xFF1976D2))
        "purple" -> DarkColors.copy(primary = Color(0xFF7B1FA2))
        else -> DarkColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
