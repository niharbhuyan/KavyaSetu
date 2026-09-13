package com.example.data.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.Shayari
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseService {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUser: FirebaseUser?
        get() = try { auth.currentUser } catch (e: Exception) { null }

    suspend fun ensureAnonymousOrExistingUser(): UserProfile = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser
            if (user != null) {
                return@withContext fetchUserProfile(user.uid)
            }
            // Sign in anonymously if not signed in yet
            val authResult = auth.signInAnonymously().await()
            val newUser = authResult.user
            if (newUser != null) {
                val profile = UserProfile(
                    uid = newUser.uid,
                    displayName = "Guest Shayar",
                    penName = "Raahi",
                    bio = "Discovering the depths of poetry across worlds.",
                    email = null,
                    isEmailVerified = false,
                    authProvider = "anonymous",
                    isGuest = true,
                    shayariCount = 1,
                    streakDays = 5
                )
                saveUserProfile(profile)
                return@withContext profile
            }
        } catch (e: Exception) {
            Log.w("FirebaseService", "Firebase auth fallback: ${e.message}")
        }
        // Fallback default profile
        UserProfile(
            uid = "local_poet_guest",
            displayName = "Guest Shayar",
            penName = "Parwaaz",
            bio = "Words carrying emotions across boundaries.",
            email = null,
            isEmailVerified = false,
            authProvider = "anonymous",
            isGuest = true,
            streakDays = 5
        )
    }

    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return@withContext Result.failure(Exception("Authentication succeeded but user was null"))
            val existing = try { fetchUserProfile(user.uid) } catch (e: Exception) { null }
            val profile = existing?.copy(
                email = user.email,
                isEmailVerified = user.isEmailVerified,
                authProvider = "password",
                isGuest = false
            ) ?: UserProfile(
                uid = user.uid,
                displayName = user.displayName ?: email.substringBefore("@"),
                penName = (user.displayName ?: email.substringBefore("@")),
                bio = "Sharing verses that touch the soul.",
                photoUrl = user.photoUrl?.toString(),
                email = user.email,
                isEmailVerified = user.isEmailVerified,
                authProvider = "password",
                preferredLanguage = "hindi",
                isGuest = false
            )
            saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Email sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String,
        penName: String
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return@withContext Result.failure(Exception("Failed to create user"))
            
            try {
                val changeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.ifBlank { "Poet" })
                    .build()
                user.updateProfile(changeRequest).await()
            } catch (e: Exception) {
                Log.w("FirebaseService", "Profile name update failed: ${e.message}")
            }

            val profile = UserProfile(
                uid = user.uid,
                displayName = displayName.ifBlank { "Poet" },
                penName = penName.ifBlank { displayName.ifBlank { "Shayar" } },
                bio = "Poet weaving words into eternal echoes.",
                photoUrl = null,
                email = user.email,
                isEmailVerified = user.isEmailVerified,
                authProvider = "password",
                preferredLanguage = "hindi",
                favoriteEmotion = "ishq",
                isGuest = false,
                followers = 150,
                following = 25,
                shayariCount = 1,
                streakDays = 5
            )
            saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Sign up failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInAnonymously().await()
            val user = result.user ?: return@withContext Result.failure(Exception("Anonymous login failed"))
            val profile = UserProfile(
                uid = user.uid,
                displayName = "Guest Shayar",
                penName = "Parwaaz",
                bio = "Exploring verses as a guest poet.",
                email = null,
                isEmailVerified = false,
                authProvider = "anonymous",
                isGuest = true,
                streakDays = 5
            )
            saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Anonymous sign-in error", e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Password reset error", e)
            Result.failure(e)
        }
    }

    suspend fun sendEmailVerification(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("No signed-in user found"))
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Send verification error", e)
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("No signed-in user found"))
            user.updatePassword(newPass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Update password error", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("No active session to delete"))
            val uid = user.uid
            try {
                firestore.collection("users").document(uid).delete().await()
            } catch (ignored: Exception) {}
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Delete account error", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredential(idToken: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return@withContext Result.failure(Exception("Failed to sign in"))
            
            val existing = try { fetchUserProfile(user.uid) } catch (e: Exception) { null }
            val profile = existing?.copy(
                displayName = user.displayName ?: existing.displayName,
                email = user.email,
                photoUrl = user.photoUrl?.toString(),
                authProvider = "google",
                isGuest = false
            ) ?: UserProfile(
                uid = user.uid,
                displayName = user.displayName ?: "Poet",
                penName = user.displayName?.split(" ")?.firstOrNull() ?: "Shayar",
                bio = "Sharing verses that touch the soul.",
                photoUrl = user.photoUrl?.toString(),
                email = user.email,
                isEmailVerified = user.isEmailVerified,
                authProvider = "google",
                preferredLanguage = "hindi",
                isGuest = false,
                streakDays = 5
            )
            saveUserProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Google Sign-in error", e)
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            val data = hashMapOf(
                "uid" to profile.uid,
                "displayName" to profile.displayName,
                "penName" to profile.penName,
                "bio" to profile.bio,
                "photoUrl" to (profile.photoUrl ?: ""),
                "email" to (profile.email ?: ""),
                "isEmailVerified" to profile.isEmailVerified,
                "authProvider" to profile.authProvider,
                "preferredLanguage" to profile.preferredLanguage,
                "favoriteEmotion" to profile.favoriteEmotion,
                "isGuest" to profile.isGuest,
                "followers" to profile.followers,
                "following" to profile.following,
                "shayariCount" to profile.shayariCount,
                "streakDays" to profile.streakDays
            )
            firestore.collection("users").document(profile.uid)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.w("FirebaseService", "Error saving profile to firestore: ${e.message}")
            false
        }
    }

    suspend fun fetchUserProfile(uid: String): UserProfile = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                val currUser = auth.currentUser
                return@withContext UserProfile(
                    uid = doc.getString("uid") ?: uid,
                    displayName = doc.getString("displayName") ?: (currUser?.displayName ?: "Poet"),
                    penName = doc.getString("penName") ?: "Shayar",
                    bio = doc.getString("bio") ?: "Spreading poetic warmth.",
                    photoUrl = doc.getString("photoUrl") ?: currUser?.photoUrl?.toString(),
                    email = doc.getString("email") ?: currUser?.email,
                    isEmailVerified = doc.getBoolean("isEmailVerified") ?: (currUser?.isEmailVerified ?: false),
                    authProvider = doc.getString("authProvider") ?: (if (currUser?.isAnonymous == true) "anonymous" else "password"),
                    preferredLanguage = doc.getString("preferredLanguage") ?: "hindi",
                    favoriteEmotion = doc.getString("favoriteEmotion") ?: "ishq",
                    isGuest = doc.getBoolean("isGuest") ?: (currUser?.isAnonymous ?: true),
                    followers = doc.getLong("followers")?.toInt() ?: 120,
                    following = doc.getLong("following")?.toInt() ?: 45,
                    shayariCount = doc.getLong("shayariCount")?.toInt() ?: 0,
                    streakDays = doc.getLong("streakDays")?.toInt() ?: 5
                )
            }
        } catch (e: Exception) {
            Log.w("FirebaseService", "Error fetching user profile from firestore: ${e.message}")
        }
        val currUser = auth.currentUser
        UserProfile(
            uid = uid,
            displayName = currUser?.displayName ?: "Poet",
            penName = "Sukhanwar",
            email = currUser?.email,
            isEmailVerified = currUser?.isEmailVerified ?: false,
            authProvider = if (currUser?.isAnonymous == true) "anonymous" else "password",
            isGuest = currUser?.isAnonymous ?: true,
            streakDays = 5
        )
    }

    suspend fun syncShayariToFirestore(shayari: Shayari): Boolean = withContext(Dispatchers.IO) {
        try {
            val map = hashMapOf(
                "id" to shayari.id,
                "lines" to shayari.lines,
                "author" to shayari.author,
                "penName" to shayari.penName,
                "language" to shayari.language,
                "emotion" to shayari.emotion,
                "likesCount" to shayari.likesCount,
                "timestamp" to shayari.timestamp,
                "translationEnglish" to shayari.translationEnglish,
                "translationHindi" to shayari.translationHindi,
                "translationOdia" to shayari.translationOdia,
                "poeticAnalysis" to shayari.poeticAnalysis,
                "isTrending" to shayari.isTrending,
                "isDailyPick" to shayari.isDailyPick,
                "cardBackgroundUrl" to (shayari.cardBackgroundUrl ?: "")
            )
            firestore.collection("shayaris").document(shayari.id)
                .set(map, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.w("FirebaseService", "Firestore sync error: ${e.message}")
            false
        }
    }

    suspend fun fetchRemoteShayaris(): List<Shayari> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("shayaris")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(40)
                .get()
                .await()

            return@withContext snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val lines = doc.getString("lines") ?: return@mapNotNull null
                val author = doc.getString("author") ?: "Anonymous"
                val penName = doc.getString("penName") ?: ""
                val language = doc.getString("language") ?: "hindi"
                val emotion = doc.getString("emotion") ?: "ishq"
                val likes = doc.getLong("likesCount")?.toInt() ?: 0
                val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                Shayari(
                    id = id,
                    lines = lines,
                    author = author,
                    penName = penName,
                    language = language,
                    emotion = emotion,
                    likesCount = likes,
                    timestamp = ts,
                    translationEnglish = doc.getString("translationEnglish") ?: "",
                    translationHindi = doc.getString("translationHindi") ?: "",
                    translationOdia = doc.getString("translationOdia") ?: "",
                    poeticAnalysis = doc.getString("poeticAnalysis") ?: "",
                    isTrending = doc.getBoolean("isTrending") ?: false,
                    isDailyPick = doc.getBoolean("isDailyPick") ?: false,
                    cardBackgroundUrl = doc.getString("cardBackgroundUrl")
                )
            }
        } catch (e: Exception) {
            Log.w("FirebaseService", "Error reading firestore shayaris: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateLikeCount(shayariId: String, delta: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val ref = firestore.collection("shayaris").document(shayariId)
            firestore.runTransaction { tx ->
                val snapshot = tx.get(ref)
                val current = snapshot.getLong("likesCount") ?: 0
                tx.update(ref, "likesCount", maxOf(0, current + delta))
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseService", "Sign out error", e)
        }
    }
}
