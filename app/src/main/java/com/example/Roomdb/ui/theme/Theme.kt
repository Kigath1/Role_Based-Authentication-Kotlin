package com.example.Roomdb.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val KaziLightColorScheme = lightColorScheme(
    primary = KKPrimary,
    onPrimary = KKOnPrimary,
    primaryContainer = KKPrimaryContainer,
    onPrimaryContainer = KKOnPrimaryContainer,
    inversePrimary = KKInversePrimary,

    secondary = KKSecondary,
    onSecondary = KKOnSecondary,
    secondaryContainer = KKSecondaryContainer,
    onSecondaryContainer = KKOnSecondaryContainer,

    tertiary = KKTertiary,
    onTertiary = KKOnTertiary,
    tertiaryContainer = KKTertiaryContainer,
    onTertiaryContainer = KKOnTertiaryContainer,

    error = KKError,
    onError = KKOnError,
    errorContainer = KKErrorContainer,
    onErrorContainer = KKOnErrorContainer,

    background = KKBackground,
    onBackground = KKOnBackground,
    surface = KKSurface,
    onSurface = KKOnSurface,
    surfaceVariant = KKSurfaceVariant,
    onSurfaceVariant = KKOnSurfaceVariant,

    outline = KKOutline,
    outlineVariant = KKOutlineVariant,

    surfaceTint = KKSurfaceTint,
    inverseSurface = KKInverseSurface,
    inverseOnSurface = KKInverseOnSurface,

    surfaceDim = KKSurfaceDim,
    surfaceBright = KKSurfaceBright,
    surfaceContainerLowest = KKSurfaceContainerLowest,
    surfaceContainerLow = KKSurfaceContainerLow,
    surfaceContainer = KKSurfaceContainer,
    surfaceContainerHigh = KKSurfaceContainerHigh,
    surfaceContainerHighest = KKSurfaceContainerHighest,
)

// Derived dark scheme using the "fixed"/inverse roles from the design tokens.
private val KaziDarkColorScheme = darkColorScheme(
    primary = KKPrimaryFixedDim,
    onPrimary = KKOnPrimaryFixed,
    primaryContainer = KKOnPrimaryFixedVariant,
    onPrimaryContainer = KKPrimaryFixed,
    inversePrimary = KKPrimary,

    secondary = KKSecondaryFixedDim,
    onSecondary = KKOnSecondaryFixed,
    secondaryContainer = KKOnSecondaryFixedVariant,
    onSecondaryContainer = KKSecondaryFixed,

    tertiary = KKTertiaryFixedDim,
    onTertiary = KKOnTertiaryFixed,
    tertiaryContainer = KKOnTertiaryFixedVariant,
    onTertiaryContainer = KKTertiaryFixed,

    background = KKOnBackground,
    onBackground = KKBackground,
    surface = KKOnBackground,
    onSurface = KKBackground,
    surfaceVariant = KKOnSurfaceVariant,
    onSurfaceVariant = KKOutlineVariant,

    outline = KKOutline,
    outlineVariant = KKOnSurfaceVariant,

    surfaceTint = KKPrimaryFixedDim,
    inverseSurface = KKBackground,
    inverseOnSurface = KKOnBackground,
)

/**
 * Kazi.Konnect brand theme. Dynamic color is intentionally OFF —
 * this app has a fixed brand palette, not a per-device Material You one.
 */

val KaziShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp),   // cards, bottom sheets — matches rounded-[20px] in mockups
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun KaziKonnectTheme(
    darkTheme: Boolean = false, // set to isSystemInDarkTheme() once dark screens are verified
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KaziDarkColorScheme else KaziLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = KaziShapes,
        content = content
    )
}

/** @deprecated Use [KaziKonnectTheme]. Kept so existing call sites still compile. */
@Deprecated("Use KaziKonnectTheme", ReplaceWith("KaziKonnectTheme(darkTheme, content = content)"))
@Composable
fun TestsTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    KaziKonnectTheme(darkTheme = darkTheme, content = content)
}