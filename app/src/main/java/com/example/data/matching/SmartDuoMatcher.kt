package com.example.data.matching

import com.example.data.model.PublicProfileData

data class CompatibilityResult(
    val score: Int,
    val highlights: List<String>,
    val synergyTag: String
)

object SmartDuoMatcher {

    fun calculate(user: PublicProfileData, candidate: PublicProfileData): CompatibilityResult {
        var score = 0
        val highlights = mutableListOf<String>()

        // 1. Game Mode Alignment (30 pts max)
        if (user.preferredMode.equals(candidate.preferredMode, ignoreCase = true)) {
            score += 30
            highlights.add("Same Game Mode: ${user.preferredMode}")
        } else {
            score += 10
        }

        // 2. Playstyle Synergy (25 pts max)
        val style1 = user.playingStyle.lowercase()
        val style2 = candidate.playingStyle.lowercase()
        val isIdealSynergy = (style1 == "rusher" && style2 == "sniper") ||
                             (style1 == "sniper" && style2 == "rusher") ||
                             (style1 == "igl" && style2 == "rusher") ||
                             (style1 == "rusher" && style2 == "igl") ||
                             (style1 == "support" && style2 == "rusher") ||
                             (style1 == "rusher" && style2 == "support")

        if (isIdealSynergy) {
            score += 25
            highlights.add("Ideal Combat Synergy: ${user.playingStyle} + ${candidate.playingStyle}")
        } else if (style1 == style2) {
            score += 18
            highlights.add("Twin Playstyle: Dual ${user.playingStyle}")
        } else {
            score += 12
            highlights.add("Adaptive Playstyle")
        }

        // 3. Schedule Compatibility (25 pts max)
        val sched1 = user.playingSchedule.lowercase()
        val sched2 = candidate.playingSchedule.lowercase()
        if (sched1 == sched2) {
            score += 25
            highlights.add("Matching Schedule: ${user.playingSchedule}")
        } else if (sched1 == "any time" || sched2 == "any time") {
            score += 22
            highlights.add("Flexible Schedule Match")
        } else {
            score += 8
        }

        // 4. Experience Tier (15 pts max)
        val exp1 = user.experience.lowercase()
        val exp2 = candidate.experience.lowercase()
        if (exp1 == exp2) {
            score += 15
            highlights.add("Matched Experience: ${user.experience}")
        } else if ((exp1.contains("pro") && exp2.contains("advanced")) ||
                   (exp1.contains("advanced") && exp2.contains("pro")) ||
                   (exp1.contains("intermediate") && exp2.contains("advanced")) ||
                   (exp1.contains("advanced") && exp2.contains("intermediate"))) {
            score += 10
            highlights.add("Complementary Experience")
        } else {
            score += 5
        }

        // 5. Language (5 pts max)
        if (user.language.equals(candidate.language, ignoreCase = true)) {
            score += 5
            highlights.add("Shared Communication: ${user.language}")
        } else {
            score += 2
        }

        val finalScore = score.coerceIn(40, 99)

        val synergyTag = when {
            finalScore >= 90 -> "GOD DUO SYNERGY"
            finalScore >= 80 -> "HIGH COMPATIBILITY"
            finalScore >= 70 -> "BALANCED DUO"
            else -> "POTENTIAL PARTNER"
        }

        return CompatibilityResult(
            score = finalScore,
            highlights = highlights,
            synergyTag = synergyTag
        )
    }
}
