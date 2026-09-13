package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class PromptMood(
    val id: String,
    val displayName: String,
    val hindiLabel: String,
    val odiaLabel: String,
    val iconEmoji: String,
    val color: Color,
    val emotionCode: String
) {
    LOVE(
        id = "love",
        displayName = "Love & Romance",
        hindiLabel = "इश्क़",
        odiaLabel = "ପ୍ରେମ",
        iconEmoji = "❤️",
        color = Color(0xFFE91E63),
        emotionCode = "ishq"
    ),
    SAD(
        id = "sad",
        displayName = "Sadness & Heartbreak",
        hindiLabel = "दर्द",
        odiaLabel = "ବିରହ",
        iconEmoji = "🥀",
        color = Color(0xFF5C6BC0),
        emotionCode = "dard"
    ),
    INSPIRATIONAL(
        id = "inspirational",
        displayName = "Courage & Inspiration",
        hindiLabel = "हौसला",
        odiaLabel = "ପ୍ରେରଣା",
        iconEmoji = "🦅",
        color = Color(0xFFFF8F00),
        emotionCode = "hausla"
    ),
    FRIENDSHIP(
        id = "friendship",
        displayName = "Friendship & Bonds",
        hindiLabel = "दोस्ती",
        odiaLabel = "ମିତ୍ରତା",
        iconEmoji = "🤝",
        color = Color(0xFF2E7D32),
        emotionCode = "dosti"
    ),
    MYSTIC(
        id = "mystic",
        displayName = "Mystic & Sufi",
        hindiLabel = "रूहानी",
        odiaLabel = "ଆଧ୍ୟାତ୍ମିକ",
        iconEmoji = "🕊️",
        color = Color(0xFF8E24AA),
        emotionCode = "sufi"
    ),
    NOSTALGIA(
        id = "nostalgia",
        displayName = "Nostalgia & Memories",
        hindiLabel = "यादें",
        odiaLabel = "ସ୍ମୃତି",
        iconEmoji = "🌙",
        color = Color(0xFF00838F),
        emotionCode = "yaadein"
    );

    companion object {
        fun fromId(id: String): PromptMood =
            entries.find { it.id.equals(id, ignoreCase = true) } ?: LOVE
    }
}

data class ShayariPrompt(
    val id: String,
    val title: String,
    val promptText: String,
    val mood: PromptMood,
    val emotionCode: String,
    val poeticCadenceHint: String,
    val tags: List<String>
)

object PromptLibraryData {
    val prompts: List<ShayariPrompt> = listOf(
        // LOVE & ROMANCE
        ShayariPrompt(
            id = "love_1",
            title = "Monsoon & Unspoken Longing",
            promptText = "First monsoon raindrops dancing on cold window glass while silently awaiting the beloved's footsteps.",
            mood = PromptMood.LOVE,
            emotionCode = "ishq",
            poeticCadenceHint = "Imagery of rain, heartbeat, and tender anticipation",
            tags = listOf("Rain", "Waiting", "Tender")
        ),
        ShayariPrompt(
            id = "love_2",
            title = "Eyes that Speak Silent Vows",
            promptText = "Two gazes meeting across a crowded hall, conversing in unspoken vows that spoken words could never capture.",
            mood = PromptMood.LOVE,
            emotionCode = "ishq",
            poeticCadenceHint = "Soft gaze, silence louder than whispers, destiny",
            tags = listOf("Eyes", "Crowd", "Silence")
        ),
        ShayariPrompt(
            id = "love_3",
            title = "Midnight Terrace Stargazing",
            promptText = "Stargazing on an endless terrace under the moon, whispering your name to the night breeze with every breath.",
            mood = PromptMood.LOVE,
            emotionCode = "ishq",
            poeticCadenceHint = "Moonlight, nocturnal calm, sweet devotion",
            tags = listOf("Moonlight", "Night", "Devotion")
        ),
        ShayariPrompt(
            id = "love_4",
            title = "A Smile that Rebuilds the Soul",
            promptText = "How a single fleeting, shy smile can rebuild an entire ruined city inside a weary and wandering heart.",
            mood = PromptMood.LOVE,
            emotionCode = "ishq",
            poeticCadenceHint = "Beauty, warmth, miraculous healing through love",
            tags = listOf("Smile", "Healing", "Beauty")
        ),
        ShayariPrompt(
            id = "love_5",
            title = "Sacred Devotion",
            promptText = "Loving someone not for what they offer, but because their quiet presence turns every moment into a sacred sanctuary.",
            mood = PromptMood.LOVE,
            emotionCode = "ishq",
            poeticCadenceHint = "Spiritual love, surrender, profound peace",
            tags = listOf("Soul", "Sanctuary", "Pure")
        ),

        // SADNESS & HEARTBREAK
        ShayariPrompt(
            id = "sad_1",
            title = "Cold Chai & Fading Echoes",
            promptText = "An untouched cup of cold tea at our regular table, with phantom echoes of laughter that will never return.",
            mood = PromptMood.SAD,
            emotionCode = "dard",
            poeticCadenceHint = "Tea stall, solitude, haunting memory",
            tags = listOf("Chai", "Empty", "Parting")
        ),
        ShayariPrompt(
            id = "sad_2",
            title = "Letters Never Posted",
            promptText = "Unsent letters written in raw ink late at night, preserved in dusty notebooks for someone who is now a stranger.",
            mood = PromptMood.SAD,
            emotionCode = "dard",
            poeticCadenceHint = "Ink, distance, unspoken grief",
            tags = listOf("Letters", "Secrets", "Distance")
        ),
        ShayariPrompt(
            id = "sad_3",
            title = "Golden Leaves of Autumn",
            promptText = "Watching dried autumn leaves fall one by one in silence, learning that profound departures often make no sound.",
            mood = PromptMood.SAD,
            emotionCode = "dard",
            poeticCadenceHint = "Autumn wind, silent farewells, passing time",
            tags = listOf("Autumn", "Leaves", "Silence")
        ),
        ShayariPrompt(
            id = "sad_4",
            title = "Smiles for the World, Solitude in the Dark",
            promptText = "Laughing freely for the world outside while keeping a weeping, tender soul locked behind closed doors at night.",
            mood = PromptMood.SAD,
            emotionCode = "dard",
            poeticCadenceHint = "Mask of joy, midnight truth, quiet fortitude",
            tags = listOf("Mask", "Night", "Pain")
        ),
        ShayariPrompt(
            id = "sad_5",
            title = "Footsteps in Desert Sands",
            promptText = "Promises once tall as mountains vanishing like morning haze, leaving lonely footprints fading in the desert wind.",
            mood = PromptMood.SAD,
            emotionCode = "dard",
            poeticCadenceHint = "Desert, fleeting vows, solitude",
            tags = listOf("Desert", "Promises", "Vanishing")
        ),

        // INSPIRATIONAL & COURAGE
        ShayariPrompt(
            id = "inspire_1",
            title = "Falcon Against the Tempest",
            promptText = "Spreading defiant wings into thunder and gale, proving that iron cages cannot trap a spirit born to conquer skies.",
            mood = PromptMood.INSPIRATIONAL,
            emotionCode = "hausla",
            poeticCadenceHint = "Eagle flight, thunder, unstoppable ambition",
            tags = listOf("Wings", "Thunder", "Ambition")
        ),
        ShayariPrompt(
            id = "inspire_2",
            title = "The Solitary Clay Lamp",
            promptText = "A fragile earthen lamp standing firm on a stormy threshold, fearlessly challenging the vast kingdom of dark night.",
            mood = PromptMood.INSPIRATIONAL,
            emotionCode = "hausla",
            poeticCadenceHint = "Clay lamp (diya), resilience, light over darkness",
            tags = listOf("Diya", "Courage", "Hope")
        ),
        ShayariPrompt(
            id = "inspire_3",
            title = "Ashes Forging Diamonds",
            promptText = "Transforming every cruel wound and stinging failure into the molten gold that sculpts an invincible destiny.",
            mood = PromptMood.INSPIRATIONAL,
            emotionCode = "hausla",
            poeticCadenceHint = "Rebirth, endurance, rising higher after falls",
            tags = listOf("Destiny", "Fire", "Resilience")
        ),
        ShayariPrompt(
            id = "inspire_4",
            title = "Carving Paths Through Stone",
            promptText = "A calm mountain spring cutting through impenetrable granite cliffs not by sudden rage, but through ceaseless perseverance.",
            mood = PromptMood.INSPIRATIONAL,
            emotionCode = "hausla",
            poeticCadenceHint = "River flow, persistence, overcoming colossal obstacles",
            tags = listOf("River", "Stone", "Perseverance")
        ),
        ShayariPrompt(
            id = "inspire_5",
            title = "The Flame Within",
            promptText = "When all external lights are extinguished by the cold world, awakening the blinding sun that dwells inside your own ribs.",
            mood = PromptMood.INSPIRATIONAL,
            emotionCode = "hausla",
            poeticCadenceHint = "Inner light, self-mastery, unshakeable dignity",
            tags = listOf("Inner Light", "Strength", "Awakening")
        ),

        // FRIENDSHIP & BONDS
        ShayariPrompt(
            id = "friend_1",
            title = "Cutting Chai on Rainy Corners",
            promptText = "Splitting a single glass of steaming cutting chai among broke young dreamers on a torrential monsoon street corner.",
            mood = PromptMood.FRIENDSHIP,
            emotionCode = "dosti",
            poeticCadenceHint = "Tea glass, brotherhood, carefree youth",
            tags = listOf("Chai", "Youth", "Laughter")
        ),
        ShayariPrompt(
            id = "friend_2",
            title = "Silent Companion in the Dark",
            promptText = "A steadfast friend who never asks for an explanation of your tears, but sits beside you silently till dawn breaks.",
            mood = PromptMood.FRIENDSHIP,
            emotionCode = "dosti",
            poeticCadenceHint = "Loyalty, quiet support, unconditional trust",
            tags = listOf("Loyalty", "Support", "Trust")
        ),
        ShayariPrompt(
            id = "friend_3",
            title = "Reunion Across Decades",
            promptText = "Meeting an old companion after years of silence at a train platform, picking up the laughter as if no time ever passed.",
            mood = PromptMood.FRIENDSHIP,
            emotionCode = "dosti",
            poeticCadenceHint = "Railway station, timeless bond, warm nostalgia",
            tags = listOf("Reunion", "Timeless", "Warmth")
        ),
        ShayariPrompt(
            id = "friend_4",
            title = "Shielding Against the World",
            promptText = "Someone who stands as an unbreakable shield when the crowd throws stones, celebrating your victories as their own.",
            mood = PromptMood.FRIENDSHIP,
            emotionCode = "dosti",
            poeticCadenceHint = "Protection, shared pride, unbreakable bond",
            tags = listOf("Shield", "Pride", "Honor")
        ),

        // MYSTIC & SUFI
        ShayariPrompt(
            id = "sufi_1",
            title = "Whirling in the Divine Ray",
            promptText = "Spinning like a dervish in dust and light, stripping away false ego until only the eternal Beloved is seen everywhere.",
            mood = PromptMood.MYSTIC,
            emotionCode = "sufi",
            poeticCadenceHint = "Rumi cadence, dervish whirl, self-dissolution",
            tags = listOf("Dervish", "Ego", "Divine")
        ),
        ShayariPrompt(
            id = "sufi_2",
            title = "Temple Bell & Twilight River",
            promptText = "Where the evening temple conch and the gentle Odia river current merge, erasing all divide between the seeker and the sacred.",
            mood = PromptMood.MYSTIC,
            emotionCode = "sufi",
            poeticCadenceHint = "Conch, river flow, peace, spiritual oneness",
            tags = listOf("River", "Conch", "Sacred")
        ),
        ShayariPrompt(
            id = "sufi_3",
            title = "The Universe in a Tear",
            promptText = "Realizing that humanity is not just a fragile drop lost in the ocean, but the entire restless cosmos folded into one human heart.",
            mood = PromptMood.MYSTIC,
            emotionCode = "sufi",
            poeticCadenceHint = "Cosmic contemplation, mysticism, spiritual depth",
            tags = listOf("Cosmos", "Ocean", "Heart")
        ),
        ShayariPrompt(
            id = "sufi_4",
            title = "Incense of the Heart",
            promptText = "Burning slowly like sweet sandalwood in a quiet shrine, offering all fragrance to the universe without asking for a name.",
            mood = PromptMood.MYSTIC,
            emotionCode = "sufi",
            poeticCadenceHint = "Sandalwood, surrender, selfless devotion",
            tags = listOf("Sandalwood", "Surrender", "Grace")
        ),

        // NOSTALGIA & MEMORIES
        ShayariPrompt(
            id = "nostalgia_1",
            title = "Grandmother's Courtyard Folktales",
            promptText = "Listening to grandmother's tales on woven cot beds under open stars, while the ancient neem tree whispers in the breeze.",
            mood = PromptMood.NOSTALGIA,
            emotionCode = "yaadein",
            poeticCadenceHint = "Village courtyard, starry sky, childhood safety",
            tags = listOf("Grandmother", "Village", "Stars")
        ),
        ShayariPrompt(
            id = "nostalgia_2",
            title = "Petrichor of Hometown Soil",
            promptText = "The fragrant aroma of baked soil when the first summer drizzle touches the village tiles, stirring memories of innocent days.",
            mood = PromptMood.NOSTALGIA,
            emotionCode = "yaadein",
            poeticCadenceHint = "Mitti ki khushboo, summer rain, innocent days",
            tags = listOf("Petrichor", "Rain", "Hometown")
        ),
        ShayariPrompt(
            id = "nostalgia_3",
            title = "Yellowed Photograph in the Attic",
            promptText = "Discovering a dusty portrait in an old wooden trunk, where youthful smiles continue to hold eternal springtime.",
            mood = PromptMood.NOSTALGIA,
            emotionCode = "yaadein",
            poeticCadenceHint = "Attic, old portrait, bittersweet time",
            tags = listOf("Photo", "Attic", "Springtime")
        ),
        ShayariPrompt(
            id = "nostalgia_4",
            title = "Paper Boats on Monsoon Streams",
            promptText = "Crafting paper boats on village street gutters, truly believing with wide eyes that they would sail across the seven seas.",
            mood = PromptMood.NOSTALGIA,
            emotionCode = "yaadein",
            poeticCadenceHint = "Paper boats, childhood wonder, lost innocence",
            tags = listOf("Paper Boats", "Childhood", "Wonder")
        )
    )

    fun getPromptsByMood(mood: PromptMood?): List<ShayariPrompt> =
        if (mood == null) prompts else prompts.filter { it.mood == mood }

    fun searchPrompts(query: String, mood: PromptMood?): List<ShayariPrompt> {
        val base = getPromptsByMood(mood)
        if (query.isBlank()) return base
        val q = query.trim().lowercase()
        return base.filter {
            it.title.lowercase().contains(q) ||
                it.promptText.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
        }
    }
}
