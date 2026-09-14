package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

data class SherBaaziTurn(
    val speaker: String, // "You" or "Sukhanwar (AI)"
    val lines: String,
    val author: String,
    val endingLetter: Char,
    val language: String
)

@Composable
fun SherBaaziGameDialog(
    allShayaris: List<Shayari>,
    audioReciter: AudioReciter,
    onDismiss: () -> Unit
) {
    var userCoupletInput by remember { mutableStateOf("") }
    var currentLetterTarget by remember { mutableStateOf('न') }
    var score by remember { mutableIntStateOf(10) }
    var streak by remember { mutableIntStateOf(1) }

    val gameTurns = remember {
        mutableStateListOf(
            SherBaaziTurn(
                speaker = "Sukhanwar (Master Poet)",
                lines = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले",
                author = "Mirza Ghalib",
                endingLetter = 'न',
                language = "hindi"
            )
        )
    }

    fun findAiResponse(lastLetter: Char) {
        val candidate = allShayaris.find {
            val first = it.lines.trim().firstOrNull { ch -> ch.isLetter() } ?: ' '
            first.equals(lastLetter, ignoreCase = true) ||
            it.lines.lowercase().startsWith(lastLetter.lowercaseChar())
        } ?: allShayaris.shuffled().firstOrNull()

        if (candidate != null) {
            val lines = candidate.lines.trim()
            val endChar = lines.lastOrNull { it.isLetter() } ?: 'र'
            gameTurns.add(
                SherBaaziTurn(
                    speaker = "Sukhanwar (Master Poet)",
                    lines = lines,
                    author = candidate.author,
                    endingLetter = endChar,
                    language = candidate.language
                )
            )
            currentLetterTarget = endChar
        }
    }

    fun submitUserVerse() {
        val trimmed = userCoupletInput.trim()
        if (trimmed.isBlank()) return

        val endChar = trimmed.lastOrNull { it.isLetter() } ?: 'क'
        gameTurns.add(
            SherBaaziTurn(
                speaker = "You (Poet)",
                lines = trimmed,
                author = "Your Words",
                endingLetter = endChar,
                language = "hindi"
            )
        )
        score += 15
        streak++
        userCoupletInput = ""

        findAiResponse(endChar)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚔️", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "Sher Baazi (شیر بازی)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "Classical Couplet Antakshari Duel",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Score and Target Letter Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = VelvetRose.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(20.dp))
                            Text("Score: $score pts", fontWeight = FontWeight.Bold, color = AntiqueGold, fontSize = 13.sp)
                            Text("• Streak: ${streak}x", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Next Letter: '$currentLetterTarget'",
                                fontWeight = FontWeight.Bold,
                                color = DeepMidnight,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chat / Recital Chain History
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(gameTurns) { turn ->
                        val isUser = turn.speaker.startsWith("You")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) VelvetRose.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isUser) VelvetRose else AntiqueGold.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = turn.speaker,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isUser) VelvetRose else AntiqueGold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Ends in '${turn.endingLetter}'",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        IconButton(
                                            onClick = { audioReciter.speak(turn.lines, turn.language) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.VolumeUp,
                                                contentDescription = "Recite",
                                                tint = AntiqueGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = turn.lines,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "— ${turn.author}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Suggestions from classic library matching the target letter
                val suggestions = remember(currentLetterTarget, allShayaris) {
                    allShayaris.filter {
                        val firstChar = it.lines.trim().firstOrNull { c -> c.isLetter() } ?: ' '
                        firstChar.equals(currentLetterTarget, ignoreCase = true) ||
                        it.lines.lowercase().startsWith(currentLetterTarget.lowercaseChar())
                    }.take(3)
                }

                if (suggestions.isNotEmpty()) {
                    Text(
                        text = "Quick Classic Verses for '$currentLetterTarget':",
                        fontSize = 11.sp,
                        color = AntiqueGold,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestions) { s ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .clickable { userCoupletInput = s.lines }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${s.lines.take(30)}... (${s.author})",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userCoupletInput,
                        onValueChange = { userCoupletInput = it },
                        placeholder = { Text("Recite couplet starting with '$currentLetterTarget'...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sher_baazi_input"),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntiqueGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Button(
                        onClick = { submitUserVerse() },
                        enabled = userCoupletInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp).testTag("sher_baazi_submit")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Recite")
                    }
                }
            }
        }
    }
}
