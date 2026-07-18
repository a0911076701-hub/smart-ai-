package com.yourcompany.appname.presentation.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ZakyAITheme(themeName: String = "dark", content: @Composable () -> Unit) {
    val colors = when (themeName) {
        "light" -> lightColorScheme(
            primary = PrimaryBlue,
            secondary = PrimaryPurple,
            background = Color(0xFFF5F5F5),
            surface = Color(0xFFFFFFFF)
        )
        "neon" -> darkColorScheme(
            primary = NeonGreen,
            secondary = PrimaryPurple,
            background = BackgroundDark,
            surface = SurfaceDark
        )
        "blue" -> darkColorScheme(
            primary = PrimaryBlue,
            secondary = PrimaryPurple,
            background = BackgroundDark,
            surface = SurfaceDark
        )
        "purple" -> darkColorScheme(
            primary = PrimaryPurple,
            secondary = PrimaryBlue,
            background = BackgroundDark,
            surface = SurfaceDark
        )
        else -> darkColorScheme(
            primary = PrimaryBlue,
            secondary = PrimaryPurple,
            background = BackgroundDark,
            surface = SurfaceDark
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}
