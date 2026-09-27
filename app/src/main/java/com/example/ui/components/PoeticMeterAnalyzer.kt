package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.MysticTealSoft
import com.example.ui.theme.VelvetRose

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

@Composable
fun BahrTaqtiMeterDialog(
    couplet: String,
    onDismiss: () -> Unit
) {
    val analysis = remember(couplet) {
        PoeticMeterAnalyzer.analyzeCouplet(couplet)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("bahr_taqti_meter_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚖️", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Bahr & Taqti Analyzer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "बह्र और तक़्तीअ • Ghazal Metronome",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detected Meter Badge
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Detected Beher / Meter (बह्र):",
                            fontSize = 11.sp,
                            color = AntiqueGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = analysis.detectedBeherOrChhanda,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Symmetry Score Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rhythm Symmetry Score:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${analysis.symmetryScore}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (analysis.symmetryScore >= 80) AntiqueGold else VelvetRose
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { analysis.symmetryScore / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = AntiqueGold,
                            trackColor = AntiqueGold.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysis.rhythmBalanceComment,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown: Misra 1 & Misra 2
                Text(
                    text = "Hemistich (Misra) Breakdown:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Misra 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Misra 1 (पहला मिसरा):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${analysis.line1Syllables} Syllables • ${analysis.line1Matras} Matras", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AntiqueGold.copy(alpha = 0.08f),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Weight Pattern: ${analysis.line1Pattern} (| = Laghu, S = Guru)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AntiqueGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Misra 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Misra 2 (दूसरा मिसरा):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${analysis.line2Syllables} Syllables • ${analysis.line2Matras} Matras", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AntiqueGold.copy(alpha = 0.08f),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Weight Pattern: ${analysis.line2Pattern} (| = Laghu, S = Guru)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AntiqueGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Radif & Qafiya
                if (analysis.identifiedRadif != null || analysis.identifiedQafiya != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val radifVal = analysis.identifiedRadif
                        if (radifVal != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MysticTeal.copy(alpha = 0.2f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Radif (रदीफ़)", fontSize = 10.sp, color = MysticTealSoft)
                                    Text("'$radifVal'", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MysticTealSoft)
                                }
                            }
                        }
                        val qafiyaVal = analysis.identifiedQafiya
                        if (qafiyaVal != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VelvetRose.copy(alpha = 0.2f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Qafiya (क़ाफ़िया)", fontSize = 10.sp, color = AntiqueGold)
                                    Text("${qafiyaVal.first} / ${qafiyaVal.second}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Poetic Suggestions
                analysis.poeticSuggestions.forEach { sug ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💡", fontSize = 12.sp)
                        Text(
                            text = sug,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close Meter Metronome",
                        color = DeepMidnight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
