package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.TarhiChallenge
import com.example.data.model.TarhiSubmission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

/**
 * Manages daily Tarhi Mushaira challenges and community completions with auto-updating cycles.
 */
object TarhiMushairaManager {
    private const val PREFS_NAME = "tarhi_mushaira_prefs"
    private const val KEY_LAST_DAY = "key_tarhi_day"
    private const val KEY_USER_SUBMISSIONS = "key_user_tarhi_submissions"

    private val sampleChallenges = listOf(
        TarhiChallenge(
            id = "tarhi_1",
            dayNumber = 1,
            openingMisra = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले",
            poetReference = "Mirza Asadullah Khan Ghalib",
            bahrName = "Bahr-e-Ramal Musamman Makhboon (فاعلاتن فعلاتن فعلاتن فعلن)",
            requiredQafiaPattern = "दम, कम, ग़म, सनम",
            requiredRadif = "निकले",
            submissions = listOf(
                TarhiSubmission(
                    id = "sub_1",
                    challengeId = "tarhi_1",
                    poetName = "Ahmad Faraz",
                    takhallus = "Faraz",
                    openingMisra = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले",
                    secondMisra = "सुना है लोग उसे आँख भर के देखते हैं / सो उस के शहर में कुछ दिन ठहर के देखते हैं",
                    upvotes = 42
                ),
                TarhiSubmission(
                    id = "sub_2",
                    challengeId = "tarhi_1",
                    poetName = "Faiz Ahmad Faiz",
                    takhallus = "Faiz",
                    openingMisra = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले",
                    secondMisra = "गुलों में रंग भरे बाद-ए-नौबहार चले / चले भी आओ कि गुलशन का कारोबार चले",
                    upvotes = 38
                )
            )
        ),
        TarhiChallenge(
            id = "tarhi_2",
            dayNumber = 2,
            openingMisra = "इब्तिदा-ए-इश्क़ है रोता है क्या",
            poetReference = "Meer Taqi Meer (Khuda-e-Sukhan)",
            bahrName = "Bahr-e-Hazaj Musaddas Mahzoof (مفاعیلن مفاعیلن فعولن)",
            requiredQafiaPattern = "रोता, होता, सोता, खोता",
            requiredRadif = "है क्या",
            submissions = listOf(
                TarhiSubmission(
                    id = "sub_3",
                    challengeId = "tarhi_2",
                    poetName = "Dagh Dehlvi",
                    takhallus = "Dagh",
                    openingMisra = "इब्तिदा-ए-इश्क़ है रोता है क्या",
                    secondMisra = "आगे आगे देखिए होता है क्या",
                    upvotes = 56
                )
            )
        ),
        TarhiChallenge(
            id = "tarhi_3",
            dayNumber = 3,
            openingMisra = "दिल-ए-नादाँ तुझे हुआ क्या है",
            poetReference = "Mirza Ghalib",
            bahrName = "Bahr-e-Mutaqaarib Musamman Saalim (فعولن فعولن فعولن فعولن)",
            requiredQafiaPattern = "हुआ, दवा, रज़ा, खता",
            requiredRadif = "क्या है",
            submissions = listOf(
                TarhiSubmission(
                    id = "sub_4",
                    challengeId = "tarhi_3",
                    poetName = "Allama Iqbal",
                    takhallus = "Iqbal",
                    openingMisra = "दिल-ए-नादाँ तुझे हुआ क्या है",
                    secondMisra = "आख़िर इस दर्द की दवा क्या है",
                    upvotes = 64
                )
            )
        ),
        TarhiChallenge(
            id = "tarhi_4",
            dayNumber = 4,
            openingMisra = "सितारों से आगे जहाँ और भी हैं",
            poetReference = "Allama Muhammad Iqbal",
            bahrName = "Bahr-e-Kamil Musamman Saalim",
            requiredQafiaPattern = "जहाँ, इम्तिहाँ, कारवाँ, आशियाँ",
            requiredRadif = "और भी हैं",
            submissions = listOf(
                TarhiSubmission(
                    id = "sub_5",
                    challengeId = "tarhi_4",
                    poetName = "Jigar Moradabadi",
                    takhallus = "Jigar",
                    openingMisra = "सितारों से आगे जहाँ और भी हैं",
                    secondMisra = "अभी इश्क़ के इम्तिहाँ और भी हैं",
                    upvotes = 51
                )
            )
        )
    )

    private val _currentChallenge = MutableStateFlow<TarhiChallenge>(sampleChallenges[0])
    val currentChallenge: StateFlow<TarhiChallenge> = _currentChallenge.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        resolveCurrentChallenge()
    }

    /**
     * Resolves the current Tarhi based on calendar day rotation (auto-updates each day and hourly).
     */
    fun resolveCurrentChallenge() {
        val cal = Calendar.getInstance()
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % sampleChallenges.size
        val baseChallenge = sampleChallenges[index]

        // Load custom user submissions from SharedPreferences
        val userSubs = loadUserSubmissions(baseChallenge.id)
        val combinedSubmissions = (userSubs + baseChallenge.submissions).distinctBy { it.id }

        _currentChallenge.value = baseChallenge.copy(
            dayNumber = dayOfYear,
            submissions = combinedSubmissions
        )
    }

    fun submitCouplet(challengeId: String, secondMisra: String, poetName: String, takhallus: String): TarhiSubmission {
        val challenge = _currentChallenge.value
        val newSub = TarhiSubmission(
            challengeId = challengeId,
            poetName = poetName.ifBlank { "You (Shayar)" },
            takhallus = takhallus.ifBlank { "Nawaaz" },
            openingMisra = challenge.openingMisra,
            secondMisra = secondMisra.trim(),
            upvotes = 1,
            isUserSubmission = true
        )

        val updatedList = listOf(newSub) + challenge.submissions
        _currentChallenge.value = challenge.copy(submissions = updatedList)
        saveUserSubmission(newSub)
        return newSub
    }

    fun toggleUpvote(submissionId: String) {
        val challenge = _currentChallenge.value
        val updated = challenge.submissions.map { sub ->
            if (sub.id == submissionId) {
                sub.copy(upvotes = sub.upvotes + 1)
            } else {
                sub
            }
        }
        _currentChallenge.value = challenge.copy(submissions = updated)
    }

    private fun saveUserSubmission(submission: TarhiSubmission) {
        val p = prefs ?: return
        try {
            val existingJson = p.getString(KEY_USER_SUBMISSIONS, "[]") ?: "[]"
            val array = JSONArray(existingJson)
            val obj = JSONObject().apply {
                put("id", submission.id)
                put("challengeId", submission.challengeId)
                put("poetName", submission.poetName)
                put("takhallus", submission.takhallus)
                put("openingMisra", submission.openingMisra)
                put("secondMisra", submission.secondMisra)
                put("upvotes", submission.upvotes)
                put("submittedAtMillis", submission.submittedAtMillis)
            }
            array.put(obj)
            p.edit().putString(KEY_USER_SUBMISSIONS, array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadUserSubmissions(challengeId: String): List<TarhiSubmission> {
        val p = prefs ?: return emptyList()
        val list = mutableListOf<TarhiSubmission>()
        try {
            val existingJson = p.getString(KEY_USER_SUBMISSIONS, "[]") ?: "[]"
            val array = JSONArray(existingJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                if (obj.optString("challengeId") == challengeId) {
                    list.add(
                        TarhiSubmission(
                            id = obj.getString("id"),
                            challengeId = obj.getString("challengeId"),
                            poetName = obj.optString("poetName", "You"),
                            takhallus = obj.optString("takhallus", ""),
                            openingMisra = obj.optString("openingMisra", ""),
                            secondMisra = obj.optString("secondMisra", ""),
                            upvotes = obj.optInt("upvotes", 1),
                            isUserSubmission = true,
                            submittedAtMillis = obj.optLong("submittedAtMillis", System.currentTimeMillis())
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        return list
    }
}
