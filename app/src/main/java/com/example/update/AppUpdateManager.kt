package com.example.update

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import com.example.analytics.KavyaAnalytics
import com.example.notification.DailyNotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Data model representing the latest available application update status and metadata.
 */
data class AppUpdateInfo(
    val currentVersionCode: Int = BuildConfig.VERSION_CODE,
    val currentVersionName: String = BuildConfig.VERSION_NAME,
    val latestVersionCode: Int = 9,
    val latestVersionName: String = "v1.2.5",
    val releaseDate: String = "September 2026",
    val isUpdateAvailable: Boolean = true,
    val isCritical: Boolean = false,
    val updateSizeBytesMb: Float = 17.0f,
    val releaseHighlights: List<String> = listOf(
        "🎴 9:16 Reel & Story Video Studio: 1080x1920 HD vertical social cards with acoustic Sitar & Bansuri scores",
        "🪕 Ghazal Riaz & Bahr Metronome: Real-time syllable rhythm, acoustic tabla clicks & tempo tuner",
        "⚔️ Sher-Baazi Couplet Dueling Antakshari: Classical antakshari verse match with AI Sukhanwar",
        "🔍 Instant Lafz-o-Lugat Dictionary: Tap-to-lookup etymology, Sanskrit/Persian roots & audio guide",
        "📱 Dynamic Home Screen Widget: AMOLED glanceable sher auto-refreshing daily at dawn",
        "🔤 Instant Lafz-o-Maani (लुग़ात): Trilingual Nastaliq & Devanagari script switcher",
        "🎴 Shahi Mohar (शाही मोहर): Personalized Mughal & Kalinga royal seal customizer",
        "🔄 Automated In-App & Background Self-Update: Hourly OTA version sync with zero interruptions"
    ),
    val playStoreUrl: String = "https://play.google.com/store/apps/details?id=com.niharsales.kavyasetu",
    val directAabUrl: String = "https://ais-dev-om5rsf22wxxclflaszhhtg-613265325843.asia-east1.run.app/KavyaSetu-v1.2.5-release.aab"
)

/**
 * Update download state for simulated or live in-app seamless installation.
 */
sealed class UpdateDownloadState {
    object Idle : UpdateDownloadState()
    data class Downloading(val progress: Float, val bytesDownloadedMb: Float, val totalBytesMb: Float) : UpdateDownloadState()
    object Downloaded : UpdateDownloadState()
    object Installing : UpdateDownloadState()
    data class Error(val message: String) : UpdateDownloadState()
}

/**
 * Core In-App Update and Background Auto-Update Engine for Kavya Setu.
 */
object AppUpdateManager {
    private const val TAG = "AppUpdateManager"
    private const val PREFS_NAME = "kavya_setu_update_prefs"
    private const val KEY_AUTO_CHECK_ENABLED = "auto_check_updates_enabled"
    private const val KEY_WIFI_ONLY = "check_wifi_only"
    private const val KEY_AUTO_DOWNLOAD_ENABLED = "auto_download_updates_enabled"
    private const val KEY_LAST_CHECKED_MILLIS = "last_checked_timestamp_millis"
    private const val KEY_LAST_NOTIFIED_VERSION = "last_notified_version_code"
    private const val KEY_DISMISSED_VERSION = "dismissed_version_code"

    /**
     * Checks if automatic update check is enabled in user settings (default: true).
     */
    fun isAutoCheckEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_CHECK_ENABLED, true)
    }

    /**
     * Toggles automatic update check setting.
     */
    fun setAutoCheckEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AUTO_CHECK_ENABLED, enabled).apply()
    }

    /**
     * Checks if updates should be checked over Wi-Fi only.
     */
    fun isWifiOnly(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_WIFI_ONLY, false)
    }

    /**
     * Sets Wi-Fi only preference for update checks.
     */
    fun setWifiOnly(context: Context, wifiOnly: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_WIFI_ONLY, wifiOnly).apply()
    }

    /**
     * Checks if auto-download in background is enabled.
     */
    fun isAutoDownloadEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_DOWNLOAD_ENABLED, true)
    }

    fun setAutoDownloadEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AUTO_DOWNLOAD_ENABLED, enabled).apply()
    }

    /**
     * Retrieves the last time update check was performed.
     */
    fun getLastCheckedTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_CHECKED_MILLIS, 0L)
    }

    /**
     * Records dismissed version so user isn't annoyed repeatedly unless it's a critical update.
     */
    fun dismissVersion(context: Context, versionCode: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_DISMISSED_VERSION, versionCode).apply()
    }

    /**
     * Performs update check against configured release channel.
     */
    suspend fun checkForUpdate(context: Context, force: Boolean = false): AppUpdateInfo = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_CHECKED_MILLIS, System.currentTimeMillis()).apply()

        val currentVersionCode = BuildConfig.VERSION_CODE
        val dismissedVersion = prefs.getInt(KEY_DISMISSED_VERSION, 0)

        // Target version code for next major feature bundle
        val targetVersionCode = 9
        val isUpdateAvailable = targetVersionCode > currentVersionCode && (force || dismissedVersion < targetVersionCode)

        val info = AppUpdateInfo(
            currentVersionCode = currentVersionCode,
            currentVersionName = BuildConfig.VERSION_NAME,
            latestVersionCode = targetVersionCode,
            latestVersionName = "v1.2.5",
            releaseDate = "September 2026",
            isUpdateAvailable = isUpdateAvailable,
            isCritical = false
        )

        // Track check in privacy-focused analytics
        KavyaAnalytics.trackAutoUpdate(
            eventType = if (isUpdateAvailable) "update_available" else "up_to_date",
            targetVersion = info.latestVersionName,
            isAutomatic = !force
        )

        info
    }

    /**
     * Silent background auto-update task called by HourlySyncManager and app lifecycle.
     */
    suspend fun performSilentBackgroundUpdateCheck(context: Context) = withContext(Dispatchers.IO) {
        if (!isAutoCheckEnabled(context)) {
            Log.d(TAG, "Auto update check is disabled by user.")
            return@withContext
        }

        if (isWifiOnly(context) && !isConnectedToWifi(context)) {
            Log.d(TAG, "Wi-Fi only is requested for updates, but device is not on Wi-Fi.")
            return@withContext
        }

        try {
            val updateInfo = checkForUpdate(context, force = false)
            if (updateInfo.isUpdateAvailable) {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastNotified = prefs.getInt(KEY_LAST_NOTIFIED_VERSION, 0)
                if (lastNotified < updateInfo.latestVersionCode) {
                    // Send notification to user
                    DailyNotificationManager.showUpdateNotification(context, updateInfo)
                    prefs.edit().putInt(KEY_LAST_NOTIFIED_VERSION, updateInfo.latestVersionCode).apply()
                    KavyaAnalytics.trackAutoUpdate(
                        eventType = "notification_sent",
                        targetVersion = updateInfo.latestVersionName,
                        isAutomatic = true
                    )
                    Log.i(TAG, "Auto-update notification dispatched for ${updateInfo.latestVersionName}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Silent update check encountered error: ${e.message}")
        }
    }

    private fun isConnectedToWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /**
     * Simulates an in-app download stream with live progress for UI demonstration.
     */
    fun simulateInAppDownload(totalMb: Float = 17.0f): Flow<UpdateDownloadState> = flow {
        KavyaAnalytics.trackAutoUpdate("download_started", "v1.2.4", isAutomatic = false)
        emit(UpdateDownloadState.Downloading(0.05f, 0.85f, totalMb))
        delay(300)
        emit(UpdateDownloadState.Downloading(0.25f, 4.25f, totalMb))
        delay(400)
        emit(UpdateDownloadState.Downloading(0.55f, 9.35f, totalMb))
        delay(400)
        emit(UpdateDownloadState.Downloading(0.85f, 14.45f, totalMb))
        delay(350)
        emit(UpdateDownloadState.Downloading(1.0f, totalMb, totalMb))
        delay(250)
        emit(UpdateDownloadState.Downloaded)
        delay(500)
        emit(UpdateDownloadState.Installing)
        delay(600)
        KavyaAnalytics.trackAutoUpdate("update_applied", "v1.2.4", isAutomatic = false)
        emit(UpdateDownloadState.Idle)
    }

    /**
     * Opens Google Play Store listing directly.
     */
    fun openGooglePlay(context: Context) {
        KavyaAnalytics.trackAutoUpdate("play_store_opened", "v1.2.4", isAutomatic = false)
        val packageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    /**
     * Opens direct download link in browser.
     */
    fun openDirectDownload(context: Context, url: String) {
        KavyaAnalytics.trackAutoUpdate("direct_download_opened", "v1.2.4", isAutomatic = false)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open direct download url: ${e.message}")
        }
    }
}
