package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayStreakInfo(
    val dayLabel: String,
    val dateString: String,
    val isCompleted: Boolean,
    val isToday: Boolean
)

data class PoeticMilestone(
    val daysRequired: Int,
    val title: String,
    val subtitle: String,
    val badgeEmoji: String,
    val description: String,
    val isUnlocked: Boolean
)

data class PoeticStreakState(
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val isCompletedToday: Boolean = false,
    val todayReadCount: Int = 0,
    val todayComposedCount: Int = 0,
    val lastActiveDate: String = "",
    val streakTitle: String = "शायर-ए-मुब्तदी",
    val streakSubtitle: String = "Poetry Novice",
    val motivationalQuote: String = "क़लम की रवानगी और लफ़्ज़ों का सफ़र हर रोज़ नया रंग लाता है।",
    val weeklyHistory: List<DayStreakInfo> = emptyList(),
    val currentMilestone: PoeticMilestone? = null,
    val nextMilestone: PoeticMilestone? = null,
    val allMilestones: List<PoeticMilestone> = emptyList()
) {
    val totalActivitiesToday: Int
        get() = todayReadCount + todayComposedCount
}

object PoeticStreakManager {
    private const val PREFS_NAME = "kavya_setu_poetic_streak"
    private const val KEY_CURRENT_STREAK = "current_streak"
    private const val KEY_LONGEST_STREAK = "longest_streak"
    private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
    private const val KEY_TODAY_READ_COUNT = "today_read_count"
    private const val KEY_TODAY_COMPOSED_COUNT = "today_composed_count"
    private const val KEY_ACTIVE_DATES_SET = "active_dates_set"
    private const val KEY_REMINDER_HOUR = "reminder_hour"
    private const val KEY_REMINDER_MINUTE = "reminder_minute"
    private const val KEY_REMINDER_ENABLED = "reminder_enabled"

    private val _streakState = MutableStateFlow(PoeticStreakState())
    val streakState: StateFlow<PoeticStreakState> = _streakState.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
    }

    fun initialize(context: Context) {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, "") ?: ""
        var currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 1).coerceAtLeast(1)
        var longestStreak = prefs.getInt(KEY_LONGEST_STREAK, currentStreak).coerceAtLeast(currentStreak)
        val activeDates = prefs.getStringSet(KEY_ACTIVE_DATES_SET, emptySet())?.toMutableSet() ?: mutableSetOf()

        val isCompletedToday: Boolean
        val todayRead: Int
        val todayComposed: Int

        if (lastDate == today) {
            isCompletedToday = true
            todayRead = prefs.getInt(KEY_TODAY_READ_COUNT, 0)
            todayComposed = prefs.getInt(KEY_TODAY_COMPOSED_COUNT, 0)
        } else {
            // New calendar day: reset today counters
            isCompletedToday = false
            todayRead = 0
            todayComposed = 0

            // If user did not participate yesterday and it's not the initial state
            if (lastDate.isNotBlank() && lastDate != yesterday) {
                // Streak lapsed
                currentStreak = 0
                prefs.edit().putInt(KEY_CURRENT_STREAK, 0).apply()
            }
            prefs.edit()
                .putInt(KEY_TODAY_READ_COUNT, 0)
                .putInt(KEY_TODAY_COMPOSED_COUNT, 0)
                .apply()
        }

        updateState(
            context = context,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            isCompletedToday = isCompletedToday,
            todayRead = todayRead,
            todayComposed = todayComposed,
            lastDate = lastDate,
            activeDates = activeDates
        )
    }

    fun recordPoemRead(context: Context, poemId: String = "") {
        recordActivity(context, isRead = true)
    }

    fun recordPoemComposed(context: Context, title: String = "") {
        recordActivity(context, isRead = false)
    }

    private fun recordActivity(context: Context, isRead: Boolean) {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, "") ?: ""
        var currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 1)
        var longestStreak = prefs.getInt(KEY_LONGEST_STREAK, currentStreak)
        val activeDates = prefs.getStringSet(KEY_ACTIVE_DATES_SET, emptySet())?.toMutableSet() ?: mutableSetOf()

        var todayRead = if (lastDate == today) prefs.getInt(KEY_TODAY_READ_COUNT, 0) else 0
        var todayComposed = if (lastDate == today) prefs.getInt(KEY_TODAY_COMPOSED_COUNT, 0) else 0

        if (isRead) {
            todayRead += 1
        } else {
            todayComposed += 1
        }

        var isNewDayActivity = false
        if (lastDate != today) {
            isNewDayActivity = true
            if (lastDate == yesterday) {
                currentStreak += 1
            } else {
                currentStreak = 1
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak
            }
            activeDates.add(today)
        }

        prefs.edit()
            .putString(KEY_LAST_ACTIVE_DATE, today)
            .putInt(KEY_CURRENT_STREAK, currentStreak)
            .putInt(KEY_LONGEST_STREAK, longestStreak)
            .putInt(KEY_TODAY_READ_COUNT, todayRead)
            .putInt(KEY_TODAY_COMPOSED_COUNT, todayComposed)
            .putStringSet(KEY_ACTIVE_DATES_SET, activeDates)
            .apply()

        updateState(
            context = context,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            isCompletedToday = true,
            todayRead = todayRead,
            todayComposed = todayComposed,
            lastDate = today,
            activeDates = activeDates
        )
    }

    private fun updateState(
        context: Context,
        currentStreak: Int,
        longestStreak: Int,
        isCompletedToday: Boolean,
        todayRead: Int,
        todayComposed: Int,
        lastDate: String,
        activeDates: Set<String>
    ) {
        val milestones = getMilestoneDefinitions(currentStreak)
        val currentMilestone = milestones.lastOrNull { it.isUnlocked } ?: milestones.first()
        val nextMilestone = milestones.firstOrNull { !it.isUnlocked }
        val weeklyHistory = generateWeeklyHistory(activeDates)

        val (title, subtitle) = getPoeticHonorific(currentStreak)
        val quote = getMotivationalQuote(currentStreak)

        _streakState.value = PoeticStreakState(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            isCompletedToday = isCompletedToday,
            todayReadCount = todayRead,
            todayComposedCount = todayComposed,
            lastActiveDate = lastDate,
            streakTitle = title,
            streakSubtitle = subtitle,
            motivationalQuote = quote,
            weeklyHistory = weeklyHistory,
            currentMilestone = currentMilestone,
            nextMilestone = nextMilestone,
            allMilestones = milestones
        )
    }

    private fun generateWeeklyHistory(activeDates: Set<String>): List<DayStreakInfo> {
        val result = mutableListOf<DayStreakInfo>()
        val cal = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)

        val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val dayLabels = arrayOf("S", "M", "T", "W", "T", "F", "S")

        // Look back 6 days + today = 7 days total
        val datesList = mutableListOf<Date>()
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            datesList.add(c.time)
        }

        for (date in datesList) {
            val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
            val c = Calendar.getInstance().apply { time = date }
            val dayOfWeek = c.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
            val label = dayLabels[dayOfWeek - 1]
            val isCompleted = activeDates.contains(dateStr)
            val isToday = (dateStr == todayStr)

            result.add(
                DayStreakInfo(
                    dayLabel = label,
                    dateString = SimpleDateFormat("MMM d", Locale.getDefault()).format(date),
                    isCompleted = isCompleted,
                    isToday = isToday
                )
            )
        }

        return result
    }

    private fun getPoeticHonorific(streakDays: Int): Pair<String, String> {
        return when {
            streakDays >= 30 -> "साहिब-ए-दीवान" to "Master of the Diwan (30+ Days)"
            streakDays >= 14 -> "उस्ताद-ए-ज़माँ" to "Legendary Maestro (14+ Days)"
            streakDays >= 7 -> "मीर-ए-ग़ज़ल" to "Master of Couplets (7+ Days)"
            streakDays >= 3 -> "सुख़नवर" to "Verse Artisan (3+ Days)"
            streakDays >= 1 -> "शायर-ए-मुब्तदी" to "Poetic Apprentice (1+ Day)"
            else -> "तालिब-ए-शायरी" to "Seeker of Verses"
        }
    }

    private fun getMotivationalQuote(streakDays: Int): String {
        return when {
            streakDays >= 30 -> "दीवान मुकम्मल हुआ, अब आपकी शायरी हवाओं में ख़ुशबू की तरह बिखर रही है।"
            streakDays >= 14 -> "लफ़्ज़ों की तासीर अब आपकी नब्ज़ बन चुकी है, शायरी का ये सफ़र बेमिसाल है।"
            streakDays >= 7 -> "हफ़्ते भर का रियाज़ रंग ला रहा है। हर शेर में आपकी रूह नज़र आती है।"
            streakDays >= 3 -> "सुख़न का कारवाँ चल पड़ा है, मुसलसल क़दम बढ़ाते रहिए।"
            else -> "मशक़्क़त की रदीफ़ और क़ाफ़िया बाँधते रहिए, एक दिन शायरी आपकी पहचान बन जाएगी।"
        }
    }

    private fun getMilestoneDefinitions(currentStreak: Int): List<PoeticMilestone> {
        return listOf(
            PoeticMilestone(
                daysRequired = 1,
                title = "शायर-ए-मुब्तदी",
                subtitle = "First Spark of Ink",
                badgeEmoji = "🌱",
                description = "Started the daily journey of reading or composing poetry.",
                isUnlocked = currentStreak >= 1
            ),
            PoeticMilestone(
                daysRequired = 3,
                title = "सुख़नवर",
                subtitle = "Verse Artisan",
                badgeEmoji = "🔥",
                description = "3 consecutive days of poetic immersion.",
                isUnlocked = currentStreak >= 3
            ),
            PoeticMilestone(
                daysRequired = 7,
                title = "मीर-ए-ग़ज़ल",
                subtitle = "Master of Couplets",
                badgeEmoji = "✨",
                description = "A full week of non-stop literary riyaz.",
                isUnlocked = currentStreak >= 7
            ),
            PoeticMilestone(
                daysRequired = 14,
                title = "उस्ताद-ए-ज़माँ",
                subtitle = "Legendary Maestro",
                badgeEmoji = "👑",
                description = "14 days of unwavering poetic discipline and mastery.",
                isUnlocked = currentStreak >= 14
            ),
            PoeticMilestone(
                daysRequired = 30,
                title = "साहिब-ए-दीवान",
                subtitle = "Poetic Luminary",
                badgeEmoji = "🌟",
                description = "A whole month of daily verses: Diwan completed!",
                isUnlocked = currentStreak >= 30
            )
        )
    }

    fun getStreakShareText(): String {
        val state = _streakState.value
        return """
            🔥 My Daily Poetic Streak on Kavya Setu: ${state.currentStreak} Days!
            ✨ Title: ${state.streakTitle} (${state.streakSubtitle})
            📖 Best Streak: ${state.longestStreak} Days
            
            "${state.motivationalQuote}"
            
            Discover Hindi, Odia & Urdu Shayari daily on Kavya Setu:
            https://kavyasetu.page.link/app
        """.trimIndent()
    }

    fun isReminderEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REMINDER_ENABLED, true)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }

    fun getReminderTime(context: Context): Pair<Int, Int> {
        val prefs = getPrefs(context)
        val hour = prefs.getInt(KEY_REMINDER_HOUR, 19)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, 30)
        return hour to minute
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        getPrefs(context).edit()
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()
    }
}
