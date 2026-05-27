package com.ukdw.pplaicoach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * === TEMA PPL AI COACH ===
 * Material Design 3 Light Theme dengan palet warna custom.
 * Menggunakan warna biru tua + oranye untuk kesan profesional dan energik.
 */
private val PPLLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark,

    secondary = SecondaryOrange,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryOrangeLight,
    onSecondaryContainer = SecondaryOrangeDark,

    tertiary = TertiaryTeal,
    onTertiary = OnTertiary,

    background = BackgroundLight,
    onBackground = OnBackground,

    surface = SurfaceLight,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariant,

    error = RestDayRed,
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
)

@Composable
fun PPLAICoachTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PPLLightColorScheme,
        typography = PPLTypography,
        content = content
    )
}
