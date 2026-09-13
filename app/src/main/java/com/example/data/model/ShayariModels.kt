package com.example.data.model

import com.squareup.moshi.JsonClass

enum class Language(val displayName: String, val code: String, val scriptSample: String) {
    ALL("All", "all", "✨"),
    HINDI("हिंदी", "hindi", "शायरी"),
    ODIA("ଓଡ଼ିଆ", "odia", "କବିତା"),
    ENGLISH("English", "english", "Poetry");

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ALL
    }
}

enum class Emotion(
    val englishLabel: String,
    val hindiLabel: String,
    val odiaLabel: String,
    val code: String,
    val emoji: String
) {
    ISHQ("Love", "इश्क़", "ପ୍ରେମ", "ishq", "❤️"),
    DARD("Heartbreak", "दर्द", "ବିରହ", "dard", "🥀"),
    HAUSLA("Courage", "हौसला", "ପ୍ରେରଣା", "hausla", "🦅"),
    DOSTI("Friendship", "दोस्ती", "ମିତ୍ରତା", "dosti", "🤝"),
    SUFI("Mystic", "रूहानी", "ଆଧ୍ୟାତ୍ମିକ", "sufi", "🕊️"),
    YAADEIN("Nostalgia", "यादें", "ସ୍ମୃତି", "yaadein", "🌙");

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.HINDI -> "$hindiLabel $emoji"
        Language.ODIA -> "$odiaLabel $emoji"
        else -> "$englishLabel $emoji"
    }

    companion object {
        fun fromCode(code: String): Emotion =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ISHQ
    }
}

@JsonClass(generateAdapter = true)
data class Shayari(
    val id: String,
    val lines: String,
    val author: String,
    val penName: String = "",
    val language: String, // "hindi", "odia", "english"
    val emotion: String, // "ishq", "dard", etc.
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val translationEnglish: String = "",
    val translationHindi: String = "",
    val translationOdia: String = "",
    val poeticAnalysis: String = "",
    val isTrending: Boolean = false,
    val isDailyPick: Boolean = false,
    val cardBackgroundUrl: String? = null,
    val isDownloaded: Boolean = false,
    val downloadedAt: Long? = null,
    val moderationStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED", "FLAGGED"
    val moderationReason: String? = null,
    val moderationSeverity: String = "SAFE", // "SAFE", "SUSPICIOUS", "HIGH_RISK"
    val moderatedAt: Long? = null
)

enum class ModerationStatus(val displayName: String, val code: String) {
    PENDING("Pending Review", "PENDING"),
    APPROVED("Approved", "APPROVED"),
    REJECTED("Rejected", "REJECTED"),
    FLAGGED("Flagged by Auto-Filter", "FLAGGED")
}

enum class ModerationSeverity(val label: String, val level: Int) {
    SAFE("Clean / Safe", 0),
    SUSPICIOUS("Potential Risk", 1),
    HIGH_RISK("High Risk Violation", 2)
}

data class ModerationCheckResult(
    val isFlagged: Boolean,
    val severity: ModerationSeverity,
    val suggestedStatus: String,
    val detectedIssues: List<String>,
    val riskScore: Float,
    val summary: String
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    val uid: String,
    val displayName: String,
    val penName: String = "Shayar",
    val bio: String = "Words carrying the weight of my heart.",
    val photoUrl: String? = null,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val authProvider: String = "anonymous", // "password", "google", "anonymous"
    val preferredLanguage: String = "hindi",
    val favoriteEmotion: String = "ishq",
    val isGuest: Boolean = true,
    val followers: Int = 124,
    val following: Int = 48,
    val shayariCount: Int = 0,
    val streakDays: Int = 5
)

enum class ActivityType(val emoji: String, val label: String) {
    COMPOSE("✍️", "AI Composition"),
    SAVED("🔖", "Bookmarked"),
    UNSAVED("🗑️", "Removed Bookmark"),
    LIKED("❤️", "Liked Couplet"),
    RECITED("🎧", "Recited Audio"),
    CARD_EXPORT("🎨", "Card Created"),
    PROMPT_USED("💡", "Prompt Inspiration"),
    LOGIN("🔑", "Authentication"),
    STREAK("🔥", "Daily Streak")
}

data class UserActivityItem(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PoeticAnalysisResult(
    val behrMeter: String = "",
    val radif: String = "",
    val qaafiya: String = "",
    val takhallus: String = "",
    val emotionalWeight: String = "",
    val literaryCommentary: String = ""
)
