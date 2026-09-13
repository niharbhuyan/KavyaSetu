package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShayariDailyWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_WIDGET"

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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val goAsync = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val dailyShayari = database.shayariDao().getDailyPick().firstOrNull()?.toDomain()
                    ?: database.shayariDao().getAllApprovedShayaris().firstOrNull()?.firstOrNull()?.toDomain()

                for (appWidgetId in appWidgetIds) {
                    updateWidgetInstance(context, appWidgetManager, appWidgetId, dailyShayari)
                }
            } finally {
                goAsync.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, ShayariDailyWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, ids)
        }
    }

    private fun updateWidgetInstance(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        shayari: Shayari?
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_shayari_daily)

        val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
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
            views.setTextViewText(R.id.widget_text_tag, "${emotion.emoji} ${emotion.englishLabel} • ${lang.displayName}")
        } else {
            views.setTextViewText(
                R.id.widget_text_lines,
                "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले...\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले"
            )
            views.setTextViewText(R.id.widget_text_author, "— Mirza Ghalib")
            views.setTextViewText(R.id.widget_text_tag, "❤️ Ishq • हिंदी")
        }

        // Tap on widget opens MainActivity
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_SOURCE", "WIDGET_SHAYARI_DAILY")
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, openPendingIntent)

        // Refresh button click
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
