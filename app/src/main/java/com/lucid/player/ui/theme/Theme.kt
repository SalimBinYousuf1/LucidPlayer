package com.lucid.player.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/* ─── AMOLED Void Palette ──────────────────────────────────────────────────── */
object LucidColors {
    // Backgrounds — pure AMOLED stack
    val Void        = Color(0xFF000000)
    val Abyss       = Color(0xFF060608)
    val Depth       = Color(0xFF0C0C12)
    val Surface     = Color(0xFF111118)
    val SurfaceHigh = Color(0xFF18181F)
    val Card        = Color(0xFF1C1C26)
    val CardHigh    = Color(0xFF22222E)

    // Accent family — electric indigo / cosmic blue
    val Indigo      = Color(0xFF7C5CFC)   // primary
    val IndigoLight = Color(0xFF9B7FFE)
    val IndigoDim   = Color(0xFF4A3490)
    val Cosmic      = Color(0xFF4B6EF6)   // secondary
    val CosmicLight = Color(0xFF7A95FF)
    val Aurora      = Color(0xFF00E5FF)   // cyan highlight
    val AuroraGlow  = Color(0xFF00B8CC)
    val Ember       = Color(0xFFFF4D6D)   // love / danger
    val EmberGlow   = Color(0xFFFF8FA3)
    val Jade        = Color(0xFF00D68F)   // success

    // Text
    val Text100     = Color(0xFFF4F4FF)
    val Text80      = Color(0xFFBBBBD0)
    val Text50      = Color(0xFF7777A0)
    val Text30      = Color(0xFF44445A)

    // Glass
    val Glass10     = Color(0x1AFFFFFF)
    val Glass15     = Color(0x26FFFFFF)
    val GlassBorder = Color(0x22FFFFFF)
    val GlowIndigo  = Color(0x40 + 0x7C and 0xFF shl 16 or (0x5C shl 8) or 0xFC) // runtime computed
}

/* ─── Spacing tokens ───────────────────────────────────────────────────────── */
data class LucidSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

val LocalSpacing = staticCompositionLocalOf { LucidSpacing() }

/* ─── Material3 color scheme ───────────────────────────────────────────────── */
private val LucidScheme = darkColorScheme(
    primary               = LucidColors.Indigo,
    onPrimary             = Color.White,
    primaryContainer      = LucidColors.IndigoDim,
    onPrimaryContainer    = LucidColors.IndigoLight,
    secondary             = LucidColors.Cosmic,
    onSecondary           = Color.White,
    secondaryContainer    = Color(0xFF1A2060),
    onSecondaryContainer  = LucidColors.CosmicLight,
    tertiary              = LucidColors.Aurora,
    onTertiary            = LucidColors.Void,
    background            = LucidColors.Void,
    onBackground          = LucidColors.Text100,
    surface               = LucidColors.Surface,
    onSurface             = LucidColors.Text100,
    surfaceVariant        = LucidColors.Card,
    onSurfaceVariant      = LucidColors.Text80,
    outline               = LucidColors.GlassBorder,
    outlineVariant        = LucidColors.Text30,
    error                 = LucidColors.Ember,
    onError               = Color.White,
    inverseSurface        = LucidColors.Text100,
    inverseOnSurface      = LucidColors.Void,
    scrim                 = Color(0xCC000000),
)

@Composable
fun LucidPlayerTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides LucidSpacing()) {
        MaterialTheme(
            colorScheme = LucidScheme,
            typography  = LucidTypography,
            content     = content
        )
    }
}
