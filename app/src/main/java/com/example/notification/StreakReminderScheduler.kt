package com.example.notification

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.PoeticStreakManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object StreakReminderScheduler {
    private const val TAG = "StreakReminderScheduler"
    const val UNIQUE_PERIODIC_WORK_NAME = "daily_poetic_streak_work"
    const val UNIQUE_TEST_WORK_NAME = "test_poetic_streak_work"

    /**
     * Schedules a 24-hour repeating WorkManager task targeting the specified hour and minute.
     * Defaults to evening prime reading hour (7:30 PM).
     */
    fun scheduleDailyReminder(context: Context, hourOfDay: Int = 19, minute: Int = 30) {
        val workManager = WorkManager.getInstance(context)

        // Save preferences
        PoeticStreakManager.setReminderTime(context, hourOfDay, minute)
        PoeticStreakManager.setReminderEnabled(context, true)

        val currentCal = Calendar.getInstance()
        val targetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(currentCal)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelayMs = targetCal.timeInMillis - currentCal.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyPoeticStreakReminderWorker>(
            24, TimeUnit.HOURS,
            1, TimeUnit.HOURS // 1 hour flex interval
        )
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag("streak_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )

        Log.d(TAG, "Scheduled daily poetic streak WorkManager reminder with initial delay ${initialDelayMs / 1000 / 60} mins (targeting $hourOfDay:$minute)")
    }

    /**
     * Enqueues an immediate one-shot WorkManager task for instant preview / verification.
     */
    fun triggerInstantTestReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val oneTimeWork = OneTimeWorkRequestBuilder<DailyPoeticStreakReminderWorker>()
            .addTag("test_streak_reminder")
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_TEST_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWork
        )
        Log.d(TAG, "Enqueued instant test streak reminder via WorkManager")
    }

    /**
     * Cancels any scheduled streak reminder WorkManager jobs.
     */
    fun cancelDailyReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
        PoeticStreakManager.setReminderEnabled(context, false)
        Log.d(TAG, "Cancelled scheduled daily poetic streak WorkManager reminders.")
    }

    fun updateSettings(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        if (enabled) {
            scheduleDailyReminder(context, hour, minute)
        } else {
            cancelDailyReminder(context)
        }
    }
}
