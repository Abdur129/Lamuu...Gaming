package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.example.ui.auth.AuthManager
import com.example.ui.components.GamerAvatar
import com.example.ui.components.LanguageToggleButton
import com.example.ui.localization.LocalizationState
import com.example.ui.localization.Strings
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DuoConnectTheme {
                AppContent()
            }
        }
    }
}

enum class ScreenState {
    MAIN_NAV,
    PROFILE_SETUP,
    ADMIN_PANEL,
    NOTIFICATIONS,
    SETTINGS
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val repository = remember { AppRepository.getInstance(context) }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { currentUser = Firebase.auth.currentUser },
            onUnauthenticated = {},
            scope = scope
        )
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        val uid = currentUser!!.uid
        val email = currentUser!!.email ?: ""

        // User Account data
        var userData by remember { mutableStateOf<UserData?>(null) }
        var privateProfile by remember { mutableStateOf<PrivateProfileData?>(null) }
        var publicProfile by remember { mutableStateOf<PublicProfileData?>(null) }

        // Data streams
        val candidates by repository.observeCandidateProfiles().collectAsState(initial = emptyList())
        val incomingRequests by repository.observeIncomingRequests(uid).collectAsState(initial = emptyList())
        val outgoingRequests by repository.observeOutgoingRequests(uid).collectAsState(initial = emptyList())
        val activeMatch by repository.observeActiveDuo(uid).collectAsState(initial = null)
        val tournaments by repository.observeTournaments().collectAsState(initial = emptyList())
        val myRegistrations by repository.observeMyRegistrations(uid).collectAsState(initial = emptyList())
        val matchResults by repository.observeMatchResults().collectAsState(initial = emptyList())
        val notifications by repository.observeNotifications(uid).collectAsState(initial = emptyList())

        // Admin streams
        val allPublicProfiles by repository.observeAllPublicProfiles().collectAsState(initial = emptyList())
        val allPrivateProfiles by repository.observeAllPrivateProfiles().collectAsState(initial = emptyList())

        // Load profile on auth
        LaunchedEffect(uid) {
            val user = repository.getOrCreateUserRecord(email)
            userData = user
            privateProfile = repository.getPrivateProfile(uid)
            publicProfile = repository.getPublicProfile(uid)
        }

        // Navigation state
        var currentScreen by remember { mutableStateOf(ScreenState.MAIN_NAV) }
        var bottomNavIndex by remember { mutableStateOf(0) } // 0: Home, 1: Find Duo, 2: Duo Hub, 3: Tournaments, 4: Leaderboard

        val userRole = userData?.role ?: if (email.equals("hafezabdurrahman22@gmail.com", ignoreCase = true)) "superadmin" else "player"
        val isAdmin = userRole.contains("admin") || email.equals("hafezabdurrahman22@gmail.com", ignoreCase = true)
        val unreadNotificationCount = notifications.count { !it.read }

        when (currentScreen) {
            ScreenState.PROFILE_SETUP -> {
                BackHandler { currentScreen = ScreenState.MAIN_NAV }
                ProfileSetupScreen(
                    currentUserId = uid,
                    currentUserEmail = email,
                    existingPrivate = privateProfile,
                    existingPublic = publicProfile,
                    onSaveSuccess = {
                        scope.launch {
                            privateProfile = repository.getPrivateProfile(uid)
                            publicProfile = repository.getPublicProfile(uid)
                            currentScreen = ScreenState.MAIN_NAV
                        }
                    },
                    onBack = { currentScreen = ScreenState.MAIN_NAV },
                    onSaveProfile = { priv, pub ->
                        repository.savePrivateProfile(priv)
                        repository.savePublicProfile(pub)
                    }
                )
            }
            ScreenState.ADMIN_PANEL -> {
                BackHandler { currentScreen = ScreenState.MAIN_NAV }
                Scaffold(
                    topBar = {
                        TopBarWithBack(
                            title = Strings.adminPanel,
                            onBack = { currentScreen = ScreenState.MAIN_NAV }
                        )
                    },
                    containerColor = DarkBg
                ) { innerPadding ->
                    AdminScreen(
                        publicProfiles = allPublicProfiles,
                        privateProfiles = allPrivateProfiles,
                        tournaments = tournaments,
                        onApproveProfile = { targetUid ->
                            scope.launch { repository.approveProfile(targetUid) }
                        },
                        onSuspendPlayer = { targetUid ->
                            scope.launch { repository.suspendPlayer(targetUid) }
                        },
                        onAdminMatchDuos = { p1, p2 ->
                            scope.launch { repository.createAdminDuoMatch(p1, p2) }
                        },
                        onCreateTournament = { tourney ->
                            scope.launch { repository.createTournament(tourney) }
                        },
                        onUpdateRoomDetails = { tourneyId, roomId, pwd, visible ->
                            scope.launch { repository.updateTournamentRoom(tourneyId, roomId, pwd, visible) }
                        },
                        onSaveMatchResult = { res ->
                            scope.launch { repository.saveMatchResult(res) }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            ScreenState.NOTIFICATIONS -> {
                BackHandler { currentScreen = ScreenState.MAIN_NAV }
                Scaffold(
                    topBar = {
                        TopBarWithBack(
                            title = Strings.notifications,
                            onBack = { currentScreen = ScreenState.MAIN_NAV }
                        )
                    },
                    containerColor = DarkBg
                ) { innerPadding ->
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAsRead = { notifId ->
                            scope.launch { repository.markNotificationAsRead(notifId) }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            ScreenState.SETTINGS -> {
                BackHandler { currentScreen = ScreenState.MAIN_NAV }
                Scaffold(
                    topBar = {
                        TopBarWithBack(
                            title = Strings.settings,
                            onBack = { currentScreen = ScreenState.MAIN_NAV }
                        )
                    },
                    containerColor = DarkBg
                ) { innerPadding ->
                    SettingsScreen(
                        currentUserId = uid,
                        currentUserEmail = email,
                        userRole = userRole,
                        publicProfile = publicProfile,
                        privateProfile = privateProfile,
                        onNavigateToEditProfile = { currentScreen = ScreenState.PROFILE_SETUP },
                        onToggleMatchingAvailability = { available ->
                            scope.launch {
                                publicProfile?.let {
                                    repository.savePublicProfile(it.copy(isAvailableForDuo = available))
                                    publicProfile = repository.getPublicProfile(uid)
                                }
                            }
                        },
                        onSignOut = {
                            AuthManager.signOut(
                                context = context,
                                credentialManager = credentialManager,
                                onSignOutComplete = { currentUser = null },
                                scope = scope
                            )
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            ScreenState.MAIN_NAV -> {
                Scaffold(
                    topBar = {
                        MainAppTopBar(
                            isAdmin = isAdmin,
                            unreadCount = unreadNotificationCount,
                            onOpenAdmin = { currentScreen = ScreenState.ADMIN_PANEL },
                            onOpenNotifications = { currentScreen = ScreenState.NOTIFICATIONS },
                            onOpenSettings = { currentScreen = ScreenState.SETTINGS }
                        )
                    },
                    bottomBar = {
                        MainAppBottomNav(
                            selectedIndex = bottomNavIndex,
                            incomingRequestsCount = incomingRequests.size,
                            onTabSelected = { bottomNavIndex = it }
                        )
                    },
                    containerColor = DarkBg
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (bottomNavIndex) {
                            0 -> HomeScreen(
                                hasCompletedProfile = publicProfile != null,
                                onNavigateToProfileSetup = { currentScreen = ScreenState.PROFILE_SETUP },
                                onNavigateToFindDuo = { bottomNavIndex = 1 },
                                onNavigateToTournaments = { bottomNavIndex = 3 },
                                onNavigateToLeaderboard = { bottomNavIndex = 4 },
                                tournaments = tournaments
                            )
                            1 -> FindDuoScreen(
                                currentUserProfile = publicProfile,
                                candidates = candidates,
                                sentRequests = outgoingRequests,
                                onSendRequest = { targetUserId, targetIgn, score, msg ->
                                    scope.launch {
                                        val myIgn = publicProfile?.ign ?: "DuoPlayer"
                                        repository.sendDuoRequest(targetUserId, targetIgn, myIgn, score, msg)
                                    }
                                }
                            )
                            2 -> DuoHubScreen(
                                currentUserId = uid,
                                activeMatch = activeMatch,
                                incomingRequests = incomingRequests,
                                outgoingRequests = outgoingRequests,
                                onAcceptRequest = { req ->
                                    scope.launch { repository.respondToDuoRequest(req, accept = true) }
                                },
                                onRejectRequest = { req ->
                                    scope.launch { repository.respondToDuoRequest(req, accept = false) }
                                },
                                onUnmatch = { matchId, partnerId ->
                                    scope.launch { repository.unmatchDuo(matchId, partnerId) }
                                }
                            )
                            3 -> TournamentsScreen(
                                currentUserId = uid,
                                currentUserIgn = publicProfile?.ign ?: "Player",
                                activeDuoPartnerIgn = if (activeMatch?.player1Id == uid) activeMatch?.player2Ign else activeMatch?.player1Ign,
                                activeDuoPartnerId = if (activeMatch?.player1Id == uid) activeMatch?.player2Id else activeMatch?.player1Id,
                                tournaments = tournaments,
                                myRegistrations = myRegistrations,
                                onRegisterTournament = { tourneyId, teamName, pIgn, partIgn, partId ->
                                    scope.launch {
                                        repository.registerForTournament(tourneyId, teamName, pIgn, partIgn, partId)
                                    }
                                }
                            )
                            4 -> LeaderboardScreen(
                                results = matchResults
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppTopBar(
    isAdmin: Boolean,
    unreadCount: Int,
    onOpenAdmin: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_duo_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, GlassBorderNeonGreen, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "DUO CONNECT",
                        color = NeonGreenPrimary,
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "FREE FIRE ESPORTS",
                        color = NeonPurpleLight,
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        },
        actions = {
            LanguageToggleButton()
            Spacer(modifier = Modifier.width(6.dp))

            if (isAdmin) {
                IconButton(
                    onClick = onOpenAdmin,
                    modifier = Modifier.testTag("admin_nav_button")
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = GoldYellow,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            IconButton(
                onClick = onOpenNotifications,
                modifier = Modifier.testTag("notifications_nav_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(containerColor = NeonPurplePrimary) {
                                Text(unreadCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("settings_nav_button")
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GlassDarkSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithBack(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = RajdhaniFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GlassDarkSurface)
    )
}

@Composable
fun MainAppBottomNav(
    selectedIndex: Int,
    incomingRequestsCount: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = GlassDarkBackground,
        contentColor = NeonGreenPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    NeonGreenPrimary.copy(alpha = 0.4f),
                    NeonPurplePrimary.copy(alpha = 0.4f)
                )
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        )
    ) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor = NeonGreenPrimary,
            selectedTextColor = NeonGreenPrimary,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary,
            indicatorColor = NeonGreenPrimary.copy(alpha = 0.18f)
        )

        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
            colors = navColors,
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.FlashOn, contentDescription = "Find Duo") },
            label = { Text("Find Duo", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
            colors = navColors,
            modifier = Modifier.testTag("nav_find_duo")
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onTabSelected(2) },
            icon = {
                BadgedBox(
                    badge = {
                        if (incomingRequestsCount > 0) {
                            Badge(containerColor = NeonPurplePrimary) {
                                Text(incomingRequestsCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Group, contentDescription = "My Duo")
                }
            },
            label = { Text("Duo Hub", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
            colors = navColors,
            modifier = Modifier.testTag("nav_duo_hub")
        )
        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Tournaments") },
            label = { Text("Tournaments", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navColors,
            modifier = Modifier.testTag("nav_tournaments")
        )
        NavigationBarItem(
            selected = selectedIndex == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard") },
            label = { Text("Leaderboard", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navColors,
            modifier = Modifier.testTag("nav_leaderboard")
        )
    }
}
