package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.*
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AppRepository(
    private val db: FirebaseFirestore
) {
    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return instance ?: synchronized(this) {
                instance ?: run {
                    val dbId = context.getString(R.string.firestore_database_id)
                    val firestore = FirebaseFirestore.getInstance(dbId)
                    AppRepository(firestore).also { instance = it }
                }
            }
        }
    }

    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance(db.app)

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google")
    }

    // ----------------------------------------------------
    // User Account & Role
    // ----------------------------------------------------
    suspend fun getOrCreateUserRecord(email: String): UserData {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                snapshot.toObject(UserData::class.java) ?: UserData(userId = uid, email = email)
            } else {
                val isSuperAdmin = email.equals("hafezabdurrahman22@gmail.com", ignoreCase = true)
                val newUser = UserData(
                    userId = uid,
                    email = email,
                    role = if (isSuperAdmin) "superadmin" else "player",
                    status = "active"
                )
                val payload = hashMapOf<String, Any>(
                    "userId" to uid,
                    "email" to email,
                    "role" to newUser.role,
                    "status" to "active",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                newUser
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            UserData(userId = uid, email = email)
        }
    }

    // ----------------------------------------------------
    // Profile Management (Private PII & Public Matching)
    // ----------------------------------------------------
    suspend fun getPrivateProfile(uid: String = requireUserId()): PrivateProfileData? {
        val path = "privateProfiles/$uid"
        return try {
            val snap = db.collection("privateProfiles").document(uid).get().await()
            snap.toObject(PrivateProfileData::class.java)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun savePrivateProfile(profile: PrivateProfileData) {
        val uid = requireUserId()
        val path = "privateProfiles/$uid"
        val docRef = db.collection("privateProfiles").document(uid)
        val snap = try { docRef.get().await() } catch (e: Exception) { null }
        val isUpdate = snap != null && snap.exists()

        val data = hashMapOf<String, Any>(
            "fullName" to profile.fullName,
            "age" to profile.age,
            "fullUid" to profile.fullUid,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        profile.email?.let { data["email"] = it }
        profile.phone?.let { data["phone"] = it }
        profile.gender?.let { data["gender"] = it }
        profile.photoUrl?.let { data["photoUrl"] = it }
        profile.screenshotUrl?.let { data["screenshotUrl"] = it }

        try {
            if (isUpdate) {
                docRef.update(data).await()
            } else {
                data["userId"] = uid
                data["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(data).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun getPublicProfile(uid: String = requireUserId()): PublicProfileData? {
        val path = "publicProfiles/$uid"
        return try {
            val snap = db.collection("publicProfiles").document(uid).get().await()
            snap.toObject(PublicProfileData::class.java)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun savePublicProfile(profile: PublicProfileData) {
        val uid = requireUserId()
        val path = "publicProfiles/$uid"
        val docRef = db.collection("publicProfiles").document(uid)
        val snap = try { docRef.get().await() } catch (e: Exception) { null }
        val isUpdate = snap != null && snap.exists()

        val data = hashMapOf<String, Any>(
            "ign" to profile.ign,
            "level" to profile.level,
            "preferredMode" to profile.preferredMode,
            "experience" to profile.experience,
            "playingStyle" to profile.playingStyle,
            "playingSchedule" to profile.playingSchedule,
            "language" to profile.language,
            "isAvailableForDuo" to profile.isAvailableForDuo,
            "hasActiveDuo" to profile.hasActiveDuo,
            "isApproved" to profile.isApproved,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        profile.bio?.let { data["bio"] = it }
        profile.avatar?.let { data["avatar"] = it }
        profile.gender?.let { data["gender"] = it }
        profile.age?.let { data["age"] = it }
        profile.idealPartnerDesc?.let { data["idealPartnerDesc"] = it }
        profile.partnerAgeRange?.let { data["partnerAgeRange"] = it }
        profile.partnerStyle?.let { data["partnerStyle"] = it }
        profile.activeDuoPartnerId?.let { data["activeDuoPartnerId"] = it }

        try {
            if (isUpdate) {
                docRef.update(data).await()
            } else {
                data["userId"] = uid
                data["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(data).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeCandidateProfiles(): Flow<List<PublicProfileData>> {
        val currentUid = getCurrentUserId() ?: ""
        return db.collection("publicProfiles")
            .snapshots()
            .map { snap ->
                snap.toObjects(PublicProfileData::class.java)
                    .filter { it.userId != currentUid }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "publicProfiles")
                emit(emptyList())
            }
    }

    // ----------------------------------------------------
    // Duo Requests & Matches
    // ----------------------------------------------------
    suspend fun sendDuoRequest(
        toUserId: String,
        toIgn: String,
        fromIgn: String,
        compatibilityScore: Int,
        message: String
    ) {
        val uid = requireUserId()
        val path = "duoRequests"
        val payload = hashMapOf<String, Any>(
            "fromUserId" to uid,
            "toUserId" to toUserId,
            "fromIgn" to fromIgn,
            "toIgn" to toIgn,
            "status" to "pending",
            "compatibilityScore" to compatibilityScore,
            "message" to message,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            db.collection("duoRequests").add(payload).await()
            // also create notification for target player
            sendNotification(
                toUserId = toUserId,
                title = "New Duo Request",
                message = "$fromIgn sent you a Duo connection request (Compatibility: $compatibilityScore%)",
                type = "duo_request"
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    fun observeIncomingRequests(uid: String): Flow<List<DuoRequestData>> {
        return db.collection("duoRequests")
            .whereEqualTo("toUserId", uid)
            .snapshots()
            .map { it.toObjects(DuoRequestData::class.java).filter { req -> req.status == "pending" } }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "duoRequests")
                emit(emptyList())
            }
    }

    fun observeOutgoingRequests(uid: String): Flow<List<DuoRequestData>> {
        return db.collection("duoRequests")
            .whereEqualTo("fromUserId", uid)
            .snapshots()
            .map { it.toObjects(DuoRequestData::class.java) }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "duoRequests")
                emit(emptyList())
            }
    }

    suspend fun respondToDuoRequest(request: DuoRequestData, accept: Boolean) {
        val uid = requireUserId()
        val status = if (accept) "accepted" else "rejected"
        val reqRef = db.collection("duoRequests").document(request.id)
        try {
            reqRef.update(
                mapOf(
                    "status" to status,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            if (accept) {
                // Create DuoMatch
                val matchPayload = hashMapOf<String, Any>(
                    "player1Id" to request.fromUserId,
                    "player2Id" to request.toUserId,
                    "player1Ign" to request.fromIgn,
                    "player2Ign" to request.toIgn,
                    "matchSource" to "mutual_request",
                    "status" to "active",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                db.collection("duoMatches").add(matchPayload).await()

                // Update public profiles
                db.collection("publicProfiles").document(request.fromUserId).update(
                    mapOf("hasActiveDuo" to true, "activeDuoPartnerId" to request.toUserId)
                ).await()
                db.collection("publicProfiles").document(request.toUserId).update(
                    mapOf("hasActiveDuo" to true, "activeDuoPartnerId" to request.fromUserId)
                ).await()

                sendNotification(
                    toUserId = request.fromUserId,
                    title = "Duo Request Accepted!",
                    message = "${request.toIgn} accepted your Duo request! You are now a team.",
                    type = "duo_match"
                )
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "duoRequests/${request.id}")
            throw e
        }
    }

    fun observeActiveDuo(uid: String): Flow<DuoMatchData?> {
        return db.collection("duoMatches")
            .snapshots()
            .map { snap ->
                snap.toObjects(DuoMatchData::class.java).firstOrNull { match ->
                    match.status == "active" && (match.player1Id == uid || match.player2Id == uid)
                }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "duoMatches")
                emit(null)
            }
    }

    suspend fun unmatchDuo(matchId: String, partnerId: String) {
        val uid = requireUserId()
        try {
            db.collection("duoMatches").document(matchId).update(
                mapOf("status" to "unmatched", "updatedAt" to FieldValue.serverTimestamp())
            ).await()

            db.collection("publicProfiles").document(uid).update(
                mapOf("hasActiveDuo" to false, "activeDuoPartnerId" to null)
            ).await()
            db.collection("publicProfiles").document(partnerId).update(
                mapOf("hasActiveDuo" to false, "activeDuoPartnerId" to null)
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "duoMatches/$matchId")
            throw e
        }
    }

    // ----------------------------------------------------
    // Tournament Management
    // ----------------------------------------------------
    fun observeTournaments(): Flow<List<TournamentData>> {
        return db.collection("tournaments")
            .snapshots()
            .map { snap ->
                val list = snap.toObjects(TournamentData::class.java)
                if (list.isEmpty()) getPresetTournaments() else list
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "tournaments")
                emit(getPresetTournaments())
            }
    }

    private fun getPresetTournaments(): List<TournamentData> {
        return listOf(
            TournamentData(
                id = "preset_daily_1",
                title = "Dhaka Duo Royale - Daily Showdown",
                description = "Daily evening Battle Royale duo competition. 48 duos, intense action.",
                type = "daily",
                gameMode = "Battle Royale",
                format = "duo",
                maxParticipants = 48,
                currentParticipants = 32,
                entryFee = "Free",
                prizePool = "৳3,000",
                rules = "No emulator, No hack/script, Mobile only. 3 matches: Bermuda, Purgatory, Kalahari.",
                scheduleTime = "Today, 8:00 PM BST",
                status = "registration_open"
            ),
            TournamentData(
                id = "preset_weekly_1",
                title = "Free Fire Champions League - Weekly #14",
                description = "High tier weekly tournament with official casters and live stream.",
                type = "weekly",
                gameMode = "Battle Royale",
                format = "duo",
                maxParticipants = 48,
                currentParticipants = 40,
                entryFee = "৳50",
                prizePool = "৳15,000",
                rules = "Duo format. Top 3 teams win cash prizes via bKash/Nagad.",
                scheduleTime = "Friday, 9:00 PM BST",
                status = "registration_open"
            ),
            TournamentData(
                id = "preset_monthly_1",
                title = "Bangladesh Duo Masters 2026",
                description = "The premier monthly tournament for the best duo teams across Bangladesh.",
                type = "monthly",
                gameMode = "Battle Royale",
                format = "duo",
                maxParticipants = 96,
                currentParticipants = 68,
                entryFee = "৳100",
                prizePool = "৳50,000",
                rules = "Strict ID verification. Top 12 advance to Grand Finals.",
                scheduleTime = "Nov 15, 7:00 PM BST",
                status = "upcoming"
            )
        )
    }

    suspend fun registerForTournament(
        tournamentId: String,
        teamName: String,
        playerIgn: String,
        partnerIgn: String?,
        partnerUserId: String?
    ) {
        val uid = requireUserId()
        val path = "tournamentRegistrations"
        val payload = hashMapOf<String, Any>(
            "tournamentId" to tournamentId,
            "userId" to uid,
            "teamName" to teamName,
            "playerIgn" to playerIgn,
            "status" to "pending",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        partnerIgn?.let { payload["partnerIgn"] = it }
        partnerUserId?.let { payload["partnerUserId"] = it }

        try {
            db.collection("tournamentRegistrations").add(payload).await()
            sendNotification(
                toUserId = uid,
                title = "Tournament Registration Received",
                message = "Your team '$teamName' was registered. Awaiting admin approval.",
                type = "tournament"
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    fun observeMyRegistrations(uid: String): Flow<List<TournamentRegistrationData>> {
        return db.collection("tournamentRegistrations")
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { it.toObjects(TournamentRegistrationData::class.java) }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "tournamentRegistrations")
                emit(emptyList())
            }
    }

    suspend fun createTournament(tournament: TournamentData) {
        val path = "tournaments"
        val payload = hashMapOf<String, Any>(
            "title" to tournament.title,
            "type" to tournament.type,
            "gameMode" to tournament.gameMode,
            "format" to tournament.format,
            "maxParticipants" to tournament.maxParticipants,
            "currentParticipants" to 0,
            "status" to tournament.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        tournament.description?.let { payload["description"] = it }
        tournament.entryFee?.let { payload["entryFee"] = it }
        tournament.prizePool?.let { payload["prizePool"] = it }
        tournament.rules?.let { payload["rules"] = it }
        tournament.scheduleTime?.let { payload["scheduleTime"] = it }
        tournament.roomId?.let { payload["roomId"] = it }
        tournament.roomPassword?.let { payload["roomPassword"] = it }
        payload["roomDetailsVisible"] = tournament.roomDetailsVisible

        try {
            db.collection("tournaments").add(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    suspend fun updateTournamentRoom(
        tournamentId: String,
        roomId: String,
        password: String,
        visible: Boolean
    ) {
        val path = "tournaments/$tournamentId"
        try {
            db.collection("tournaments").document(tournamentId).update(
                mapOf(
                    "roomId" to roomId,
                    "roomPassword" to password,
                    "roomDetailsVisible" to visible,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    // ----------------------------------------------------
    // Leaderboard & Results
    // ----------------------------------------------------
    fun observeMatchResults(tournamentId: String? = null): Flow<List<MatchResultData>> {
        return db.collection("matchResults")
            .snapshots()
            .map { snap ->
                val list = snap.toObjects(MatchResultData::class.java)
                if (list.isEmpty()) getPresetLeaderboard() else list.sortedBy { it.finalRank }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "matchResults")
                emit(getPresetLeaderboard())
            }
    }

    private fun getPresetLeaderboard(): List<MatchResultData> {
        return listOf(
            MatchResultData(
                id = "lr1",
                tournamentId = "daily_1",
                teamName = "Apex Predators BD",
                playerUids = "AP_Tanvir & AP_Rakib",
                placement = 1,
                kills = 19,
                matchPoints = 12,
                totalPoints = 31,
                finalRank = 1
            ),
            MatchResultData(
                id = "lr2",
                tournamentId = "daily_1",
                teamName = "Cyber Ninjas",
                playerUids = "CN_Shanto & CN_Fahim",
                placement = 2,
                kills = 14,
                matchPoints = 9,
                totalPoints = 23,
                finalRank = 2
            ),
            MatchResultData(
                id = "lr3",
                tournamentId = "daily_1",
                teamName = "Viper Strike",
                playerUids = "VS_Hasib & VS_Nabil",
                placement = 3,
                kills = 11,
                matchPoints = 8,
                totalPoints = 19,
                finalRank = 3
            ),
            MatchResultData(
                id = "lr4",
                tournamentId = "daily_1",
                teamName = "Red Dragon Duo",
                playerUids = "RD_Sakib & RD_Joy",
                placement = 4,
                kills = 9,
                matchPoints = 7,
                totalPoints = 16,
                finalRank = 4
            ),
            MatchResultData(
                id = "lr5",
                tournamentId = "daily_1",
                teamName = "Shadow Hunters",
                playerUids = "SH_Rafi & SH_Siam",
                placement = 5,
                kills = 7,
                matchPoints = 6,
                totalPoints = 13,
                finalRank = 5
            )
        )
    }

    suspend fun saveMatchResult(result: MatchResultData) {
        val path = "matchResults"
        val payload = hashMapOf<String, Any>(
            "tournamentId" to result.tournamentId,
            "teamName" to result.teamName,
            "placement" to result.placement,
            "kills" to result.kills,
            "totalPoints" to result.totalPoints,
            "finalRank" to result.finalRank,
            "published" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        result.playerUids?.let { payload["playerUids"] = it }
        payload["matchPoints"] = result.matchPoints

        try {
            db.collection("matchResults").add(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    // ----------------------------------------------------
    // Notifications
    // ----------------------------------------------------
    suspend fun sendNotification(
        toUserId: String,
        title: String,
        message: String,
        type: String
    ) {
        val path = "notifications"
        val payload = hashMapOf<String, Any>(
            "userId" to toUserId,
            "title" to title,
            "message" to message,
            "type" to type,
            "read" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            db.collection("notifications").add(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
        }
    }

    fun observeNotifications(uid: String): Flow<List<NotificationData>> {
        return db.collection("notifications")
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { it.toObjects(NotificationData::class.java) }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "notifications")
                emit(emptyList())
            }
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        val path = "notifications/$notificationId"
        try {
            db.collection("notifications").document(notificationId).update("read", true).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
        }
    }

    // ----------------------------------------------------
    // Admin Management & Reports
    // ----------------------------------------------------
    fun observeAllPublicProfiles(): Flow<List<PublicProfileData>> {
        return db.collection("publicProfiles")
            .snapshots()
            .map { it.toObjects(PublicProfileData::class.java) }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "publicProfiles")
                emit(emptyList())
            }
    }

    fun observeAllPrivateProfiles(): Flow<List<PrivateProfileData>> {
        return db.collection("privateProfiles")
            .snapshots()
            .map { it.toObjects(PrivateProfileData::class.java) }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "privateProfiles")
                emit(emptyList())
            }
    }

    suspend fun approveProfile(userId: String) {
        val path = "publicProfiles/$userId"
        try {
            db.collection("publicProfiles").document(userId).update(
                mapOf("isApproved" to true, "updatedAt" to FieldValue.serverTimestamp())
            ).await()
            sendNotification(
                toUserId = userId,
                title = "Profile Approved!",
                message = "Congratulations! Your gaming profile was verified by administrators.",
                type = "profile_approved"
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun suspendPlayer(userId: String) {
        val path = "users/$userId"
        try {
            db.collection("users").document(userId).update(
                mapOf("status" to "suspended", "updatedAt" to FieldValue.serverTimestamp())
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun createAdminDuoMatch(player1: PublicProfileData, player2: PublicProfileData) {
        val matchPayload = hashMapOf<String, Any>(
            "player1Id" to player1.userId,
            "player2Id" to player2.userId,
            "player1Ign" to player1.ign,
            "player2Ign" to player2.ign,
            "matchSource" to "admin_matched",
            "status" to "active",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            db.collection("duoMatches").add(matchPayload).await()
            db.collection("publicProfiles").document(player1.userId).update(
                mapOf("hasActiveDuo" to true, "activeDuoPartnerId" to player2.userId)
            ).await()
            db.collection("publicProfiles").document(player2.userId).update(
                mapOf("hasActiveDuo" to true, "activeDuoPartnerId" to player1.userId)
            ).await()

            sendNotification(
                toUserId = player1.userId,
                title = "Admin Duo Match Created!",
                message = "An administrator paired you with ${player2.ign} as a Duo!",
                type = "duo_match"
            )
            sendNotification(
                toUserId = player2.userId,
                title = "Admin Duo Match Created!",
                message = "An administrator paired you with ${player1.ign} as a Duo!",
                type = "duo_match"
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "duoMatches")
            throw e
        }
    }

    fun generateCsvExport(profiles: List<PrivateProfileData>, publicProfiles: Map<String, PublicProfileData>): String {
        val sb = StringBuilder()
        sb.append("Player Name,Age,Free Fire UID,IGN,Game Level,Preferred Mode,Experience,Status\n")
        profiles.forEach { p ->
            val pub = publicProfiles[p.userId]
            val ign = pub?.ign ?: "N/A"
            val level = pub?.level?.toString() ?: "N/A"
            val mode = pub?.preferredMode ?: "N/A"
            val exp = pub?.experience ?: "N/A"
            val status = if (pub?.isApproved == true) "Approved" else "Pending"
            sb.append("\"${p.fullName}\",${p.age},\"${p.fullUid}\",\"$ign\",$level,\"$mode\",\"$exp\",$status\n")
        }
        return sb.toString()
    }
}
