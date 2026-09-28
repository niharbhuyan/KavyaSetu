package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.PoeticStreakManager
import com.example.data.repository.ShayariOfTheDayManager
import kotlinx.coroutines.flow.firstOrNull

/**
 * Android WorkManager CoroutineWorker for scheduling daily reminders to check
 * the user's poetic streak and read the curated 'Daily Pick' poem.
 */
class DailyPoeticStreakReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "DailyPoeticStreakReminderWorker running...")
        return try {
            // Check if reminder is enabled in user settings
            if (!PoeticStreakManager.isReminderEnabled(appContext)) {
                Log.d(TAG, "Streak reminders are disabled by user preference.")
                return Result.success()
            }

            // Refresh current streak state
            PoeticStreakManager.initialize(appContext)
            val streakState = PoeticStreakManager.streakState.value

            // Resolve today's Daily Pick poem
            val database = AppDatabase.getDatabase(appContext)
            val dailyShayari = ShayariOfTheDayManager.resolveDailyPick(appContext, database)
                ?: database.shayariDao().getDailyPick().firstOrNull()?.toDomain()
                ?: database.shayariDao().getAllShayaris().firstOrNull()?.firstOrNull()?.toDomain()

            showStreakReminderNotification(
                context = appContext,
                streak = streakState.currentStreak,
                isCompletedToday = streakState.isCompletedToday,
                streakTitle = streakState.streakTitle,
                dailyShayari = dailyShayari
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in DailyPoeticStreakReminderWorker: ${e.message}", e)
            Result.retry()
        }
    }

    private fun showStreakReminderNotification(
        context: Context,
        streak: Int,
        isCompletedToday: Boolean,
        streakTitle: String,
        dailyShayari: com.example.data.model.Shayari?
    ) {
        // Notification permission check for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Skipping notification.")
                return
            }
        }

        createNotificationChannel(context)

        val title: String
        val body: String
        val previewVerse = dailyShayari?.lines?.lines()?.firstOrNull() ?: dailyShayari?.lines ?: "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले..."
        val author = dailyShayari?.author ?: "मिर्ज़ा ग़ालिब"

        if (!isCompletedToday) {
            // High urgency motivational reminder to preserve streak
            title = if (streak > 0) {
                "🔥 Keep Your $streak-Day Poetic Streak Alive!"
            } else {
                "🌅 Today's Daily Pick Awaits You!"
            }
            body = "Today's Featured Verse by $author:\n\"$previewVerse\"\nRead or compose a couplet to maintain your $streakTitle streak!"
        } else {
            // Celebration reminder to enjoy the evening pick
            title = "✨ $streak-Day Streak Secured! • $streakTitle"
            body = "Today's Daily Pick: \"$previewVerse\"\n— $author. Tap to listen and explore."
        }

        // Deep Link Intent to open MainActivity and highlight Daily Pick & Streak
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_STREAK, true)
            putExtra(EXTRA_OPEN_DAILY_PICK, true)
            if (dailyShayari != null) {
                putExtra(ShayariFirebaseMessagingService.EXTRA_SHAYARI_ID, dailyShayari.id)
                putExtra(ShayariFirebaseMessagingService.EXTRA_DEEP_LINK, "shayari://detail?id=${dailyShayari.id}")
                data = android.net.Uri.parse("shayari://detail?id=${dailyShayari.id}")
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            STREAK_NOTIFICATION_ID,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(previewVerse)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
                    .setSummaryText(if (isCompletedToday) "🔥 $streak Days Active" else "⏳ Streak Pending")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_view,
                "📖 Read Daily Pick",
                pendingIntent
            )
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(STREAK_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Poetic Streak & Daily Pick Reminders"
            val descriptionText = "Daily reminders to maintain your poetic streak and enjoy curated poetry"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "StreakReminderWorker"
        const val CHANNEL_ID = "kavya_poetic_streak_channel"
        const val STREAK_NOTIFICATION_ID = 2026
        const val EXTRA_OPEN_STREAK = "open_streak_dialog"
        const val EXTRA_OPEN_DAILY_PICK = "open_daily_pick"
    }
}
