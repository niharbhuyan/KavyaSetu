package com.example.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import com.example.data.repository.ShayariOfTheDayManager
import com.example.notification.ShayariFirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ShayariDailyWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_WIDGET"
        const val PREFS_NAME = "kavya_setu_daily_widget_prefs"
        const val PREF_KEY_LAST_DATE = "pref_widget_last_date"
        const val PREF_KEY_OFFSET = "pref_widget_offset"
        private const val WIDGET_ALARM_REQUEST_CODE = 9988

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, ShayariDailyWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, ShayariDailyWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }

        fun schedule24HourUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ShayariDailyWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                WIDGET_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule for the next calendar midnight (24-hour cycle)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, 1)
            }

            try {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } catch (_: Exception) {
                // Ignore security or permission restrictions gracefully
            }
        }

        fun cancel24HourUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, ShayariDailyWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                WIDGET_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }

        fun pinWidgetToHomeScreen(context: Context, onResult: (Boolean) -> Unit = {}) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                val provider = ComponentName(context, ShayariDailyWidgetProvider::class.java)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    val pinnedIntent = Intent(context, ShayariDailyWidgetProvider::class.java)
                    val successCallback = PendingIntent.getBroadcast(
                        context,
                        0,
                        pinnedIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    appWidgetManager.requestPinAppWidget(provider, null, successCallback)
                    onResult(true)
                    return
                }
            }
            onResult(false)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        schedule24HourUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancel24HourUpdate(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        schedule24HourUpdate(context)

        val goAsync = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastDate = prefs.getString(PREF_KEY_LAST_DATE, null)
                val offset = if (lastDate == todayStr) {
                    prefs.getInt(PREF_KEY_OFFSET, 0)
                } else {
                    // New day (24-hour cycle rolled over) -> reset offset to today's natural pick
                    prefs.edit().putString(PREF_KEY_LAST_DATE, todayStr).putInt(PREF_KEY_OFFSET, 0).apply()
                    0
                }

                // Select featured poem from collection
                val collection = database.shayariDao().getAllApprovedShayaris().firstOrNull() ?: emptyList()
                val domainList = collection.map { it.toDomain() }
                val featuredPoem = ShayariOfTheDayManager.selectDailyShayari(domainList, todayStr, offset)
                    ?: database.shayariDao().getDailyPick().firstOrNull()?.toDomain()
                    ?: domainList.firstOrNull()

                if (featuredPoem != null) {
                    database.shayariDao().clearDailyPicks()
                    database.shayariDao().setDailyPick(featuredPoem.id)
                }

                for (appWidgetId in appWidgetIds) {
                    updateWidgetInstance(context, appWidgetManager, appWidgetId, featuredPoem)
                }
            } finally {
                goAsync.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val currentOffset = prefs.getInt(PREF_KEY_OFFSET, 0)
            prefs.edit().putInt(PREF_KEY_OFFSET, currentOffset + 1).apply()

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, ShayariDailyWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            if (ids.isNotEmpty()) {
                onUpdate(context, appWidgetManager, ids)
            }
        }
    }

    private fun updateWidgetInstance(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        shayari: Shayari?
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_shayari_daily)

        val dateStr = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
        views.setTextViewText(R.id.widget_date, dateStr)

        if (shayari != null) {
            views.setTextViewText(R.id.widget_text_lines, shayari.lines)
            val authorSignature = if (shayari.penName.isNotBlank()) {
                "— ${shayari.author} '${shayari.penName}'"
            } else {
                "— ${shayari.author}"
            }
            views.setTextViewText(R.id.widget_text_author, authorSignature)

            val emotion = Emotion.fromCode(shayari.emotion)
            val lang = Language.fromCode(shayari.language)
            val categoryPart = if (shayari.category.isNotBlank()) " • ${shayari.category.replaceFirstChar { it.uppercase() }}" else ""
            views.setTextViewText(R.id.widget_text_tag, "${emotion.emoji} ${emotion.englishLabel} • ${lang.displayName}$categoryPart")
        } else {
            views.setTextViewText(
                R.id.widget_text_lines,
                "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले...\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले"
            )
            views.setTextViewText(R.id.widget_text_author, "— Mirza Ghalib")
            views.setTextViewText(R.id.widget_text_tag, "❤️ Ishq • हिंदी • Love")
        }

        // Tap on widget opens MainActivity with deep link directly to the featured poem
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_SOURCE", "WIDGET_DAILY_PICK")
            if (shayari != null) {
                data = Uri.parse("shayari://detail?id=${shayari.id}")
                putExtra(ShayariFirebaseMessagingService.EXTRA_SHAYARI_ID, shayari.id)
                putExtra("id", shayari.id)
            }
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, openPendingIntent)

        // Refresh button click to cycle through the featured collection
        val refreshIntent = Intent(context, ShayariDailyWidgetProvider::class.java).apply {
            action = ACTION_REFRESH_WIDGET
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
