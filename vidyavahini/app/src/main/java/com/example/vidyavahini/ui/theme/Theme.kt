package com.example.vidyavahini.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors
val Amber400    = Color(0xFFFAC775)
val Amber600    = Color(0xFFBA7517)
val Amber900    = Color(0xFF633806)
val Teal500     = Color(0xFF1D9E75)
val Teal700     = Color(0xFF085041)
val TealLight   = Color(0xFFE1F5EE)
val Coral500    = Color(0xFFD85A30)
val CoralLight  = Color(0xFFFAECE7)
val GreenLight  = Color(0xFFEAF3DE)
val Green600    = Color(0xFF639922)

private val LightColorScheme = lightColorScheme(
    primary             = Amber600,
    onPrimary           = Color.White,
    primaryContainer    = Amber400,
    onPrimaryContainer  = Amber900,
    secondary           = Teal500,
    onSecondary         = Color.White,
    secondaryContainer  = TealLight,
    onSecondaryContainer = Teal700,
    error               = Coral500,
    errorContainer      = CoralLight,
    background          = Color(0xFFFFFBF5),
    surface             = Color.White,
    onSurface           = Color(0xFF1C1B1F),
    surfaceVariant      = Color(0xFFF5F0E8),
    outline             = Color(0xFFD0C8BC)
)

private val DarkColorScheme = darkColorScheme(
    primary             = Amber400,
    onPrimary           = Amber900,
    primaryContainer    = Amber600,
    onPrimaryContainer  = Amber400,
    secondary           = Teal500,
    onSecondary         = Color.White,
    secondaryContainer  = Teal700,
    onSecondaryContainer = TealLight,
    error               = Coral500,
    background          = Color(0xFF1A1610),
    surface             = Color(0xFF251F18),
    onSurface           = Color(0xFFEDE0D4)
)

@Composable
fun VidyaVahiniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}