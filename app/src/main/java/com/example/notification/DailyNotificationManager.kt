package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val database = AppDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            val dailyShayari = database.shayariDao().getDailyPick().firstOrNull()?.toDomain()
                ?: database.shayariDao().getAllShayaris().firstOrNull()?.firstOrNull()?.toDomain()

            val title = "🌅 Morning Shayari • ନୂଆ କବିତା"
            val body = if (dailyShayari != null) {
                "${dailyShayari.lines}\n— ${dailyShayari.author}"
            } else {
                "\"हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले...\"\nDiscover today's trending verses in Hindi, Odia & English."
            }

            DailyNotificationManager.showNotification(
                context = context,
                title = title,
                content = body,
                shayariId = dailyShayari?.id
            )
            // Re-schedule for next morning
            DailyNotificationManager.scheduleDailyMorningAlarm(context)
        }
    }
}

object DailyNotificationManager {
    const val CHANNEL_ID = "daily_shayari_channel"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Morning Shayari"
            val descriptionText = "Fresh daily trending couplets and poetic thoughts every morning"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleDailyMorningAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DailyNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Alarm permission handling
        }
    }

    fun showNotification(context: Context, title: String, content: String, shayariId: String? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            if (!shayariId.isNullOrBlank()) {
                data = android.net.Uri.parse("shayari://detail?id=$shayariId")
                putExtra(ShayariFirebaseMessagingService.EXTRA_SHAYARI_ID, shayariId)
                putExtra(ShayariFirebaseMessagingService.EXTRA_DEEP_LINK, "shayari://detail?id=$shayariId")
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            (shayariId ?: "default").hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content.lines().firstOrNull() ?: content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .apply {
                if (!shayariId.isNullOrBlank()) {
                    addAction(
                        android.R.drawable.ic_menu_view,
                        "Read Details",
                        pendingIntent
                    )
                }
            }
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
