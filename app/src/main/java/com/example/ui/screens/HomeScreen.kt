package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentData
import com.example.ui.components.*
import com.example.ui.localization.LanguageCode
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    hasCompletedProfile: Boolean,
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToFindDuo: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    tournaments: List<TournamentData>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                NeonGreenPrimary.copy(alpha = 0.7f),
                                NeonPurplePrimary.copy(alpha = 0.5f),
                                Color(0x3300F0FF)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF131D2E),
                                Color(0xFF090D15)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NeonGreenPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreenPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "FREE FIRE ESPORTS BANGLADESH",
                                color = NeonGreenPrimary,
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = Strings.heroHeadline,
                        color = TextPrimary,
                        style = EsportsDisplayTitle,
                        fontSize = 26.sp,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Strings.heroSubtitle,
                        color = TextSecondary,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!hasCompletedProfile) {
                            EsportsButton(
                                text = Strings.createProfile,
                                onClick = onNavigateToProfileSetup,
                                icon = Icons.Default.PersonAdd,
                                gradientColors = listOf(NeonGreenPrimary, NeonGreenDark),
                                textColor = Color(0xFF02140A),
                                modifier = Modifier.weight(1f),
                                testTag = "hero_create_profile"
                            )
                        } else {
                            EsportsButton(
                                text = Strings.findDuo,
                                onClick = onNavigateToFindDuo,
                                icon = Icons.Default.FlashOn,
                                gradientColors = listOf(NeonGreenPrimary, NeonPurplePrimary),
                                textColor = Color(0xFF02140A),
                                modifier = Modifier.weight(1f),
                                testTag = "hero_find_duo"
                            )
                        }
                        EsportsButton(
                            text = Strings.exploreTournaments,
                            onClick = onNavigateToTournaments,
                            icon = Icons.Default.EmojiEvents,
                            gradientColors = listOf(NeonPurplePrimary, NeonPurpleDark),
                            textColor = Color.White,
                            modifier = Modifier.weight(1f),
                            testTag = "hero_explore_tournaments"
                        )
                    }
                }
            }
        }

        // Live Statistics Counter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = if (LocalizationState.currentLanguage == LanguageCode.BN) "অ্যাক্টিভ প্লেয়ার" else "Active Players",
                    value = "1,450+",
                    icon = Icons.Default.SportsEsports,
                    color = NeonGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (LocalizationState.currentLanguage == LanguageCode.BN) "Duo গঠিত" else "Duos Formed",
                    value = "520+",
                    icon = Icons.Default.Group,
                    color = NeonPurplePrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (LocalizationState.currentLanguage == LanguageCode.BN) "প্রাইজ পুল" else "Prize Pool",
                    value = "৳68,000",
                    icon = Icons.Default.AttachMoney,
                    color = GoldYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // How it Works Steps
        item {
            SectionHeader(
                title = if (LocalizationState.currentLanguage == LanguageCode.BN) "কিভাবে কাজ করে?" else "How It Works",
                subtitle = if (LocalizationState.currentLanguage == LanguageCode.BN) "৩টি সহজ ধাপে আপনার Duo টিম গড়ুন" else "3 Simple Steps to Build Your Duo"
            )
            EsportsCard {
                StepRow(
                    step = "1",
                    title = if (LocalizationState.currentLanguage == LanguageCode.BN) "প্রোফাইল নিবন্ধন" else "Register Gaming Profile",
                    desc = if (LocalizationState.currentLanguage == LanguageCode.BN) "আপনার Free Fire IGN, UID, লেভেল এবং খেলার ধরণ যুক্ত করুন।" else "Add your Free Fire IGN, UID, preferred mode and playstyle.",
                    accentColor = NeonGreenPrimary
                )
                HorizontalDivider(color = DarkSurfaceBorder, modifier = Modifier.padding(vertical = 10.dp))
                StepRow(
                    step = "2",
                    title = if (LocalizationState.currentLanguage == LanguageCode.BN) "স্মার্ট Duo ম্যাচিং" else "Smart Duo Matchmaker",
                    desc = if (LocalizationState.currentLanguage == LanguageCode.BN) "আমাদের অ্যালগরিদম আপনাকে সবচেয়ে মানানসই পার্টনার দেখাবে।" else "Our engine matches you with highest synergy players (Rusher + Sniper).",
                    accentColor = NeonPurplePrimary
                )
                HorizontalDivider(color = DarkSurfaceBorder, modifier = Modifier.padding(vertical = 10.dp))
                StepRow(
                    step = "3",
                    title = if (LocalizationState.currentLanguage == LanguageCode.BN) "টুর্নামেন্টে লড়াই করুন" else "Compete & Win Tournaments",
                    desc = if (LocalizationState.currentLanguage == LanguageCode.BN) "দৈনিক ও সাপ্তাহিক টুর্নামেন্টে প্রবেশ করে ট্রফি ও প্রাইজমানি জিতুন।" else "Join daily & weekly esports tournaments for trophies and cash prizes.",
                    accentColor = GoldYellow
                )
            }
        }

        // Featured Upcoming Tournaments
        item {
            SectionHeader(
                title = if (LocalizationState.currentLanguage == LanguageCode.BN) "আসন্ন টুর্নামেন্ট" else "Featured Tournaments",
                actionText = if (LocalizationState.currentLanguage == LanguageCode.BN) "সব দেখুন" else "View All",
                onActionClick = onNavigateToTournaments
            )
        }

        items(tournaments.take(2)) { tournament ->
            EsportsCard(
                borderColor = Color(0x3300E676),
                onClick = onNavigateToTournaments
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = tournament.type.uppercase(),
                        color = when (tournament.type) {
                            "daily" -> NeonGreenPrimary
                            "weekly" -> NeonPurplePrimary
                            else -> GoldYellow
                        }
                    )
                    Text(
                        text = tournament.prizePool ?: "৳5,000",
                        color = NeonGreenPrimary,
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tournament.title,
                    color = TextPrimary,
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📅 ${tournament.scheduleTime ?: "Daily 8:00 PM"}",
                        color = TextSecondary,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "👥 ${tournament.currentParticipants}/${tournament.maxParticipants} Players",
                        color = TextMuted,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Quick Navigation Grid
        item {
            SectionHeader(
                title = if (LocalizationState.currentLanguage == LanguageCode.BN) "দ্রুত অ্যাক্সেস" else "Quick Shortcuts"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = Strings.findDuo,
                    subtitle = if (LocalizationState.currentLanguage == LanguageCode.BN) "পার্টনার খুঁজুন" else "Find Partner",
                    icon = Icons.Default.FlashOn,
                    color = NeonGreenPrimary,
                    onClick = onNavigateToFindDuo,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = Strings.leaderboard,
                    subtitle = if (LocalizationState.currentLanguage == LanguageCode.BN) "শীর্ষ প্লেয়ার" else "Top Scorers",
                    icon = Icons.Default.Leaderboard,
                    color = NeonPurplePrimary,
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Privacy Guarantee Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, GlassBorderNeonGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .background(GlassDarkSurface)
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Shield",
                        tint = NeonGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (LocalizationState.currentLanguage == LanguageCode.BN) "সম্পূর্ণ সুরক্ষিত ও গোপনীয়" else "100% Confidential & Secure",
                            color = TextPrimary,
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Strings.privacyNotice,
                            color = TextSecondary,
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlassDarkSurface)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontFamily = InterFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StepRow(
    step: String,
    title: String,
    desc: String,
    accentColor: Color = NeonGreenPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = accentColor,
                fontFamily = RajdhaniFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = TextSecondary,
                fontFamily = InterFontFamily,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(GlassDarkSurface)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontFamily = InterFontFamily,
                fontSize = 11.sp
            )
        }
    }
}
