package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ShayariEntity
import com.example.data.model.Shayari
import com.example.widget.ShayariDailyWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ShayariOfTheDayManager {

    fun getFormattedDate(): String {
        return SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    fun getShortDate(): String {
        return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
    }

    /**
     * Deterministically select today's featured verse from any given shayari list.
     * Rotates once every 24 hours based on the calendar dateKey, with optional offset support.
     */
    fun selectDailyShayari(
        list: List<Shayari>,
        dateKey: String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()),
        offset: Int = 0
    ): Shayari? {
        if (list.isEmpty()) return null
        val baseIndex = (dateKey.hashCode() and 0x7FFFFFFF) % list.size
        val selectedIndex = ((baseIndex + offset) % list.size + list.size) % list.size
        return list[selectedIndex].copy(isDailyPick = true)
    }

    /**
     * Deterministically select today's featured verse from the approved collection.
     */
    suspend fun resolveDailyPick(
        context: Context,
        database: AppDatabase,
        offset: Int = 0
    ): Shayari? = withContext(Dispatchers.IO) {
        val dao = database.shayariDao()
        val approvedList = dao.getAllApprovedShayaris().firstOrNull() ?: emptyList()
        if (approvedList.isEmpty()) return@withContext null

        val domainList = approvedList.map { it.toDomain() }
        val dateKey = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val selected = selectDailyShayari(domainList, dateKey, offset) ?: return@withContext null

        dao.clearDailyPicks()
        dao.setDailyPick(selected.id)

        // Notify Homescreen Widget
        ShayariDailyWidgetProvider.updateAllWidgets(context)

        selected
    }
}
