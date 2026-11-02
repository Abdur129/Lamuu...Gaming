package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserData(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val role: String = "player", // player, moderator, admin, superadmin
    val status: String = "active", // active, suspended
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PrivateProfileData(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val age: Int = 18,
    val email: String? = null,
    val phone: String? = null,
    val fullUid: String = "",
    val gender: String? = null,
    val photoUrl: String? = null,
    val screenshotUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PublicProfileData(
    @DocumentId val id: String = "",
    val userId: String = "",
    val ign: String = "",
    val level: Int = 50,
    val preferredMode: String = "BR Ranked", // BR Ranked, CS Ranked, Lone Wolf
    val experience: String = "Advanced", // Beginner, Intermediate, Advanced, Esports Pro
    val playingStyle: String = "Rusher", // Rusher, Sniper, Support, IGL, Flanker
    val playingSchedule: String = "Night", // Morning, Afternoon, Evening, Night, Any Time
    val language: String = "Bengali", // Bengali, English, Mixed
    val bio: String? = null,
    val avatar: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val idealPartnerDesc: String? = null,
    val partnerAgeRange: String? = null,
    val partnerStyle: String? = null,
    val isAvailableForDuo: Boolean = true,
    val hasActiveDuo: Boolean = false,
    val activeDuoPartnerId: String? = null,
    val isApproved: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DuoRequestData(
    @DocumentId val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val fromIgn: String = "",
    val toIgn: String = "",
    val status: String = "pending", // pending, accepted, rejected, cancelled
    val compatibilityScore: Int = 85,
    val message: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DuoMatchData(
    @DocumentId val id: String = "",
    val player1Id: String = "",
    val player2Id: String = "",
    val player1Ign: String = "",
    val player2Ign: String = "",
    val matchSource: String = "mutual_request", // mutual_request, admin_matched
    val status: String = "active", // active, unmatched
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TournamentData(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String? = null,
    val type: String = "daily", // daily, weekly, monthly, special
    val gameMode: String = "Battle Royale", // Battle Royale, Clash Squad
    val format: String = "duo", // solo, duo, squad
    val maxParticipants: Int = 48,
    val currentParticipants: Int = 0,
    val entryFee: String? = "Free", // Free or e.g. "৳50"
    val prizePool: String? = "৳5,000",
    val rules: String? = null,
    val scheduleTime: String? = null,
    val status: String = "registration_open", // upcoming, registration_open, registration_closed, ongoing, completed, cancelled
    val roomId: String? = null,
    val roomPassword: String? = null,
    val roomDetailsVisible: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TournamentRegistrationData(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val userId: String = "",
    val teamName: String = "",
    val playerIgn: String = "",
    val partnerIgn: String? = null,
    val partnerUserId: String? = null,
    val status: String = "pending", // pending, approved, rejected
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MatchResultData(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val teamName: String = "",
    val playerUids: String? = null,
    val placement: Int = 1,
    val kills: Int = 0,
    val matchPoints: Int = 0,
    val totalPoints: Int = 0,
    val finalRank: Int = 1,
    val published: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class NotificationData(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "general", // duo_request, duo_match, tournament, profile_approved, system
    val read: Boolean = false,
    val actionUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ReportData(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val reportedUserId: String = "",
    val reason: String = "",
    val details: String? = null,
    val status: String = "open", // open, investigating, resolved, dismissed
    val adminNotes: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
