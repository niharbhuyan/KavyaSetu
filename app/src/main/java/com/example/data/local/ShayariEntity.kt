package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Shayari
import com.example.data.model.UserProfile

@Entity(tableName = "shayari_posts")
data class ShayariEntity(
    @PrimaryKey val id: String,
    val lines: String,
    val author: String,
    val penName: String,
    val language: String,
    val emotion: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val timestamp: Long,
    val translationEnglish: String,
    val translationHindi: String,
    val translationOdia: String,
    val poeticAnalysis: String,
    val isTrending: Boolean,
    val isDailyPick: Boolean,
    val cardBackgroundUrl: String?,
    val isDownloaded: Boolean = false,
    val downloadedAt: Long? = null,
    val moderationStatus: String = "APPROVED",
    val moderationReason: String? = null,
    val moderationSeverity: String = "SAFE",
    val moderatedAt: Long? = null
) {
    fun toDomain(): Shayari = Shayari(
        id = id,
        lines = lines,
        author = author,
        penName = penName,
        language = language,
        emotion = emotion,
        likesCount = likesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        timestamp = timestamp,
        translationEnglish = translationEnglish,
        translationHindi = translationHindi,
        translationOdia = translationOdia,
        poeticAnalysis = poeticAnalysis,
        isTrending = isTrending,
        isDailyPick = isDailyPick,
        cardBackgroundUrl = cardBackgroundUrl,
        isDownloaded = isDownloaded,
        downloadedAt = downloadedAt,
        moderationStatus = moderationStatus,
        moderationReason = moderationReason,
        moderationSeverity = moderationSeverity,
        moderatedAt = moderatedAt
    )

    companion object {
        fun fromDomain(domain: Shayari): ShayariEntity = ShayariEntity(
            id = domain.id,
            lines = domain.lines,
            author = domain.author,
            penName = domain.penName,
            language = domain.language,
            emotion = domain.emotion,
            likesCount = domain.likesCount,
            isLiked = domain.isLiked,
            isSaved = domain.isSaved,
            timestamp = domain.timestamp,
            translationEnglish = domain.translationEnglish,
            translationHindi = domain.translationHindi,
            translationOdia = domain.translationOdia,
            poeticAnalysis = domain.poeticAnalysis,
            isTrending = domain.isTrending,
            isDailyPick = domain.isDailyPick,
            cardBackgroundUrl = domain.cardBackgroundUrl,
            isDownloaded = domain.isDownloaded,
            downloadedAt = domain.downloadedAt,
            moderationStatus = domain.moderationStatus,
            moderationReason = domain.moderationReason,
            moderationSeverity = domain.moderationSeverity,
            moderatedAt = domain.moderatedAt
        )
    }
}

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val displayName: String,
    val penName: String,
    val bio: String,
    val photoUrl: String?,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val authProvider: String = "anonymous",
    val preferredLanguage: String,
    val favoriteEmotion: String,
    val isGuest: Boolean,
    val followers: Int,
    val following: Int,
    val shayariCount: Int,
    val streakDays: Int = 5
) {
    fun toDomain(): UserProfile = UserProfile(
        uid = uid,
        displayName = displayName,
        penName = penName,
        bio = bio,
        photoUrl = photoUrl,
        email = email,
        isEmailVerified = isEmailVerified,
        authProvider = authProvider,
        preferredLanguage = preferredLanguage,
        favoriteEmotion = favoriteEmotion,
        isGuest = isGuest,
        followers = followers,
        following = following,
        shayariCount = shayariCount,
        streakDays = streakDays
    )

    companion object {
        fun fromDomain(domain: UserProfile): UserProfileEntity = UserProfileEntity(
            uid = domain.uid,
            displayName = domain.displayName,
            penName = domain.penName,
            bio = domain.bio,
            photoUrl = domain.photoUrl,
            email = domain.email,
            isEmailVerified = domain.isEmailVerified,
            authProvider = domain.authProvider,
            preferredLanguage = domain.preferredLanguage,
            favoriteEmotion = domain.favoriteEmotion,
            isGuest = domain.isGuest,
            followers = domain.followers,
            following = domain.following,
            shayariCount = domain.shayariCount,
            streakDays = domain.streakDays
        )
    }
}
