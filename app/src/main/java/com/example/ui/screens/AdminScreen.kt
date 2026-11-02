package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.matching.SmartDuoMatcher
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    publicProfiles: List<PublicProfileData>,
    privateProfiles: List<PrivateProfileData>,
    tournaments: List<TournamentData>,
    onApproveProfile: (userId: String) -> Unit,
    onSuspendPlayer: (userId: String) -> Unit,
    onAdminMatchDuos: (player1: PublicProfileData, player2: PublicProfileData) -> Unit,
    onCreateTournament: (TournamentData) -> Unit,
    onUpdateRoomDetails: (tournamentId: String, roomId: String, password: String, visible: Boolean) -> Unit,
    onSaveMatchResult: (MatchResultData) -> Unit,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableStateOf(0) }
    // 0: Overview & Players, 1: Matchmaker, 2: Tournaments & Rooms, 3: Results Scoring, 4: Data Export
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPlayerForDetails by remember { mutableStateOf<Pair<PrivateProfileData?, PublicProfileData?>?>(null) }

    // Matchmaker state
    var matchmakerPlayer1 by remember { mutableStateOf<PublicProfileData?>(null) }
    var matchmakerPlayer2 by remember { mutableStateOf<PublicProfileData?>(null) }

    // Tournament creation modal state
    var showCreateTournamentDialog by remember { mutableStateOf(false) }
    var newTourneyTitle by remember { mutableStateOf("") }
    var newTourneyType by remember { mutableStateOf("daily") }
    var newTourneyPrize by remember { mutableStateOf("৳10,000") }
    var newTourneyEntry by remember { mutableStateOf("Free") }
    var newTourneySchedule by remember { mutableStateOf("Tonight 8:30 PM BST") }

    // Room update modal state
    var selectedTournamentForRoom by remember { mutableStateOf<TournamentData?>(null) }
    var roomIdInput by remember { mutableStateOf("") }
    var roomPasswordInput by remember { mutableStateOf("") }
    var roomVisibleInput by remember { mutableStateOf(false) }

    // Scoring modal state
    var showAddResultDialog by remember { mutableStateOf(false) }
    var resultTeamName by remember { mutableStateOf("") }
    var resultPlayers by remember { mutableStateOf("") }
    var resultPlacement by remember { mutableStateOf("1") }
    var resultKills by remember { mutableStateOf("10") }
    var resultPoints by remember { mutableStateOf("25") }

    val pubMap = remember(publicProfiles) { publicProfiles.associateBy { it.userId } }
    val csvContent = remember(privateProfiles, pubMap) {
        val sb = StringBuilder()
        sb.append("Player Name,Age,Free Fire UID,IGN,Game Level,Preferred Mode,Experience,Status\n")
        privateProfiles.forEach { p ->
            val pub = pubMap[p.userId]
            val ign = pub?.ign ?: "N/A"
            val level = pub?.level?.toString() ?: "N/A"
            val mode = pub?.preferredMode ?: "N/A"
            val exp = pub?.experience ?: "N/A"
            val status = if (pub?.isApproved == true) "Approved" else "Pending"
            sb.append("\"${p.fullName}\",${p.age},\"${p.fullUid}\",\"$ign\",$level,\"$mode\",\"$exp\",$status\n")
        }
        sb.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Admin Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ADMIN CONTROL CENTER",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Player verification, tournament rooms & data export",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            StatusBadge(text = "SUPER ADMIN", color = GoldYellow)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Admin Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = adminTab,
            containerColor = DarkSurfaceCard,
            contentColor = NeonCyan,
            edgePadding = 0.dp,
            divider = {},
            indicator = {}
        ) {
            val tabs = listOf("Players", "Matchmaker", "Tournaments", "Results", "CSV Export")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = adminTab == index,
                    onClick = { adminTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (adminTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (adminTab == index) NeonCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (adminTab) {
            0 -> {
                // Players Management Tab
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("খুঁজুন (Name, IGN, বা UID)", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                val combinedPlayers = remember(publicProfiles, privateProfiles, searchQuery) {
                    val privMap = privateProfiles.associateBy { it.userId }
                    val allUids = (publicProfiles.map { it.userId } + privateProfiles.map { it.userId }).distinct()
                    allUids.mapNotNull { uid ->
                        val pub = publicProfiles.firstOrNull { it.userId == uid }
                        val priv = privMap[uid]
                        val nameMatch = priv?.fullName?.contains(searchQuery, ignoreCase = true) == true
                        val uidMatch = priv?.fullUid?.contains(searchQuery, ignoreCase = true) == true
                        val ignMatch = pub?.ign?.contains(searchQuery, ignoreCase = true) == true
                        if (searchQuery.isBlank() || nameMatch || uidMatch || ignMatch) {
                            priv to pub
                        } else null
                    }
                }

                Text(
                    text = "মোট নিবন্ধিত প্লেয়ার: ${combinedPlayers.size}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(combinedPlayers) { (priv, pub) ->
                        EsportsCard(
                            borderColor = if (pub?.isApproved == true) NeonGreen.copy(alpha = 0.4f) else GoldYellow.copy(alpha = 0.4f),
                            onClick = { selectedPlayerForDetails = priv to pub }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pub?.ign ?: priv?.fullName ?: "Unknown Player",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "UID: ${priv?.fullUid ?: "Private"} • Level ${pub?.level ?: 50}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                    if (priv != null) {
                                        Text(
                                            text = "নাম: ${priv.fullName} (${priv.age} yr)",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (pub?.isApproved != true && pub != null) {
                                        Button(
                                            onClick = { onApproveProfile(pub.userId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Approve", color = Color(0xFF00381B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        StatusBadge(text = "APPROVED", color = NeonGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Manual Duo Matchmaker
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text(
                            text = "অ্যাডমিন ম্যানুয়াল Duo ম্যাচিং (Manual Pairing)",
                            color = NeonCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "যেকোনো দুইজন খেলোয়াড়কে সরাসরি Duo টিম হিসেবে কানেক্ট করুন।",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Player 1 selector
                            EsportsCard(
                                modifier = Modifier.weight(1f),
                                borderColor = if (matchmakerPlayer1 != null) NeonCyan else DarkSurfaceBorder
                            ) {
                                Text(text = "প্লেয়ার ১ (Player A)", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (matchmakerPlayer1 != null) {
                                    Text(text = matchmakerPlayer1!!.ign, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${matchmakerPlayer1!!.playingStyle} • Level ${matchmakerPlayer1!!.level}", color = TextSecondary, fontSize = 11.sp)
                                    TextButton(onClick = { matchmakerPlayer1 = null }) {
                                        Text("রিমুভ", color = ErrorRed, fontSize = 11.sp)
                                    }
                                } else {
                                    Text(text = "নিচের তালিকা থেকে সিলেক্ট করুন", color = TextMuted, fontSize = 11.sp)
                                }
                            }

                            // Player 2 selector
                            EsportsCard(
                                modifier = Modifier.weight(1f),
                                borderColor = if (matchmakerPlayer2 != null) NeonPurple else DarkSurfaceBorder
                            ) {
                                Text(text = "প্লেয়ার ২ (Player B)", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (matchmakerPlayer2 != null) {
                                    Text(text = matchmakerPlayer2!!.ign, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${matchmakerPlayer2!!.playingStyle} • Level ${matchmakerPlayer2!!.level}", color = TextSecondary, fontSize = 11.sp)
                                    TextButton(onClick = { matchmakerPlayer2 = null }) {
                                        Text("রিমুভ", color = ErrorRed, fontSize = 11.sp)
                                    }
                                } else {
                                    Text(text = "নিচের তালিকা থেকে সিলেক্ট করুন", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    if (matchmakerPlayer1 != null && matchmakerPlayer2 != null) {
                        val p1 = matchmakerPlayer1!!
                        val p2 = matchmakerPlayer2!!
                        val comp = SmartDuoMatcher.calculate(p1, p2)

                        item {
                            EsportsCard(borderColor = NeonGreen) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "কম্প্যাটিবিলিটি স্কোর:", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "${comp.score}% (${comp.synergyTag})", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                comp.highlights.forEach { h ->
                                    Text(text = "• $h", color = TextSecondary, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                EsportsButton(
                                    text = "এই দুইজনকে DUO হিসেবে ম্যাচ করুন",
                                    onClick = {
                                        onAdminMatchDuos(p1, p2)
                                        matchmakerPlayer1 = null
                                        matchmakerPlayer2 = null
                                        Toast.makeText(context, "Duo Match Created Successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    gradientColors = listOf(NeonGreen, Color(0xFF047857)),
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "admin_confirm_match_btn"
                                )
                            }
                        }
                    }

                    item {
                        Text(text = "উপলব্ধ খেলোয়াড়দের তালিকা:", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    items(publicProfiles) { p ->
                        EsportsCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = p.ign, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "স্টাইল: ${p.playingStyle} • মোড: ${p.preferredMode}", color = TextSecondary, fontSize = 11.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { matchmakerPlayer1 = p },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Set Player 1", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { matchmakerPlayer2 = p },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Set Player 2", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tournaments & Room Management
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
                                text = "টুর্নামেন্ট ও রুম পরিচালনা",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showCreateTournamentDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("নতুন টুর্নামেন্ট", color = Color(0xFF00381B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(tournaments) { tourney ->
                        EsportsCard(borderColor = NeonCyan.copy(alpha = 0.4f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = tourney.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = "Prize: ${tourney.prizePool} • Fee: ${tourney.entryFee} • ${tourney.type}", color = TextSecondary, fontSize = 12.sp)
                                }
                                StatusBadge(text = tourney.status.uppercase(), color = NeonCyan)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "রুম আইডি: ${tourney.roomId ?: "দেওয়া হয়নি"}", color = TextPrimary, fontSize = 12.sp)
                                    Text(text = "রুম দেখা যাচ্ছে: ${if (tourney.roomDetailsVisible) "হ্যাঁ (Visible)" else "না (Hidden)"}", color = if (tourney.roomDetailsVisible) NeonGreen else GoldYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        selectedTournamentForRoom = tourney
                                        roomIdInput = tourney.roomId ?: ""
                                        roomPasswordInput = tourney.roomPassword ?: ""
                                        roomVisibleInput = tourney.roomDetailsVisible
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("রুম আইডি দিন", color = NeonCyan, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Results & Scoring
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
                                text = "ম্যাচ রেজাল্ট ও স্কোর যোগ করুন",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showAddResultDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("স্কোর এন্ট্রি", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item {
                        EsportsCard {
                            Text(text = "স্কোরিং রুলস (Scoring Rules):", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "১ম স্থান: ১২ পয়েন্ট | ২য় স্থান: ৯ পয়েন্ট | ৩য় স্থান: ৮ পয়েন্ট | প্রতি Kill: ১ পয়েন্ট", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "স্কোর এন্ট্রি করার সাথে সাথে লিডারবোর্ডের র্যাঙ্কিং স্বয়ংক্রিয়ভাবে আপডেট হয়ে যাবে।", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            4 -> {
                // CSV Export
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text(
                            text = "প্লেয়ার ডেটা এক্সপোর্ট (CSV Export)",
                            color = NeonGreen,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "সকল নিবন্ধিত প্লেয়ারের তথ্য এক্সেল বা গুগল শিটে ব্যবহারের জন্য CSV ফরম্যাটে এক্সপোর্ট করুন।",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    item {
                        EsportsCard {
                            Text(text = "CSV প্রিভিউ (${privateProfiles.size} Players):", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFF0A0F18),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = csvContent.take(400) + if (csvContent.length > 400) "\n..." else "",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            EsportsButton(
                                text = "CSV কপি করুন (Copy to Clipboard)",
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("DuoConnect_Players", csvContent)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "CSV ডেটা ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                icon = Icons.Default.ContentCopy,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "copy_csv_btn"
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Tournament Dialog
    if (showCreateTournamentDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTournamentDialog = false },
            title = { Text("নতুন টুর্নামেন্ট তৈরি করুন", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTourneyTitle,
                        onValueChange = { newTourneyTitle = it },
                        label = { Text("টুর্নামেন্ট নাম*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTourneyPrize,
                        onValueChange = { newTourneyPrize = it },
                        label = { Text("প্রাইজ পুল (যেমন ৳10,000)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTourneyEntry,
                        onValueChange = { newTourneyEntry = it },
                        label = { Text("এন্ট্রি ফি (যেমন Free বা ৳50)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTourneySchedule,
                        onValueChange = { newTourneySchedule = it },
                        label = { Text("ম্যাচ শিডিউল") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTourneyTitle.isNotBlank()) {
                            onCreateTournament(
                                TournamentData(
                                    title = newTourneyTitle,
                                    type = newTourneyType,
                                    gameMode = "Battle Royale",
                                    format = "duo",
                                    maxParticipants = 48,
                                    entryFee = newTourneyEntry,
                                    prizePool = newTourneyPrize,
                                    scheduleTime = newTourneySchedule,
                                    status = "registration_open"
                                )
                            )
                            showCreateTournamentDialog = false
                            Toast.makeText(context, "Tournament Created!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("তৈরি করুন", color = Color(0xFF00381B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTournamentDialog = false }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }

    // Room Details Dialog
    if (selectedTournamentForRoom != null) {
        val tourney = selectedTournamentForRoom!!
        AlertDialog(
            onDismissRequest = { selectedTournamentForRoom = null },
            title = { Text("কাস্টম রুম তথ্য: ${tourney.title}", color = TextPrimary, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = roomIdInput,
                        onValueChange = { roomIdInput = it },
                        label = { Text("Room ID (কাস্টম রুম আইডি)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = roomPasswordInput,
                        onValueChange = { roomPasswordInput = it },
                        label = { Text("Room Password (পাসওয়ার্ড)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = roomVisibleInput,
                            onCheckedChange = { roomVisibleInput = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "প্লেয়ারদের কাছে রুম আইডি প্রকাশ করুন", color = TextPrimary, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateRoomDetails(tourney.id, roomIdInput, roomPasswordInput, roomVisibleInput)
                        selectedTournamentForRoom = null
                        Toast.makeText(context, "Room Details Updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("সংরক্ষণ করুন", color = Color(0xFF041017), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTournamentForRoom = null }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }

    // Add Result Dialog
    if (showAddResultDialog) {
        AlertDialog(
            onDismissRequest = { showAddResultDialog = false },
            title = { Text("ম্যাচ রেজাল্ট এন্ট্রি", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = resultTeamName,
                        onValueChange = { resultTeamName = it },
                        label = { Text("টিম নাম (Team Name)*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resultPlayers,
                        onValueChange = { resultPlayers = it },
                        label = { Text("প্লেয়ারদের নাম (e.g. Tanvir & Rakib)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resultPlacement,
                        onValueChange = { resultPlacement = it },
                        label = { Text("স্থান (Placement Rank 1-12)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resultKills,
                        onValueChange = { resultKills = it },
                        label = { Text("মোট কিলস (Total Kills)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resultPoints,
                        onValueChange = { resultPoints = it },
                        label = { Text("মোট পয়েন্ট (Total Points)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resultTeamName.isNotBlank()) {
                            onSaveMatchResult(
                                MatchResultData(
                                    tournamentId = "daily_1",
                                    teamName = resultTeamName,
                                    playerUids = resultPlayers,
                                    placement = resultPlacement.toIntOrNull() ?: 1,
                                    kills = resultKills.toIntOrNull() ?: 0,
                                    totalPoints = resultPoints.toIntOrNull() ?: 0,
                                    finalRank = resultPlacement.toIntOrNull() ?: 1,
                                    published = true
                                )
                            )
                            showAddResultDialog = false
                            Toast.makeText(context, "Result Published to Leaderboard!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("পাবলিশ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddResultDialog = false }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }

    // Player Details Dialog
    if (selectedPlayerForDetails != null) {
        val (priv, pub) = selectedPlayerForDetails!!
        AlertDialog(
            onDismissRequest = { selectedPlayerForDetails = null },
            title = { Text(text = "প্লেয়ার ভেরিফিকেশন তথ্য", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "IGN: ${pub?.ign ?: "N/A"}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "পূর্ণ নাম: ${priv?.fullName ?: "N/A"}", color = TextPrimary, fontSize = 13.sp)
                    Text(text = "Free Fire UID: ${priv?.fullUid ?: "N/A"}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "বয়স: ${priv?.age ?: 18} বছর", color = TextSecondary, fontSize = 13.sp)
                    if (priv?.phone != null) {
                        Text(text = "ফোন: ${priv.phone}", color = TextSecondary, fontSize = 13.sp)
                    }
                    Text(text = "খেলার মোড: ${pub?.preferredMode ?: "BR"} • স্টাইল: ${pub?.playingStyle ?: "Rusher"}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "সময়: ${pub?.playingSchedule ?: "Night"}", color = TextSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                if (pub != null && !pub.isApproved) {
                    Button(
                        onClick = {
                            onApproveProfile(pub.userId)
                            selectedPlayerForDetails = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                    ) {
                        Text("Approve Profile", color = Color(0xFF00381B))
                    }
                } else {
                    TextButton(onClick = { selectedPlayerForDetails = null }) {
                        Text("ঠিক আছে", color = NeonCyan)
                    }
                }
            },
            dismissButton = {
                if (priv != null) {
                    TextButton(onClick = {
                        onSuspendPlayer(priv.userId)
                        selectedPlayerForDetails = null
                    }) {
                        Text("Suspend", color = ErrorRed)
                    }
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}
