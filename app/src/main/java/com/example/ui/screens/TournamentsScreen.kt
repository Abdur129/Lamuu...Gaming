package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentData
import com.example.data.model.TournamentRegistrationData
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun TournamentsScreen(
    currentUserId: String,
    currentUserIgn: String,
    activeDuoPartnerIgn: String?,
    activeDuoPartnerId: String?,
    tournaments: List<TournamentData>,
    myRegistrations: List<TournamentRegistrationData>,
    onRegisterTournament: (tournamentId: String, teamName: String, playerIgn: String, partnerIgn: String?, partnerUserId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Explore, 1: My Tournaments
    var selectedFilter by remember { mutableStateOf("All") }

    var selectedTournamentForRegistration by remember { mutableStateOf<TournamentData?>(null) }
    var teamNameInput by remember { mutableStateOf("Team $currentUserIgn") }
    var partnerIgnInput by remember { mutableStateOf(activeDuoPartnerIgn ?: "") }

    val filteredTournaments = remember(tournaments, selectedFilter) {
        if (selectedFilter == "All") tournaments
        else tournaments.filter { it.type.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceCard,
            contentColor = NeonCyan,
            divider = {},
            indicator = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "সব টুর্নামেন্ট" else "All Tournaments",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) NeonCyan else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "আমার টুর্নামেন্ট" else "My Tournaments",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) NeonCyan else TextSecondary
                        )
                        if (myRegistrations.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = NeonCyan,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = myRegistrations.size.toString(),
                                    color = Color(0xFF041017),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf("All", "Daily", "Weekly", "Monthly")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredTournaments) { tournament ->
                    val isAlreadyRegistered = myRegistrations.any { it.tournamentId == tournament.id }

                    EsportsCard(borderColor = NeonCyan.copy(alpha = 0.35f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(
                                text = "${tournament.type.uppercase()} • ${tournament.format.uppercase()}",
                                color = when (tournament.type) {
                                    "daily" -> NeonCyan
                                    "weekly" -> NeonPurple
                                    else -> GoldYellow
                                }
                            )
                            Text(
                                text = tournament.prizePool ?: "৳5,000",
                                color = NeonGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tournament.title,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (!tournament.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = tournament.description, color = TextSecondary, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = tournament.scheduleTime ?: "Today, 8 PM BST", color = TextSecondary, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "এন্ট্রি: ${tournament.entryFee ?: "Free"}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isAlreadyRegistered) {
                            Surface(
                                color = DarkSurfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "নিবন্ধিত (Registered)", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            EsportsButton(
                                text = Strings.registerNow,
                                onClick = {
                                    selectedTournamentForRegistration = tournament
                                },
                                icon = Icons.Default.HowToReg,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "register_tournament_${tournament.id}"
                            )
                        }
                    }
                }
            }
        } else {
            // My Tournaments Tab
            if (myRegistrations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "আপনি এখনো কোনো টুর্নামেন্টে নিবন্ধন করেননি।",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(myRegistrations) { reg ->
                        val tourney = tournaments.firstOrNull { it.id == reg.tournamentId }

                        EsportsCard(borderColor = if (reg.status == "approved") NeonGreen else GoldYellow) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = reg.teamName,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusBadge(
                                    text = reg.status.uppercase(),
                                    color = if (reg.status == "approved") NeonGreen else GoldYellow
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "প্লেয়ার: ${reg.playerIgn} ${if (!reg.partnerIgn.isNullOrBlank()) "& ${reg.partnerIgn}" else ""}",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )

                            if (tourney != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "টুর্নামেন্ট: ${tourney.title}",
                                    color = NeonCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "সময়: ${tourney.scheduleTime ?: "Upcoming"}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            // ROOM CREDENTIALS DISPLAY
                            if (tourney != null && tourney.roomDetailsVisible && reg.status == "approved") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF072922))
                                        .border(1.dp, NeonGreen, RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "অফিসিয়াল কাস্টম রুম ক্রেডেনশিয়াল", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "রুম আইডি (Room ID): ${tourney.roomId ?: "N/A"}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "পাসওয়ার্ড (Password): ${tourney.roomPassword ?: "N/A"}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceVariant)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "কাস্টম রুম আইডি ও পাসওয়ার্ড ম্যাচ শুরুর ১৫ মিনিট আগে শুধুমাত্র অনুমোদিত টিমদের দেওয়া হবে।",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Tournament Registration Modal
    if (selectedTournamentForRegistration != null) {
        val tourney = selectedTournamentForRegistration!!
        AlertDialog(
            onDismissRequest = { selectedTournamentForRegistration = null },
            title = {
                Text(
                    text = "টুর্নামেন্ট নিবন্ধন: ${tourney.title}",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ফরম্যাট: ${tourney.format.uppercase()} • এন্ট্রি: ${tourney.entryFee ?: "Free"}",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = teamNameInput,
                        onValueChange = { teamNameInput = it },
                        label = { Text("টিমের নাম (Team Name)*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceBorder
                        )
                    )
                    OutlinedTextField(
                        value = currentUserIgn,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("প্লেয়ার ১ IGN") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = DarkSurfaceBorder,
                            disabledTextColor = TextPrimary
                        )
                    )
                    if (tourney.format == "duo" || tourney.format == "squad") {
                        OutlinedTextField(
                            value = partnerIgnInput,
                            onValueChange = { partnerIgnInput = it },
                            label = { Text("Duo পার্টনারের IGN*") },
                            placeholder = { Text("আপনার Duo পার্টনারের নাম") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRegisterTournament(
                            tourney.id,
                            teamNameInput,
                            currentUserIgn,
                            partnerIgnInput.ifBlank { null },
                            activeDuoPartnerId
                        )
                        selectedTournamentForRegistration = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("নিশ্চিত করুন (Confirm)", color = Color(0xFF00381B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTournamentForRegistration = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
