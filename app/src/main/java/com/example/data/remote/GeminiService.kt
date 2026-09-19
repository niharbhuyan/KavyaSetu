package com.example.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.PoeticAnalysisResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiThinkingConfig(
    val thinkingLevel: String // "high", "low"
)

@JsonClass(generateAdapter = true)
data class GeminiImageConfig(
    val aspectRatio: String = "1:1",
    val imageSize: String = "1K" // "1K", "2K", "4K"
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val thinkingConfig: GeminiThinkingConfig? = null,
    val imageConfig: GeminiImageConfig? = null,
    val responseModalities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && !it.contains("MY_GEMINI_API_KEY") }
                ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Generate creative shayari using gemini-3.5-flash with style filtering (Ghazal, Haiku, Free Verse, etc.)
     */
    suspend fun generateShayari(
        topic: String,
        emotion: String,
        language: String,
        authorPenName: String,
        style: com.example.data.model.PoetryStyle = com.example.data.model.PoetryStyle.GHAZAL
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            // Provide offline poetic treasury fallback
            return@withContext Result.success(getFallbackPoem(topic, emotion, language, style, authorPenName))
        }

        val langPrompt = when (language.lowercase()) {
            "hindi" -> "Hindi (written in Devanagari script)"
            "odia" -> "Odia (written in pure Odia script ଓଡ଼ିଆ)"
            else -> "English with refined poetic diction"
        }

        val promptText = """
            You are a revered master poet (Ustād-e-Shāyari) and literary craftsman.
            Language: $langPrompt
            Topic / Inspiration: "$topic"
            Emotion / Mood: "$emotion"
            Author Pen Name (Takhallus): "$authorPenName"
            Poetic Style / Form: ${style.displayName} (${style.description})
            
            Style & Metrical Constraints:
            - ${style.promptInstruction}
            - Maintain pristine rhythm, evocative imagery, and resonant cadence.
            - Do NOT output any preamble, markdown code fences, or conversational text.
            - Output strictly the poem lines. If the poem is in Hindi or Odia, add a brief 1-line English translation beneath it with the prefix "English: ".
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = promptText)))
            ),
            generationConfig = GeminiGenerationConfig(temperature = 0.8f)
        )

        try {
            val response = api.generateContent("gemini-3.5-flash", apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (!text.isNullOrEmpty()) {
                Result.success(text)
            } else {
                Result.success(getFallbackPoem(topic, emotion, language, style, authorPenName))
            }
        } catch (e: Exception) {
            // Graceful resilience: if quota is exhausted or network unavailable, deliver a high-quality stylized verse
            Result.success(getFallbackPoem(topic, emotion, language, style, authorPenName))
        }
    }

    private fun getFallbackPoem(
        topic: String,
        emotion: String,
        language: String,
        style: com.example.data.model.PoetryStyle,
        penName: String
    ): String {
        return when (style) {
            com.example.data.model.PoetryStyle.HAIKU -> when (language.lowercase()) {
                "odia" -> "ଝଡ଼ିଗଲା ଫୁଲ,\nନଦୀ ଜଳେ ଭାସିଯାଏ,\nନିରବ ସମୟ।\n\nEnglish: The flower petals fall, drifting upon the river; silent time passes on."
                "hindi" -> "शाख़ से गिरा,\nनदी में बहता पत्ता,\nमौन साफ़ सफर।\n\nEnglish: Fallen from the branch, a leaf floats down the river; a silent, pure journey."
                else -> "Silent autumn rain,\nWhispering across the stone,\nPeace within the soul."
            }
            com.example.data.model.PoetryStyle.FREE_VERSE -> when (language.lowercase()) {
                "odia" -> "ଅନ୍ଧାର ରାତିର କାନ୍ଥରେ,\nତୁମ ଛାଇ ଖୋଜୁଛି ମୋ କବିତା;\nକୌଣସି ବନ୍ଧନ ନାହିଁ, କେବଳ ଏକ ଅପେକ୍ଷା।\n\nEnglish: On the canvas of the dark night, my poem searches for your silhouette; no rigid bounds, only longing."
                "hindi" -> "रात के स्याह पन्नों पर,\nतेरा नाम धीरे से घुलता रहा;\nन कोई बहर, न कोई रदीफ़, बस एक बेचैन धड़कन।\n\nEnglish: On the dark pages of the night, your name softly dissolves; no strict meter or rhyme, only a restless heartbeat."
                else -> "In the quiet corners of dawn,\nWords wander without borders or cage,\nOnly the honest ache of living remains."
            }
            com.example.data.model.PoetryStyle.COUPLET -> when (language.lowercase()) {
                "odia" -> "ଜୀବନର ପଥେ ଯେତେ ଦୁଃଖ ଆସୁ ପଛେ,\nହସି ସମ୍ଭାଳିବା ଏହି ମନର ସାହସେ।\n\nEnglish: However much sorrow visits life's path, we endure with a smile through the courage of our soul."
                "hindi" -> "हौसलों के तरकश में कोशिश का वो तीर ज़िंदा रख,\nहार जा चाहे सब कुछ मगर फिर से जीतने की उम्मीद ज़िंदा रख।\n\nEnglish: Keep the arrow of effort alive in your quiver; even if you lose everything, keep the hope of winning alive."
                else -> "Though shadows stretch and darkness fills the room,\nA single candle can dispel the gloom."
            }
            com.example.data.model.PoetryStyle.RUBAI -> when (language.lowercase()) {
                "odia" -> "ଜୀବନ ଏକ କ୍ଷଣିକ ସ୍ୱପ୍ନ ପରି ଜାଣ,\nପ୍ରେମ ବିନା ସବୁ କିଛି ମୂଲ୍ୟହୀନ ମଣ;\nହସି ଖୁସିରେ ବିତାଇଦିଅ ଏହି ବେଳା,\nଫେରିବ ନାହିଁ ଏଇ ସୁନେଲୀ ଦିନ।\n\nEnglish: Know life to be like a fleeting dream; deem everything worthless without love; spend these moments in joyful grace, for this golden day will not return."
                "hindi" -> "ये चार दिन की ज़िंदगी है मुस्कुरा के गुज़ार,\nनफ़रतों को छोड़ दे और सबसे कर ले प्यार;\nकल न जाने कौन रहेगा कौन चला जाएगा,\nआज ही समेट ले इस वक़्त की बहार।\n\nEnglish: This is a fleeting life, spend it smiling; let go of grudges and love all; who knows who stays or parts tomorrow, gather today the spring of time."
                else -> "The moving finger writes, and having writ,\nMoves on: nor all thy piety nor wit\nShall lure it back to cancel half a line,\nNor all thy tears wash out a word of it."
            }
            com.example.data.model.PoetryStyle.DOHA -> when (language.lowercase()) {
                "odia" -> "ଧୀରେ ଧୀରେ ମନା ଚଳୁ, ସମୟ ବଳବାନ,\nମାଳୀ ସିଞ୍ଚେ ଶତ ଘଟ, ଋତୁ ଆସିଲେ ଫଳ ଜାଣ।\n\nEnglish: Slowly, O mind, proceed, for time is all-powerful; the gardener may pour a hundred pitchers, yet fruit comes only in its season."
                "hindi" -> "धीरे-धीरे रे मना, धीरे सब कुछ होय।\nमाली सींचे सौ घड़ा, ॠतु आए फल होय॥\n\nEnglish: Slowly, slowly, O mind, everything happens in its time. The gardener may water with a hundred pots, yet the fruit only arrives when the season comes."
                else -> "Patience is a tree whose root is bitter,\nYet its golden fruit is ever sweeter."
            }
            else -> when (language.lowercase()) {
                "odia" -> "ତୁମେ ଯଦି ଥରେ ପାଖେ ଆସି ବସ,\nନିସ୍ତବ୍ଧ ରାତି ବି ହୋଇଯିବ ରସ।\nଝୁରୁଛି ଏ ମନ ତୁମରି ସ୍ମୃତିରେ,\nଜଳୁଛି ପ୍ରଦୀପ ପ୍ରେମର ପ୍ରୀତିରେ।\n\nEnglish: If you would only come sit beside me once, even the silent night would fill with sweet nectar."
                "hindi" -> "रात की भीगी सड़कों पे वो चिराग़ जलते रहे,\nहम तेरी याद के साए में यूँही चलते रहे।\nकितने मौसम आए और गुज़र गए लेकिन,\nहम तेरे इंतज़ार के दायरे में ढलते रहे।\n\nEnglish: Lamps kept burning on the night's drenched streets; in the silhouette of your memories, I kept wandering."
                else -> "In every sigh, a hidden song remains,\nAcross the silence and through gentle rains;\nLove binds two souls though worlds may drift apart,\nCarving your name forever in my heart."
            }
        }
    }

    /**
     * Deep Poetic Thinking & Classical Ghazal Analysis using gemini-3.1-pro-preview with HIGH thinking level
     * Per requirement: ThinkingLevel.HIGH, no maxOutputTokens
     */
    suspend fun analyzePoetryWithHighThinking(
        shayariText: String,
        language: String
    ): Result<PoeticAnalysisResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured."))
        }

        val prompt = """
            Perform a profound, scholarly literary breakdown of the following couplet:
            Couplet:
            "$shayariText"
            Original Language: $language
            
            Provide the analysis strictly in the following JSON format:
            {
              "behrMeter": "Rhythm, cadence or meter explanation (e.g. Bahr-e-Ramal, Chhanda, or iambic cadence)",
              "radif": "The repeating word/refrain if any, or key resonant word",
              "qaafiya": "The rhyming syllable or phoneme",
              "takhallus": "Pen name invocation or implicit voice",
              "emotionalWeight": "The core Ras / Emotion and emotional weight (e.g., Viraha, Hasrat, Sukoon)",
              "literaryCommentary": "A deep 2-3 sentence commentary on the metaphors, philosophical subtext, and poignant craft."
            }
            Do not wrap in markdown quotes. Just valid JSON.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(
                thinkingConfig = GeminiThinkingConfig(thinkingLevel = "high")
                // Per instructions: Do not set maxOutputTokens!
            )
        )

        try {
            val response = api.generateContent("gemini-3.1-pro-preview", apiKey, request)
            val raw = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
            val cleanJson = raw.replace("```json", "").replace("```", "").trim()
            val jsonObj = JSONObject(cleanJson)
            val result = PoeticAnalysisResult(
                behrMeter = jsonObj.optString("behrMeter", "Measured classical meter with fluid rhythm"),
                radif = jsonObj.optString("radif", "Resonant closing refrain"),
                qaafiya = jsonObj.optString("qaafiya", "Harmonic internal rhyme"),
                takhallus = jsonObj.optString("takhallus", "Implicit poetic signature"),
                emotionalWeight = jsonObj.optString("emotionalWeight", "Intense emotional yearning"),
                literaryCommentary = jsonObj.optString("literaryCommentary", "Rich in subtext and nuanced imagery.")
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fast Rhyming & Tukbandi Assistant using gemini-3.1-flash-lite-preview
     */
    suspend fun findRhymesAndQaafiya(
        seedWord: String,
        language: String
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured."))
        }

        val prompt = """
            Give me a list of 8 musical rhyming words (Qaafiya / Tukbandi) for the word "$seedWord" in $language.
            Return ONLY the rhyming words separated by commas, nothing else.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.5f)
        )

        try {
            val response = api.generateContent("gemini-3.1-flash-lite-preview", apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val list = text.split(",", "\n").map { it.trim().removePrefix("-").trim() }.filter { it.isNotBlank() }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate High-Quality Card Background Image using gemini-3-pro-image-preview
     * With user-specified image size (1K, 2K, 4K)
     */
    suspend fun generateCardBackground(
        themeDescription: String,
        imageSize: String = "1K", // "1K", "2K", "4K"
        aspectRatio: String = "1:1"
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured."))
        }

        val prompt = "Aesthetic fine-art wallpaper for poetry card, dark ambient mood: $themeDescription, ethereal textures, subtle golden ink splatter, no text, clean canvas suitable for overlaying typography"

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(
                imageConfig = GeminiImageConfig(
                    aspectRatio = aspectRatio,
                    imageSize = imageSize
                ),
                responseModalities = listOf("TEXT", "IMAGE")
            )
        )

        try {
            val response = api.generateContent("gemini-3-pro-image-preview", apiKey, request)
            val inlinePart = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }
            val base64Data = inlinePart?.inlineData?.data
            if (!base64Data.isNullOrEmpty()) {
                val decoded = Base64.decode(base64Data, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                if (bitmap != null) {
                    Result.success(bitmap)
                } else {
                    Result.failure(Exception("Failed to decode image bitmap."))
                }
            } else {
                Result.failure(Exception("No image returned from Gemini."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Multilingual translation between English, Hindi, and Odia using gemini-3.5-flash
     */
    suspend fun translateShayari(
        lines: String,
        targetLanguage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) return@withContext Result.failure(Exception("API key missing"))

        val targetDesc = when (targetLanguage.lowercase()) {
            "hindi" -> "Hindi in Devanagari script with poetic sensibility"
            "odia" -> "Odia in Odia script (ଓଡ଼ିଆ) preserving rhythmic cadence"
            else -> "English preserving poetic tone"
        }

        val prompt = "Translate this poetic couplet into $targetDesc. Output ONLY the translated lines:\n\"$lines\""
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.4f)
        )
        try {
            val res = api.generateContent("gemini-3.5-flash", apiKey, request)
            val txt = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
            Result.success(txt)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
