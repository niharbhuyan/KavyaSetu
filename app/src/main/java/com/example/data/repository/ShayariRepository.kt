package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.ShayariDao
import com.example.data.local.ShayariEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.ActivityType
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import com.example.data.model.UserActivityItem
import com.example.data.model.UserProfile
import com.example.data.remote.FirebaseService
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ShayariRepository(
    private val dao: ShayariDao,
    private val firebaseService: FirebaseService
) {
    private val _userActivities = MutableStateFlow<List<UserActivityItem>>(
        listOf(
            UserActivityItem(
                id = "act_1",
                type = ActivityType.STREAK,
                title = "5-Day Streak Active",
                description = "Continuing daily poetic journey across Hindi & Odia verses.",
                timestamp = System.currentTimeMillis() - 3600000
            ),
            UserActivityItem(
                id = "act_2",
                type = ActivityType.SAVED,
                title = "Couplet Bookmarked",
                description = "Saved Mirza Ghalib: \"हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले...\"",
                timestamp = System.currentTimeMillis() - 7200000
            ),
            UserActivityItem(
                id = "act_3",
                type = ActivityType.RECITED,
                title = "Audio Recitation Played",
                description = "Listened to Kabisurya Pratibha's Odia masterpiece in high-clarity voice.",
                timestamp = System.currentTimeMillis() - 14400000
            ),
            UserActivityItem(
                id = "act_4",
                type = ActivityType.COMPOSE,
                title = "AI Couplet Composed",
                description = "Crafted custom Nazm on 'Rain & Memories' with Gemini 2.5 Flash.",
                timestamp = System.currentTimeMillis() - 86400000
            ),
            UserActivityItem(
                id = "act_5",
                type = ActivityType.CARD_EXPORT,
                title = "Visual Card Created",
                description = "Exported parchment-styled calligraphy card to gallery.",
                timestamp = System.currentTimeMillis() - 172800000
            )
        )
    )
    val userActivities: Flow<List<UserActivityItem>> = _userActivities.asStateFlow()

    fun recordActivity(type: ActivityType, title: String, description: String) {
        val newItem = UserActivityItem(
            id = UUID.randomUUID().toString(),
            type = type,
            title = title,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        _userActivities.value = listOf(newItem) + _userActivities.value.take(40)
    }

    val allShayaris: Flow<List<Shayari>> = dao.getAllApprovedShayaris().map { list ->
        list.map { it.toDomain() }
    }

    val allModerationShayaris: Flow<List<Shayari>> = dao.getAllShayaris().map { list ->
        list.map { it.toDomain() }
    }

    val pendingModerationShayaris: Flow<List<Shayari>> = dao.getPendingModerationShayaris().map { list ->
        list.map { it.toDomain() }
    }

    val savedShayaris: Flow<List<Shayari>> = dao.getSavedShayaris().map { list ->
        list.map { it.toDomain() }
    }

    val downloadedShayaris: Flow<List<Shayari>> = dao.getDownloadedShayaris().map { list ->
        list.map { it.toDomain() }
    }

    val downloadedCount: Flow<Int> = dao.getDownloadedCount()

    val dailyPick: Flow<Shayari?> = dao.getDailyPick().map { it?.toDomain() }

    fun getUserProfile(uid: String): Flow<UserProfile?> =
        dao.getUserProfile(uid).map { it?.toDomain() }

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val count = dao.getCount()
        if (count == 0) {
            val seeds = getCuratedSeedShayaris()
            dao.insertShayaris(seeds.map { ShayariEntity.fromDomain(it) })
            // Set initial daily pick
            val first = seeds.firstOrNull { it.moderationStatus == "APPROVED" }
            if (first != null) {
                dao.setDailyPick(first.id)
            }
        }
    }

    suspend fun toggleLike(shayari: Shayari): Shayari = withContext(Dispatchers.IO) {
        val newLiked = !shayari.isLiked
        val newCount = if (newLiked) shayari.likesCount + 1 else maxOf(0, shayari.likesCount - 1)
        dao.updateLikeStatus(shayari.id, newLiked, newCount)
        firebaseService.updateLikeCount(shayari.id, if (newLiked) 1 else -1)
        if (newLiked) {
            recordActivity(ActivityType.LIKED, "Liked Couplet", "Loved ${shayari.author}'s verse: \"${shayari.lines.take(30)}...\"")
        }
        shayari.copy(isLiked = newLiked, likesCount = newCount)
    }

    suspend fun toggleSave(shayari: Shayari): Shayari = withContext(Dispatchers.IO) {
        val newSaved = !shayari.isSaved
        dao.updateSaveStatus(shayari.id, newSaved)
        if (newSaved) {
            recordActivity(ActivityType.SAVED, "Bookmarked Couplet", "Saved to offline vault: \"${shayari.lines.take(30)}...\"")
        } else {
            recordActivity(ActivityType.UNSAVED, "Removed Bookmark", "Removed \"${shayari.lines.take(25)}...\" from saved")
        }
        shayari.copy(isSaved = newSaved)
    }

    suspend fun toggleDownload(shayari: Shayari): Shayari = withContext(Dispatchers.IO) {
        val newDownloaded = !shayari.isDownloaded
        val downloadTime = if (newDownloaded) System.currentTimeMillis() else null
        dao.updateDownloadStatus(shayari.id, newDownloaded, downloadTime)
        shayari.copy(isDownloaded = newDownloaded, downloadedAt = downloadTime)
    }

    suspend fun downloadAllFavorites() = withContext(Dispatchers.IO) {
        val saved = dao.getSavedShayaris()
        // get first item snapshot
        val ids = mutableListOf<String>()
        val list = dao.getAllApprovedShayaris()
        // Mark all saved shayaris as downloaded
        dao.batchDownload(ids, System.currentTimeMillis())
    }

    suspend fun downloadAllFavorites(favIds: List<String>) = withContext(Dispatchers.IO) {
        if (favIds.isNotEmpty()) {
            dao.batchDownload(favIds, System.currentTimeMillis())
        }
    }

    suspend fun downloadCuratedPack(packType: String) = withContext(Dispatchers.IO) {
        val allApproved = dao.getAllApprovedShayaris()
        // Batch download all or specific language
        val now = System.currentTimeMillis()
        val allSeeds = getCuratedSeedShayaris().filter { it.moderationStatus == "APPROVED" }
        val targetIds = when (packType) {
            "hindi" -> allSeeds.filter { it.language == "hindi" }.map { it.id }
            "odia" -> allSeeds.filter { it.language == "odia" }.map { it.id }
            "english" -> allSeeds.filter { it.language == "english" }.map { it.id }
            "romance" -> allSeeds.filter { it.emotion == "ishq" }.map { it.id }
            else -> allSeeds.map { it.id }
        }
        if (targetIds.isNotEmpty()) {
            dao.batchDownload(targetIds, now)
        }
    }

    suspend fun clearDownloadedStorage() = withContext(Dispatchers.IO) {
        dao.clearAllDownloaded()
    }

    // Moderation Actions
    suspend fun approveSubmission(id: String) = withContext(Dispatchers.IO) {
        dao.updateModerationStatus(
            id = id,
            status = "APPROVED",
            reason = "Approved by Administrator",
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun rejectSubmission(id: String, reason: String) = withContext(Dispatchers.IO) {
        dao.updateModerationStatus(
            id = id,
            status = "REJECTED",
            reason = reason.ifBlank { "Rejected by Administrator" },
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun flagSubmission(id: String, reason: String) = withContext(Dispatchers.IO) {
        dao.updateModerationStatus(
            id = id,
            status = "FLAGGED",
            reason = reason.ifBlank { "Flagged for manual compliance audit" },
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun publishShayari(shayari: Shayari): Shayari = withContext(Dispatchers.IO) {
        // Run automated content moderation scanner
        val moderationResult = com.example.moderation.ContentModerator.analyze(
            text = shayari.lines,
            author = shayari.author,
            penName = shayari.penName
        )

        val evaluatedShayari = shayari.copy(
            moderationStatus = moderationResult.suggestedStatus,
            moderationSeverity = moderationResult.severity.name,
            moderationReason = if (moderationResult.detectedIssues.isNotEmpty()) {
                moderationResult.detectedIssues.joinToString("; ")
            } else {
                "Automated check: Verified Clean"
            },
            moderatedAt = System.currentTimeMillis()
        )

        dao.insertShayari(ShayariEntity.fromDomain(evaluatedShayari))
        if (evaluatedShayari.moderationStatus == "APPROVED") {
            firebaseService.syncShayariToFirestore(evaluatedShayari)
        }
        evaluatedShayari
    }

    suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.insertUserProfile(UserProfileEntity.fromDomain(profile))
        firebaseService.saveUserProfile(profile)
    }

    // --- Firebase Auth Operations ---
    suspend fun signInWithEmail(email: String, pass: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val result = firebaseService.signInWithEmail(email, pass)
        result.onSuccess { profile ->
            dao.insertUserProfile(UserProfileEntity.fromDomain(profile))
            recordActivity(ActivityType.LOGIN, "Signed In (Email)", "Logged in as ${profile.displayName} (${profile.email})")
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String, displayName: String, penName: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val result = firebaseService.signUpWithEmail(email, pass, displayName, penName)
        result.onSuccess { profile ->
            dao.insertUserProfile(UserProfileEntity.fromDomain(profile))
            recordActivity(ActivityType.LOGIN, "Account Created", "Welcome to KavyaSetu, ${profile.displayName}!")
        }
    }

    suspend fun signInAnonymously(): Result<UserProfile> = withContext(Dispatchers.IO) {
        val result = firebaseService.signInAnonymously()
        result.onSuccess { profile ->
            dao.insertUserProfile(UserProfileEntity.fromDomain(profile))
            recordActivity(ActivityType.LOGIN, "Guest Session", "Active as Guest Shayar")
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        firebaseService.sendPasswordReset(email)
    }

    suspend fun sendEmailVerification(): Result<Unit> = withContext(Dispatchers.IO) {
        firebaseService.sendEmailVerification()
    }

    suspend fun updatePassword(newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        firebaseService.updatePassword(newPass)
    }

    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        firebaseService.deleteAccount()
    }

    fun signOutUser() {
        firebaseService.signOut()
        recordActivity(ActivityType.LOGIN, "Signed Out", "Switched to local offline guest mode")
    }

    suspend fun checkAndUpdateDailyTrending(forceRefresh: Boolean = false): Shayari? = withContext(Dispatchers.IO) {
        // Daily rotation logic based on date format
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val seeds = getCuratedSeedShayaris()
        val index = (todayStr.hashCode() and 0x7FFFFFFF) % seeds.size
        val selected = seeds[index]

        dao.clearDailyPicks()
        val dailyShayari = selected.copy(isDailyPick = true, isTrending = true)
        dao.insertShayari(ShayariEntity.fromDomain(dailyShayari))
        dao.setDailyPick(dailyShayari.id)
        dailyShayari
    }

    private fun getCuratedSeedShayaris(): List<Shayari> {
        val now = System.currentTimeMillis()
        return listOf(
            // --- HINDI SHAYARI ---
            Shayari(
                id = "sh_hi_1",
                lines = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले",
                author = "Mirza Ghalib",
                penName = "Ghalib",
                language = "hindi",
                emotion = "ishq",
                likesCount = 342,
                timestamp = now - 1000000,
                translationEnglish = "Thousands of desires, each worth dying for; many I have fulfilled, yet so many remain.",
                translationOdia = "ହଜାରେ ଏମିତି ଇଚ୍ଛା ଯାହା ପାଇଁ ପ୍ରାଣ ଯାଏ, ବହୁତ ସ୍ୱପ୍ନ ପୂରଣ ହେଲା ତଥାପି ବାକି ରହିଯାଏ।",
                poeticAnalysis = "Masterpiece of Urdu ghazal structure, capturing the insatiable human longing with profound irony.",
                isTrending = true,
                isDailyPick = true
            ),
            Shayari(
                id = "sh_hi_2",
                lines = "शाख़ों से टूट जाएँ वो पत्ते नहीं हैं हम\nआँधी से कोई कह दे कि औक़ात में रहे",
                author = "Rahat Indori",
                penName = "Rahat",
                language = "hindi",
                emotion = "hausla",
                likesCount = 512,
                timestamp = now - 2000000,
                translationEnglish = "We are not leaves that fall from brittle boughs; tell the ferocious storm to know its limits.",
                translationOdia = "ଡାଳରୁ ଝଡ଼ିବା ପତ୍ର ନୋହୁଁ ଆମେ, ଝଡ଼କୁ କହିଦିଅ ସୀମାରେ ରହିବାକୁ।",
                poeticAnalysis = "Defiant and electrifying couplet embodying resilience and fearless pride.",
                isTrending = true
            ),
            Shayari(
                id = "sh_hi_3",
                lines = "वक़्त रहता नहीं कहीं टिक कर\nआदत इस की भी आदमी सी है",
                author = "Gulzar",
                penName = "Gulzar",
                language = "hindi",
                emotion = "yaadein",
                likesCount = 289,
                timestamp = now - 3000000,
                translationEnglish = "Time never halts or stays in one place; its restless habits are so like those of man.",
                translationOdia = "ସମୟ କେବେ ଗୋଟିଏ ଜାଗାରେ ଅଟକେନି, ଏହାର ଅଭ୍ୟାସ ମଣିଷ ପରି ଚଞ୍ଚଳ।",
                poeticAnalysis = "Modern existential couplet using delicate personification of time.",
                isTrending = false
            ),
            Shayari(
                id = "sh_hi_4",
                lines = "ना मंदिर में मिला ना मस्जिद में मिला\nजब दिल में झाँका तो हर जर्रे में तू मिला",
                author = "Bulleh Shah",
                penName = "Sufi",
                language = "hindi",
                emotion = "sufi",
                likesCount = 410,
                timestamp = now - 4000000,
                translationEnglish = "Neither in the temple nor in the mosque was He found; but peering into the soul, the Divine spark shone everywhere.",
                translationOdia = "ମନ୍ଦିର କି ମସଜିଦ୍‌ରେ ମିଳିଲାନି, ନିଜ ହୃଦୟରେ ଦେଖିଲା ବେଳକୁ ତୁମେ ହିଁ ତୁମେ ଥିଲ।",
                poeticAnalysis = "Transcendent Sufi poetry pointing to the omnipresence of divine grace.",
                isTrending = true
            ),
            Shayari(
                id = "sh_hi_5",
                lines = "ज़िंदगी की इस कश्मकश में बस इतना ही कमाया है\nजब भी हम बिखरे दोस्तों ने बाँहों में उठाया है",
                author = "Waseem Barelvi",
                penName = "Waseem",
                language = "hindi",
                emotion = "dosti",
                likesCount = 230,
                timestamp = now - 5000000,
                translationEnglish = "In life's relentless strife, this alone is my wealth: whenever I fell apart, my friends pieced me whole.",
                translationOdia = "ଜୀବନ ଯୁଦ୍ଧରେ ଏତିକି କମାଇଛି, ଯେବେ ଭାଙ୍ଗି ପଡ଼ିଲି ବନ୍ଧୁମାନେ ହିଁ ସମ୍ଭାଳି ନେଲେ।",
                poeticAnalysis = "Warm tribute to loyal companionship that stands firm in adversity.",
                isTrending = false
            ),

            // --- ODIA SHAYARI / KABITA COUPLETS ---
            Shayari(
                id = "sh_or_1",
                lines = "ତୁମ ଆଖିର ଗଭୀର ନୀରବତା କହେ ଅକୁହା କାହାଣୀ,\nହୃଦୟର ପ୍ରତିଟି ସ୍ପନ୍ଦନେ ଲେଖା ତୁମରି ନାମର ବାଣୀ।",
                author = "Kabisurya Pratibha",
                penName = "କବିସୂର୍ଯ୍ୟ",
                language = "odia",
                emotion = "ishq",
                likesCount = 378,
                timestamp = now - 600000,
                translationEnglish = "The deep silence in your eyes speaks an unuttered tale; in every beat of my heart is inscribed your sacred name.",
                translationHindi = "तेरी आँखों की खामोशी में एक अनकही दास्तां है, दिल की हर धड़कन में बस तेरा ही नाम रवां है।",
                poeticAnalysis = "Lyrical Odia couplet marked by melodious alliteration and soulful devotion.",
                isTrending = true,
                isDailyPick = false
            ),
            Shayari(
                id = "sh_or_2",
                lines = "ଝଡ଼ର ତାଣ୍ଡବେ ବି ଥରିବନି ଆମ ଦମ୍ଭିଲା ପାଦ ଦୁଇଟି,\nସ୍ୱପ୍ନର ପଥେ ଜଳିବ ଆଲୋକ ଜିତିବୁ ସଂଗ୍ରାମଟି।",
                author = "Utkal Bard",
                penName = "ଉତ୍କଳୀୟ",
                language = "odia",
                emotion = "hausla",
                likesCount = 445,
                timestamp = now - 1500000,
                translationEnglish = "Even amidst the roaring tempest, our firm footsteps will not falter; on the path of dreams, triumphant dawn shall break.",
                translationHindi = "तूफानों के दौर में भी हमारे कदम लड़खड़ाएंगे नहीं, हौसलों के दम पर हम नया सवेरा लाएंगे।",
                poeticAnalysis = "Vibrant patriotic and motivational meter in classical Odia verse tradition.",
                isTrending = true
            ),
            Shayari(
                id = "sh_or_3",
                lines = "ସୁଖ ଆଉ ଦୁଃଖର ଏଇ ଛାଇ ଆଲୁଅ ସଂସାରେ,\nକେବଳ ବନ୍ଧୁତା ହିଁ ରହେ ନିଃସ୍ୱାର୍ଥ ହୋଇ ପାଖରେ।",
                author = "Mayadhar Mansingh",
                penName = "ମାନସିଂହ",
                language = "odia",
                emotion = "dosti",
                likesCount = 295,
                timestamp = now - 2500000,
                translationEnglish = "In this mortal realm of shifting sun and shadow, true friendship alone stands selfless and pure by your side.",
                translationHindi = "धूप-छाँव के इस जीवन में सब कुछ बदल जाता है, बस एक सच्चा दोस्त ही हमेशा साथ निभाता है।",
                poeticAnalysis = "Philosophical Odia couplet honoring unconditional friendship.",
                isTrending = false
            ),
            Shayari(
                id = "sh_or_4",
                lines = "ଅତୀତର ସେହି ସୁନେଲି ସନ୍ଧ୍ୟା ଯେବେ ମନେ ପଡ଼ିଯାଏ,\nଆଖିର ଅଶ୍ରୁ ଝରି ପଡ଼ି ସ୍ମୃତିର କବିତା ହୋଇଯାଏ।",
                author = "Sachidananda Routray",
                penName = "ରାଉତରାୟ",
                language = "odia",
                emotion = "yaadein",
                likesCount = 310,
                timestamp = now - 3500000,
                translationEnglish = "When those golden sunsets of the past reawaken in memory, tears flow gently, transforming into timeless poetry.",
                translationHindi = "बीते दिनों की वो सुनहरी शाम जब याद आती है, पलकों से गिरती बूँदें शायरी बन जाती हैं।",
                poeticAnalysis = "Nostalgic romanticism rooted in coastal Odia poetic heritage.",
                isTrending = false
            ),

            // --- ENGLISH POETIC COUPLETS ---
            Shayari(
                id = "sh_en_1",
                lines = "In the quiet chambers of the midnight soul,\nYour absence burns brighter than what made me whole.",
                author = "Julian Sterling",
                penName = "Sterling",
                language = "english",
                emotion = "dard",
                likesCount = 388,
                timestamp = now - 800000,
                translationHindi = "आधी रात की इस तन्हाई में तेरी कमी यूं खलती है, जैसे वीरान महफ़िल में कोई शमा जलती है।",
                translationOdia = "ଅଧରାତିର ନିର୍ଜନତାରେ ତୁମ ଅନୁପସ୍ଥିତି ଏମିତି ଜଳେ, ଯେମିତି କ୍ଷତ ଭିତରେ ନିଆଁର ଶିଖା ଥରେ।",
                poeticAnalysis = "Rich emotional cadence highlighting paradox: absence as an intensely palpable presence.",
                isTrending = true
            ),
            Shayari(
                id = "sh_en_2",
                lines = "Storms may strip the ancient branches bare and cold,\nYet deep below, the undefeated roots hold fire and gold.",
                author = "Elena Vance",
                penName = "Vance",
                language = "english",
                emotion = "hausla",
                likesCount = 490,
                timestamp = now - 1800000,
                translationHindi = "आँधियां भले ही शाखों को वीरान कर दें, जो जड़ें ज़मीन में हैं वो कभी मिट नहीं सकतीं।",
                translationOdia = "ଝଡ଼ ଯେତେ ଶାଖା ଭାଙ୍ଗିଲେ ବି ଚେର ଅଚଳ ରହେ, ଭିତରେ ଥିବା ବିଶ୍ୱାସ ସଦା ଜୟଗାନ ଗାଏ।",
                poeticAnalysis = "Inspirational couplet celebrating unseen resilience and inner strength.",
                isTrending = true
            ),
            Shayari(
                id = "sh_en_3",
                lines = "We searched through skies for temples in the air,\nOnly to find the universe breathing in quiet prayer.",
                author = "Rumi Translated",
                penName = "Mystic",
                language = "english",
                emotion = "sufi",
                likesCount = 422,
                timestamp = now - 2800000,
                translationHindi = "हम ढूंढते रहे फलक पर खुदा का घर, वो तो बस सांसों की सरगोशी में मुस्कुरा रहा था।",
                translationOdia = "ଆମେ ଆକାଶରେ ଖୋଜୁଥିଲୁ ଈଶ୍ୱରଙ୍କ ଆବାସ, ସେ ତ ଅନ୍ତରରେ ନେଉଥିଲେ ନିରବ ଶ୍ୱାସ।",
                poeticAnalysis = "Sufic epiphany in English couplet rhythm.",
                isTrending = false
            ),
            Shayari(
                id = "sh_hi_6",
                lines = "दिल से रोए मगर होंठों से मुस्कुरा बैठे,\nयूँ ही हम किसी से वफ़ा निभा बैठे।",
                author = "Sahir Ludhianvi",
                penName = "Sahir",
                language = "hindi",
                emotion = "dard",
                likesCount = 365,
                timestamp = now - 900000,
                translationEnglish = "The heart wept in anguish yet the lips offered a smile; this is how faithfully we loved, enduring all in silence.",
                translationOdia = "ହୃଦୟ କାନ୍ଦିଲା କିନ୍ତୁ ଓଠରେ ହସ ଥିଲା, ଏମିତି ହିଁ ଆମେ ପ୍ରେମର ପ୍ରତିଶ୍ରୁତି ପାଳିଲୁ।",
                poeticAnalysis = "Iconic expression of unrequited love and poignant stoicism.",
                isTrending = true
            ),
            Shayari(
                id = "sh_hi_7",
                lines = "हो गई है पीर पर्वत-सी पिघलनी चाहिए,\nइस हिमालय से कोई गंगा निकलनी चाहिए।",
                author = "Dushyant Kumar",
                penName = "Dushyant",
                language = "hindi",
                emotion = "hausla",
                likesCount = 580,
                timestamp = now - 1100000,
                translationEnglish = "This pain, huge as a mountain, must melt away now; from this very Himalaya, a Ganges must flow.",
                translationOdia = "ଏଇ ଦୁଃଖର ପାହାଡ଼ ଏବେ ତରଳିବା ଦରକାର, ହିମାଳୟରୁ ନୂଆ ଗଙ୍ଗା ବହିବା ଦରକାର।",
                poeticAnalysis = "Epoch-defining revolutionary Hindi ghazal igniting hope and righteous transformation.",
                isTrending = true
            ),
            Shayari(
                id = "sh_or_5",
                lines = "ଲୁହରେ ଭିଜିଲା ଆଖିର ପଲକ କହେ ଅଶ୍ରୁଳ ବେଦନା,\nଯାହାକୁ ସାଇତି ରଖିଲି ମନେ ସେ ତ ବୁଝିଲାନି ଯନ୍ତ୍ରଣା।",
                author = "Radhanath Ray",
                penName = "କବିବର",
                language = "odia",
                emotion = "dard",
                likesCount = 312,
                timestamp = now - 1300000,
                translationEnglish = "Tears soak the lashes telling a tale of unuttered sorrow; the one treasured in heart never perceived the pain.",
                translationHindi = "आँसुओं से भीगी पलकें कहती हैं बेपनाह दर्द, जिसे चाहा उसने कभी ये तड़प समझी नहीं।",
                poeticAnalysis = "Soulful Odia classical verse tracing the silent ache of unexpressed grief.",
                isTrending = false
            ),
            Shayari(
                id = "sh_en_4",
                lines = "She walks in beauty, like the night\nOf cloudless climes and starry skies.",
                author = "Lord Byron",
                penName = "Byron",
                language = "english",
                emotion = "ishq",
                likesCount = 440,
                timestamp = now - 1700000,
                translationHindi = "वो हुस्न की मल्लिका रातों की तरह चलती है, जैसे बे-अब्र फलक पर तारों की महफ़िल सजती है।",
                translationOdia = "ସେ ଚାଲେ ସୌନ୍ଦର୍ଯ୍ୟର ପରିଭାଷା ହୋଇ, ତାରାଖଚିତ ନିର୍ମଳ ଆକାଶର ନୀରବତା ପରି।",
                poeticAnalysis = "Harmonious romantic hymn celebrating pure, incandescent grace.",
                isTrending = true
            ),
            // --- MODERATION QUEUE SEED SUBMISSIONS (FOR ADMIN REVIEW) ---
            Shayari(
                id = "mod_pend_1",
                lines = "तेरी आँखों की गहराई में डूब जाने का हुनर सीखा है,\nदुनिया से बेखबर अब तेरे ख्यालों में जीने का असर देखा है।",
                author = "Farhan Siddiqui",
                penName = "Farhan",
                language = "hindi",
                emotion = "ishq",
                likesCount = 0,
                timestamp = now - 120000,
                translationEnglish = "I learned the art of submerging into the depth of your eyes; untethered from the world, I live within your thoughts.",
                translationOdia = "ତୁମ ଆଖିର ଗଭୀରତାରେ ବୁଡ଼ିଯିବାର କଳା ଶିଖିଛି, ସଂସାର ଭୁଲି ତୁମ ସ୍ମୃତିରେ ଜଞ୍ଜାଳ କାଟିଛି।",
                poeticAnalysis = "Lyrical romantic couplet with delicate meter.",
                moderationStatus = "PENDING",
                moderationSeverity = "SAFE",
                moderationReason = "Automated scan: Clean couplet. Awaiting administrator review."
            ),
            Shayari(
                id = "mod_flag_1",
                lines = "Best daily shayari! Join our Telegram & WhatsApp group: https://t.me/shayari_free or call 9876543210 for paid promo!",
                author = "PromoSpam99",
                penName = "Promo",
                language = "english",
                emotion = "dosti",
                likesCount = 0,
                timestamp = now - 340000,
                translationHindi = "स्पैम लिंक और प्रोमोशन",
                translationOdia = "ଅନାବଶ୍ୟକ ବିଜ୍ଞାପନ",
                moderationStatus = "FLAGGED",
                moderationSeverity = "HIGH_RISK",
                moderationReason = "Automated scan: Prohibited URL link detected; Phone number pattern detected; Commercial spam pattern: \"whatsapp group\""
            ),
            Shayari(
                id = "mod_flag_2",
                lines = "तुम जैसे kamina और harami को क्या पता इश्क क्या होता है,\nदिल तोड़ कर चले गए अब पछताने का क्या फायदा।",
                author = "Heartbroken_Anon",
                penName = "Gumnaam",
                language = "hindi",
                emotion = "dard",
                likesCount = 0,
                timestamp = now - 520000,
                translationEnglish = "Abusive verse containing derogatory terms.",
                moderationStatus = "FLAGGED",
                moderationSeverity = "HIGH_RISK",
                moderationReason = "Automated scan: Abusive or inappropriate keywords detected (kamina, harami)"
            ),
            Shayari(
                id = "mod_pend_2",
                lines = "ସମୟ ଯେତେ ବଦଳିଲେ ବି ବଦଳିବନି ଏ ମିତ୍ରତା,\nତୋ ହସରେ ଖୁସି ମୋର ତୋ ଆଖି ଲୁହରେ ବ୍ୟଥା।",
                author = "Soumya Ranjan",
                penName = "ମିତ୍ର",
                language = "odia",
                emotion = "dosti",
                likesCount = 0,
                timestamp = now - 680000,
                translationHindi = "वक़्त जितना भी बदले कभी नहीं बदलेगी ये दोस्ती, तेरी हँसी में मेरी ख़ुशी तेरे अश्कों में मेरा दर्द।",
                translationEnglish = "No matter how time transforms, our bond remains eternal; your laughter is my joy, your tear is my grief.",
                poeticAnalysis = "Sweet melodious Odia couplet on unconditional companionship.",
                moderationStatus = "PENDING",
                moderationSeverity = "SAFE",
                moderationReason = "Automated scan: Clean verse. Awaiting administrator review."
            )
        )
    }
}
