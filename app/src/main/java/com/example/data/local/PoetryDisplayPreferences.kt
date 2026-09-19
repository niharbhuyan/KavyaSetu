package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

object PoetryDisplayPreferences {
    private const val PREFS_NAME = "kavya_setu_display_prefs"
    private const val KEY_FONT_SIZE = "poetry_font_size_sp"
    private const val KEY_LINE_HEIGHT_MULT = "poetry_line_height_mult"
    private const val KEY_FONT_FAMILY = "poetry_font_family"

    const val DEFAULT_FONT_SIZE = 20f
    const val DEFAULT_LINE_HEIGHT_MULT = 1.6f
    const val DEFAULT_FONT_FAMILY = "serif"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFontSize(context: Context): Float {
        return getPrefs(context).getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE)
    }

    fun getFontSizeSp(context: Context): Float = getFontSize(context)

    fun saveFontSize(context: Context, sizeSp: Float) {
        getPrefs(context).edit().putFloat(KEY_FONT_SIZE, sizeSp).apply()
    }

    fun getLineHeightMult(context: Context): Float {
        return getPrefs(context).getFloat(KEY_LINE_HEIGHT_MULT, DEFAULT_LINE_HEIGHT_MULT)
    }

    fun saveLineHeightMult(context: Context, mult: Float) {
        getPrefs(context).edit().putFloat(KEY_LINE_HEIGHT_MULT, mult).apply()
    }

    fun getFontFamily(context: Context): String {
        return getPrefs(context).getString(KEY_FONT_FAMILY, DEFAULT_FONT_FAMILY) ?: DEFAULT_FONT_FAMILY
    }

    fun getFontFamilyType(context: Context): String = getFontFamily(context)

    fun saveFontFamily(context: Context, family: String) {
        getPrefs(context).edit().putString(KEY_FONT_FAMILY, family).apply()
    }

    fun saveSettings(context: Context, fontSizeSp: Float, lineHeightMult: Float, fontFamilyType: String) {
        getPrefs(context).edit()
            .putFloat(KEY_FONT_SIZE, fontSizeSp)
            .putFloat(KEY_LINE_HEIGHT_MULT, lineHeightMult)
            .putString(KEY_FONT_FAMILY, fontFamilyType)
            .apply()
    }

    fun resetToDefaults(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
