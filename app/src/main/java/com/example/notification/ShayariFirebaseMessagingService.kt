package com.example.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.Shayari
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Firebase Cloud Messaging Service for 'Shayari of the Day' morning push notifications.
 * Handles background & foreground message reception with custom deep links to the details view.
 */
class ShayariFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token: $token")
        saveTokenToPreferences(this, token)

        // Automatically subscribe device to the daily morning shayari topic
        subscribeToDailyTopic()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // 1. Extract payload data
        val data = remoteMessage.data
        val shayariId = data[KEY_SHAYARI_ID]
            ?: data["id"]
            ?: "daily_pick"

        val title = data[KEY_TITLE]
            ?: remoteMessage.notification?.title
            ?: "🌅 Morning Shayari • ଆଜିର କବିତା"

        val author = data[KEY_AUTHOR] ?: "Classical Master"
        val language = data[KEY_LANGUAGE] ?: "Multilingual"
        val body = data[KEY_BODY]
            ?: remoteMessage.notification?.body
            ?: "\"हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले...\"\n— $author"

        val deepLinkUri = data[KEY_DEEP_LINK] ?: "shayari://detail?id=$shayariId"

        // 2. Present push notification with deep link to details view
        showPushNotification(
            context = this,
            title = title,
            body = body,
            shayariId = shayariId,
            deepLinkUri = deepLinkUri,
            author = author,
            language = language
        )
    }

    private fun subscribeToDailyTopic() {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_DAILY_SHAYARI)
                .addOnSuccessListener {
                    Log.i(TAG, "Subscribed successfully to FCM topic: $TOPIC_DAILY_SHAYARI")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to subscribe to FCM topic: ${e.message}")
                }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseMessaging subscribe error: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "ShayariFCM"
        const val TOPIC_DAILY_SHAYARI = "daily_morning_shayari"
        const val PREFS_NAME = "shayari_fcm_prefs"
        const val PREF_KEY_TOKEN = "fcm_device_token"

        const val EXTRA_SHAYARI_ID = "com.example.shayari.EXTRA_SHAYARI_ID"
        const val EXTRA_DEEP_LINK = "com.example.shayari.EXTRA_DEEP_LINK"

        const val KEY_SHAYARI_ID = "shayari_id"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_AUTHOR = "author"
        const val KEY_LANGUAGE = "language"
        const val KEY_DEEP_LINK = "deep_link"

        private const val NOTIFICATION_ID_FCM = 2002

        fun saveTokenToPreferences(context: Context, token: String) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(PREF_KEY_TOKEN, token).apply()
        }

        fun getSavedToken(context: Context): String? {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(PREF_KEY_TOKEN, null)
        }

        /**
         * Initialize FCM on app startup: ensures notification channel exists,
         * registers topic subscription and updates device token.
         */
        fun initialize(context: Context, onTokenRetrieved: ((String) -> Unit)? = null) {
            DailyNotificationManager.createNotificationChannel(context)

            try {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            Log.d(TAG, "Retrieved FCM Token: $token")
                            saveTokenToPreferences(context, token)
                            onTokenRetrieved?.invoke(token)
                        } else {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        }
                    }

                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_DAILY_SHAYARI)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed to FCM topic '$TOPIC_DAILY_SHAYARI'")
                        }
                    }
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseMessaging initialization fallback: ${e.message}")
            }
        }

        /**
         * Shows push notification that deep links straight to the Shayari Details View.
         */
        fun showPushNotification(
            context: Context,
            title: String,
            body: String,
            shayariId: String,
            deepLinkUri: String = "shayari://detail?id=$shayariId",
            author: String = "",
            language: String = ""
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            DailyNotificationManager.createNotificationChannel(context)

            // Deep link Intent targeting MainActivity with URI and extra
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(deepLinkUri)
                putExtra(EXTRA_SHAYARI_ID, shayariId)
                putExtra(EXTRA_DEEP_LINK, deepLinkUri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                shayariId.hashCode(),
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val bigTextContent = if (author.isNotBlank()) {
                "$body\n\n— $author (${language.replaceFirstChar { it.uppercase() }})"
            } else {
                body
            }

            val notification = NotificationCompat.Builder(context, DailyNotificationManager.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body.lines().firstOrNull() ?: body)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(bigTextContent)
                        .setSummaryText("Shayari of the Day • Tap for details")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                // Action button: Directly opens the details view
                .addAction(
                    android.R.drawable.ic_menu_view,
                    "Read Details",
                    pendingIntent
                )
                .build()

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID_FCM, notification)
        }

        /**
         * Simulates an incoming FCM Daily Push Notification with deep link for testing.
         */
        fun simulateDailyMorningFcmPush(context: Context, customShayari: Shayari? = null) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = AppDatabase.getDatabase(context)
                val targetShayari = customShayari
                    ?: database.shayariDao().getDailyPick().firstOrNull()?.toDomain()
                    ?: database.shayariDao().getAllShayaris().firstOrNull()?.firstOrNull()?.toDomain()

                if (targetShayari != null) {
                    showPushNotification(
                        context = context,
                        title = "🌅 FCM: Shayari of the Day • ଆଜିର କବିତା",
                        body = targetShayari.lines,
                        shayariId = targetShayari.id,
                        deepLinkUri = "shayari://detail?id=${targetShayari.id}",
                        author = targetShayari.author,
                        language = targetShayari.language
                    )
                } else {
                    showPushNotification(
                        context = context,
                        title = "🌅 FCM: Shayari of the Day",
                        body = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले",
                        shayariId = "s1",
                        deepLinkUri = "shayari://detail?id=s1",
                        author = "Mirza Ghalib",
                        language = "hindi"
                    )
                }
            }
        }
    }
}
