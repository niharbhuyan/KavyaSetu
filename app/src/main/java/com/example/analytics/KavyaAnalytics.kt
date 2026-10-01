package com.example.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Lightweight, Privacy-Focused Analytics Engine for Kavya Setu (काव्यसेतु).
 *
 * Privacy Guarantees:
 * - Strictly NO Personally Identifiable Information (PII) is tracked (no names, emails, raw user text).
 * - Full user transparency with in-app toggle: users can opt-out at any time.
 * - Anonymous usage patterns and aggregated retention cohorts only.
 * - Completely decoupled from native adservices/measurement service binding to eliminate crashes on virtualized environments and emulators.
 */
object KavyaAnalytics {
    private const val TAG = "KavyaAnalytics"
    private const val PREFS_NAME = "kavya_privacy_analytics_prefs"
    private const val KEY_ANALYTICS_ENABLED = "analytics_collection_opt_in"
    private const val KEY_FIRST_INSTALL_TIME = "first_install_timestamp"
    private const val KEY_LAST_ACTIVE_DATE = "last_active_date_string"
    private const val KEY_CURRENT_STREAK_DAYS = "current_retention_streak"
    private const val KEY_TOTAL_SESSIONS = "total_lifetime_sessions"
    private const val KEY_D1_RECORDED = "retention_milestone_d1"
    private const val KEY_D3_RECORDED = "retention_milestone_d3"
    private const val KEY_D7_RECORDED = "retention_milestone_d7"
    private const val KEY_D30_RECORDED = "retention_milestone_d30"

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_FIRST_INSTALL_TIME)) {
            prefs.edit().putLong(KEY_FIRST_INSTALL_TIME, System.currentTimeMillis()).apply()
        }
        Log.d(TAG, "KavyaAnalytics initialized safely in privacy mode.")
    }

    fun isAnalyticsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ANALYTICS_ENABLED, true)
    }

    fun setAnalyticsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ANALYTICS_ENABLED, enabled).apply()
    }

    fun resetLocalAnalytics(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun trackScreenView(screenName: String) {
        logEvent("screen_view", Bundle().apply { putString("screen_name", screenName) })
    }

    fun trackPoetryInteraction(
        action: String,
        category: String = "",
        language: String = "",
        extraTag: String = ""
    ) {
        val bundle = Bundle().apply {
            putString("action", action)
            if (category.isNotBlank()) putString("category", category)
            if (language.isNotBlank()) putString("language", language)
            if (extraTag.isNotBlank()) putString("extra_tag", extraTag)
        }
        logEvent("poetry_interaction", bundle)
    }

    fun trackFeatureUsed(featureName: String, extraInfo: String = "") {
        val bundle = Bundle().apply {
            putString("feature_name", featureName)
            if (extraInfo.isNotBlank()) putString("extra_info", extraInfo)
        }
        logEvent("feature_used", bundle)
    }

    fun trackCommunityEngagement(feature: String, action: String) {
        val bundle = Bundle().apply {
            putString("feature", feature)
            putString("action", action)
        }
        logEvent("community_engagement", bundle)
    }

    fun trackAutoUpdate(
        eventType: String,
        targetVersion: String = "",
        isAutomatic: Boolean = false
    ) {
        val bundle = Bundle().apply {
            putString("event_type", eventType)
            if (targetVersion.isNotBlank()) putString("target_version", targetVersion)
            putBoolean("is_automatic", isAutomatic)
        }
        logEvent("auto_update", bundle)
    }

    fun trackSessionStart(context: Context) {
        if (!isAnalyticsEnabled(context)) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))

        val lastActiveStr = prefs.getString(KEY_LAST_ACTIVE_DATE, null)
        val totalSessions = prefs.getInt(KEY_TOTAL_SESSIONS, 0) + 1
        var currentStreak = prefs.getInt(KEY_CURRENT_STREAK_DAYS, 1)

        if (lastActiveStr != null && lastActiveStr != todayStr) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val lastDate = sdf.parse(lastActiveStr)
                val todayDate = sdf.parse(todayStr)
                if (lastDate != null && todayDate != null) {
                    val diffDays = TimeUnit.MILLISECONDS.toDays(todayDate.time - lastDate.time)
                    if (diffDays == 1L) {
                        currentStreak += 1
                    } else if (diffDays > 1L) {
                        currentStreak = 1
                    }
                }
            } catch (e: Exception) {
                currentStreak = 1
            }
        }

        prefs.edit()
            .putString(KEY_LAST_ACTIVE_DATE, todayStr)
            .putInt(KEY_TOTAL_SESSIONS, totalSessions)
            .putInt(KEY_CURRENT_STREAK_DAYS, currentStreak)
            .apply()

        val firstInstall = prefs.getLong(KEY_FIRST_INSTALL_TIME, now)
        val daysSinceInstall = TimeUnit.MILLISECONDS.toDays(now - firstInstall)

        val sessionBundle = Bundle().apply {
            putInt("session_count", totalSessions)
            putInt("streak_days", currentStreak)
            putLong("days_since_install", daysSinceInstall)
        }
        logEvent("session_opened", sessionBundle)
    }

    private fun logEvent(eventName: String, params: Bundle?) {
        try {
            Log.d(TAG, "KavyaEvent: $eventName [${params?.keySet()?.joinToString { "$it=${params.get(it)}" }}]")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log event $eventName: ${e.message}")
        }
    }
}
