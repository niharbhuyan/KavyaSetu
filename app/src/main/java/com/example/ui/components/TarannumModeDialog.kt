package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RagaTarannum
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

@Composable
fun TarannumModeDialog(
    shayari: Shayari,
    audioReciter: AudioReciter,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val synthesizer = remember { TarannumSynthesizer(context) }

    val isPlayingSynth by synthesizer.isPlaying.collectAsState()
    val currentRaga by synthesizer.currentRaga.collectAsState()
    val activeSwara by synthesizer.activeSwara.collectAsState()
    val droneVolume by synthesizer.volume.collectAsState()

    var isSingingVoice by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            synthesizer.release()
            audioReciter.stop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp)),
            color = DeepMidnight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AntiqueGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪕", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tarannum Mode • ترنم",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AntiqueGold
                            )
                            Text(
                                text = "Classical Ghazal Melodic Chanting with Ragas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_tarannum_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val lazyListState = rememberLazyListState()
                val readingMetrics = remember(shayari.lines) {
                    calculatePoemReadingMetrics(shayari.lines)
                }

                // Reading Progress Bar at top of Tarannum chanter with Share & Copy buttons
                LazyListPoetryReadingProgressBar(
                    lazyListState = lazyListState,
                    totalItems = 5,
                    poemMetrics = readingMetrics,
                    modifier = Modifier.padding(bottom = 6.dp),
                    onSharePoem = {
                        com.example.util.SocialShareHelper.sharePoem(context, shayari)
                    },
                    onCopyPoem = {
                        com.example.util.SocialShareHelper.copyPoem(context, shayari)
                    }
                )

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Verse Display
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "— ❦ —",
                                    color = AntiqueGold.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = shayari.lines,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        lineHeight = 26.sp
                                    ),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "~ ${shayari.author} ${if (shayari.penName.isNotBlank()) "‘${shayari.penName}’" else ""} ~",
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = AntiqueGold
                                )
                            }
                        }
                    }

                    // Raga Selection
                    item {
                        Text(
                            text = "Select Classical Raga Scale (राग):",
                            style = MaterialTheme.typography.labelMedium,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(RagaTarannum.entries) { raga ->
                                val isSelected = raga == currentRaga
                                Card(
                                    modifier = Modifier
                                        .width(170.dp)
                                        .clickable {
                                            synthesizer.startTarannum(raga)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) AntiqueGold.copy(alpha = 0.22f) else CharcoalElevated
                                    ),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AntiqueGold) else null
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(raga.emoji, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = raga.ragaName,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) AntiqueGold else Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = raga.hindiName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VelvetRose
                                        )
                                        Text(
                                            text = raga.timeOfDay,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = raga.mood,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.LightGray,
                                            fontSize = 9.sp,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Active Swara Visualizer & Drone status
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = if (isPlayingSynth) AntiqueGold else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isPlayingSynth) "Acoustic Drone Active: ${currentRaga.ragaName}" else "Drone Stopped",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isPlayingSynth) AntiqueGold else Color.Gray
                                        )
                                    }

                                    // Toggle Drone
                                    IconButton(
                                        onClick = {
                                            if (isPlayingSynth) {
                                                synthesizer.stopTarannum()
                                            } else {
                                                synthesizer.startTarannum(currentRaga)
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingSynth) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = "Toggle Drone",
                                            tint = AntiqueGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Visual Swaras line
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    items(currentRaga.scaleSwaras) { swara ->
                                        val isActive = swara == activeSwara && isPlayingSynth
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isActive) AntiqueGold else CharcoalElevated.copy(alpha = 0.8f)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isActive) AntiqueGold else Color.Gray,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                                .then(if (isActive) Modifier.scale(glowScale) else Modifier)
                                        ) {
                                            Text(
                                                text = swara,
                                                fontSize = 12.sp,
                                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isActive) DeepMidnight else Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Drone Volume
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Drone Volume", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Slider(
                                        value = droneVolume,
                                        onValueChange = { synthesizer.setVolume(it) },
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = AntiqueGold,
                                            activeTrackColor = AntiqueGold,
                                            inactiveTrackColor = Color.DarkGray
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Sing with Tarannum Action
                    item {
                        Button(
                            onClick = {
                                if (!isPlayingSynth) {
                                    synthesizer.startTarannum(currentRaga)
                                }
                                isSingingVoice = true
                                audioReciter.speak(
                                    shayari.lines,
                                    shayari.language
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("play_tarannum_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSingingVoice) "Melodic Tarannum Chanting..." else "Sing in Tarannum (غزل ترنم)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tarannum melds classical Raga resonance with vocal elongation, breathing life into classical sher and ghazals as practiced in royal mushairas.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
