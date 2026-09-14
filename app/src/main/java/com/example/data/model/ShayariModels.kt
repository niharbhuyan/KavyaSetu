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
    val emoji: String,
    val subtitle: String = ""
) {
    ISHQ("Love", "इश्क़", "ପ୍ରେମ", "ishq", "❤️", "Romance & Devotion"),
    DARD("Sad", "दर्द", "ବିରହ", "dard", "🥀", "Heartbreak & Melancholy"),
    HAUSLA("Inspirational", "हौसला", "ପ୍ରେରଣା", "hausla", "🦅", "Courage & Resilience"),
    DOSTI("Friendship", "दोस्ती", "ମିତ୍ରତା", "dosti", "🤝", "Bonds & Camaraderie"),
    SUFI("Mystic", "रूहानी", "ଆଧ୍ୟାତ୍ମିକ", "sufi", "🕊️", "Spiritual Peace"),
    YAADEIN("Nostalgia", "यादें", "ସ୍ମୃତି", "yaadein", "🌙", "Memories & Solitude");

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.HINDI -> "$hindiLabel $emoji"
        Language.ODIA -> "$odiaLabel $emoji"
        else -> "$englishLabel $emoji"
    }

    companion object {
        fun fromCode(code: String): Emotion {
            val normalized = code.lowercase().trim()
            return when (normalized) {
                "ishq", "love", "romance", "prem" -> ISHQ
                "dard", "sad", "heartbreak", "gam", "biraha" -> DARD
                "hausla", "inspirational", "inspiration", "courage", "motivation", "prerana" -> HAUSLA
                "dosti", "friendship", "yaari", "mitrata" -> DOSTI
                "sufi", "mystic", "spiritual", "roohani", "adhyatmika" -> SUFI
                "yaadein", "nostalgia", "memories", "yaad", "smruti" -> YAADEIN
                else -> entries.find {
                    it.code.equals(code, ignoreCase = true) ||
                    it.englishLabel.equals(code, ignoreCase = true)
                } ?: ISHQ
            }
        }
    }
}

enum class PoemCategory(
    val id: String,
    val displayName: String,
    val hindiName: String,
    val odiaName: String,
    val emoji: String,
    val description: String,
    val defaultTags: List<String>
) {
    ALL(
        id = "all",
        displayName = "All Categories",
        hindiName = "सभी श्रेणियाँ",
        odiaName = "ସମସ୍ତ ବର୍ଗ",
        emoji = "✨",
        description = "All poems across every category",
        defaultTags = emptyList()
    ),
    LOVE(
        id = "love",
        displayName = "Love",
        hindiName = "इश्क़ / प्रेम",
        odiaName = "ପ୍ରେମ",
        emoji = "❤️",
        description = "Romance, affection, deep passion, longing & devotion",
        defaultTags = listOf("love", "romance", "ishq", "prem", "heart", "longing")
    ),
    NATURE(
        id = "nature",
        displayName = "Nature",
        hindiName = "प्रकृति / कुदरत",
        odiaName = "ପ୍ରକୃତି",
        emoji = "🍃",
        description = "Rain, seasons, spring, rivers, sky, flowers & dawn",
        defaultTags = listOf("nature", "rain", "kudrat", "monsoon", "prakriti", "flowers", "breeze")
    ),
    SORROW(
        id = "sorrow",
        displayName = "Sorrow",
        hindiName = "दर्द / विरह",
        odiaName = "ବିରହ / ଦୁଃଖ",
        emoji = "🥀",
        description = "Heartbreak, tears, grief, melancholy & separation",
        defaultTags = listOf("sorrow", "dard", "heartbreak", "separation", "tears", "virah", "grief")
    ),
    INSPIRATION(
        id = "inspiration",
        displayName = "Inspiration",
        hindiName = "हौसला / प्रेरणा",
        odiaName = "ପ୍ରେରଣା",
        emoji = "🦅",
        description = "Courage, perseverance, resilience, fearless spirit & hope",
        defaultTags = listOf("inspiration", "hausla", "courage", "strength", "hope", "fire", "prerana")
    ),
    MYSTIC(
        id = "mystic",
        displayName = "Mystic",
        hindiName = "रूहानी / सूफ़ी",
        odiaName = "ଆଧ୍ୟାତ୍ମିକ",
        emoji = "🕊️",
        description = "Sufi devotion, spiritual bliss, transcendence & divine peace",
        defaultTags = listOf("mystic", "sufi", "spiritual", "peace", "divine", "soul", "roohani")
    ),
    FRIENDSHIP(
        id = "friendship",
        displayName = "Friendship",
        hindiName = "दोस्ती / यारी",
        odiaName = "ମିତ୍ରତା",
        emoji = "🤝",
        description = "Companionship, loyal camaraderie & shared paths",
        defaultTags = listOf("friendship", "dosti", "yaari", "bonds", "companionship", "mitrata")
    ),
    PHILOSOPHY(
        id = "philosophy",
        displayName = "Philosophy",
        hindiName = "फ़लसफ़ा / ज़िंदगी",
        odiaName = "ଦର୍ଶନ",
        emoji = "📜",
        description = "Existential reflections, destiny, time & life wisdom",
        defaultTags = listOf("philosophy", "life", "zindagi", "wisdom", "time", "darshan")
    );

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.HINDI -> "$hindiLabel $emoji"
        Language.ODIA -> "$odiaName $emoji"
        else -> "$displayName $emoji"
    }

    val hindiLabel: String get() = hindiName

    companion object {
        fun fromId(id: String?): PoemCategory {
            if (id.isNullOrBlank()) return LOVE
            val lower = id.trim().lowercase()
            return entries.find {
                it.id.equals(lower, ignoreCase = true) ||
                it.displayName.equals(lower, ignoreCase = true) ||
                it.name.equals(lower, ignoreCase = true)
            } ?: fallbackFromKeyword(lower)
        }

        fun fallbackFromKeyword(keyword: String): PoemCategory {
            val k = keyword.lowercase()
            return when {
                k.contains("nature") || k.contains("prakriti") || k.contains("kudrat") || k.contains("rain") || k.contains("spring") || k.contains("monsoon") || k.contains("barsha") || k.contains("flower") -> NATURE
                k.contains("sorrow") || k.contains("sad") || k.contains("dard") || k.contains("virah") || k.contains("biraha") || k.contains("gam") || k.contains("tear") || k.contains("grief") || k.contains("dukh") -> SORROW
                k.contains("inspire") || k.contains("inspiration") || k.contains("hausla") || k.contains("prerana") || k.contains("courage") || k.contains("motivation") || k.contains("hope") || k.contains("fire") -> INSPIRATION
                k.contains("love") || k.contains("ishq") || k.contains("prem") || k.contains("romance") || k.contains("dil") || k.contains("pyar") -> LOVE
                k.contains("mystic") || k.contains("sufi") || k.contains("spiritual") || k.contains("roohani") || k.contains("bhakti") -> MYSTIC
                k.contains("friend") || k.contains("dosti") || k.contains("yaari") || k.contains("mitrata") -> FRIENDSHIP
                k.contains("philosophy") || k.contains("life") || k.contains("zindagi") || k.contains("darshan") || k.contains("waqt") -> PHILOSOPHY
                else -> LOVE
            }
        }
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
    val category: String = "love", // "love", "nature", "sorrow", "inspiration", "mystic", "friendship", "philosophy"
    val tags: String = "", // comma-separated custom tags e.g. "nature, rain, monsoon"
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
) {
    fun getCategoryEnum(): PoemCategory = PoemCategory.fromId(category)

    fun getAllTags(): List<String> {
        val list = mutableListOf<String>()
        val cat = getCategoryEnum()
        if (cat != PoemCategory.ALL) {
            list.add(cat.displayName.lowercase())
        }
        if (tags.isNotBlank()) {
            tags.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }.forEach {
                if (!list.contains(it)) list.add(it)
            }
        }
        return list
    }

    fun matchesCategory(categoryFilter: PoemCategory): Boolean {
        if (categoryFilter == PoemCategory.ALL) return true
        return getCategoryEnum() == categoryFilter || getAllTags().any { it.equals(categoryFilter.id, ignoreCase = true) || it.equals(categoryFilter.displayName, ignoreCase = true) }
    }

    fun matchesTag(tagQuery: String): Boolean {
        if (tagQuery.isBlank()) return true
        val query = tagQuery.trim().lowercase().removePrefix("#")
        return getCategoryEnum().id.contains(query, ignoreCase = true) ||
               getCategoryEnum().displayName.contains(query, ignoreCase = true) ||
               getAllTags().any { it.contains(query, ignoreCase = true) } ||
               lines.contains(query, ignoreCase = true)
    }
}

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

@JsonClass(generateAdapter = true)
data class Anthology(
    val id: String,
    val title: String,
    val description: String,
    val icon: String = "📚",
    val shayariIds: Set<String> = emptySet(),
    val createdAt: Long = System.currentTimeMillis()
)

