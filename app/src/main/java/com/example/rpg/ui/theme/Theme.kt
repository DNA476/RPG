package com.example.rpg.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Ember,
    secondary = VitalGreen,
    tertiary = BloodRed,
    background = SlateNight,
    surface = Iron,
    onPrimary = Color(0xFF111111),
    onSecondary = Color(0xFF111111),
    onTertiary = Color.White,
    onBackground = Bone,
    onSurface = Bone,
)

private val LightColorScheme = lightColorScheme(
    primary = EmberDark,
    secondary = SlateNight,
    tertiary = BloodRed,
    background = Bone,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
)

/**
 * App theme tuned for a gritty fitness-RPG visual style.
 */
@Composable
fun RPGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
