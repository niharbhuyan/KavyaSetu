package com.example.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.analytics.KavyaAnalytics
import com.example.data.model.Shayari
import com.example.data.local.ShayariEntity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Firebase Dynamic Links and Universal Deep Linking Engine for Kavya Setu (काव्यसेतु).
 *
 * Facilitates 1-tap sharing of specific poems, ghazals, and couplets.
 * When recipients tap the link:
 * - If app is installed: opens directly to the exact poem in the app.
 * - If app is not installed: navigates to the Play Store / web preview, then opens the poem on first launch.
 */
object FirebaseDynamicLinkManager {
    private const val TAG = "KavyaDynamicLinks"

    // Primary Firebase Dynamic Links Domain
    const val DYNAMIC_LINK_DOMAIN = "https://kavyasetu.page.link"

    // Canonical Web & App Link Domains
    const val APP_DEEP_LINK_HOST = "https://kavyasetu.app"
    const val WEB_PREVIEW_URL = "https://ais-dev-om5rsf22wxxclflaszhhtg-613265325843.asia-east1.run.app"

    /**
     * Builds the target deep link URI representing a specific poetry piece.
     */
    fun createTargetDeepLinkUri(shayariId: String): Uri {
        return Uri.parse("$APP_DEEP_LINK_HOST/poem?id=$shayariId")
    }

    /**
     * Generates a Firebase Short Dynamic Link asynchronously.
     * Falls back to a deterministic Long Dynamic Link or Universal App Link if network fails.
     */
    suspend fun generateShortDynamicLink(context: Context, shayari: Shayari): String {
        return suspendCancellableCoroutine { continuation ->
            try {
                val targetUri = createTargetDeepLinkUri(shayari.id)
                val packageName = context.packageName

                val title = "${shayari.author} — ${shayari.category.replaceFirstChar { it.uppercase() }}"
                val excerpt = shayari.lines.lines().take(2).joinToString(" ").take(100)

                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(targetUri)
                    .setDomainUriPrefix(DYNAMIC_LINK_DOMAIN)
                    .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder(packageName)
                            .setMinimumVersion(1)
                            .build()
                    )
                    .setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle(title)
                            .setDescription(excerpt)
                            .build()
                    )
                    .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                    .addOnSuccessListener { result ->
                        val shortUrl = result.shortLink?.toString()
                        if (!shortUrl.isNullOrBlank()) {
                            Log.d(TAG, "Generated Short Dynamic Link: $shortUrl")
                            continuation.resume(shortUrl)
                        } else {
                            val fallback = buildDeterministicDynamicLink(context, shayari)
                            continuation.resume(fallback)
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.w(TAG, "Failed to create short dynamic link, using fallback: ${error.message}")
                        val fallback = buildDeterministicDynamicLink(context, shayari)
                        continuation.resume(fallback)
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Exception during dynamic link generation: ${e.message}")
                val fallback = buildDeterministicDynamicLink(context, shayari)
                continuation.resume(fallback)
            }
        }
    }

    /**
     * Builds a deterministic Firebase Dynamic Link URL containing full parameters.
     * Guaranteed to work even offline or before cloud DNS resolution.
     */
    fun buildDeterministicDynamicLink(context: Context, shayari: Shayari): String {
        val targetUri = Uri.encode("$APP_DEEP_LINK_HOST/poem?id=${shayari.id}")
        val packageName = context.packageName
        val title = Uri.encode("Verse by ${shayari.author} — Kavya Setu")
        val description = Uri.encode(shayari.lines.lines().firstOrNull()?.take(80) ?: "Read classical poetry")

        return "$DYNAMIC_LINK_DOMAIN/?link=$targetUri&apn=$packageName&st=$title&sd=$description"
    }

    /**
     * Resolves incoming dynamic links or intent URIs and extracts the poetry ID.
     */
    fun processIncomingIntent(
        activity: Activity,
        intent: Intent?,
        onPoemResolved: (String) -> Unit
    ) {
        if (intent == null) return

        // 1. First check explicit Intent Extras (from Push Notifications or Internal Navigation)
        val extraId = intent.getStringExtra("shayari_id")
            ?: intent.getStringExtra("id")
            ?: intent.getStringExtra("poem_id")

        if (!extraId.isNullOrBlank()) {
            Log.d(TAG, "Found Shayari ID in intent extras: $extraId")
            KavyaAnalytics.trackFeatureUsed("deep_link_opened", "extra:$extraId")
            onPoemResolved(extraId)
            return
        }

        // Only invoke Firebase Dynamic Links SDK if intent actually contains URI data or is ACTION_VIEW
        val uri = intent.data
        if (uri == null && intent.action != Intent.ACTION_VIEW) {
            // Standard app launch from launcher/task — skip external measurement binding
            return
        }

        // 2. Resolve via Firebase Dynamic Links SDK
        try {
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(activity) { pendingData ->
                    val deepLinkUri = pendingData?.link
                    if (deepLinkUri != null) {
                        Log.d(TAG, "Received Firebase Dynamic Link: $deepLinkUri")
                        val poemId = extractPoemId(deepLinkUri)
                        if (!poemId.isNullOrBlank()) {
                            KavyaAnalytics.trackFeatureUsed("dynamic_link_opened", "fdl:$poemId")
                            onPoemResolved(poemId)
                            return@addOnSuccessListener
                        }
                    }
                    // If FDL had no link, check intent data directly
                    fallbackDirectIntentCheck(intent, onPoemResolved)
                }
                .addOnFailureListener(activity) { e ->
                    Log.w(TAG, "Firebase Dynamic Link extraction failed: ${e.message}")
                    fallbackDirectIntentCheck(intent, onPoemResolved)
                }
        } catch (e: Exception) {
            Log.w(TAG, "Exception calling FirebaseDynamicLinks: ${e.message}")
            fallbackDirectIntentCheck(intent, onPoemResolved)
        }
    }

    private fun fallbackDirectIntentCheck(intent: Intent, onPoemResolved: (String) -> Unit) {
        val data = intent.data ?: return
        Log.d(TAG, "Checking direct intent data: $data")
        val poemId = extractPoemId(data)
        if (!poemId.isNullOrBlank()) {
            KavyaAnalytics.trackFeatureUsed("deep_link_opened", "direct:$poemId")
            onPoemResolved(poemId)
        }
    }

    /**
     * Extracts poem ID from diverse URI schemes:
     * - https://kavyasetu.page.link/?link=https://kavyasetu.app/poem?id=123
     * - https://kavyasetu.app/poem?id=123
     * - https://kavyasetu.app/detail/123
     * - shayari://detail?id=123
     * - https://ais-dev-...run.app/poem?id=123
     */
    fun extractPoemId(uri: Uri): String? {
        // Check if there is an embedded nested 'link' parameter (as in Firebase Dynamic Links)
        val nestedLink = uri.getQueryParameter("link")
        if (!nestedLink.isNullOrBlank()) {
            try {
                val nestedUri = Uri.parse(nestedLink)
                val nestedId = extractPoemId(nestedUri)
                if (!nestedId.isNullOrBlank()) return nestedId
            } catch (e: Exception) {
                Log.w(TAG, "Could not parse nested dynamic link: $nestedLink")
            }
        }

        // Direct query parameters
        uri.getQueryParameter("id")?.takeIf { it.isNotBlank() }?.let { return it }
        uri.getQueryParameter("poem_id")?.takeIf { it.isNotBlank() }?.let { return it }
        uri.getQueryParameter("shayari_id")?.takeIf { it.isNotBlank() }?.let { return it }

        // Path segment resolution (e.g., /poem/mir_01 or /detail/ghalib_02)
        val segments = uri.pathSegments
        if (segments != null && segments.size >= 2) {
            val prefix = segments[0]
            if (prefix == "poem" || prefix == "detail" || prefix == "sher") {
                val idSegment = segments[1]
                if (idSegment.isNotBlank()) return idSegment
            }
        } else if (segments != null && segments.size == 1) {
            val single = segments[0]
            if (single.isNotBlank() && single != "poem" && single != "detail") {
                return single
            }
        }

        return null
    }

    /**
     * Shares a poem with its generated Firebase Dynamic Link via the standard Android share sheet.
     */
    fun sharePoemWithDynamicLink(
        context: Context,
        shayari: Shayari,
        dynamicLink: String
    ) {
        val shareText = buildString {
            append("📜 ")
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
            append("\n\n✨ Open and listen in Kavya Setu:\n")
            append(dynamicLink)
            append("\n\n#KavyaSetu #Poetry")
        }

        KavyaAnalytics.trackPoetryInteraction(
            action = "share_dynamic_link",
            category = shayari.category,
            language = shayari.language,
            extraTag = shayari.id
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Poetry by ${shayari.author} — Kavya Setu")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Deep Link via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
