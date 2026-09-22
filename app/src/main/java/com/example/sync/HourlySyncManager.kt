package com.example.sync

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.remote.FirebaseService
import com.example.data.repository.ShayariOfTheDayManager
import com.example.data.repository.ShayariRepository
import com.example.widget.ShayariDailyWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Hourly Auto-Update & Auto-Sync Engine for Kavya Setu.
 * Periodically updates trending metrics, rotates curated verses,
 * synchronizes remote Firestore updates, and pushes fresh state to the homescreen widget.
 */
class HourlySyncReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Hourly sync triggered: auto-refreshing poetry, trending picks, and widget")
        val goAsync = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                HourlySyncManager.performSync(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error during hourly background refresh: ${e.message}", e)
            } finally {
                goAsync.finish()
            }
        }
    }

    companion object {
        private const val TAG = "HourlySyncReceiver"
    }
}

object HourlySyncManager {
    private const val TAG = "HourlySyncManager"
    private const val REQUEST_CODE = 4488
    const val ACTION_HOURLY_SYNC = "com.example.sync.ACTION_HOURLY_SYNC"

    /**
     * Schedules inexact hourly repeating background sync.
     * Respects Android battery optimization and Doze mode.
     */
    fun scheduleHourlySync(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HourlySyncReceiver::class.java).apply {
            action = ACTION_HOURLY_SYNC
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent
            )
            Log.d(TAG, "Successfully scheduled hourly auto-refresh alarm")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to schedule hourly alarm: ${e.message}")
        }
    }

    /**
     * Executes the full auto-update and refresh pipeline:
     * 1. Fetches latest Firestore remote community submissions.
     * 2. Recalculates trending poetry based on user likes, reading activity, and time decay.
     * 3. Resolves and updates the daily pick / homescreen widget.
     */
    suspend fun performSync(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val firebaseService = FirebaseService()
        val repository = ShayariRepository(database.shayariDao(), firebaseService)

        // 1. Sync remote shayaris from Firestore
        try {
            val remoteList = firebaseService.fetchRemoteShayaris()
            if (remoteList.isNotEmpty()) {
                val entities = remoteList.map { com.example.data.local.ShayariEntity.fromDomain(it) }
                database.shayariDao().insertShayaris(entities)
                Log.d(TAG, "Synced ${remoteList.size} remote poems to local database")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Remote Firestore sync skipped/offline: ${e.message}")
        }

        // 2. Refresh trending metrics
        try {
            repository.checkAndUpdateDailyTrending(forceRefresh = false)
        } catch (e: Exception) {
            Log.w(TAG, "Daily trending refresh error: ${e.message}")
        }

        // 3. Update Homescreen Widget
        try {
            ShayariOfTheDayManager.resolveDailyPick(context, database)
            ShayariDailyWidgetProvider.updateAllWidgets(context)
        } catch (e: Exception) {
            Log.w(TAG, "Widget refresh error: ${e.message}")
        }

        // 4. Update Tarhi Mushaira and Raat Mehfil hourly states
        try {
            com.example.data.local.TarhiMushairaManager.resolveCurrentChallenge()
            com.example.data.local.RaatMehfilManager.refreshMidnightStatus()
            Log.d(TAG, "Tarhi challenge and Raat Mehfil state refreshed")
        } catch (e: Exception) {
            Log.w(TAG, "Feature state refresh error: ${e.message}")
        }
    }
}
