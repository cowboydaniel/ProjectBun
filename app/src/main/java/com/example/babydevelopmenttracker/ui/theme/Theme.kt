package com.example.babydevelopmenttracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NeutralLightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant
)

private val NeutralDarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant
)

private val BlossomLightColorScheme = lightColorScheme(
    primary = blossom_light_primary,
    onPrimary = blossom_light_onPrimary,
    primaryContainer = blossom_light_primaryContainer,
    onPrimaryContainer = blossom_light_onPrimaryContainer,
    secondary = blossom_light_secondary,
    onSecondary = blossom_light_onSecondary,
    secondaryContainer = blossom_light_secondaryContainer,
    onSecondaryContainer = blossom_light_onSecondaryContainer,
    tertiary = blossom_light_tertiary,
    onTertiary = blossom_light_onTertiary,
    tertiaryContainer = blossom_light_tertiaryContainer,
    onTertiaryContainer = blossom_light_onTertiaryContainer,
    error = blossom_light_error,
    onError = blossom_light_onError,
    background = blossom_light_background,
    onBackground = blossom_light_onBackground,
    surface = blossom_light_surface,
    onSurface = blossom_light_onSurface,
    surfaceVariant = blossom_light_surfaceVariant,
    onSurfaceVariant = blossom_light_onSurfaceVariant
)

private val BlossomDarkColorScheme = darkColorScheme(
    primary = blossom_dark_primary,
    onPrimary = blossom_dark_onPrimary,
    primaryContainer = blossom_dark_primaryContainer,
    onPrimaryContainer = blossom_dark_onPrimaryContainer,
    secondary = blossom_dark_secondary,
    onSecondary = blossom_dark_onSecondary,
    secondaryContainer = blossom_dark_secondaryContainer,
    onSecondaryContainer = blossom_dark_onSecondaryContainer,
    tertiary = blossom_dark_tertiary,
    onTertiary = blossom_dark_onTertiary,
    tertiaryContainer = blossom_dark_tertiaryContainer,
    onTertiaryContainer = blossom_dark_onTertiaryContainer,
    error = blossom_dark_error,
    onError = blossom_dark_onError,
    background = blossom_dark_background,
    onBackground = blossom_dark_onBackground,
    surface = blossom_dark_surface,
    onSurface = blossom_dark_onSurface,
    surfaceVariant = blossom_dark_surfaceVariant,
    onSurfaceVariant = blossom_dark_onSurfaceVariant
)

private val SkylineLightColorScheme = lightColorScheme(
    primary = skyline_light_primary,
    onPrimary = skyline_light_onPrimary,
    primaryContainer = skyline_light_primaryContainer,
    onPrimaryContainer = skyline_light_onPrimaryContainer,
    secondary = skyline_light_secondary,
    onSecondary = skyline_light_onSecondary,
    secondaryContainer = skyline_light_secondaryContainer,
    onSecondaryContainer = skyline_light_onSecondaryContainer,
    tertiary = skyline_light_tertiary,
    onTertiary = skyline_light_onTertiary,
    tertiaryContainer = skyline_light_tertiaryContainer,
    onTertiaryContainer = skyline_light_onTertiaryContainer,
    error = skyline_light_error,
    onError = skyline_light_onError,
    background = skyline_light_background,
    onBackground = skyline_light_onBackground,
    surface = skyline_light_surface,
    onSurface = skyline_light_onSurface,
    surfaceVariant = skyline_light_surfaceVariant,
    onSurfaceVariant = skyline_light_onSurfaceVariant
)

private val SkylineDarkColorScheme = darkColorScheme(
    primary = skyline_dark_primary,
    onPrimary = skyline_dark_onPrimary,
    primaryContainer = skyline_dark_primaryContainer,
    onPrimaryContainer = skyline_dark_onPrimaryContainer,
    secondary = skyline_dark_secondary,
    onSecondary = skyline_dark_onSecondary,
    secondaryContainer = skyline_dark_secondaryContainer,
    onSecondaryContainer = skyline_dark_onSecondaryContainer,
    tertiary = skyline_dark_tertiary,
    onTertiary = skyline_dark_onTertiary,
    tertiaryContainer = skyline_dark_tertiaryContainer,
    onTertiaryContainer = skyline_dark_onTertiaryContainer,
    error = skyline_dark_error,
    onError = skyline_dark_onError,
    background = skyline_dark_background,
    onBackground = skyline_dark_onBackground,
    surface = skyline_dark_surface,
    onSurface = skyline_dark_onSurface,
    surfaceVariant = skyline_dark_surfaceVariant,
    onSurfaceVariant = skyline_dark_onSurfaceVariant
)

@Composable
fun BabyDevelopmentTrackerTheme(
    themePreference: ThemePreference = ThemePreference.Neutral,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val supportsDynamicColor = themePreference == ThemePreference.Neutral &&
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        supportsDynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> staticColorSchemeFor(themePreference, darkTheme)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun staticColorSchemeFor(
    themePreference: ThemePreference,
    darkTheme: Boolean
): ColorScheme {
    return when (themePreference) {
        ThemePreference.Neutral -> if (darkTheme) NeutralDarkColorScheme else NeutralLightColorScheme
        ThemePreference.Blossom -> if (darkTheme) BlossomDarkColorScheme else BlossomLightColorScheme
        ThemePreference.Skyline -> if (darkTheme) SkylineDarkColorScheme else SkylineLightColorScheme
    }
}

fun themePreviewColors(themePreference: ThemePreference) = when (themePreference) {
    ThemePreference.Neutral -> listOf(
        md_theme_light_primary,
        md_theme_light_secondary,
        md_theme_light_tertiary
    )

    ThemePreference.Blossom -> listOf(
        blossom_light_primary,
        blossom_light_secondary,
        blossom_light_tertiary
    )

    ThemePreference.Skyline -> listOf(
        skyline_light_primary,
        skyline_light_secondary,
        skyline_light_tertiary
    )
}
