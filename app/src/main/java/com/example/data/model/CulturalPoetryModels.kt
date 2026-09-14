package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

/**
 * 1. Kalam-e-Ustaad AI Personas (Poetic Mentors)
 */
enum class UstaadId(val id: String) {
    GHALIB("ghalib"),
    FAIZ("faiz"),
    KABIR("kabir"),
    BHANJA("bhanja"),
    MEER("meer"),
    RAHAT("rahat")
}

data class UstaadPersona(
    val id: UstaadId,
    val name: String,
    val nativeName: String,
    val eraAndOrigin: String,
    val title: String,
    val emblem: String,
    val signatureQuote: String,
    val signatureStyle: String,
    val greetingSalutation: String,
    val promptSystemPersona: String
)

data class WordIslah(
    val originalWord: String,
    val suggestedWord: String,
    val poeticReason: String
)

data class UstaadCritiqueResult(
    val persona: UstaadPersona,
    val greeting: String,
    val critique: String,
    val revisedCouplet: String,
    val wordReplacements: List<WordIslah>,
    val meterComment: String,
    val encouragement: String
)

/**
 * 2. Tarannum (Melodic Ghazal Singing Modes & Ragas)
 */
enum class RagaTarannum(
    val ragaName: String,
    val hindiName: String,
    val timeOfDay: String,
    val mood: String,
    val emoji: String,
    val rootPitchHz: Double,
    val scaleSwaras: List<String>,
    val scaleFrequencies: List<Double>
) {
    BHAIRAVI(
        ragaName = "Raag Bhairavi",
        hindiName = "राग भैरवी",
        timeOfDay = "Dawn & Concluding Ghazal",
        mood = "Pathos, Devotion & Bittersweet Farewell",
        emoji = "🌅",
        rootPitchHz = 146.83, // D3
        scaleSwaras = listOf("Sa", "re (komal)", "ga (komal)", "ma", "Pa", "dha (komal)", "ni (komal)", "Sa'"),
        scaleFrequencies = listOf(146.83, 155.56, 174.61, 196.00, 220.00, 233.08, 261.63, 293.66)
    ),
    YAMAN(
        ragaName = "Raag Yaman",
        hindiName = "राग यमन",
        timeOfDay = "Evening Twilight",
        mood = "Romantic Sereneness & Serene Devotion",
        emoji = "🌆",
        rootPitchHz = 138.59, // C#3
        scaleSwaras = listOf("Ni'", "Re", "Ga", "Ma (teevra)", "Dha", "Ni", "Sa'"),
        scaleFrequencies = listOf(130.81, 155.56, 174.61, 196.00, 233.08, 261.63, 277.18)
    ),
    DARBARI(
        ragaName = "Raag Darbari",
        hindiName = "राग दरबारी कान्हड़ा",
        timeOfDay = "Deep Midnight",
        mood = "Regal Gravity, Profound Solitude & Majestic Grief",
        emoji = "🌌",
        rootPitchHz = 130.81, // C3
        scaleSwaras = listOf("Sa", "Re", "ga (komal)", "ma", "Pa", "dha (komal)", "ni (komal)"),
        scaleFrequencies = listOf(130.81, 146.83, 155.56, 174.61, 196.00, 207.65, 233.08)
    ),
    KAFI(
        ragaName = "Raag Kafi",
        hindiName = "राग काफ़ी",
        timeOfDay = "Monsoon & Spring Afternoon",
        mood = "Folk Romance, Earthy Rain & Playful Longing",
        emoji = "🌧️",
        rootPitchHz = 146.83, // D3
        scaleSwaras = listOf("Sa", "Re", "ga (komal)", "ma", "Pa", "Dha", "ni (komal)"),
        scaleFrequencies = listOf(146.83, 164.81, 174.61, 196.00, 220.00, 246.94, 261.63)
    )
}

/**
 * 3. Virtual Mehfil
 */
enum class MehfilReactionType(
    val title: String,
    val arabicUrdu: String,
    val iconEmoji: String,
    val meaning: String
) {
    WAH_WAH("Wah Wah!", "واہ واہ!", "🌹", "Superb lyrical beauty!"),
    MUKARRAR("Mukarrar Irshad!", "مکرر ارشاد!", "🔄", "Encore! Recite once more!"),
    SUBHANALLAH("Subhanallah!", "سبحان اللہ!", "✨", "Praise be to the Divine spark!"),
    KHOOB("Khoob!", "خوب!", "👏", "Splendid craftsmanship!")
}

data class MehfilAttendee(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val city: String
)

data class MehfilComment(
    val id: String,
    val senderName: String,
    val avatarEmoji: String,
    val text: String,
    val reactionType: MehfilReactionType? = null,
    val timestampMs: Long = System.currentTimeMillis()
)

/**
 * 4. Lafz-o-Maani (Poetic Etymology & Word Tree)
 */
data class PoeticWordDefinition(
    val wordLatin: String,
    val wordUrdu: String,
    val wordHindi: String,
    val wordOdia: String = "",
    val originLanguage: String, // "Persian", "Arabic", "Sanskrit", "Braj", "Odia"
    val literalMeaning: String,
    val poeticNuance: String,
    val rootDerivation: String,
    val rhymingCompanions: List<String>, // Ham-Qafiya
    val exampleCouplet: String,
    val exampleAuthor: String,
    val categoryTag: String // "Love", "Mysticism", "Grief", "Aesthetics"
)

/**
 * 5. Poet's Diwan (PDF Publisher Studio)
 */
enum class DiwanCoverStyle(
    val displayName: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val description: String
) {
    MUGHAL_PARCHMENT("Mughal Antique Parchment", 0xFFF7F2E7, 0xFFC9A050, "Gold filigree borders on aged handmade vellum"),
    ROYAL_VELVET("Royal Velvet & Gold", 0xFF2A1017, 0xFFE0BB68, "Deep regal maroon with lustrous gold leaf"),
    MIDNIGHT_INDIGO("Midnight Indigo & Silver", 0xFF0D1B2A, 0xFFE0E1DD, "Starlit deep sapphire with pure moonlight silver"),
    ODIA_PALM_LEAF("Utkala Tala Patra", 0xFF3D2E1E, 0xFFE8C88B, "Warm ancient palm-leaf engraving with Pattachitra motifs")
}

data class DiwanConfig(
    val bookTitle: String,
    val subtitle: String,
    val poetName: String,
    val takhallus: String,
    val dedication: String,
    val coverStyle: DiwanCoverStyle,
    val selectedShayariIds: Set<String>,
    val includeTranslations: Boolean = true,
    val includeTakhallusSeal: Boolean = true
)

/**
 * 6. Calligraphy Studio
 */
enum class CalligraphyInk(
    val displayName: String,
    val color: Color,
    val description: String
) {
    ZAFRAN_GOLD("Za'fran Gold", Color(0xFFE6C280), "Radiant saffron shimmer"),
    KOHL_NOIR("Kohl Black", Color(0xFF18181B), "Pure carbon lampblack ink"),
    ZAMARRUD_EMERALD("Zamarrud Green", Color(0xFF1B4D3E), "Lush Mughal court emerald"),
    LAPIS_INDIGO("Lapis Indigo", Color(0xFF1E3F66), "Deep ancient celestial blue"),
    VELVET_CRIMSON("Gulabi Velvet", Color(0xFF9E2A2B), "Passionate rose madder")
}

enum class CalligraphyTexture(
    val displayName: String,
    val bgColor: Color,
    val description: String
) {
    ANTIQUE_PARCHMENT("Aged Parchment", Color(0xFFF7F2E7), "Warm deckled vellum with subtle tooth"),
    TALA_PATRA("Palm Leaf (ତାଳପତ୍ର)", Color(0xFFEAD8B7), "Etched natural palm fiber warmth"),
    MIDNIGHT_CANVAS("Midnight Velvet", Color(0xFF0F172A), "Dark obsidian night sky for golden strokes")
}
