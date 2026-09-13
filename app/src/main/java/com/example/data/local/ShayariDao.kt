package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShayariDao {
    @Query("SELECT * FROM shayari_posts ORDER BY timestamp DESC")
    fun getAllShayaris(): Flow<List<ShayariEntity>>

    @Query("SELECT * FROM shayari_posts WHERE moderationStatus = 'APPROVED' ORDER BY timestamp DESC")
    fun getAllApprovedShayaris(): Flow<List<ShayariEntity>>

    @Query("SELECT * FROM shayari_posts WHERE moderationStatus IN ('PENDING', 'FLAGGED') ORDER BY timestamp DESC")
    fun getPendingModerationShayaris(): Flow<List<ShayariEntity>>

    @Query("SELECT * FROM shayari_posts WHERE moderationStatus = :status ORDER BY timestamp DESC")
    fun getShayarisByModerationStatus(status: String): Flow<List<ShayariEntity>>

    @Query("SELECT * FROM shayari_posts WHERE isDailyPick = 1 AND moderationStatus = 'APPROVED' LIMIT 1")
    fun getDailyPick(): Flow<ShayariEntity?>

    @Query("SELECT * FROM shayari_posts WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedShayaris(): Flow<List<ShayariEntity>>

    @Query("SELECT * FROM shayari_posts WHERE isDownloaded = 1 ORDER BY downloadedAt DESC, timestamp DESC")
    fun getDownloadedShayaris(): Flow<List<ShayariEntity>>

    @Query("SELECT COUNT(*) FROM shayari_posts WHERE isDownloaded = 1")
    fun getDownloadedCount(): Flow<Int>

    @Query("SELECT * FROM shayari_posts WHERE language = :lang AND moderationStatus = 'APPROVED' ORDER BY timestamp DESC")
    fun getShayarisByLanguage(lang: String): Flow<List<ShayariEntity>>

    @Query("SELECT * FROM shayari_posts WHERE emotion = :emotion AND moderationStatus = 'APPROVED' ORDER BY timestamp DESC")
    fun getShayarisByEmotion(emotion: String): Flow<List<ShayariEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShayaris(shayaris: List<ShayariEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShayari(shayari: ShayariEntity)

    @Update
    suspend fun updateShayari(shayari: ShayariEntity)

    @Query("UPDATE shayari_posts SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE shayari_posts SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSaveStatus(id: String, isSaved: Boolean)

    @Query("UPDATE shayari_posts SET isDownloaded = :isDownloaded, downloadedAt = :downloadedAt WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, downloadedAt: Long?)

    @Query("UPDATE shayari_posts SET isDownloaded = 1, downloadedAt = :timestamp WHERE id IN (:ids)")
    suspend fun batchDownload(ids: List<String>, timestamp: Long)

    @Query("UPDATE shayari_posts SET isDownloaded = 0, downloadedAt = NULL")
    suspend fun clearAllDownloaded()

    @Query("UPDATE shayari_posts SET moderationStatus = :status, moderationReason = :reason, moderatedAt = :timestamp WHERE id = :id")
    suspend fun updateModerationStatus(id: String, status: String, reason: String?, timestamp: Long)

    @Query("UPDATE shayari_posts SET isDailyPick = 0")
    suspend fun clearDailyPicks()

    @Query("UPDATE shayari_posts SET isDailyPick = 1 WHERE id = :id")
    suspend fun setDailyPick(id: String)

    @Query("SELECT COUNT(*) FROM shayari_posts")
    suspend fun getCount(): Int

    // User Profile Queries
    @Query("SELECT * FROM user_profiles WHERE uid = :uid LIMIT 1")
    fun getUserProfile(uid: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfileEntity)
}
