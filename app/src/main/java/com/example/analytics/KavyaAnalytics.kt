package com.example.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
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
 * - Safely handles offline or uninitialized states without blocking UI.
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

    private var firebaseAnalytics: FirebaseAnalytics? = null

    /**
     * Initializes analytics safely with user consent check.
     */
    fun initialize(context: Context) {
        val enabled = isAnalyticsEnabled(context)
        if (com.example.ads.AdMobManager.isRunningInEmulator) {
            Log.d(TAG, "Running in emulator; disabling analytics/measurement service binding.")
            firebaseAnalytics = null
        } else {
            try {
                firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                firebaseAnalytics?.setAnalyticsCollectionEnabled(enabled)
                Log.d(TAG, "Firebase Analytics initialized. Collection enabled: $enabled")
            } catch (e: Exception) {
                Log.w(TAG, "Firebase Analytics could not be initialized: ${e.message}")
            }
        }

        // Initialize first install tracking timestamp if not present
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_FIRST_INSTALL_TIME)) {
            prefs.edit().putLong(KEY_FIRST_INSTALL_TIME, System.currentTimeMillis()).apply()
        }
    }

    /**
     * Checks if anonymous analytics collection is enabled by the user (default: true).
     */
    fun isAnalyticsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ANALYTICS_ENABLED, true)
    }

    /**
     * Updates user's opt-in/opt-out preference.
     */
    fun setAnalyticsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ANALYTICS_ENABLED, enabled).apply()
        try {
            firebaseAnalytics?.setAnalyticsCollectionEnabled(enabled)
            if (!enabled) {
                firebaseAnalytics?.resetAnalyticsData()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to toggle analytics collection: ${e.message}")
        }
        Log.i(TAG, "User updated analytics consent: $enabled")
    }

    /**
     * Clears all local analytics and retention cache.
     */
    fun resetLocalAnalytics(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        try {
            firebaseAnalytics?.resetAnalyticsData()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to reset Firebase analytics data: ${e.message}")
        }
    }

    /**
     * Tracks daily session start and updates anonymous retention milestones (D1, D3, D7, D30).
     */
    fun trackSessionStart(context: Context) {
        if (!isAnalyticsEnabled(context)) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastActiveDate = prefs.getString(KEY_LAST_ACTIVE_DATE, null)
        val totalSessions = prefs.getInt(KEY_TOTAL_SESSIONS, 0) + 1
        var streak = prefs.getInt(KEY_CURRENT_STREAK_DAYS, 1)

        val firstInstallTime = prefs.getLong(KEY_FIRST_INSTALL_TIME, System.currentTimeMillis())
        val daysSinceInstall = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - firstInstallTime).toInt()

        if (lastActiveDate != todayStr) {
            // New day session
            if (lastActiveDate != null) {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val lastDate = sdf.parse(lastActiveDate)
                    val todayDate = sdf.parse(todayStr)
                    if (lastDate != null && todayDate != null) {
                        val diffDays = TimeUnit.MILLISECONDS.toDays(todayDate.time - lastDate.time)
                        if (diffDays == 1L) {
                            streak += 1
                        } else if (diffDays > 1L) {
                            streak = 1 // reset streak
                        }
                    }
                } catch (e: Exception) {
                    streak = 1
                }
            }

            prefs.edit()
                .putString(KEY_LAST_ACTIVE_DATE, todayStr)
                .putInt(KEY_CURRENT_STREAK_DAYS, streak)
                .putInt(KEY_TOTAL_SESSIONS, totalSessions)
                .apply()

            // Check retention milestones
            checkAndLogRetentionMilestones(prefs, daysSinceInstall)

            // Log session retention event
            val params = Bundle().apply {
                putInt("streak_days", streak)
                putInt("days_since_install", daysSinceInstall)
                putInt("total_sessions", totalSessions)
            }
            logEvent("daily_retention_active", params)
        } else {
            prefs.edit().putInt(KEY_TOTAL_SESSIONS, totalSessions).apply()
        }

        logEvent("user_session_start", Bundle().apply {
            putInt("session_number", totalSessions)
            putInt("streak_days", streak)
        })
    }

    private fun checkAndLogRetentionMilestones(prefs: android.content.SharedPreferences, daysSinceInstall: Int) {
        val editor = prefs.edit()
        if (daysSinceInstall >= 1 && !prefs.getBoolean(KEY_D1_RECORDED, false)) {
            editor.putBoolean(KEY_D1_RECORDED, true)
            logEvent("retention_cohort_d1", Bundle().apply { putInt("milestone_day", 1) })
        }
        if (daysSinceInstall >= 3 && !prefs.getBoolean(KEY_D3_RECORDED, false)) {
            editor.putBoolean(KEY_D3_RECORDED, true)
            logEvent("retention_cohort_d3", Bundle().apply { putInt("milestone_day", 3) })
        }
        if (daysSinceInstall >= 7 && !prefs.getBoolean(KEY_D7_RECORDED, false)) {
            editor.putBoolean(KEY_D7_RECORDED, true)
            logEvent("retention_cohort_d7", Bundle().apply { putInt("milestone_day", 7) })
        }
        if (daysSinceInstall >= 30 && !prefs.getBoolean(KEY_D30_RECORDED, false)) {
            editor.putBoolean(KEY_D30_RECORDED, true)
            logEvent("retention_cohort_d30", Bundle().apply { putInt("milestone_day", 30) })
        }
        editor.apply()
    }

    /**
     * Tracks screen transitions anonymously.
     */
    fun trackScreenView(screenName: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "KavyaSetu_$screenName")
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    /**
     * Tracks poetry interactions (read, bookmark, copy, share, audio recital).
     */
    fun trackPoetryInteraction(
        action: String, // "read", "bookmark", "copy", "share", "audio_recite", "tarannum_play"
        category: String,
        language: String,
        extraTag: String? = null
    ) {
        val params = Bundle().apply {
            putString("action_type", action)
            putString("category", category)
            putString("language", language)
            extraTag?.let { putString("extra_tag", it) }
        }
        logEvent("poetry_interaction", params)
    }

    /**
     * Tracks creative and classical literary studio tools.
     */
    fun trackFeatureUsed(featureName: String, actionDetail: String? = null) {
        val params = Bundle().apply {
            putString("feature_name", featureName)
            actionDetail?.let { putString("action_detail", it) }
        }
        logEvent("feature_used", params)
    }

    /**
     * Tracks community & game engagement (Sher-Baazi, Antakshari, Dastaangoi, Raat Mehfil).
     */
    fun trackCommunityEngagement(activityType: String, status: String) {
        val params = Bundle().apply {
            putString("activity_type", activityType)
            putString("status", status)
        }
        logEvent("community_engagement", params)
    }

    /**
     * Tracks automated in-app updates and background update checks.
     */
    fun trackAutoUpdate(
        eventType: String, // "check_triggered", "update_available", "auto_download_started", "update_applied", "notification_sent"
        targetVersion: String,
        isAutomatic: Boolean = true
    ) {
        val params = Bundle().apply {
            putString("update_event", eventType)
            putString("target_version", targetVersion)
            putBoolean("is_automatic", isAutomatic)
        }
        logEvent("app_auto_update", params)
    }

    /**
     * Tracks reading goals and user achievement milestones.
     */
    fun trackGoalAchieved(goalCount: Int, streakDays: Int) {
        val params = Bundle().apply {
            putInt("goal_count", goalCount)
            putInt("streak_days", streakDays)
        }
        logEvent("reading_goal_achieved", params)
    }

    /**
     * Safely logs event to Firebase Analytics without crashing if disabled or offline.
     */
    private fun logEvent(eventName: String, params: Bundle) {
        try {
            firebaseAnalytics?.logEvent(eventName, params)
            Log.d(TAG, "Logged event: $eventName -> $params")
        } catch (e: Exception) {
            Log.w(TAG, "Could not log event $eventName: ${e.message}")
        }
    }
}
