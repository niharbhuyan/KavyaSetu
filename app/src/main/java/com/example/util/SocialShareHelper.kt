package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.local.ShayariEntity
import com.example.data.local.TakhallusSealManager
import com.example.data.model.Shayari

/**
 * Social media format presets for exporting and sharing poems via Android Intent.
 */
enum class SocialFormatPreset(
    val title: String,
    val description: String,
    val iconEmoji: String
) {
    WHATSAPP_MARKDOWN(
        title = "WhatsApp / Chat",
        description = "Bold & italics formatting with clean dividers",
        iconEmoji = "💬"
    ),
    INSTAGRAM_CAPTION(
        title = "Instagram / Threads",
        description = "Aesthetic quotation quotes, spacing & rich hashtags",
        iconEmoji = "📸"
    ),
    TWITTER_X(
        title = "X (Twitter)",
        description = "Concise couplet format optimized for character limits",
        iconEmoji = "🐦"
    ),
    ROYAL_PARCHMENT(
        title = "Royal Parchment",
        description = "Classical Unicode filigree box & royal seal signature",
        iconEmoji = "📜"
    ),
    MINIMALIST(
        title = "Clean Minimal",
        description = "Pure literary verses without markdown decoration",
        iconEmoji = "✨"
    )
}

/**
 * Utility for formatting and sharing poems, couplets, and AI-generated shayari to social media platforms
 * using the Android Intent system (Intent.ACTION_SEND).
 */
object SocialShareHelper {

    /**
     * Builds the standard social share intent using Intent.ACTION_SEND.
     */
    fun createShareIntent(text: String, subject: String = "Poetic Verse"): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
    }

    /**
     * Copies text to system clipboard with a toast notification.
     */
    fun copyToClipboard(context: Context, text: String, label: String = "Poetry") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Formatted poetry copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Copies poem couplets, author pen-name, and translation directly to clipboard with a toast confirmation.
     */
    fun copyPoem(context: Context, shayari: Shayari) {
        val textToCopy = buildString {
            append(shayari.lines.trim())
            append("\n\n— ")
            append(shayari.author)
            if (!shayari.penName.isNullOrBlank() && shayari.penName != shayari.author) {
                append(" (${shayari.penName})")
            }
            if (!shayari.translationEnglish.isNullOrBlank()) {
                append("\n\nTranslation: ")
                append(shayari.translationEnglish.trim())
            }
            append("\n\n#KavyaSetu #Poetry")
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Poem by ${shayari.author}", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Poem copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    fun copyPoem(context: Context, shayari: ShayariEntity) {
        val textToCopy = buildString {
            append(shayari.lines.trim())
            append("\n\n— ")
            append(shayari.author)
            if (!shayari.penName.isNullOrBlank() && shayari.penName != shayari.author) {
                append(" (${shayari.penName})")
            }
            if (!shayari.translationEnglish.isNullOrBlank()) {
                append("\n\nTranslation: ")
                append(shayari.translationEnglish.trim())
            }
            append("\n\n#KavyaSetu #Poetry")
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Poem by ${shayari.author}", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Poem copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    fun copyPoem(context: Context, lines: String, author: String, translation: String? = null) {
        val textToCopy = buildString {
            append(lines.trim())
            append("\n\n— ")
            append(author)
            if (!translation.isNullOrBlank()) {
                append("\n\nTranslation: ")
                append(translation.trim())
            }
            append("\n\n#KavyaSetu #Poetry")
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Poem by $author", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Poem copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    /**
     * Launches an explicit intent targeting a specific package (e.g. WhatsApp, Twitter),
     * and gracefully falls back to Intent.createChooser if the app is not installed.
     */
    fun shareToTargetApp(
        context: Context,
        text: String,
        packageName: String,
        appName: String,
        subject: String = "Poetic Verse — Kavya Setu"
    ) {
        val targetIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            // Verify if there is an activity that can handle this specific package intent
            val pm = context.packageManager
            val activities = pm.queryIntentActivities(targetIntent, 0)
            if (activities.isNotEmpty()) {
                context.startActivity(targetIntent)
                return
            }
        } catch (e: Exception) {
            // Package lookup failed, fallback to chooser
        }

        // Special fallback for Twitter/X web intent if app is not installed
        if (packageName == "com.twitter.android") {
            try {
                val tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(text)
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
                return
            } catch (e: Exception) {
                // Ignore and fall through to system chooser
            }
        }

        // Fallback to standard Android Intent chooser
        val fallbackIntent = createShareIntent(text, subject)
        val chooser = Intent.createChooser(fallbackIntent, "Share via $appName or other app").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Shares formatted text directly to WhatsApp.
     */
    fun shareToWhatsApp(context: Context, text: String) {
        shareToTargetApp(
            context = context,
            text = text,
            packageName = "com.whatsapp",
            appName = "WhatsApp"
        )
    }

    /**
     * Shares formatted text directly to X (Twitter).
     */
    fun shareToTwitter(context: Context, text: String) {
        shareToTargetApp(
            context = context,
            text = text,
            packageName = "com.twitter.android",
            appName = "X (Twitter)"
        )
    }

    /**
     * Shares formatted text directly to Telegram.
     */
    fun shareToTelegram(context: Context, text: String) {
        shareToTargetApp(
            context = context,
            text = text,
            packageName = "org.telegram.messenger",
            appName = "Telegram"
        )
    }

    /**
     * Shares formatted text directly to Instagram.
     */
    fun shareToInstagram(context: Context, text: String) {
        shareToTargetApp(
            context = context,
            text = text,
            packageName = "com.instagram.android",
            appName = "Instagram"
        )
    }

    /**
     * Shares formatted text using standard Android Intent Chooser dialog.
     */
    fun shareViaChooser(
        context: Context,
        text: String,
        subject: String = "Poetry from Kavya Setu",
        title: String = "Share Formatted Poetry to Social Media"
    ) {
        val intent = createShareIntent(text, subject)
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Comprehensive formatter generating custom formatted text based on selected preset and toggles.
     */
    fun buildCustomFormattedPoem(
        lines: String,
        author: String,
        penName: String? = null,
        language: String = "hindi",
        emotion: String? = null,
        style: String? = null,
        topic: String? = null,
        preset: SocialFormatPreset = SocialFormatPreset.WHATSAPP_MARKDOWN,
        includeSeal: Boolean = true,
        includeHashtags: Boolean = true,
        includeWatermark: Boolean = true,
        includeMetadata: Boolean = true,
        takhallusSealText: String? = null
    ): String {
        val cleanLines = lines.trim()
        val authorName = if (!penName.isNullOrBlank() && penName != author) "$author ($penName)" else author
        val langTag = when (language.lowercase()) {
            "hindi" -> "#HindiShayari #UrduPoetry"
            "odia" -> "#OdiaKabita #OdiaPoetry"
            "english" -> "#EnglishPoetry #PoetryCommunity"
            else -> "#Poetry #Shayari"
        }

        val sealSignature = if (includeSeal) {
            val sealName = takhallusSealText ?: TakhallusSealManager.currentSeal.value.takhallus
            "🪶 $sealName"
        } else null

        val metadataLine = if (includeMetadata) {
            val metaParts = mutableListOf<String>()
            if (!emotion.isNullOrBlank()) metaParts.add("Mood: ${emotion.replaceFirstChar { it.uppercase() }}")
            if (!style.isNullOrBlank()) metaParts.add("Style: ${style.replaceFirstChar { it.uppercase() }}")
            if (!topic.isNullOrBlank()) metaParts.add("Theme: \"$topic\"")
            if (metaParts.isNotEmpty()) metaParts.joinToString(" • ") else null
        } else null

        return when (preset) {
            SocialFormatPreset.WHATSAPP_MARKDOWN -> {
                buildString {
                    append("🖋️ *_Kavya Setu • काव्यसेतु_*\n")
                    append("━━━━━━━━━━━━━━━━━━━━\n\n")
                    // Bold the couplet lines for WhatsApp
                    val formattedLines = cleanLines.lines().joinToString("\n") { line ->
                        if (line.isNotBlank()) "*$line*" else ""
                    }
                    append(formattedLines)
                    append("\n\n")
                    append("— _${authorName}_")
                    if (sealSignature != null) append("  $sealSignature")
                    append("\n")
                    if (metadataLine != null) {
                        append("🎭 _${metadataLine}_\n")
                    }
                    append("\n")
                    if (includeWatermark) {
                        append("✨ _Shared via Kavya Setu (Built by Nihar Sales)_\n")
                    }
                    if (includeHashtags) {
                        append("$langTag #KavyaSetu #ShayariOfTheSoul")
                    }
                }.trim()
            }

            SocialFormatPreset.INSTAGRAM_CAPTION -> {
                buildString {
                    append("✦ ━━━━━━━━━━━━━━━━━━ ✦\n")
                    append("❝\n")
                    append(cleanLines)
                    append("\n❞\n")
                    append("✦ ━━━━━━━━━━━━━━━━━━ ✦\n\n")
                    append("— $authorName\n")
                    if (sealSignature != null) {
                        append("$sealSignature • Takhallus Seal\n")
                    }
                    if (metadataLine != null) {
                        append("[$metadataLine]\n")
                    }
                    append("\n")
                    if (includeWatermark) {
                        append("Captured with @KavyaSetuApp\n")
                    }
                    if (includeHashtags) {
                        append("• • •\n")
                        append("$langTag #PoetryCommunity #InstaPoet #WritersOfInstagram #KavyaSetu #ShayariLovers #Ghazal #WordPorn")
                    }
                }.trim()
            }

            SocialFormatPreset.TWITTER_X -> {
                buildString {
                    append(cleanLines)
                    append("\n\n")
                    append("— $authorName")
                    if (sealSignature != null) append(" $sealSignature")
                    append("\n")
                    if (includeWatermark) {
                        append("via @KavyaSetu\n")
                    }
                    if (includeHashtags) {
                        append("$langTag #KavyaSetu")
                    }
                }.trim()
            }

            SocialFormatPreset.ROYAL_PARCHMENT -> {
                buildString {
                    append("╔═══════════════════════════════╗\n")
                    append("  ✨ KAVYA SETU • دیوانِ سخن ✨\n")
                    append("╠═══════════════════════════════╣\n\n")
                    cleanLines.lines().forEach { l ->
                        if (l.isNotBlank()) append("   $l\n") else append("\n")
                    }
                    append("\n╠═══════════════════════════════╣\n")
                    append("  👤 Shayar: $authorName\n")
                    if (sealSignature != null) {
                        append("  $sealSignature (Mohar)\n")
                    }
                    if (metadataLine != null) {
                        append("  📜 $metadataLine\n")
                    }
                    if (includeWatermark) {
                        append("  🏛️ Mehfil: Kavya Setu (Nihar Sales)\n")
                    }
                    append("╚═══════════════════════════════╝")
                    if (includeHashtags) {
                        append("\n$langTag #KavyaSetu #ClassicalPoetry")
                    }
                }.trim()
            }

            SocialFormatPreset.MINIMALIST -> {
                buildString {
                    append(cleanLines)
                    append("\n\n")
                    append("— $authorName")
                    if (sealSignature != null) append(" ($sealSignature)")
                    if (metadataLine != null) append("\n$metadataLine")
                    if (includeWatermark) {
                        append("\n\n(via Kavya Setu App)")
                    }
                    if (includeHashtags) {
                        append("\n$langTag #KavyaSetu")
                    }
                }.trim()
            }
        }
    }

    /**
     * Shares a poem to social media apps via Intent.ACTION_SEND with Intent.createChooser.
     */
    fun sharePoem(
        context: Context,
        lines: String,
        author: String,
        language: String,
        emotion: String? = null,
        category: String? = null,
        penName: String? = null,
        id: String? = null
    ) {
        val shareText = formatPoemForSocial(
            lines = lines,
            author = author,
            language = language,
            emotion = emotion,
            category = category,
            penName = penName,
            id = id
        )
        shareViaChooser(context, shareText, subject = "Verse by $author — Kavya Setu")
    }

    fun sharePoem(context: Context, shayari: Shayari) {
        sharePoem(
            context = context,
            lines = shayari.lines,
            author = shayari.author,
            language = shayari.language,
            emotion = shayari.emotion,
            category = shayari.category,
            penName = shayari.penName,
            id = shayari.id
        )
    }

    fun sharePoem(context: Context, shayari: ShayariEntity) {
        sharePoem(
            context = context,
            lines = shayari.lines,
            author = shayari.author,
            language = shayari.language,
            emotion = shayari.emotion,
            category = shayari.category,
            penName = shayari.penName,
            id = shayari.id
        )
    }

    /**
     * Shares Gemini-generated shayari to social media platforms using Intent.ACTION_SEND.
     */
    fun shareGeminiShayari(
        context: Context,
        lines: String,
        emotion: String,
        language: String,
        author: String = "AI Poet",
        topic: String = ""
    ) {
        val shareText = formatGeminiPoemForSocial(
            lines = lines,
            emotion = emotion,
            language = language,
            author = author,
            topic = topic
        )
        shareViaChooser(context, shareText, subject = "AI Composed Shayari — Kavya Setu")
    }

    /**
     * Formats a classic or community poem for social media posting.
     */
    fun formatPoemForSocial(
        lines: String,
        author: String,
        language: String,
        emotion: String? = null,
        category: String? = null,
        penName: String? = null,
        id: String? = null
    ): String {
        val base = buildCustomFormattedPoem(
            lines = lines,
            author = author,
            penName = penName,
            language = language,
            emotion = emotion,
            style = category,
            preset = SocialFormatPreset.WHATSAPP_MARKDOWN,
            includeSeal = true,
            includeHashtags = true,
            includeWatermark = true,
            includeMetadata = true
        )
        return if (!id.isNullOrBlank()) {
            "$base\n\n🔗 Read in App: shayari://detail?id=$id"
        } else {
            base
        }
    }

    /**
     * Formats a Gemini AI-composed poem for social media posting.
     */
    fun formatGeminiPoemForSocial(
        lines: String,
        emotion: String,
        language: String,
        author: String = "AI Poet",
        topic: String = ""
    ): String {
        val base = buildCustomFormattedPoem(
            lines = lines,
            author = author.ifBlank { "AI Poet" },
            language = language,
            emotion = emotion,
            topic = topic,
            preset = SocialFormatPreset.WHATSAPP_MARKDOWN,
            includeSeal = true,
            includeHashtags = true,
            includeWatermark = true,
            includeMetadata = true
        )
        return buildString {
            append(base)
            append("\n\n🤖 Composed with Gemini AI on Kavya Setu\n")
            append("#GeminiAI #AIShayari")
        }.trim()
    }
}

