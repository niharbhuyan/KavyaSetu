package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActivityType
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.PoeticAnalysisResult
import com.example.data.model.Shayari
import com.example.data.model.UserActivityItem
import com.example.data.model.UserProfile
import com.example.data.remote.GeminiClient
import com.example.data.repository.ShayariRepository
import com.example.notification.DailyNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val repository: ShayariRepository) : ViewModel() {

    val selectedLanguage = MutableStateFlow(Language.ALL)
    val selectedEmotion = MutableStateFlow<Emotion?>(null)
    val searchQuery = MutableStateFlow("")

    private val _userProfileState = MutableStateFlow(
        UserProfile("local_poet_guest", "Guest Shayar", "Parwaaz", bio = "Words carrying the weight of my heart.", streakDays = 5)
    )
    val userProfile: StateFlow<UserProfile> = _userProfileState.asStateFlow()

    val userActivities: StateFlow<List<UserActivityItem>> = repository.userActivities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isAuthLoading = MutableStateFlow(false)
    val authError = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.getUserProfile("local_poet_guest").collect { p ->
                if (p != null && _userProfileState.value.isGuest) {
                    _userProfileState.value = p
                }
            }
        }
    }

    val dailyPick: StateFlow<Shayari?> = repository.dailyPick
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val savedShayaris: StateFlow<List<Shayari>> = repository.savedShayaris
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Offline Access State
    val downloadedShayaris: StateFlow<List<Shayari>> = repository.downloadedShayaris
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val downloadedCount: StateFlow<Int> = repository.downloadedCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val isOfflineSimulated = MutableStateFlow(false)

    // Moderation System State
    val allModerationSubmissions: StateFlow<List<Shayari>> = repository.allModerationShayaris
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingModerationSubmissions: StateFlow<List<Shayari>> = repository.pendingModerationShayaris
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val testScanInput = MutableStateFlow("")
    val testScanResult = MutableStateFlow<com.example.data.model.ModerationCheckResult?>(null)

    // Deep Link & Details View State
    val selectedDetailShayari = MutableStateFlow<Shayari?>(null)
    val fcmToken = MutableStateFlow<String?>(null)

    @Suppress("UNCHECKED_CAST")
    val displayedShayaris: StateFlow<List<Shayari>> = combine(
        repository.allShayaris,
        downloadedShayaris,
        isOfflineSimulated,
        selectedLanguage,
        selectedEmotion,
        searchQuery
    ) { args: Array<Any?> ->
        val all = args[0] as List<Shayari>
        val downloaded = args[1] as List<Shayari>
        val simulatedOffline = args[2] as Boolean
        val lang = args[3] as Language
        val emotion = args[4] as Emotion?
        val query = args[5] as String

        val sourceList = if (simulatedOffline) downloaded else all
        sourceList.filter { shayari ->
            val matchesLang = when (lang) {
                Language.ALL -> true
                else -> shayari.language.equals(lang.code, ignoreCase = true)
            }
            val matchesEmotion = emotion == null ||
                    shayari.emotion.equals(emotion.code, ignoreCase = true) ||
                    shayari.emotion.equals(emotion.englishLabel, ignoreCase = true) ||
                    Emotion.fromCode(shayari.emotion) == emotion

            val emotionObj = Emotion.fromCode(shayari.emotion)
            val matchesQuery = query.isBlank() ||
                    shayari.lines.contains(query, ignoreCase = true) ||
                    shayari.author.contains(query, ignoreCase = true) ||
                    shayari.penName.contains(query, ignoreCase = true) ||
                    shayari.translationEnglish.contains(query, ignoreCase = true) ||
                    shayari.translationHindi.contains(query, ignoreCase = true) ||
                    shayari.translationOdia.contains(query, ignoreCase = true) ||
                    shayari.emotion.contains(query, ignoreCase = true) ||
                    emotionObj.englishLabel.contains(query, ignoreCase = true) ||
                    emotionObj.hindiLabel.contains(query, ignoreCase = true) ||
                    emotionObj.odiaLabel.contains(query, ignoreCase = true)

            matchesLang && matchesEmotion && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // AI Studio State
    val isComposing = MutableStateFlow(false)
    val composedResult = MutableStateFlow<String?>(null)
    val compositionError = MutableStateFlow<String?>(null)

    val isAnalyzingWithHighThinking = MutableStateFlow(false)
    val highThinkingAnalysis = MutableStateFlow<PoeticAnalysisResult?>(null)
    val analysisError = MutableStateFlow<String?>(null)

    val isSearchingRhymes = MutableStateFlow(false)
    val rhymingSuggestions = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    fun onLanguageSelected(lang: Language) {
        selectedLanguage.value = lang
    }

    fun onEmotionSelected(emotion: Emotion?) {
        selectedEmotion.value = emotion
    }

    fun clearEmotionFilter() {
        selectedEmotion.value = null
    }

    fun clearAllFilters() {
        selectedEmotion.value = null
        selectedLanguage.value = Language.ALL
        searchQuery.value = ""
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun toggleLike(shayari: Shayari) {
        viewModelScope.launch {
            repository.toggleLike(shayari)
        }
    }

    fun toggleSave(shayari: Shayari) {
        viewModelScope.launch {
            repository.toggleSave(shayari)
        }
    }

    fun refreshDailyTrending() {
        viewModelScope.launch {
            repository.checkAndUpdateDailyTrending(forceRefresh = true)
        }
    }

    fun refreshDailyPickWithWidget(context: Context) {
        viewModelScope.launch {
            repository.checkAndUpdateDailyTrending(forceRefresh = true)
            com.example.widget.ShayariDailyWidgetProvider.updateAllWidgets(context)
        }
    }

    fun pinHomescreenWidget(context: Context, onResult: (Boolean) -> Unit) {
        com.example.widget.ShayariDailyWidgetProvider.pinWidgetToHomeScreen(context, onResult)
    }

    // Offline Management
    fun toggleDownload(shayari: Shayari) {
        viewModelScope.launch {
            repository.toggleDownload(shayari)
        }
    }

    fun downloadAllFavorites() {
        viewModelScope.launch {
            val favs = savedShayaris.value.map { it.id }
            repository.downloadAllFavorites(favs)
        }
    }

    fun downloadCuratedPack(packType: String) {
        viewModelScope.launch {
            repository.downloadCuratedPack(packType)
        }
    }

    fun clearDownloadedStorage() {
        viewModelScope.launch {
            repository.clearDownloadedStorage()
        }
    }

    fun toggleOfflineSimulation() {
        isOfflineSimulated.value = !isOfflineSimulated.value
    }

    // Moderation Management
    fun approveSubmission(id: String) {
        viewModelScope.launch {
            repository.approveSubmission(id)
        }
    }

    fun rejectSubmission(id: String, reason: String) {
        viewModelScope.launch {
            repository.rejectSubmission(id, reason)
        }
    }

    fun flagSubmission(id: String, reason: String) {
        viewModelScope.launch {
            repository.flagSubmission(id, reason)
        }
    }

    fun runModerationScan(text: String) {
        testScanInput.value = text
        if (text.isBlank()) {
            testScanResult.value = null
        } else {
            testScanResult.value = com.example.moderation.ContentModerator.analyze(text)
        }
    }

    fun clearModerationScan() {
        testScanInput.value = ""
        testScanResult.value = null
    }

    fun openShayariDetail(shayari: Shayari) {
        selectedDetailShayari.value = shayari
    }

    fun openShayariDetailById(id: String) {
        viewModelScope.launch {
            val all = repository.allShayaris.first()
            val match = all.find { it.id == id }
                ?: (if (id == "daily_pick") dailyPick.value else null)
                ?: dailyPick.value
                ?: all.firstOrNull()
            selectedDetailShayari.value = match
        }
    }

    fun closeShayariDetail() {
        selectedDetailShayari.value = null
    }

    fun initFcm(context: Context) {
        com.example.notification.ShayariFirebaseMessagingService.initialize(context) { token ->
            fcmToken.value = token
        }
        val cached = com.example.notification.ShayariFirebaseMessagingService.getSavedToken(context)
        if (!cached.isNullOrBlank()) {
            fcmToken.value = cached
        }
    }

    fun triggerTestNotification(context: Context) {
        val current = dailyPick.value ?: displayedShayaris.value.firstOrNull()
        val title = "🌅 Morning Shayari • ନୂଆ କବିତା"
        val body = if (current != null) {
            "${current.lines}\n— ${current.author}"
        } else {
            "\"हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले...\"\nDiscover today's trending verses in Hindi, Odia & English."
        }
        DailyNotificationManager.showNotification(context, title, body, current?.id)
    }

    fun triggerTestFcmPush(context: Context) {
        val current = dailyPick.value ?: displayedShayaris.value.firstOrNull()
        com.example.notification.ShayariFirebaseMessagingService.simulateDailyMorningFcmPush(context, current)
    }

    fun composeWithGemini(topic: String, emotion: String, language: String, penName: String) {
        viewModelScope.launch {
            isComposing.value = true
            compositionError.value = null
            composedResult.value = null

            val result = GeminiClient.generateShayari(
                topic = topic,
                emotion = emotion,
                language = language,
                authorPenName = penName
            )
            result.onSuccess { text ->
                composedResult.value = text
                repository.recordActivity(
                    ActivityType.COMPOSE,
                    "Composed AI Couplet",
                    "Generated verse on \"$topic\" in $language"
                )
            }.onFailure { e ->
                compositionError.value = e.message ?: "Failed to generate verse."
            }
            isComposing.value = false
        }
    }

    fun analyzeWithHighThinking(shayariText: String, language: String) {
        viewModelScope.launch {
            isAnalyzingWithHighThinking.value = true
            analysisError.value = null
            highThinkingAnalysis.value = null

            val result = GeminiClient.analyzePoetryWithHighThinking(shayariText, language)
            result.onSuccess {
                highThinkingAnalysis.value = it
            }.onFailure {
                analysisError.value = it.message ?: "Analysis failed."
            }
            isAnalyzingWithHighThinking.value = false
        }
    }

    fun findRhymes(seedWord: String, language: String) {
        viewModelScope.launch {
            isSearchingRhymes.value = true
            val result = GeminiClient.findRhymesAndQaafiya(seedWord, language)
            result.onSuccess {
                rhymingSuggestions.value = it
            }.onFailure {
                rhymingSuggestions.value = emptyList()
            }
            isSearchingRhymes.value = false
        }
    }

    fun publishNewShayari(
        lines: String,
        emotion: String,
        language: String,
        author: String,
        penName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val newShayari = Shayari(
                id = "usr_" + UUID.randomUUID().toString().take(8),
                lines = lines,
                author = author.ifBlank { "You" },
                penName = penName,
                language = language,
                emotion = emotion,
                likesCount = 1,
                isLiked = true,
                timestamp = System.currentTimeMillis()
            )
            repository.publishShayari(newShayari)
            onSuccess()
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            _userProfileState.value = profile
            repository.saveUserProfile(profile)
            repository.recordActivity(
                ActivityType.LOGIN,
                "Profile Updated",
                "Updated details for ${profile.displayName} ('${profile.penName}')"
            )
        }
    }

    // --- Firebase Auth Actions ---
    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            authSuccessMessage.value = null
            val result = repository.signInWithEmail(email, pass)
            result.onSuccess { profile ->
                _userProfileState.value = profile
                authSuccessMessage.value = "Successfully signed in as ${profile.displayName}!"
            }.onFailure { e ->
                authError.value = e.message ?: "Failed to sign in. Please check credentials."
            }
            isAuthLoading.value = false
        }
    }

    fun signUpWithEmail(email: String, pass: String, displayName: String, penName: String) {
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            authSuccessMessage.value = null
            val result = repository.signUpWithEmail(email, pass, displayName, penName)
            result.onSuccess { profile ->
                _userProfileState.value = profile
                authSuccessMessage.value = "Account created! Welcome to KavyaSetu, ${profile.displayName}."
            }.onFailure { e ->
                authError.value = e.message ?: "Failed to create account. Please check inputs."
            }
            isAuthLoading.value = false
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            authSuccessMessage.value = null
            val result = repository.signInAnonymously()
            result.onSuccess { profile ->
                _userProfileState.value = profile
                authSuccessMessage.value = "Switched to Guest Shayar mode."
            }.onFailure { e ->
                authError.value = e.message ?: "Guest login failed."
            }
            isAuthLoading.value = false
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                authError.value = "Please enter an email address for password reset."
                return@launch
            }
            isAuthLoading.value = true
            authError.value = null
            val result = repository.sendPasswordReset(email)
            result.onSuccess {
                authSuccessMessage.value = "Password reset instructions sent to $email."
            }.onFailure { e ->
                authError.value = e.message ?: "Could not send password reset email."
            }
            isAuthLoading.value = false
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            isAuthLoading.value = true
            val result = repository.sendEmailVerification()
            result.onSuccess {
                authSuccessMessage.value = "Verification email sent! Please check your inbox."
            }.onFailure { e ->
                authError.value = e.message ?: "Could not send verification email."
            }
            isAuthLoading.value = false
        }
    }

    fun updatePassword(newPass: String) {
        viewModelScope.launch {
            if (newPass.length < 6) {
                authError.value = "Password must be at least 6 characters."
                return@launch
            }
            isAuthLoading.value = true
            val result = repository.updatePassword(newPass)
            result.onSuccess {
                authSuccessMessage.value = "Password updated securely!"
            }.onFailure { e ->
                authError.value = e.message ?: "Failed to update password."
            }
            isAuthLoading.value = false
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            isAuthLoading.value = true
            val result = repository.deleteAccount()
            result.onSuccess {
                _userProfileState.value = UserProfile(
                    uid = "local_poet_guest",
                    displayName = "Guest Shayar",
                    penName = "Parwaaz",
                    bio = "Discovering poetry across boundaries.",
                    isGuest = true,
                    streakDays = 5
                )
                authSuccessMessage.value = "Account deleted. Switched to guest mode."
            }.onFailure { e ->
                authError.value = e.message ?: "Failed to delete account."
            }
            isAuthLoading.value = false
        }
    }

    fun signOut() {
        repository.signOutUser()
        _userProfileState.value = UserProfile(
            uid = "local_poet_guest",
            displayName = "Guest Shayar",
            penName = "Parwaaz",
            bio = "Discovering poetry across boundaries.",
            isGuest = true,
            streakDays = 5
        )
        authSuccessMessage.value = "Signed out successfully."
    }

    fun clearAuthMessages() {
        authError.value = null
        authSuccessMessage.value = null
    }

    fun recordUserActivity(type: ActivityType, title: String, description: String) {
        repository.recordActivity(type, title, description)
    }
}

class MainViewModelFactory(private val repository: ShayariRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
