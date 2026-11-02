package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.PrivateProfileData
import com.example.data.model.PublicProfileData
import com.example.ui.components.*
import com.example.ui.localization.LanguageCode
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentUserId: String,
    currentUserEmail: String?,
    userRole: String,
    publicProfile: PublicProfileData?,
    privateProfile: PrivateProfileData?,
    onNavigateToEditProfile: () -> Unit,
    onToggleMatchingAvailability: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAvailable by remember(publicProfile) {
        mutableStateOf(publicProfile?.isAvailableForDuo ?: true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = Strings.settings,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Account Profile Card
        item {
            EsportsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GamerAvatar(
                        name = publicProfile?.ign ?: currentUserEmail ?: "User",
                        size = 54,
                        borderColor = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = publicProfile?.ign ?: "Player",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = currentUserEmail ?: "Registered Player",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusBadge(
                                text = userRole.uppercase(),
                                color = if (userRole.contains("admin")) GoldYellow else NeonCyan
                            )
                            if (publicProfile?.isApproved == true) {
                                StatusBadge(text = "VERIFIED", color = NeonGreen)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                EsportsButton(
                    text = "প্রোফাইল সম্পাদন (Edit Profile)",
                    onClick = onNavigateToEditProfile,
                    icon = Icons.Default.Edit,
                    gradientColors = listOf(DarkSurfaceVariant, DarkSurfaceCard),
                    textColor = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Duo Privacy & Availability
        item {
            SectionHeader(
                title = if (LocalizationState.currentLanguage == LanguageCode.BN) "ম্যাচিং ও গোপনীয়তা" else "Matching & Privacy"
            )
            EsportsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (LocalizationState.currentLanguage == LanguageCode.BN) "Duo ম্যাচিং সক্রিয় রাখুন" else "Available for Duo Matching",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (LocalizationState.currentLanguage == LanguageCode.BN) "বন্ধ করলে অন্য প্লেয়াররা আপনাকে সাজেশনে দেখবে না।" else "Disable to pause new duo requests and match feed recommendations.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = {
                            isAvailable = it
                            onToggleMatchingAvailability(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = NeonCyan.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (LocalizationState.currentLanguage == LanguageCode.BN) "ভাষা নির্বাচন (Language)" else "App Language",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (LocalizationState.currentLanguage == LanguageCode.BN) "বর্তমান: বাংলা" else "Current: English",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    LanguageToggleButton()
                }
            }
        }

        // Privacy Architecture Info
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F1E2A))
                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (LocalizationState.currentLanguage == LanguageCode.BN) "নিরাপদ ডেটা আর্কিটেকচার" else "Secure Data Architecture",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আপনার ব্যক্তিগত ফোন, সম্পূর্ণ নাম এবং গোপনীয় Free Fire UID অন্য সাধারণ খেলোয়াড়দের থেকে আলাদা Firestore কালেকশনে সংরক্ষিত থাকে। শুধুমাত্র অ্যাডমিন এবং আপনার অনুমোদিত Duo পার্টনার সীমাবদ্ধ বিবরণ দেখতে পারেন।",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Sign Out Button
        item {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onSignOut,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = Strings.signOut, fontWeight = FontWeight.Bold)
            }
        }
    }
}
