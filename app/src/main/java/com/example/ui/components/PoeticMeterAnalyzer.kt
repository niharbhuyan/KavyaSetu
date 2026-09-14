package com.example.ui.components

data class MeterAnalysisResult(
    val line1Syllables: Int,
    val line2Syllables: Int,
    val line1Matras: Int,
    val line2Matras: Int,
    val line1Pattern: String,
    val line2Pattern: String,
    val detectedBeherOrChhanda: String,
    val identifiedQafiya: Pair<String, String>?,
    val identifiedRadif: String?,
    val symmetryScore: Int, // 0 to 100%
    val rhythmBalanceComment: String,
    val poeticSuggestions: List<String>
)

object PoeticMeterAnalyzer {

    // Analyzes a 2-line couplet (sher)
    fun analyzeCouplet(couplet: String): MeterAnalysisResult {
        val lines = couplet.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val line1 = lines.getOrNull(0) ?: ""
        val line2 = lines.getOrNull(1) ?: lines.getOrNull(0) ?: ""

        val syl1 = countSyllables(line1)
        val syl2 = countSyllables(line2)

        val matraPattern1 = generateMatraPattern(line1)
        val matraPattern2 = generateMatraPattern(line2)

        val matraCount1 = matraPattern1.count { it == 'S' } * 2 + matraPattern1.count { it == '|' }
        val matraCount2 = matraPattern2.count { it == 'S' } * 2 + matraPattern2.count { it == '|' }

        // Symmetry score based on syllable and matra closeness
        val sylDiff = kotlin.math.abs(syl1 - syl2)
        val matraDiff = kotlin.math.abs(matraCount1 - matraCount2)
        val score = (100 - (sylDiff * 8 + matraDiff * 5)).coerceIn(20, 100)

        // Qafiya & Radif detection
        val words1 = line1.split(Regex("\\s+")).filter { it.isNotBlank() }
        val words2 = line2.split(Regex("\\s+")).filter { it.isNotBlank() }

        var radif: String? = null
        var qafiya: Pair<String, String>? = null

        if (words1.isNotEmpty() && words2.isNotEmpty()) {
            val last1 = words1.last().lowercase().replace(Regex("[^\\p{L}]"), "")
            val last2 = words2.last().lowercase().replace(Regex("[^\\p{L}]"), "")

            if (last1 == last2 && last1.isNotEmpty()) {
                radif = words1.last()
                // The words preceding the radif are the qafiya
                if (words1.size >= 2 && words2.size >= 2) {
                    qafiya = Pair(words1[words1.size - 2], words2[words2.size - 2])
                }
            } else {
                qafiya = Pair(words1.last(), words2.last())
            }
        }

        // Beher / Meter identification
        val avgSyl = (syl1 + syl2) / 2
        val avgMatra = (matraCount1 + matraCount2) / 2

        val meterName = when {
            avgMatra in 22..26 -> "Doha / Chaupai Chhanda (दोहा / ଚୌପଦୀ • 24 Matras)"
            avgSyl in 10..12 -> "Beher-e-Mutaqarib (بحر متقارب • Fa'oolun Fa'oolun)"
            avgSyl in 13..15 -> "Beher-e-Ramal (بحر رمل • Faa'ilaatun Faa'ilaatun)"
            avgSyl in 16..18 -> "Beher-e-Hazaj (بحر هزج • Mafaa'eelun Mafaa'eelun)"
            avgSyl in 7..9 -> "Mukhtasar Beher (Short Metre • लघु छंद)"
            else -> "Aazaad / Free Cadence (आज़ाद नज़्म • Modern Verse)"
        }

        val balanceComment = when {
            score >= 90 -> "🌟 Flawless rhythmic balance! The two misras flow with majestic symmetry."
            score >= 75 -> "✨ Harmonious cadence! The musical flow between hemistichs is pleasing and balanced."
            else -> "⚖️ Variable meter detected. Misra 1 has $syl1 syllables while Misra 2 has $syl2 syllables."
        }

        val suggestions = mutableListOf<String>()
        if (sylDiff > 2) {
            suggestions.add("Consider pruning or adding 1-2 syllables to line 2 to match line 1's cadence.")
        }
        if (radif != null) {
            suggestions.add("Radif identified ('$radif'). Ensure both lines end in identical rhyming tones (Qafiya).")
        } else {
            suggestions.add("Try adding a recurring ending word (Radif) to create the signature Ghazal refrain.")
        }
        suggestions.add("Matra weights: Line 1 = $matraCount1 matras, Line 2 = $matraCount2 matras.")

        return MeterAnalysisResult(
            line1Syllables = syl1,
            line2Syllables = syl2,
            line1Matras = matraCount1,
            line2Matras = matraCount2,
            line1Pattern = matraPattern1,
            line2Pattern = matraPattern2,
            detectedBeherOrChhanda = meterName,
            identifiedQafiya = qafiya,
            identifiedRadif = radif,
            symmetryScore = score,
            rhythmBalanceComment = balanceComment,
            poeticSuggestions = suggestions
        )
    }

    private fun countSyllables(line: String): Int {
        val clean = line.trim()
        if (clean.isEmpty()) return 0
        // Heuristic syllable counter covering English vowels, Hindi/Odia matras
        val vowelRegex = Regex("[aeiouyAEIOUYāīūēōअआइईउऊऋएऐओऔािीुूृेैोौंଁଂଃଅଆଇଈଉଊଏଐଓଔାିୀୁୂୃେୈୋୌ]")
        val count = vowelRegex.findAll(clean).count()
        return count.coerceAtLeast(clean.split(Regex("\\s+")).size)
    }

    private fun generateMatraPattern(line: String): String {
        // Laghu = |, Guru = S
        val guruChars = Regex("[āīūēōaiouyAIOUYआईऊएऐओऔाीूेैोौୌୋୈେୂୀାଆଈଊଏଐଓଔ]")
        val words = line.split(Regex("\\s+")).filter { it.isNotBlank() }
        val sb = StringBuilder()
        for (word in words) {
            for (char in word) {
                if (char.toString().matches(guruChars)) {
                    sb.append("S")
                } else if (char.isLetter()) {
                    sb.append("|")
                }
            }
            sb.append(" ")
        }
        return sb.toString().trim().ifEmpty { "| S | S" }
    }
}
