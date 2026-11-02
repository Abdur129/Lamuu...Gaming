package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Esports Duo Connect - Dark & Charcoal Backgrounds
val DarkBg = Color(0xFF070A0F)
val DarkCharcoal = Color(0xFF0C111A)
val DarkNavySection = Color(0xFF101726)
val DarkSurface = Color(0xFF0E1522)
val DarkSurfaceVariant = Color(0xFF162032)
val DarkSurfaceCard = Color(0xFF131B2B)
val DarkSurfaceBorder = Color(0xFF223147)

// Neon Green Accents (Competitive Vitality & Victory)
val NeonGreen = Color(0xFF00E676)
val NeonGreenPrimary = Color(0xFF00E676)
val NeonGreenLight = Color(0xFF69F0AE)
val NeonGreenDark = Color(0xFF00B359)
val NeonGreenGlow = Color(0x6600E676)
val NeonGreenSubtle = Color(0x1F00E676)

// Neon Purple Accents (Esports Cyber Prestige)
val NeonPurple = Color(0xFF8B5CF6)
val NeonPurplePrimary = Color(0xFF8B5CF6)
val NeonPurpleLight = Color(0xFFA78BFA)
val NeonPurpleDark = Color(0xFF6D28D9)
val NeonPurpleGlow = Color(0x668B5CF6)
val NeonPurpleSubtle = Color(0x1F8B5CF6)

// Subtle Cyan & Tournament Accents
val NeonCyan = Color(0xFF00F0FF)
val NeonCyanHighlight = Color(0xFF00F0FF)
val NeonCyanGlow = Color(0x6600F0FF)
val NeonOrange = Color(0xFFFF7A00)
val GoldYellow = Color(0xFFFFC700)
val SilverBadge = Color(0xFFCBD5E1)
val BronzeBadge = Color(0xFFD97706)

// Text & Content Hierarchy
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val ErrorRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF22C55E)

// Glassmorphism Frosted Layers
val GlassDarkBackground = Color(0xD90B101B)      // 85% opacity
val GlassDarkSurface = Color(0xB3101828)         // 70% opacity
val GlassDarkSurfaceSubtle = Color(0x80131E33)   // 50% opacity
val GlassDarkElevated = Color(0xE616233B)        // 90% opacity
val GlassBorderDefault = Color(0x33384B68)
val GlassBorderSubtle = Color(0x1AFFFFFF)
val GlassBorderNeonGreen = Color(0x6600E676)
val GlassBorderNeonPurple = Color(0x668B5CF6)
val GlassHighlight = Color(0x26FFFFFF)

// Gradients & Ambient Shimmers
val GlassSurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xCC162236), Color(0xCC0D1422))
)

val GlassCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xE6141D2E), Color(0xCC0E1524))
)

val NeonDuoGradient = Brush.horizontalGradient(
    colors = listOf(NeonGreenPrimary, NeonPurplePrimary)
)

val NeonPurpleGreenGradient = Brush.horizontalGradient(
    colors = listOf(NeonPurplePrimary, NeonGreenPrimary)
)

val NeonCyberGradient = Brush.horizontalGradient(
    colors = listOf(NeonGreenPrimary, NeonCyan, NeonPurplePrimary)
)

val GlassNeonBorderGradient = Brush.linearGradient(
    colors = listOf(
        NeonGreen.copy(alpha = 0.7f),
        NeonPurple.copy(alpha = 0.5f),
        Color(0x3300F0FF)
    )
)

val GlassNeonGreenBorder = Brush.linearGradient(
    colors = listOf(NeonGreen.copy(alpha = 0.8f), NeonGreen.copy(alpha = 0.2f))
)

val GlassNeonPurpleBorder = Brush.linearGradient(
    colors = listOf(NeonPurple.copy(alpha = 0.8f), NeonPurple.copy(alpha = 0.2f))
)
