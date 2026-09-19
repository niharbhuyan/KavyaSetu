package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.local.ShayariEntity
import com.example.data.model.Shayari

/**
 * Utility for sharing favorite poems and Gemini-generated shayari to social media platforms
 * using Android's Intent.ACTION_SEND.
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
        val intent = createShareIntent(shareText, subject = "Verse by $author — Kavya Setu")
        val chooser = Intent.createChooser(intent, "Share Poem to Social Media").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
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
        val intent = createShareIntent(shareText, subject = "AI Composed Shayari — Kavya Setu")
        val chooser = Intent.createChooser(intent, "Share AI Shayari to Social Media").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Formats a classic or community poem for social media posting with aesthetic structure,
     * author credit, tags, and deep link attribution.
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
        val langTag = when (language.lowercase()) {
            "hindi" -> "#HindiShayari #HindiPoetry"
            "odia" -> "#OdiaKabita #OdiaPoetry"
            "english" -> "#EnglishPoetry #PoetryCommunity"
            else -> "#Poetry"
        }
        val authorCredit = if (!penName.isNullOrBlank() && penName != author) {
            "— $author ($penName)"
        } else {
            "— $author"
        }
        val metaLine = buildString {
            if (!emotion.isNullOrBlank()) append("Mood: ${emotion.replaceFirstChar { it.uppercase() }}")
            if (!category.isNullOrBlank()) {
                if (isNotEmpty()) append(" • ")
                append("Category: ${category.replaceFirstChar { it.uppercase() }}")
            }
        }

        return buildString {
            append("✨ *Kavya Setu • काव्य सेतु*\n\n")
            append(lines.trim())
            append("\n\n")
            append(authorCredit)
            if (metaLine.isNotBlank()) {
                append("\n")
                append(metaLine)
            }
            append("\n\n")
            if (!id.isNullOrBlank()) {
                append("📖 Read in app: shayari://detail?id=$id\n")
            }
            append("$langTag #KavyaSetu #ShayariOfTheSoul")
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
        val langTag = when (language.lowercase()) {
            "hindi" -> "#HindiShayari #UrduShayari"
            "odia" -> "#OdiaKabita #OdiaPoetry"
            "english" -> "#EnglishPoetry #Poetry"
            else -> "#Poetry"
        }

        return buildString {
            append("✨ *AI Composed Verse • کلامِ مصنوعی*\n")
            if (topic.isNotBlank()) {
                append("Theme: \"$topic\"\n")
            }
            append("\n")
            append(lines.trim())
            append("\n\n")
            val penCredit = if (author.isNotBlank() && author != "AI Poet") author else "Poet of the Soul"
            append("— $penCredit (Composed with Gemini AI on Kavya Setu)\n")
            append("Mood: ${emotion.replaceFirstChar { it.uppercase() }}\n\n")
            append("$langTag #GeminiAI #AIShayari #KavyaSetu #PoetryCommunity")
        }
    }
}
