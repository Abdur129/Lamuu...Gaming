package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Premium Frosted Glass Card for DUO CONNECT Esports.
 * Features a semi-transparent dark surface, inner gradient, and subtle glowing neon border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderBrush: Brush = GlassNeonBorderGradient,
    borderWidth: Dp = 1.dp,
    backgroundBrush: Brush = GlassCardGradient,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = NeonGreenPrimary.copy(alpha = 0.2f)),
            onClick = onClick
        )
    } else Modifier

    val tagModifier = if (testTag != null) Modifier.testTag(testTag) else Modifier

    Column(
        modifier = modifier
            .then(tagModifier)
            .clip(shape)
            .border(borderWidth, borderBrush, shape)
            .then(
                if (backgroundColor != null) Modifier.background(backgroundColor)
                else Modifier.background(backgroundBrush)
            )
            .then(clickModifier)
            .padding(16.dp),
        content = content
    )
}

/**
 * A general-purpose frosted glass surface.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    borderBrush: Brush = Brush.linearGradient(listOf(GlassBorderNeonPurple, GlassBorderNeonGreen)),
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = GlassDarkSurface,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .border(borderWidth, borderBrush, shape)
            .background(backgroundColor),
        content = content
    )
}

/**
 * High-octane CTA Neon Glow Button using the signature Green & Purple gradient.
 */
@Composable
fun NeonGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    gradientColors: List<Color> = listOf(NeonGreenPrimary, NeonPurplePrimary),
    textColor: Color = Color(0xFF030D08),
    testTag: String = "neon_glow_button"
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
            .height(50.dp)
            .clip(shape)
            .border(
                1.dp,
                if (enabled) Brush.horizontalGradient(listOf(NeonGreenLight.copy(alpha = 0.8f), NeonPurpleLight.copy(alpha = 0.8f)))
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

/**
 * Frosted outline button with neon accent border.
 */
@Composable
fun GlassOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonGreenPrimary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "glass_outline_button"
) {
    val shape = RoundedCornerShape(12.dp)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = accentColor.copy(alpha = 0.1f),
            contentColor = accentColor
        ),
        modifier = modifier
            .testTag(testTag)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = accentColor,
                style = EsportsButtonTextStyle
            )
        }
    }
}

/**
 * Translucent Glass Badge / Pill tag.
 */
@Composable
fun GlassBadge(
    text: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Surface(
        color = accentColor.copy(alpha = 0.12f),
        shape = shape,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = accentColor,
            style = EsportsBadgeStyle,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Glassy Outlined Text Field with neon focus glow and frosted fill.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    testTag: String? = null
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isError) ErrorRed else NeonGreenPrimary.copy(alpha = 0.5f),
        label = "field_border"
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    fontFamily = InterFontFamily,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = NeonGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            readOnly = readOnly,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassDarkSurface,
                unfocusedContainerColor = GlassDarkSurfaceSubtle,
                focusedBorderColor = NeonGreenPrimary,
                unfocusedBorderColor = GlassBorderDefault,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = NeonGreenPrimary
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = InterFontFamily,
                color = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Atmospheric Esports Hero Header with subtle ambient glow spots and Rajdhani typography.
 */
@Composable
fun EsportsHeroHeader(
    title: String,
    tagline: String,
    modifier: Modifier = Modifier,
    badgeText: String? = "OFFICIAL ESPORTS",
    action: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        NeonGreenPrimary.copy(alpha = 0.6f),
                        NeonPurplePrimary.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF131D2E),
                        DarkBg
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            if (badgeText != null) {
                GlassBadge(
                    text = badgeText,
                    accentColor = NeonGreenPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text(
                text = title,
                color = TextPrimary,
                style = EsportsDisplayTitle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tagline,
                color = TextSecondary,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            if (action != null) {
                Spacer(modifier = Modifier.height(14.dp))
                action()
            }
        }
    }
}

/**
 * Subtle Frosted Cyber Divider with fade-out ends.
 */
@Composable
fun GlassDivider(
    modifier: Modifier = Modifier,
    brush: Brush = Brush.horizontalGradient(
        listOf(
            Color.Transparent,
            NeonGreenPrimary.copy(alpha = 0.5f),
            NeonPurplePrimary.copy(alpha = 0.5f),
            Color.Transparent
        )
    ),
    thickness: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(brush)
    )
}
