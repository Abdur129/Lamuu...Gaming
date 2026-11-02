package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationData
import com.example.ui.components.EsportsCard
import com.example.ui.components.SectionHeader
import com.example.ui.localization.LocalizationState
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<NotificationData>,
    onMarkAsRead: (notificationId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            title = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "বিজ্ঞপ্তি সমূহ" else "Notifications",
            subtitle = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "সকল ইন-গেম আপডেট ও Duo অ্যালার্ট" else "All in-game updates & Duo alerts"
        )

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোনো নতুন বিজ্ঞপ্তি নেই।",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(notifications) { notif ->
                    EsportsCard(
                        borderColor = if (!notif.read) NeonCyan.copy(alpha = 0.5f) else DarkSurfaceBorder,
                        onClick = { onMarkAsRead(notif.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "duo_request" -> NeonPurple.copy(alpha = 0.2f)
                                            "duo_match" -> NeonGreen.copy(alpha = 0.2f)
                                            "tournament" -> NeonCyan.copy(alpha = 0.2f)
                                            else -> GoldYellow.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "duo_request" -> Icons.Default.PersonAdd
                                        "duo_match" -> Icons.Default.Group
                                        "tournament" -> Icons.Default.EmojiEvents
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "duo_request" -> NeonPurple
                                        "duo_match" -> NeonGreen
                                        "tournament" -> NeonCyan
                                        else -> GoldYellow
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    color = TextPrimary,
                                    fontWeight = if (!notif.read) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = notif.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            if (!notif.read) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
