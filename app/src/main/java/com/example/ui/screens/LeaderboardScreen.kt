package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchResultData
import com.example.ui.components.EsportsCard
import com.example.ui.components.GamerAvatar
import com.example.ui.localization.LocalizationState
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    results: List<MatchResultData>,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("Weekly") }

    val sortedResults = remember(results) {
        results.sortedBy { it.finalRank }
    }

    val topThree = remember(sortedResults) {
        sortedResults.take(3)
    }
    val rank1 = topThree.getOrNull(0)
    val rank2 = topThree.getOrNull(1)
    val rank3 = topThree.getOrNull(2)

    val remainingRanks = remember(sortedResults) {
        if (sortedResults.size > 3) sortedResults.drop(3) else emptyList()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timeframe selector
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val frames = listOf("Daily", "Weekly", "Monthly", "All-Time")
                items(frames) { frame ->
                    FilterChip(
                        selected = selectedTimeframe == frame,
                        onClick = { selectedTimeframe = frame },
                        label = { Text(frame) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldYellow.copy(alpha = 0.2f),
                            selectedLabelColor = GoldYellow
                        )
                    )
                }
            }
        }

        // Top 3 Podium
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B2333), Color(0xFF111722))
                        )
                    )
                    .border(1.dp, GoldYellow.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Rank 2 (Silver - Left)
                    PodiumColumn(
                        rank = 2,
                        team = rank2?.teamName ?: "Team 2",
                        points = rank2?.totalPoints ?: 23,
                        color = SilverBadge,
                        pedestalHeight = 90
                    )

                    // Rank 1 (Gold - Center)
                    PodiumColumn(
                        rank = 1,
                        team = rank1?.teamName ?: "Apex Predators",
                        points = rank1?.totalPoints ?: 31,
                        color = GoldYellow,
                        pedestalHeight = 120,
                        isChampion = true
                    )

                    // Rank 3 (Bronze - Right)
                    PodiumColumn(
                        rank = 3,
                        team = rank3?.teamName ?: "Team 3",
                        points = rank3?.totalPoints ?: 19,
                        color = BronzeBadge,
                        pedestalHeight = 70
                    )
                }
            }
        }

        // Section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "স্ট্যান্ডিংস ও র্যাংকিং" else "Standings & Rankings",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "PTS / KILLS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Rankings list (all entries)
        items(sortedResults) { result ->
            val rankColor = when (result.finalRank) {
                1 -> GoldYellow
                2 -> SilverBadge
                3 -> BronzeBadge
                else -> DarkSurfaceBorder
            }

            EsportsCard(borderColor = rankColor.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when (result.finalRank) {
                                        1 -> GoldYellow.copy(alpha = 0.2f)
                                        2 -> SilverBadge.copy(alpha = 0.2f)
                                        3 -> BronzeBadge.copy(alpha = 0.2f)
                                        else -> DarkSurfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${result.finalRank}",
                                color = when (result.finalRank) {
                                    1 -> GoldYellow
                                    2 -> SilverBadge
                                    3 -> BronzeBadge
                                    else -> TextSecondary
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = result.teamName,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (!result.playerUids.isNullOrBlank()) {
                                Text(
                                    text = result.playerUids,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${result.totalPoints} PTS",
                            color = NeonGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${result.kills} Kills",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumColumn(
    rank: Int,
    team: String,
    points: Int,
    color: Color,
    pedestalHeight: Int,
    isChampion: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (isChampion) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Champion",
                tint = GoldYellow,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        GamerAvatar(name = team, size = if (isChampion) 48 else 40, borderColor = color)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = team.take(12),
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$points PTS",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(85.dp)
                .height(pedestalHeight.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.4f), color.copy(alpha = 0.1f))
                    )
                )
                .border(
                    1.dp,
                    color.copy(alpha = 0.6f),
                    RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
