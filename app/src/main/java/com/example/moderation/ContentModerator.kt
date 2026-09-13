package com.example.moderation

import com.example.data.model.ModerationCheckResult
import com.example.data.model.ModerationSeverity
import com.example.data.model.ModerationStatus
import java.util.regex.Pattern

object ContentModerator {

    // Regex for URLs, emails, phone numbers, promotional handles
    private val URL_PATTERN = Pattern.compile(
        "(https?://\\S+|www\\.\\S+|bit\\.ly/\\S+|t\\.me/\\S+|wa\\.me/\\S+)",
        Pattern.CASE_INSENSITIVE
    )
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
        Pattern.CASE_INSENSITIVE
    )
    private val PHONE_PATTERN = Pattern.compile(
        "(\\+?\\d{1,3}[- ]?)?(\\d{10}|\\d{5}[- ]\\d{5})",
        Pattern.CASE_INSENSITIVE
    )

    // Commercial and spam phrases
    private val SPAM_PATTERNS = listOf(
        "call now", "whatsapp group", "join group", "earn money", "click here",
        "subscribe", "telegram channel", "crypto investment", "free recharge",
        "dm for paid", "promotions", "paid shayari", "contact for marriage",
        "घर बैठे पैसे कमाए", "कॉल करें", "व्हाट्सएप ग्रुप", "ଟଙ୍କା ରୋଜଗାର କରନ୍ତୁ"
    )

    // Profanity, vulgarities, and abusive terms (English, Hindi/Hinglish, Odia)
    private val ABUSE_KEYWORDS = listOf(
        // English abusive
        "bastard", "idiot", "stupid", "fuck", "bitch", "asshole", "slut", "cunt",
        // Hindi / Hinglish abusive
        "गाली", "कमीने", "हरामी", "कुत्ते", "साले", "चुतिया", "भोसड़ी", "रांड",
        "harami", "kutta", "saale", "kamina", "chutiya", "madarchod", "behenchod",
        // Odia colloquial abusive
        "ଖରାପ", "ଅସଭ୍ୟ", "କୁକୁର", "ଦୁର୍ବୃତ୍ତ"
    )

    // Violence, self-harm, hate speech
    private val HATE_VIOLENCE_KEYWORDS = listOf(
        "kill them", "murder", "hate speech", "terrorist", "hang yourself", "suicide",
        "mar dalo", "jaan se marunga", "मार डालो", "हत्या", "ଆତ୍ମହତ୍ୟା", "ମାରିଦିଅ"
    )

    fun analyze(text: String, author: String = "", penName: String = ""): ModerationCheckResult {
        val fullContent = "$text $author $penName".lowercase()
        val detectedIssues = mutableListOf<String>()
        var penaltyScore = 0f

        // 1. Check for spam links and urls
        if (URL_PATTERN.matcher(text).find()) {
            detectedIssues.add("Prohibited URL link or website found in verse")
            penaltyScore += 0.6f
        }

        // 2. Check for contact phone numbers
        if (PHONE_PATTERN.matcher(text).find()) {
            detectedIssues.add("Phone number / contact digit pattern detected")
            penaltyScore += 0.5f
        }

        // 3. Check for email addresses
        if (EMAIL_PATTERN.matcher(text).find()) {
            detectedIssues.add("Email address detected in text")
            penaltyScore += 0.4f
        }

        // 4. Check for spam commercial keywords
        for (spam in SPAM_PATTERNS) {
            if (fullContent.contains(spam.lowercase())) {
                detectedIssues.add("Commercial spam pattern: \"$spam\"")
                penaltyScore += 0.5f
                break
            }
        }

        // 5. Check for profanity and abuse
        val words = fullContent.split(Regex("[\\s,;.!?-]+"))
        val matchedAbuse = ABUSE_KEYWORDS.filter { abuse ->
            words.contains(abuse.lowercase()) || fullContent.contains(abuse.lowercase())
        }
        if (matchedAbuse.isNotEmpty()) {
            detectedIssues.add("Abusive or inappropriate keywords detected (${matchedAbuse.joinToString(", ")})")
            penaltyScore += 0.85f
        }

        // 6. Check for hate speech / violence
        val matchedHate = HATE_VIOLENCE_KEYWORDS.filter { hate ->
            fullContent.contains(hate.lowercase())
        }
        if (matchedHate.isNotEmpty()) {
            detectedIssues.add("Violence or harassment indicators detected (${matchedHate.joinToString(", ")})")
            penaltyScore += 0.95f
        }

        // 7. Check for repetitive character spam (e.g. "aaaaaaaaaaaaaa")
        if (Regex("(.)\\1{5,}").containsMatchIn(text)) {
            detectedIssues.add("Repetitive character gibberish pattern detected")
            penaltyScore += 0.35f
        }

        // 8. Brevity check
        if (text.trim().length < 8) {
            detectedIssues.add("Verse is too short or empty to qualify as poetry")
            penaltyScore += 0.3f
        }

        val riskScore = penaltyScore.coerceIn(0f, 1f)

        val severity = when {
            riskScore >= 0.7f -> ModerationSeverity.HIGH_RISK
            riskScore >= 0.35f -> ModerationSeverity.SUSPICIOUS
            else -> ModerationSeverity.SAFE
        }

        val suggestedStatus = when {
            riskScore >= 0.7f -> ModerationStatus.FLAGGED.code
            riskScore >= 0.35f -> ModerationStatus.PENDING.code
            else -> ModerationStatus.APPROVED.code
        }

        val summary = when (severity) {
            ModerationSeverity.SAFE -> "Clean verse. No policy violations detected."
            ModerationSeverity.SUSPICIOUS -> "Suspicious patterns flagged. Requires administrator verification."
            ModerationSeverity.HIGH_RISK -> "Severe policy violations identified. Recommended rejection."
        }

        return ModerationCheckResult(
            isFlagged = severity != ModerationSeverity.SAFE,
            severity = severity,
            suggestedStatus = suggestedStatus,
            detectedIssues = detectedIssues,
            riskScore = riskScore,
            summary = summary
        )
    }
}
