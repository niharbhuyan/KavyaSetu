package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class AudioReciter(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            tts?.setPitch(0.95f) // Warm, slightly deeper poetic tone
            tts?.setSpeechRate(0.85f) // Measured, poetic pace
        } else {
            Log.w("AudioReciter", "TTS init failed with status: $status")
        }
    }

    fun speak(text: String, languageCode: String) {
        if (!isReady || tts == null) return
        val locale = when (languageCode.lowercase()) {
            "hindi" -> Locale.forLanguageTag("hi-IN")
            "odia" -> Locale.forLanguageTag("or-IN")
            else -> Locale.ENGLISH
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.ENGLISH)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ShayariRecitation")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
