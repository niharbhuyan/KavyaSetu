package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class PoetMoharSeal(
    val takhallus: String,
    val sealStyle: String, // "MUGHAL_OVAL", "ROYAL_ROUND", "CALLIGRAPHIC_SHIELD", "WAX_HEXAGON"
    val inkColorHex: Long = 0xFFD8315B, // Velvet Rose default
    val subtitleTag: String = "सुख़नवर • SHAYAR"
)

object TakhallusSealManager {
    private const val PREFS_NAME = "takhallus_seal_prefs"
    private const val KEY_TAKHALLUS = "key_takhallus"
    private const val KEY_STYLE = "key_seal_style"
    private const val KEY_COLOR = "key_ink_color"
    private const val KEY_SUBTITLE = "key_subtitle"

    private val _currentSeal = MutableStateFlow(
        PoetMoharSeal(
            takhallus = "परवाज़",
            sealStyle = "MUGHAL_OVAL",
            inkColorHex = 0xFFE5B247, // Antique Gold
            subtitleTag = "काव्यसेतु • KAVYA SETU"
        )
    )
    val currentSeal: StateFlow<PoetMoharSeal> = _currentSeal.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs?.let { p ->
            val takhallus = p.getString(KEY_TAKHALLUS, "परवाज़") ?: "परवाज़"
            val style = p.getString(KEY_STYLE, "MUGHAL_OVAL") ?: "MUGHAL_OVAL"
            val color = p.getLong(KEY_COLOR, 0xFFE5B247)
            val sub = p.getString(KEY_SUBTITLE, "काव्यसेतु • KAVYA SETU") ?: "काव्यसेतु • KAVYA SETU"
            _currentSeal.value = PoetMoharSeal(takhallus, style, color, sub)
        }
    }

    fun updateSeal(takhallus: String, style: String, colorHex: Long, subtitle: String) {
        _currentSeal.value = PoetMoharSeal(takhallus, style, colorHex, subtitle)
        prefs?.edit()?.apply {
            putString(KEY_TAKHALLUS, takhallus)
            putString(KEY_STYLE, style)
            putLong(KEY_COLOR, colorHex)
            putString(KEY_SUBTITLE, subtitle)
            apply()
        }
    }
}
