package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Duo Connect Glass & Neon Styling Attributes
@Immutable
data class GlassTheme(
    val darkBackground: Color = GlassDarkBackground,
    val cardSurface: Color = GlassDarkSurface,
    val subtleSurface: Color = GlassDarkSurfaceSubtle,
    val elevatedSurface: Color = GlassDarkElevated,
    val defaultBorder: Color = GlassBorderDefault,
    val neonGreenBorder: Color = GlassBorderNeonGreen,
    val neonPurpleBorder: Color = GlassBorderNeonPurple,
    val cardGradient: Brush = GlassCardGradient,
    val surfaceGradient: Brush = GlassSurfaceGradient,
    val neonBorderGradient: Brush = GlassNeonBorderGradient,
    val neonGreenBorderGradient: Brush = GlassNeonGreenBorder,
    val neonPurpleBorderGradient: Brush = GlassNeonPurpleBorder,
    val duoGradient: Brush = NeonDuoGradient
)

val LocalGlassTheme = staticCompositionLocalOf { GlassTheme() }

val MaterialTheme.glass: GlassTheme
    @Composable
    get() = LocalGlassTheme.current

// Duo Connect Dark Esports Color Scheme
private val DuoConnectDarkColorScheme = darkColorScheme(
    primary = NeonGreenPrimary,
    onPrimary = Color(0xFF003819),
    primaryContainer = Color(0xFF005327),
    onPrimaryContainer = NeonGreenLight,

    secondary = NeonPurplePrimary,
    onSecondary = Color(0xFF2E0854),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = NeonPurpleLight,

    tertiary = NeonCyan,
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF004F59),
    onTertiaryContainer = Color(0xFF80F7FF),

    background = DarkBg,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = DarkSurfaceBorder,
    outlineVariant = GlassBorderDefault,

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun DuoConnectTheme(
    glassTheme: GlassTheme = GlassTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalGlassTheme provides glassTheme) {
        MaterialTheme(
            colorScheme = DuoConnectDarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
