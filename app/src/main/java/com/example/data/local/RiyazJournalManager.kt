package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class RiyazDraft(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val bahrTag: String = "Free Meter",
    val updatedAtMillis: Long = System.currentTimeMillis()
)

object RiyazJournalManager {
    private const val PREFS_NAME = "riyaz_journal_prefs"
    private const val KEY_DRAFTS = "key_riyaz_drafts"

    private val _drafts = MutableStateFlow<List<RiyazDraft>>(emptyList())
    val drafts: StateFlow<List<RiyazDraft>> = _drafts.asStateFlow()

    private var prefs: SharedPreferences? = null

    // Comprehensive offline Qafiya dictionary categorized by Hindi/Urdu rhyme endings
    private val qafiyaDictionary = mapOf(
        "आस" to listOf("पास", "ख़ास", "एहसास", "उदास", "लिबास", "साँस", "अहसास", "प्यास", "निराश"),
        "दिल" to listOf("मंज़िल", "महफ़िल", "क़ातिल", "साहिल", "मुस्तक़बिल", "कामिल", "बातिल", "फ़ाज़िल"),
        "रात" to listOf("बात", "मुलाक़ात", "जज़्बात", "हालत", "हयात", "सौग़ात", "बर्सात", "करामात"),
        "दम" to listOf("ग़म", "कम", "हम", "सनम", "करम", "क़सम", "हरम", "सितम", "मौसम"),
        "यार" to listOf("प्यार", "बहार", "करार", "इंतिज़ार", "ख़ुमार", "संसार", "इज़हार", "दीदार"),
        "नूर" to listOf("दूर", "फ़ितूर", "गुरूर", "हुज़ूर", "मशहूर", "दस्तूर", "क़ुसूर", "मजबूर"),
        "हाल" to listOf("सवाल", "ख़याल", "कमाल", "जमाल", "ज़वाल", "विसाल", "मलाल", "मिसाल"),
        "इश्क़" to listOf("मुश्क", "रिश्क", "दिमश्क़", "सिश्क"),
        "नज़र" to listOf("सहर", "डगर", "असर", "शहर", "ज़हर", "सफ़र", "ख़बर", "गुज़र", "लहर")
    )

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadDrafts()
    }

    fun findOfflineQafiyas(query: String): List<String> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return emptyList()

        // 1. Exact base match
        qafiyaDictionary[trimmed]?.let { return it }

        // 2. Ending rhyming match
        val ending2 = if (trimmed.length >= 2) trimmed.takeLast(2) else trimmed
        val matches = mutableListOf<String>()
        qafiyaDictionary.forEach { (_, words) ->
            words.forEach { w ->
                if (w.endsWith(ending2) && w != trimmed) {
                    matches.add(w)
                }
            }
        }
        return if (matches.isNotEmpty()) matches.distinct().take(12) else listOf("आस", "पास", "एहसास", "उदास", "ख़ास")
    }

    fun saveDraft(title: String, content: String, bahrTag: String = "Ghazal"): RiyazDraft {
        val newDraft = RiyazDraft(title = title.ifBlank { "Untitled Ghazal" }, content = content, bahrTag = bahrTag)
        val updated = listOf(newDraft) + _drafts.value
        _drafts.value = updated
        persistDrafts(updated)
        return newDraft
    }

    fun deleteDraft(draftId: String) {
        val updated = _drafts.value.filterNot { it.id == draftId }
        _drafts.value = updated
        persistDrafts(updated)
    }

    private fun persistDrafts(list: List<RiyazDraft>) {
        val p = prefs ?: return
        try {
            val array = org.json.JSONArray()
            list.forEach { d ->
                val obj = org.json.JSONObject().apply {
                    put("id", d.id)
                    put("title", d.title)
                    put("content", d.content)
                    put("bahrTag", d.bahrTag)
                    put("updatedAtMillis", d.updatedAtMillis)
                }
                array.put(obj)
            }
            p.edit().putString(KEY_DRAFTS, array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadDrafts() {
        val p = prefs ?: return
        val list = mutableListOf<RiyazDraft>()
        try {
            val json = p.getString(KEY_DRAFTS, null) ?: return
            val array = org.json.JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RiyazDraft(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        bahrTag = obj.optString("bahrTag", "Free Meter"),
                        updatedAtMillis = obj.optLong("updatedAtMillis", System.currentTimeMillis())
                    )
                )
            }
            _drafts.value = list
        } catch (_: Exception) {}
    }
}
