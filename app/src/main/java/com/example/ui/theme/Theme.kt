package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AyanoPrimaryCyan,
    onPrimary = AyanoBackgroundDark,
    primaryContainer = AyanoPrimaryBlue,
    onPrimaryContainer = AyanoTextPrimary,
    secondary = AyanoAccentPurple,
    onSecondary = AyanoTextPrimary,
    background = AyanoBackgroundDark,
    onBackground = AyanoTextPrimary,
    surface = AyanoSurfaceDark,
    onSurface = AyanoTextPrimary,
    surfaceVariant = AyanoSurfaceCard,
    onSurfaceVariant = AyanoTextSecondary,
    outline = AyanoBorder
)

@Composable
fun AyanoTestPassTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = AyanoBackgroundDark.toArgb()
                it.navigationBarColor = AyanoBackgroundDark.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
