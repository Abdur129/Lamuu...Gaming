package com.example.ui.localization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class LanguageCode {
    BN, EN
}

object LocalizationState {
    var currentLanguage by mutableStateOf(LanguageCode.BN)

    fun toggle() {
        currentLanguage = if (currentLanguage == LanguageCode.BN) LanguageCode.EN else LanguageCode.BN
    }
}

object Strings {
    val appTitle: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "DUO CONNECT" else "DUO CONNECT"

    val tagline: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN)
            "Find Your Duo. Build Your Team. Win Together."
        else
            "Find Your Duo. Build Your Team. Win Together."

    val heroHeadline: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN)
            "আপনার নিখুঁত Duo পার্টনার খুঁজুন"
        else
            "FIND YOUR PERFECT DUO"

    val heroSubtitle: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN)
            "একজন অভিজ্ঞ Duo Partner খুঁজে নাও, টিম তৈরি করো এবং অংশগ্রহণ করো প্রতিদিনের Esports Tournament-এ।"
        else
            "Connect with verified Free Fire players, team up, and dominate daily, weekly & monthly tournaments."

    val createProfile: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "প্রোফাইল তৈরি করুন" else "Create Profile"

    val exploreTournaments: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "টুর্নামেন্ট দেখুন" else "Explore Tournaments"

    val findDuo: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "Duo খুঁজুন" else "Find Duo"

    val myDuo: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "আমার Duo" else "My Duo"

    val tournaments: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "টুর্নামেন্ট" else "Tournaments"

    val leaderboard: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "লিডারবোর্ড" else "Leaderboard"

    val adminPanel: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "অ্যাডমিন প্যানেল" else "Admin Panel"

    val myProfile: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "আমার প্রোফাইল" else "My Profile"

    val notifications: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "বিজ্ঞপ্তি" else "Notifications"

    val settings: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "সেটিংস" else "Settings"

    val signInGoogle: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "গুগল দিয়ে সাইন ইন করুন" else "Sign in with Google"

    val signOut: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "লগ আউট" else "Sign Out"

    val compatibility: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "কম্প্যাটিবিলিটি" else "Compatibility"

    val sendRequest: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "Duo রিকোয়েস্ট পাঠান" else "Send Duo Request"

    val requestSent: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "রিকোয়েস্ট পাঠানো হয়েছে" else "Request Sent"

    val accept: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "গ্রহণ করুন" else "Accept"

    val reject: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "প্রত্যাখ্যান করুন" else "Reject"

    val cancel: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "বাতিল" else "Cancel"

    val unmatch: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "আনম্যাচ করুন" else "Unmatch"

    val registerNow: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "নিবন্ধন করুন" else "Register Now"

    val roomId: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "রুম আইডি" else "Room ID"

    val roomPassword: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "পাসওয়ার্ড" else "Password"

    val pendingApproval: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "অনুমোদনের অপেক্ষায়" else "Pending Approval"

    val approved: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "অনুমোদিত" else "Approved"

    val activeDuo: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "অ্যাক্টিভ Duo" else "Active Duo"

    val noActiveDuo: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "কোনো অ্যাক্টিভ Duo নেই" else "No Active Duo"

    val exportCsv: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN) "CSV এক্সপোর্ট করুন" else "Export CSV"

    val privacyNotice: String
        get() = if (LocalizationState.currentLanguage == LanguageCode.BN)
            "আপনার ব্যক্তিগত তথ্য (নাম, UID, ফোন) ১০০% নিরাপদ ও সুরক্ষিত।"
        else
            "Your private data (Name, UID, Phone) is encrypted and 100% confidential."
}
