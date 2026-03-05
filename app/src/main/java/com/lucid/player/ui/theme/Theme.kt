package com.lucid.player.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Lucid Player AMOLED Dark Color Palette
val Void = Color(0xFF000000)           // Pure AMOLED black
val DeepVoid = Color(0xFF050508)       // Near-black surface
val Surface1 = Color(0xFF0D0D14)       // Card surface
val Surface2 = Color(0xFF13131E)       // Elevated surface
val Surface3 = Color(0xFF1A1A28)       // Modal surface

val NeonPurple = Color(0xFF9D4EDD)     // Primary accent
val ElectricViolet = Color(0xFF7B2FBE) // Secondary accent
val CelestialBlue = Color(0xFF3D5AF1)  // Info / tertiary
val Aurora = Color(0xFF00D4FF)         // Highlight
val NeonPink = Color(0xFFFF2D78)       // Danger / love
val EmeraldGlow = Color(0xFF00F5A0)    // Success

val TextPrimary = Color(0xFFF0F0FF)
val TextSecondary = Color(0xFF9090B0)
val TextTertiary = Color(0xFF505070)

val GlassWhite = Color(0x14FFFFFF)    // Glass overlay
val GlassBorder = Color(0x20FFFFFF)   // Glass border

private val LucidDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = ElectricViolet,
    onPrimaryContainer = TextPrimary,
    secondary = CelestialBlue,
    onSecondary = Color.White,
    secondaryContainer = Surface2,
    onSecondaryContainer = TextPrimary,
    tertiary = Aurora,
    onTertiary = Void,
    background = Void,
    onBackground = TextPrimary,
    surface = Surface1,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = TextTertiary,
    error = NeonPink,
    onError = Color.White,
    inverseSurface = TextPrimary,
    inverseOnSurface = Void,
    scrim = Color(0xBF000000)
)

@Composable
fun LucidPlayerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LucidDarkColorScheme,
        typography = LucidTypography,
        content = content
    )
}
