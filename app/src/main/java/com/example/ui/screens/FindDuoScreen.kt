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
import com.example.data.matching.SmartDuoMatcher
import com.example.data.model.DuoRequestData
import com.example.data.model.PublicProfileData
import com.example.ui.components.*
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun FindDuoScreen(
    currentUserProfile: PublicProfileData?,
    candidates: List<PublicProfileData>,
    sentRequests: List<DuoRequestData>,
    onSendRequest: (targetUserId: String, targetIgn: String, score: Int, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedModeFilter by remember { mutableStateOf("All") }
    var selectedStyleFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    var selectedCandidateForRequest by remember { mutableStateOf<Pair<PublicProfileData, Int>?>(null) }
    var requestMessage by remember { mutableStateOf("আমি আপনার সাথে প্রতিদিন টুর্নামেন্ট ও র্যাঙ্কড খেলতে আগ্রহী!") }

    val userProfile = currentUserProfile ?: PublicProfileData(
        userId = "temp",
        ign = "My_IGN",
        preferredMode = "BR Ranked",
        playingStyle = "Rusher",
        playingSchedule = "Night",
        experience = "Advanced",
        language = "Bengali"
    )

    // Base candidates + fallback community candidates if database is fresh
    val displayCandidates = remember(candidates, selectedModeFilter, selectedStyleFilter, searchQuery) {
        val baseList = if (candidates.isNotEmpty()) candidates else getPresetCandidates()
        baseList.filter { candidate ->
            val matchesMode = selectedModeFilter == "All" || candidate.preferredMode.equals(selectedModeFilter, ignoreCase = true)
            val matchesStyle = selectedStyleFilter == "All" || candidate.playingStyle.equals(selectedStyleFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() || candidate.ign.contains(searchQuery, ignoreCase = true)
            matchesMode && matchesStyle && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("প্লেয়ারের IGN দিয়ে খুঁজুন...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_duo_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedContainerColor = DarkSurfaceCard,
                unfocusedContainerColor = DarkSurfaceCard
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mode filter pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val modes = listOf("All", "BR Ranked", "CS Ranked", "Lone Wolf")
            items(modes) { mode ->
                FilterChip(
                    selected = selectedModeFilter == mode,
                    onClick = { selectedModeFilter = mode },
                    label = { Text(mode) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Style filter pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val styles = listOf("All", "Sniper", "Rusher", "IGL", "Support")
            items(styles) { style ->
                FilterChip(
                    selected = selectedStyleFilter == style,
                    onClick = { selectedStyleFilter = style },
                    label = { Text(style) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonPurple.copy(alpha = 0.2f),
                        selectedLabelColor = NeonPurple
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Match feed
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "স্মার্ট রিকমেন্ডেশন (${displayCandidates.size})" else "Recommended Matches (${displayCandidates.size})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AI Synergy Engine",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(displayCandidates) { candidate ->
                val result = remember(candidate, userProfile) {
                    SmartDuoMatcher.calculate(userProfile, candidate)
                }
                val hasAlreadySent = sentRequests.any { req -> req.toUserId == candidate.userId && req.status == "pending" }

                EsportsCard(
                    borderColor = when {
                        result.score >= 90 -> NeonCyan.copy(alpha = 0.6f)
                        result.score >= 80 -> NeonPurple.copy(alpha = 0.5f)
                        else -> DarkSurfaceBorder
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GamerAvatar(
                                name = candidate.ign,
                                size = 44,
                                borderColor = if (result.score >= 90) NeonGreen else NeonCyan
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = candidate.ign,
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (candidate.isApproved) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Level ${candidate.level} • ${candidate.experience}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        StatusBadge(
                            text = result.synergyTag,
                            color = when {
                                result.score >= 90 -> NeonGreen
                                result.score >= 80 -> NeonCyan
                                else -> NeonPurple
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    CompatibilityMeter(score = result.score)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tags row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SmallInfoChip(icon = Icons.Default.SportsEsports, text = candidate.preferredMode)
                        SmallInfoChip(icon = Icons.Default.Shield, text = candidate.playingStyle)
                        SmallInfoChip(icon = Icons.Default.AccessTime, text = candidate.playingSchedule)
                    }

                    if (!candidate.bio.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${candidate.bio}\"",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (hasAlreadySent) {
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
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = Strings.requestSent, color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        EsportsButton(
                            text = Strings.sendRequest,
                            onClick = {
                                selectedCandidateForRequest = candidate to result.score
                            },
                            icon = Icons.Default.Send,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "send_request_${candidate.ign}"
                        )
                    }
                }
            }
        }
    }

    // Send Request Dialog
    if (selectedCandidateForRequest != null) {
        val (candidate, score) = selectedCandidateForRequest!!
        AlertDialog(
            onDismissRequest = { selectedCandidateForRequest = null },
            title = {
                Text(
                    text = "${candidate.ign}-কে Duo রিকোয়েস্ট পাঠান",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "ম্যাচিং কম্প্যাটিবিলিটি: $score%",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "একটি সুন্দর মেসেজ লিখে আপনার Duo অফারটি নিশ্চিত করুন:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = requestMessage,
                        onValueChange = { requestMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkSurfaceBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSendRequest(candidate.userId, candidate.ign, score, requestMessage)
                        selectedCandidateForRequest = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("পাঠিয়ে দিন", color = Color(0xFF041017), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCandidateForRequest = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SmallInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

fun getPresetCandidates(): List<PublicProfileData> {
    return listOf(
        PublicProfileData(
            userId = "candidate_1",
            ign = "BD_SniperQueen",
            level = 72,
            preferredMode = "BR Ranked",
            experience = "Esports Pro",
            playingStyle = "Sniper",
            playingSchedule = "Night",
            language = "Bengali",
            bio = "AWM & Barrett স্পেশালিস্ট। ভালো রাশারের সাথে Duo চ্যাম্পিয়ন হতে চাই।",
            isAvailableForDuo = true,
            isApproved = true
        ),
        PublicProfileData(
            userId = "candidate_2",
            ign = "Silent_Killer_99",
            level = 68,
            preferredMode = "BR Ranked",
            experience = "Advanced",
            playingStyle = "Rusher",
            playingSchedule = "Night",
            language = "Bengali",
            bio = "MP40 & Shotgun মাস্টার। ফুল এগ্রো গেমপ্লে খেলি।",
            isAvailableForDuo = true,
            isApproved = true
        ),
        PublicProfileData(
            userId = "candidate_3",
            ign = "Apex_IGL_Tanvir",
            level = 76,
            preferredMode = "BR Ranked",
            experience = "Esports Pro",
            playingStyle = "IGL",
            playingSchedule = "Evening",
            language = "Bengali",
            bio = "Tournament strategy & rotation expert. নিয়মিত টুর্নামেন্ট খেলি।",
            isAvailableForDuo = true,
            isApproved = true
        ),
        PublicProfileData(
            userId = "candidate_4",
            ign = "Viper_Medic_Joy",
            level = 62,
            preferredMode = "CS Ranked",
            experience = "Advanced",
            playingStyle = "Support",
            playingSchedule = "Any Time",
            language = "Bengali",
            bio = "Dimitri & Chrono ব্যবহার করি। সঠিক সময়ে কভার এবং রিভাইভ দেই।",
            isAvailableForDuo = true,
            isApproved = true
        )
    )
}
