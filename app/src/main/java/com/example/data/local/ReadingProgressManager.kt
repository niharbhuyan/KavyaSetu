package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Shayari
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReadingProgressState(
    val lastReadPoemId: String? = null,
    val lastReadAuthor: String? = null,
    val lastReadSnippet: String? = null,
    val lastReadCategory: String? = null,
    val lastReadLanguage: String? = null,
    val lastReadTimestamp: Long = 0L,
    val versesReadToday: Int = 0,
    val dailyGoal: Int = 10,
    val totalVersesRead: Int = 0,
    val readingStreakDays: Int = 1
) {
    val progressPercent: Float
        get() = if (dailyGoal > 0) (versesReadToday.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f

    val isGoalReached: Boolean
        get() = versesReadToday >= dailyGoal

    val hasBookmark: Boolean
        get() = !lastReadPoemId.isNullOrBlank()
}

object ReadingProgressManager {
    private const val PREFS_NAME = "kavya_setu_reading_progress"

    private const val KEY_LAST_READ_ID = "last_read_poem_id"
    private const val KEY_LAST_READ_AUTHOR = "last_read_poem_author"
    private const val KEY_LAST_READ_SNIPPET = "last_read_poem_snippet"
    private const val KEY_LAST_READ_CAT = "last_read_poem_cat"
    private const val KEY_LAST_READ_LANG = "last_read_poem_lang"
    private const val KEY_LAST_READ_TIME = "last_read_poem_time"

    private const val KEY_VERSES_READ_TODAY = "verses_read_today"
    private const val KEY_DAILY_GOAL = "daily_reading_goal"
    private const val KEY_TOTAL_VERSES = "total_verses_read"
    private const val KEY_LAST_DATE = "last_reading_date"
    private const val KEY_STREAK_DAYS = "reading_streak_days"

    private val _readingState = MutableStateFlow(ReadingProgressState())
    val readingState: StateFlow<ReadingProgressState> = _readingState.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    fun initialize(context: Context) {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""

        val readToday = if (lastDate == today) {
            prefs.getInt(KEY_VERSES_READ_TODAY, 0)
        } else {
            // New day: reset today's counter, evaluate streak
            0
        }

        val lastStreak = prefs.getInt(KEY_STREAK_DAYS, 1)
        val streak = if (lastDate.isNotBlank() && lastDate != today) {
            val yesterdayCal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }
            val yesterdayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(yesterdayCal.time)
            if (lastDate == yesterdayStr) lastStreak else 1
        } else {
            lastStreak
        }

        _readingState.value = ReadingProgressState(
            lastReadPoemId = prefs.getString(KEY_LAST_READ_ID, null),
            lastReadAuthor = prefs.getString(KEY_LAST_READ_AUTHOR, null),
            lastReadSnippet = prefs.getString(KEY_LAST_READ_SNIPPET, null),
            lastReadCategory = prefs.getString(KEY_LAST_READ_CAT, null),
            lastReadLanguage = prefs.getString(KEY_LAST_READ_LANG, null),
            lastReadTimestamp = prefs.getLong(KEY_LAST_READ_TIME, 0L),
            versesReadToday = readToday,
            dailyGoal = prefs.getInt(KEY_DAILY_GOAL, 10),
            totalVersesRead = prefs.getInt(KEY_TOTAL_VERSES, 0),
            readingStreakDays = streak
        )
    }

    fun recordPoemRead(context: Context, shayari: Shayari) {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""

        var currentToday = if (lastDate == today) prefs.getInt(KEY_VERSES_READ_TODAY, 0) else 0
        currentToday += 1
        val currentTotal = prefs.getInt(KEY_TOTAL_VERSES, 0) + 1

        val snippet = shayari.lines.lines().firstOrNull() ?: shayari.lines
        val now = System.currentTimeMillis()

        prefs.edit()
            .putString(KEY_LAST_READ_ID, shayari.id)
            .putString(KEY_LAST_READ_AUTHOR, shayari.author)
            .putString(KEY_LAST_READ_SNIPPET, snippet)
            .putString(KEY_LAST_READ_CAT, shayari.category)
            .putString(KEY_LAST_READ_LANG, shayari.language)
            .putLong(KEY_LAST_READ_TIME, now)
            .putInt(KEY_VERSES_READ_TODAY, currentToday)
            .putInt(KEY_TOTAL_VERSES, currentTotal)
            .putString(KEY_LAST_DATE, today)
            .apply()

        _readingState.value = _readingState.value.copy(
            lastReadPoemId = shayari.id,
            lastReadAuthor = shayari.author,
            lastReadSnippet = snippet,
            lastReadCategory = shayari.category,
            lastReadLanguage = shayari.language,
            lastReadTimestamp = now,
            versesReadToday = currentToday,
            totalVersesRead = currentTotal
        )
    }

    fun setBookmark(context: Context, shayari: Shayari) {
        val prefs = getPrefs(context)
        val snippet = shayari.lines.lines().firstOrNull() ?: shayari.lines
        val now = System.currentTimeMillis()

        prefs.edit()
            .putString(KEY_LAST_READ_ID, shayari.id)
            .putString(KEY_LAST_READ_AUTHOR, shayari.author)
            .putString(KEY_LAST_READ_SNIPPET, snippet)
            .putString(KEY_LAST_READ_CAT, shayari.category)
            .putString(KEY_LAST_READ_LANG, shayari.language)
            .putLong(KEY_LAST_READ_TIME, now)
            .apply()

        _readingState.value = _readingState.value.copy(
            lastReadPoemId = shayari.id,
            lastReadAuthor = shayari.author,
            lastReadSnippet = snippet,
            lastReadCategory = shayari.category,
            lastReadLanguage = shayari.language,
            lastReadTimestamp = now
        )
    }

    fun clearBookmark(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit()
            .remove(KEY_LAST_READ_ID)
            .remove(KEY_LAST_READ_AUTHOR)
            .remove(KEY_LAST_READ_SNIPPET)
            .remove(KEY_LAST_READ_CAT)
            .remove(KEY_LAST_READ_LANG)
            .remove(KEY_LAST_READ_TIME)
            .apply()

        _readingState.value = _readingState.value.copy(
            lastReadPoemId = null,
            lastReadAuthor = null,
            lastReadSnippet = null,
            lastReadCategory = null,
            lastReadLanguage = null,
            lastReadTimestamp = 0L
        )
    }

    fun updateDailyGoal(context: Context, newGoal: Int) {
        val validGoal = newGoal.coerceIn(3, 100)
        getPrefs(context).edit().putInt(KEY_DAILY_GOAL, validGoal).apply()
        _readingState.value = _readingState.value.copy(dailyGoal = validGoal)
    }
}
