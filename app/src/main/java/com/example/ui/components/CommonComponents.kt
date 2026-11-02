package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LanguageCode
import com.example.ui.localization.LocalizationState
import com.example.ui.theme.*

@Composable
fun EsportsCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0x3300E676),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable { onClick() }
            .border(1.dp, borderColor, shape)
            .background(GlassCardGradient)
            .padding(16.dp)
    } else {
        modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(GlassCardGradient)
            .padding(16.dp)
    }

    Column(modifier = cardModifier) {
        content()
    }
}

@Composable
fun EsportsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    gradientColors: List<Color> = listOf(NeonGreenPrimary, NeonPurplePrimary),
    textColor: Color = Color(0xFF030E08),
    testTag: String = "esports_button"
) {
    val shape = RoundedCornerShape(12.dp)
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = DarkSurfaceVariant
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .testTag(testTag)
            .height(48.dp)
            .clip(shape)
            .border(
                1.dp,
                if (enabled) Brush.horizontalGradient(listOf(NeonGreenLight.copy(alpha = 0.5f), NeonPurpleLight.copy(alpha = 0.5f)))
                else Brush.horizontalGradient(listOf(DarkSurfaceBorder, DarkSurfaceBorder)),
                shape
            )
            .background(
                if (enabled) Brush.horizontalGradient(gradientColors)
                else Brush.horizontalGradient(listOf(DarkSurfaceBorder, DarkSurfaceVariant))
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = textColor,
                    strokeWidth = 2.dp
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = textColor,
                    style = EsportsButtonTextStyle
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            style = EsportsBadgeStyle,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun CompatibilityMeter(
    score: Int,
    modifier: Modifier = Modifier
) {
    val meterColor = when {
        score >= 90 -> NeonGreenPrimary
        score >= 75 -> NeonCyan
        score >= 60 -> NeonPurplePrimary
        else -> NeonOrange
    }
    val animatedProgress by animateFloatAsState(targetValue = score / 100f, label = "compat_progress")

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MATCH SYNERGY",
                color = TextSecondary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "$score%",
                color = meterColor,
                fontFamily = RajdhaniFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = meterColor,
            trackColor = DarkSurfaceBorder
        )
    }
}

@Composable
fun LanguageToggleButton(
    modifier: Modifier = Modifier
) {
    val current = LocalizationState.currentLanguage
    Surface(
        color = GlassDarkSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300E676)),
        modifier = modifier
            .testTag("language_toggle")
            .clickable { LocalizationState.toggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Language",
                tint = NeonGreenPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (current == LanguageCode.BN) "বাংলা" else "EN",
                color = TextPrimary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GamerAvatar(
    name: String,
    size: Int = 48,
    modifier: Modifier = Modifier,
    borderColor: Color = NeonGreenPrimary
) {
    val initial = name.firstOrNull()?.uppercase() ?: "D"
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF16233B), Color(0xFF090E17))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = borderColor,
            fontFamily = RajdhaniFontFamily,
            fontSize = (size / 2.0).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                style = EsportsSectionTitle
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                color = NeonGreenPrimary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}
