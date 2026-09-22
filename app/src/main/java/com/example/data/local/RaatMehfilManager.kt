package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class RaatThemeConfig(
    val isNightModeForced: Boolean = false,
    val amberCandleGlow: Boolean = true,
    val cricketsAmbientEnabled: Boolean = false,
    val midnightHourActive: Boolean = false
)

object RaatMehfilManager {
    private const val PREFS_NAME = "raat_mehfil_prefs"
    private const val KEY_FORCED = "key_raat_forced"
    private const val KEY_AMBER = "key_raat_amber"
    private const val KEY_CRICKETS = "key_raat_crickets"

    private val _themeConfig = MutableStateFlow(RaatThemeConfig())
    val themeConfig: StateFlow<RaatThemeConfig> = _themeConfig.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs?.let { p ->
            val forced = p.getBoolean(KEY_FORCED, false)
            val amber = p.getBoolean(KEY_AMBER, true)
            val crickets = p.getBoolean(KEY_CRICKETS, false)
            val isHour = checkIsMidnightHour()
            _themeConfig.value = RaatThemeConfig(
                isNightModeForced = forced,
                amberCandleGlow = amber,
                cricketsAmbientEnabled = crickets,
                midnightHourActive = isHour
            )
        }
    }

    /**
     * Called hourly by HourlySyncManager to auto-update night state.
     */
    fun refreshMidnightStatus() {
        val isHour = checkIsMidnightHour()
        _themeConfig.value = _themeConfig.value.copy(midnightHourActive = isHour)
    }

    private fun checkIsMidnightHour(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        // Active between 10 PM (22:00) and 5 AM (05:00)
        return hour >= 22 || hour < 5
    }

    fun toggleForcedNightMode(forced: Boolean) {
        _themeConfig.value = _themeConfig.value.copy(isNightModeForced = forced)
        prefs?.edit()?.putBoolean(KEY_FORCED, forced)?.apply()
    }

    fun toggleAmberGlow(enabled: Boolean) {
        _themeConfig.value = _themeConfig.value.copy(amberCandleGlow = enabled)
        prefs?.edit()?.putBoolean(KEY_AMBER, enabled)?.apply()
    }

    fun toggleCricketsAmbient(enabled: Boolean) {
        _themeConfig.value = _themeConfig.value.copy(cricketsAmbientEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_CRICKETS, enabled)?.apply()
    }
}
