package com.example.Roomdb.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────
// Kazi.Konnect design tokens (from Figma/HTML export)
// ─────────────────────────────────────────────────────────────

// Light scheme
val KKPrimary = Color(0xFF001E40)
val KKOnPrimary = Color(0xFFFFFFFF)
val KKPrimaryContainer = Color(0xFF003366)
val KKOnPrimaryContainer = Color(0xFF799DD6)

val KKSecondary = Color(0xFF006D36)
val KKOnSecondary = Color(0xFFFFFFFF)
val KKSecondaryContainer = Color(0xFF83FBA5)
val KKOnSecondaryContainer = Color(0xFF00743A)

val KKTertiary = Color(0xFF291C00)
val KKOnTertiary = Color(0xFFFFFFFF)
val KKTertiaryContainer = Color(0xFF433000)
val KKOnTertiaryContainer = Color(0xFFC59300)
val KKTertiaryFixedDim = Color(0xFFFBBC00) // the "gold CTA" accent used for highlight buttons

val KKError = Color(0xFFBA1A1A)
val KKOnError = Color(0xFFFFFFFF)
val KKErrorContainer = Color(0xFFFFDAD6)
val KKOnErrorContainer = Color(0xFF93000A)

val KKBackground = Color(0xFFF9F9F9)
val KKOnBackground = Color(0xFF1A1C1C)
val KKSurface = Color(0xFFF9F9F9)
val KKOnSurface = Color(0xFF1A1C1C)
val KKSurfaceVariant = Color(0xFFE2E2E2)
val KKOnSurfaceVariant = Color(0xFF43474F)

val KKOutline = Color(0xFF737780)
val KKOutlineVariant = Color(0xFFC3C6D1)

val KKSurfaceDim = Color(0xFFDADADA)
val KKSurfaceBright = Color(0xFFF9F9F9)
val KKSurfaceContainerLowest = Color(0xFFFFFFFF)
val KKSurfaceContainerLow = Color(0xFFF3F3F3)
val KKSurfaceContainer = Color(0xFFEEEEEE)
val KKSurfaceContainerHigh = Color(0xFFE8E8E8)
val KKSurfaceContainerHighest = Color(0xFFE2E2E2)

val KKInverseSurface = Color(0xFF2F3131)
val KKInverseOnSurface = Color(0xFFF0F1F1)
val KKInversePrimary = Color(0xFFA7C8FF)
val KKSurfaceTint = Color(0xFF3A5F94)

// Fixed / dim roles (used to build the dark scheme + gradients)
val KKPrimaryFixed = Color(0xFFD5E3FF)
val KKPrimaryFixedDim = Color(0xFFA7C8FF)
val KKOnPrimaryFixed = Color(0xFF001B3C)
val KKOnPrimaryFixedVariant = Color(0xFF1F477B)

val KKSecondaryFixed = Color(0xFF83FBA5)
val KKSecondaryFixedDim = Color(0xFF66DD8B)
val KKOnSecondaryFixed = Color(0xFF00210C)
val KKOnSecondaryFixedVariant = Color(0xFF005227)

val KKTertiaryFixed = Color(0xFFFFDFA0)
val KKOnTertiaryFixed = Color(0xFF261A00)
val KKOnTertiaryFixedVariant = Color(0xFF5C4300)

// ─────────────────────────────────────────────────────────────
// Legacy aliases — kept so existing screens referencing these
// names keep compiling. They now point at the new brand tokens.
// Prefer MaterialTheme.colorScheme.* in new code.
// ─────────────────────────────────────────────────────────────
val KaziGreen   = KKSecondary            // was 0xFF1B6B3A, now maps to brand green
val KaziGold    = KKTertiaryFixedDim      // was 0xFFF5A623, now maps to brand gold
val SurfaceDark = KKOnBackground         // dark text/ink color, NOT a screen background anymore
val CardSurface = KKSurfaceContainerLowest
val ErrorRed    = KKError
val BorderGray  = KKOutlineVariant
val TextGray    = KKOnSurfaceVariant

// Legacy chat colors (unchanged, unrelated to this restyle)
val KKBlue = Color(0xFF1A73E8)
val KKBlueLight = Color(0xFFE8F0FE)
val KKGreen = Color(0xFF34A853)
val KKGreenLight = Color(0xFFE6F4EA)
val KKBorder = Color(0xFFE0E0E0)
val KKTextPrimary = Color(0xFF202124)
val KKTextMuted = Color(0xFF5F6368)
val SentBubble = Color(0xFF1A73E8)
val ReceivedBubble = Color(0xFFF1F3F4)

