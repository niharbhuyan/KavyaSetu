package com.example.data.model

import java.util.UUID

/**
 * Tarhi Mushaira (Daily Classical Verse Challenge).
 * In classical Hindustani tradition, a Tarhi Mushaira provides a preset opening hemistich (Misra-e-Tarha),
 * strict meter (Bahr), and rhyming pattern (Radif & Qafia).
 * Community poets submit their completions and vote on favorites.
 */
data class TarhiChallenge(
    val id: String = UUID.randomUUID().toString(),
    val dayNumber: Int,
    val openingMisra: String, // First line / Opening hemistich
    val poetReference: String, // Original inspiration (e.g. Mirza Ghalib, Meer Taqi Meer)
    val bahrName: String, // Poetic meter (e.g. Bahr-e-Ramal Musamman Makhboon)
    val requiredQafiaPattern: String, // Rhyme scheme hint (e.g. "निकले, पिघले, संभले")
    val requiredRadif: String, // Refrain (e.g. "दम निकले")
    val submissions: List<TarhiSubmission> = emptyList()
)

data class TarhiSubmission(
    val id: String = UUID.randomUUID().toString(),
    val challengeId: String,
    val poetName: String,
    val takhallus: String,
    val openingMisra: String,
    val secondMisra: String, // User's submitted completion
    val upvotes: Int = 0,
    val isUserSubmission: Boolean = false,
    val submittedAtMillis: Long = System.currentTimeMillis()
)
