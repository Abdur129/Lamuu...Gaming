package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import kotlinx.coroutines.launch
import com.example.ui.components.EsportsButton
import com.example.ui.components.EsportsCard
import com.example.ui.components.StatusBadge
import com.example.ui.localization.LocalizationState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    currentUserId: String,
    currentUserEmail: String?,
    existingPrivate: PrivateProfileData?,
    existingPublic: PublicProfileData?,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    onSaveProfile: suspend (PrivateProfileData, PublicProfileData) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }

    // Step 1: Personal
    var fullName by remember { mutableStateOf(existingPrivate?.fullName ?: "") }
    var ageStr by remember { mutableStateOf(existingPrivate?.age?.toString() ?: "20") }
    var gender by remember { mutableStateOf(existingPrivate?.gender ?: "Male") }
    var phone by remember { mutableStateOf(existingPrivate?.phone ?: "") }
    var bio by remember { mutableStateOf(existingPublic?.bio ?: "অ্যাক্টিভ Free Fire প্লেয়ার। নির্ভরযোগ্য Duo Partner খুঁজছি।") }

    // Step 2: Free Fire Info
    var ign by remember { mutableStateOf(existingPublic?.ign ?: "") }
    var fullUid by remember { mutableStateOf(existingPrivate?.fullUid ?: "") }
    var levelStr by remember { mutableStateOf(existingPublic?.level?.toString() ?: "65") }
    var preferredMode by remember { mutableStateOf(existingPublic?.preferredMode ?: "BR Ranked") }
    var experience by remember { mutableStateOf(existingPublic?.experience ?: "Advanced") }
    var playingStyle by remember { mutableStateOf(existingPublic?.playingStyle ?: "Rusher") }
    var playingSchedule by remember { mutableStateOf(existingPublic?.playingSchedule ?: "Night") }

    // Step 3: Duo Preferences
    var partnerAgeRange by remember { mutableStateOf(existingPublic?.partnerAgeRange ?: "18-25") }
    var partnerStyle by remember { mutableStateOf(existingPublic?.partnerStyle ?: "Sniper") }
    var language by remember { mutableStateOf(existingPublic?.language ?: "Bengali") }
    var idealPartnerDesc by remember { mutableStateOf(existingPublic?.idealPartnerDesc ?: "ভালো রাশ ও কভার দিতে পারে এমন সিরিয়াস প্লেয়ার।") }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (LocalizationState.currentLanguage == com.example.ui.localization.LanguageCode.BN) "প্লেয়ার প্রোফাইল সেটআপ" else "Player Profile Setup",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step Indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepBadge(number = 1, active = step >= 1, current = step == 1, label = "Personal")
                    Divider(color = if (step > 1) NeonCyan else DarkSurfaceBorder, modifier = Modifier.weight(1f))
                    StepBadge(number = 2, active = step >= 2, current = step == 2, label = "Game Info")
                    Divider(color = if (step > 2) NeonCyan else DarkSurfaceBorder, modifier = Modifier.weight(1f))
                    StepBadge(number = 3, active = step >= 3, current = step == 3, label = "Partner")
                    Divider(color = if (step > 3) NeonCyan else DarkSurfaceBorder, modifier = Modifier.weight(1f))
                    StepBadge(number = 4, active = step >= 4, current = step == 4, label = "Confirm")
                }
            }

            if (errorMessage != null) {
                item {
                    Text(text = errorMessage ?: "", color = ErrorRed, fontSize = 13.sp)
                }
            }

            when (step) {
                1 -> {
                    item {
                        Text(
                            text = "ধাপ ১: ব্যক্তিগত তথ্য (Personal Details)",
                            color = NeonCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "এই তথ্য সম্পূর্ণ গোপন থাকবে, অন্য প্লেয়াররা দেখতে পাবে না।",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("পূর্ণ নাম (Full Name)*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_full_name"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it.filter { char -> char.isDigit() } },
                            label = { Text("বয়স (Age)*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_age"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("মোবাইল নম্বর (ঐচ্ছিক - যোগাযোগের জন্য)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_phone"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("আপনার সম্পর্কে সংক্ষিপ্ত পরিচিতি (About Me)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_bio"),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        EsportsButton(
                            text = "পরবর্তী ধাপ (Next)",
                            onClick = {
                                if (fullName.isBlank()) {
                                    errorMessage = "দয়া করে আপনার পূর্ণ নাম লিখুন।"
                                } else {
                                    errorMessage = null
                                    step = 2
                                }
                            },
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "step1_next_btn"
                        )
                    }
                }
                2 -> {
                    item {
                        Text(
                            text = "ধাপ ২: Free Fire তথ্য (Gaming Profile)",
                            color = NeonCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "আপনার গেমের ইন-গেম নাম এবং খেলার ধরণ নির্ধারণ করুন।",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = ign,
                            onValueChange = { ign = it },
                            label = { Text("Free Fire ইন-গেম নাম (IGN)*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_ign"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = fullUid,
                            onValueChange = { fullUid = it },
                            label = { Text("Free Fire UID (গোপনীয় থাকবে)*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_uid"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = levelStr,
                            onValueChange = { levelStr = it.filter { char -> char.isDigit() } },
                            label = { Text("বর্তমান গেম লেভেল (Level)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_level"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        Text(text = "পছন্দের গেম মোড (Preferred Mode):", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("BR Ranked", "CS Ranked", "Lone Wolf").forEach { mode ->
                                FilterChip(
                                    selected = preferredMode == mode,
                                    onClick = { preferredMode = mode },
                                    label = { Text(mode) }
                                )
                            }
                        }
                    }
                    item {
                        Text(text = "খেলার ধরণ (Playing Style):", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Rusher", "Sniper", "Support", "IGL").forEach { style ->
                                FilterChip(
                                    selected = playingStyle == style,
                                    onClick = { playingStyle = style },
                                    label = { Text(style) }
                                )
                            }
                        }
                    }
                    item {
                        Text(text = "খেলার সময় (Playing Schedule):", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Night", "Evening", "Any Time").forEach { time ->
                                FilterChip(
                                    selected = playingSchedule == time,
                                    onClick = { playingSchedule = time },
                                    label = { Text(time) }
                                )
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { step = 1 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("পেছনে (Back)", color = TextPrimary)
                            }
                            EsportsButton(
                                text = "পরবর্তী ধাপ (Next)",
                                onClick = {
                                    if (ign.isBlank() || fullUid.isBlank()) {
                                        errorMessage = "IGN এবং UID অবশ্যই পূরণ করতে হবে।"
                                    } else {
                                        errorMessage = null
                                        step = 3
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "step2_next_btn"
                            )
                        }
                    }
                }
                3 -> {
                    item {
                        Text(
                            text = "ধাপ ৩: Duo পার্টনার পছন্দ (Partner Preferences)",
                            color = NeonCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = partnerAgeRange,
                            onValueChange = { partnerAgeRange = it },
                            label = { Text("পার্টনারের কাঙ্ক্ষিত বয়স (যেমন 18-25)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        Text(text = "কাঙ্ক্ষিত পার্টনার স্টাইল:", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Sniper", "Rusher", "Support", "Any").forEach { style ->
                                FilterChip(
                                    selected = partnerStyle == style,
                                    onClick = { partnerStyle = style },
                                    label = { Text(style) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = idealPartnerDesc,
                            onValueChange = { idealPartnerDesc = it },
                            label = { Text("আদর্শ পার্টনার কেমন হবে? (Short Description)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkSurfaceBorder
                            )
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { step = 2 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("পেছনে (Back)", color = TextPrimary)
                            }
                            EsportsButton(
                                text = "রিভিউ করুন (Preview)",
                                onClick = { step = 4 },
                                modifier = Modifier.weight(1f),
                                testTag = "step3_next_btn"
                            )
                        }
                    }
                }
                4 -> {
                    item {
                        Text(
                            text = "ধাপ ৪: নিশ্চিতকরণ ও প্রিভিউ (Review & Confirm)",
                            color = NeonGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        EsportsCard {
                            Text(text = "পাবলিক Duo প্রোফাইল (অন্যরা যা দেখবে):", color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "IGN: $ign (Level $levelStr)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "মোড: $preferredMode • স্টাইল: $playingStyle • সময়: $playingSchedule", color = TextSecondary, fontSize = 13.sp)
                            Text(text = "ভাষা: $language", color = TextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "\"$bio\"", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                    item {
                        EsportsCard(borderColor = NeonPurple.copy(alpha = 0.4f)) {
                            Text(text = "গোপনীয় ডেটা (শুধু আপনি ও অ্যাডমিন দেখতে পাবেন):", color = NeonPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "পূর্ণ নাম: $fullName", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "Free Fire UID: $fullUid", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "বয়স: $ageStr বছর", color = TextPrimary, fontSize = 13.sp)
                            if (phone.isNotBlank()) {
                                Text(text = "ফোন: $phone", color = TextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { step = 3 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("এডিট (Edit)", color = TextPrimary)
                            }
                            EsportsButton(
                                text = "প্রোফাইল জমা দিন",
                                onClick = {
                                    isSubmitting = true
                                    scope.launch {
                                        try {
                                            val priv = PrivateProfileData(
                                                userId = currentUserId,
                                                fullName = fullName,
                                                age = ageStr.toIntOrNull() ?: 20,
                                                email = currentUserEmail,
                                                phone = phone.ifBlank { null },
                                                fullUid = fullUid,
                                                gender = gender
                                            )
                                            val pub = PublicProfileData(
                                                userId = currentUserId,
                                                ign = ign,
                                                level = levelStr.toIntOrNull() ?: 50,
                                                preferredMode = preferredMode,
                                                experience = experience,
                                                playingStyle = playingStyle,
                                                playingSchedule = playingSchedule,
                                                language = language,
                                                bio = bio,
                                                partnerAgeRange = partnerAgeRange,
                                                partnerStyle = partnerStyle,
                                                idealPartnerDesc = idealPartnerDesc,
                                                isAvailableForDuo = true,
                                                hasActiveDuo = existingPublic?.hasActiveDuo ?: false,
                                                isApproved = true // active upon completion
                                            )
                                            onSaveProfile(priv, pub)
                                            isSubmitting = false
                                            onSaveSuccess()
                                        } catch (e: Exception) {
                                            isSubmitting = false
                                            errorMessage = e.message ?: "সংরক্ষণে ব্যর্থ হয়েছে।"
                                        }
                                    }
                                },
                                isLoading = isSubmitting,
                                gradientColors = listOf(NeonGreen, Color(0xFF059669)),
                                modifier = Modifier.weight(1f),
                                testTag = "submit_profile_btn"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepBadge(
    number: Int,
    active: Boolean,
    current: Boolean,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    if (current) NeonCyan
                    else if (active) NeonCyan.copy(alpha = 0.2f)
                    else DarkSurfaceVariant
                )
                .border(
                    1.dp,
                    if (current || active) NeonCyan else DarkSurfaceBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = if (current) Color(0xFF041017) else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (current) NeonCyan else TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
