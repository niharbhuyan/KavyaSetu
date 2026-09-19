package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.GeneratedPoemItem
import com.example.data.model.PoetryStyle
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object GeneratedPoemManager {
    private const val PREFS_NAME = "kavya_setu_generated_history"
    private const val KEY_HISTORY_JSON = "generated_poems_json"

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, GeneratedPoemItem::class.java)
    private val adapter = moshi.adapter<List<GeneratedPoemItem>>(listType)

    private val _history = MutableStateFlow<List<GeneratedPoemItem>>(emptyList())
    val history: StateFlow<List<GeneratedPoemItem>> = _history.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun initialize(context: Context) {
        val prefs = getPrefs(context)
        val json = prefs.getString(KEY_HISTORY_JSON, null)
        if (!json.isNullOrBlank()) {
            try {
                val list = adapter.fromJson(json) ?: emptyList()
                _history.value = list
                return
            } catch (e: Exception) {
                // fallback to default seeds
            }
        }

        // Initialize with default curated sample history across different poetry styles
        val seeds = getCuratedSampleHistory()
        _history.value = seeds
        saveToPrefs(context, seeds)
    }

    fun recordGeneratedPoem(
        context: Context,
        topic: String,
        style: PoetryStyle,
        emotion: String,
        language: String,
        penName: String,
        content: String
    ): GeneratedPoemItem {
        val newItem = GeneratedPoemItem(
            id = "gen_" + UUID.randomUUID().toString().take(8),
            topic = topic,
            styleId = style.id,
            styleDisplayName = style.displayName,
            styleEmoji = style.emoji,
            emotion = emotion,
            language = language,
            penName = penName.ifBlank { "Sukhanwar" },
            content = content,
            timestamp = System.currentTimeMillis()
        )

        val updated = listOf(newItem) + _history.value
        _history.value = updated
        saveToPrefs(context, updated)
        return newItem
    }

    fun deletePoem(context: Context, id: String) {
        val updated = _history.value.filterNot { it.id == id }
        _history.value = updated
        saveToPrefs(context, updated)
    }

    fun clearAll(context: Context) {
        _history.value = emptyList()
        saveToPrefs(context, emptyList())
    }

    private fun saveToPrefs(context: Context, list: List<GeneratedPoemItem>) {
        try {
            val json = adapter.toJson(list)
            getPrefs(context).edit().putString(KEY_HISTORY_JSON, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCuratedSampleHistory(): List<GeneratedPoemItem> {
        val now = System.currentTimeMillis()
        return listOf(
            GeneratedPoemItem(
                id = "gen_seed_1",
                topic = "Rain on antique lamp-lit streets",
                styleId = PoetryStyle.GHAZAL.id,
                styleDisplayName = PoetryStyle.GHAZAL.displayName,
                styleEmoji = PoetryStyle.GHAZAL.emoji,
                emotion = "ishq",
                language = "hindi",
                penName = "Sukhanwar",
                content = "रात की भीगी सड़कों पे वो चिराग़ जलते रहे\nहम तेरी याद के साए में यूँही चलते रहे\n\nEnglish: Lamps kept burning on the night's drenched streets; in the silhouette of your memories, I kept wandering.",
                timestamp = now - 3600000 * 2
            ),
            GeneratedPoemItem(
                id = "gen_seed_2",
                topic = "Autumn leaf falling into mountain stream",
                styleId = PoetryStyle.HAIKU.id,
                styleDisplayName = PoetryStyle.HAIKU.displayName,
                styleEmoji = PoetryStyle.HAIKU.emoji,
                emotion = "sufi",
                language = "english",
                penName = "Wanderer",
                content = "Golden autumn leaf,\nFloating down the mountain stream,\nSilent stream moves on.",
                timestamp = now - 3600000 * 8
            ),
            GeneratedPoemItem(
                id = "gen_seed_3",
                topic = "Solitary night and unfinished dreams",
                styleId = PoetryStyle.FREE_VERSE.id,
                styleDisplayName = PoetryStyle.FREE_VERSE.displayName,
                styleEmoji = PoetryStyle.FREE_VERSE.emoji,
                emotion = "dard",
                language = "odia",
                penName = "Kabisurya",
                content = "ନିସ୍ତବ୍ଧ ରାତିର ଛାଇ ତଳେ,\nଜଳୁଛି ଗୋଟିଏ ଅଧା ଲେଖା କବିତାର ଦୀପ;\nପବନ କହେ ସେ ସ୍ମୃତିର କଥା।\n\nEnglish: Beneath the shadow of a silent night, burns the lamp of a half-written poem; the breeze whispers that memory.",
                timestamp = now - 3600000 * 24
            ),
            GeneratedPoemItem(
                id = "gen_seed_4",
                topic = "Courage against raging storms",
                styleId = PoetryStyle.COUPLET.id,
                styleDisplayName = PoetryStyle.COUPLET.displayName,
                styleEmoji = PoetryStyle.COUPLET.emoji,
                emotion = "hausla",
                language = "hindi",
                penName = "Zafar",
                content = "हौसलों के तरकश में कोशिश का वो तीर ज़िंदा रख\nहार जा चाहे सब कुछ मगर फिर से जीतने की उम्मीद ज़िंदा रख",
                timestamp = now - 3600000 * 48
            )
        )
    }
}
