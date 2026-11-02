package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuoMatchData
import com.example.data.model.DuoRequestData
import com.example.ui.components.*
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun DuoHubScreen(
    currentUserId: String,
    activeMatch: DuoMatchData?,
    incomingRequests: List<DuoRequestData>,
    outgoingRequests: List<DuoRequestData>,
    onAcceptRequest: (DuoRequestData) -> Unit,
    onRejectRequest: (DuoRequestData) -> Unit,
    onUnmatch: (matchId: String, partnerId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Active Duo, 1: Incoming, 2: Sent
    var showUnmatchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
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
                        text = Strings.myDuo,
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
                            text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "আগত রিকোয়েস্ট" else "Incoming",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) NeonCyan else TextSecondary
                        )
                        if (incomingRequests.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = NeonPurple,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = incomingRequests.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "পাঠানো রিকোয়েস্ট" else "Sent",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 2) NeonCyan else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Active Duo View
                if (activeMatch != null) {
                    val partnerIgn = if (activeMatch.player1Id == currentUserId) activeMatch.player2Ign else activeMatch.player1Ign
                    val partnerId = if (activeMatch.player1Id == currentUserId) activeMatch.player2Id else activeMatch.player1Id

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(
                                        2.dp,
                                        Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF16233B), Color(0xFF0F1728))
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        color = NeonGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "OFFICIAL DUO CONNECTION",
                                            color = NeonGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        GamerAvatar(name = "You", size = 56, borderColor = NeonCyan)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Icon(
                                            Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = GoldYellow,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        GamerAvatar(name = partnerIgn, size = 56, borderColor = NeonPurple)
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = partnerIgn,
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "পার্টনার আইডি: $partnerId",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkSurfaceBorder)
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "ম্যাচ স্ট্যাটাস", color = TextSecondary, fontSize = 11.sp)
                                            Text(text = "অ্যাক্টিভ", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "ম্যাচ সোর্স", color = TextSecondary, fontSize = 11.sp)
                                            Text(text = activeMatch.matchSource.uppercase(), color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "টিম Synergy", color = TextSecondary, fontSize = 11.sp)
                                            Text(text = "95%", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(title = "Duo টিম অ্যাকশন")
                            EsportsCard {
                                Text(
                                    text = "আপনি এবং $partnerIgn এখন Duo টুর্নামেন্টে একসাথে রেজিস্টার করতে পারেন। টুর্নামেন্ট ট্যাবে গিয়ে সরাসরি আপনার পার্টনারকে ইনভাইট করুন।",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedButton(
                                    onClick = { showUnmatchDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = Strings.unmatch, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (showUnmatchDialog) {
                        AlertDialog(
                            onDismissRequest = { showUnmatchDialog = false },
                            title = { Text("Duo সংযোগ বিচ্ছিন্ন করবেন?", color = TextPrimary) },
                            text = { Text("আপনি কি নিশ্চিতভাবে $partnerIgn এর সাথে আনম্যাচ করতে চান? এর ফলে আপনারা নতুন পার্টনার খুঁজতে পারবেন।", color = TextSecondary) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onUnmatch(activeMatch.id, partnerId)
                                        showUnmatchDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                                ) {
                                    Text("হ্যাঁ, আনম্যাচ করুন")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showUnmatchDialog = false }) {
                                    Text("না, থাকুন", color = TextSecondary)
                                }
                            },
                            containerColor = DarkSurfaceCard
                        )
                    }
                } else {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.GroupAdd,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = Strings.noActiveDuo,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "আপনার কোনো অ্যাক্টিভ Duo পার্টনার নেই। 'Duo খুঁজুন' ট্যাব থেকে রিকোয়েস্ট পাঠান অথবা আগত রিকোয়েস্ট গ্রহণ করুন।",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }

            1 -> {
                // Incoming Requests View
                if (incomingRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "কোনো নতুন Duo রিকোয়েস্ট নেই।",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(incomingRequests) { req ->
                            EsportsCard(borderColor = NeonPurple.copy(alpha = 0.5f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        GamerAvatar(name = req.fromIgn, size = 40, borderColor = NeonCyan)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = req.fromIgn, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "কম্প্যাটিবিলিটি: ${req.compatibilityScore}%", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    StatusBadge(text = "PENDING", color = GoldYellow)
                                }

                                if (!req.message.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "\"${req.message}\"", color = TextSecondary, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onRejectRequest(req) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = Strings.reject, color = TextSecondary)
                                    }
                                    Button(
                                        onClick = { onAcceptRequest(req) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = Strings.accept, color = Color(0xFF00381B), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Sent Requests View
                if (outgoingRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "আপনি এখনো কোনো রিকোয়েস্ট পাঠাননি।",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(outgoingRequests) { req ->
                            EsportsCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "প্রাপক: ${req.toIgn}", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "কম্প্যাটিবিলিটি: ${req.compatibilityScore}%", color = NeonCyan, fontSize = 12.sp)
                                    }
                                    StatusBadge(
                                        text = req.status.uppercase(),
                                        color = when (req.status) {
                                            "accepted" -> NeonGreen
                                            "rejected" -> ErrorRed
                                            else -> GoldYellow
                                        }
                                    )
                                }
                                if (!req.message.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "\"${req.message}\"", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
