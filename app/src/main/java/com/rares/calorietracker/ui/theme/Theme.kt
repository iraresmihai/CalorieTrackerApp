package com.rares.calorietracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary                  = AccentPurple,
    onPrimary                = OnAccentPurple,
    primaryContainer         = AccentPurpleContainer,
    onPrimaryContainer       = OnAccentPurpleContainer,
    secondary                = PurpleGrey80,
    onSecondary              = DarkBackground,
    secondaryContainer       = PurpleGrey40,
    onSecondaryContainer     = DarkOnBackground,
    background               = DarkBackground,
    onBackground             = DarkOnBackground,
    surface                  = DarkSurface,
    onSurface                = DarkOnSurface,
    surfaceContainer         = DarkSurfaceContainer,
    surfaceContainerHigh     = DarkSurfaceContainerHigh,
    surfaceContainerHighest  = DarkSurfaceContainerHighest,
    onSurfaceVariant         = DarkOnSurfaceVariant,
    outline                  = DarkOutline,
    outlineVariant           = DarkOutlineVariant,
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
