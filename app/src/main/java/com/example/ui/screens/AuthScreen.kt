package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.ui.auth.AuthManager
import com.example.ui.components.EsportsButton
import com.example.ui.components.LanguageToggleButton
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Language Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LanguageToggleButton()
            }

            // Center Branding & Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            2.dp,
                            Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                            RoundedCornerShape(24.dp)
                        )
                        .background(Color(0xFF141C2A)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_duo_logo),
                        contentDescription = "Duo Connect Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "DUO CONNECT",
                    color = NeonCyan,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = Strings.tagline,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // Feature Highlights
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceCard)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureRow(
                        icon = Icons.Default.FlashOn,
                        title = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "স্মার্ট Duo ম্যাচিং ইঞ্জিন" else "Smart Duo Matchmaker",
                        desc = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "মোড, খেলার ধরণ ও সময় মিলিয়ে সেরা পার্টনার খুঁজুন।" else "Match by playstyle, BR/CS mode and schedule."
                    )
                    FeatureRow(
                        icon = Icons.Default.EmojiEvents,
                        title = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "প্রতিদিনের Esports টুর্নামেন্ট" else "Daily Esports Tournaments",
                        desc = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "দৈনিক, সাপ্তাহিক ও মাসিক টুর্নামেন্টে ক্যাশ প্রাইজ জিতুন।" else "Daily, weekly & monthly events with cash prizes (৳)."
                    )
                    FeatureRow(
                        icon = Icons.Default.Security,
                        title = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "সম্পূর্ণ সুরক্ষিত প্লেয়ার গোপনীয়তা" else "Zero-Trust Privacy",
                        desc = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "আপনার UID ও ব্যক্তিগত তথ্য অন্যদের কাছে উন্মুক্ত নয়।" else "Your private UID and data stay confidential."
                    )
                }
            }

            // Bottom Actions (Interactive Google Sign In)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                EsportsButton(
                    text = Strings.signInGoogle,
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        AuthManager.onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isLoading = false
                                onAuthSuccess()
                            },
                            onAuthError = { err ->
                                isLoading = false
                                errorMessage = err
                            },
                            scope = scope,
                            onAuthCancelled = {
                                isLoading = false
                            }
                        )
                    },
                    isLoading = isLoading,
                    gradientColors = listOf(Color.White, Color(0xFFE2E8F0)),
                    textColor = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "google_sign_in_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "সাইন ইন করে আপনি Duo Connect এর কমিউনিটি নীতিমালা ও টুর্নামেন্ট নিয়মের সাথে সম্মত হচ্ছেন।",
                    color = TextMuted,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
