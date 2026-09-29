package com.example.ui.components

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.VelvetRose
import kotlin.math.sin

data class ClassicalBahrPattern(
    val nameHindi: String,
    val nameUrdu: String,
    val meterFormula: String,
    val beatsCount: Int,
    val taalsName: String,
    val syllablesPattern: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoeticRiazMetronomeDialog(
    onDismiss: () -> Unit
) {
    val bahrPresets = remember {
        listOf(
            ClassicalBahrPattern("बह्र-ए-हज़ज (Bahr Hazaj)", "بحرِ ہزج", "مفاعیلن مفاعیلن مفاعیلن مفاعیلن", 4, "Rupak / Deepchandi (7-8 Matras)", "U - - - | U - - - | U - - - | U - - -"),
            ClassicalBahrPattern("बह्र-ए-रमल (Bahr Ramal)", "بحرِ رمل", "فاعلاتن فاعلاتن فاعلاتن فاعلاتن", 4, "Keherwa / Dadra (8 Matras)", "- U - - | - U - - | - U - - | - U - -"),
            ClassicalBahrPattern("बह्र-ए-मुतकारिब (Bahr Mutaqarib)", "بحرِ متقارب", "فعولن فعولن فعولن فعولن", 4, "Teentaal (16 Matras)", "U - - | U - - | U - - | U - -"),
            ClassicalBahrPattern("बह्र-ए-कामिल (Bahr Kaamil)", "بحرِ کامل", "متفاعلن متفاعلن متفاعلن", 3, "Roopak Taal (7 Matras)", "U U - U - | U U - U - | U U - U -")
        )
    }

    var selectedBahr by remember { mutableStateOf(bahrPresets[0]) }
    var bpm by remember { mutableFloatStateOf(72f) }
    var isRunning by remember { mutableStateOf(false) }
    var currentBeat by remember { mutableIntStateOf(0) }

    // Pulsing metronome visual state
    val infiniteTransition = rememberInfiniteTransition(label = "metronome_pulse")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween((60000 / bpm.toInt()).coerceAtLeast(300), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Acoustic tone generator for Tabla Bol click simulation
    DisposableEffect(isRunning, bpm) {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        if (isRunning) {
            val interval = (60000L / bpm.toLong()).coerceIn(250L, 2000L)
            runnable = object : Runnable {
                override fun run() {
                    currentBeat = (currentBeat + 1) % selectedBahr.beatsCount
                    try {
                        val sampleRate = 8000
                        val durationMs = 40
                        val numSamples = durationMs * sampleRate / 1000
                        val buffer = ShortArray(numSamples)
                        val freq = if (currentBeat == 0) 587.33 else 440.0 // D5 on Sam, A4 on Khali
                        for (i in 0 until numSamples) {
                            val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                            buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.4).toInt().toShort()
                        }
                        val track = AudioTrack.Builder()
                            .setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                                    .build()
                            )
                            .setAudioFormat(
                                AudioFormat.Builder()
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(sampleRate)
                                    .build()
                            )
                            .setBufferSizeInBytes(buffer.size * 2)
                            .setTransferMode(AudioTrack.MODE_STATIC)
                            .build()
                        track.write(buffer, 0, buffer.size)
                        track.play()
                        handler.postDelayed({ track.release() }, 200)
                    } catch (_: Exception) {}
                    handler.postDelayed(this, interval)
                }
            }
            handler.post(runnable)
        }

        onDispose {
            runnable?.let { handler.removeCallbacks(it) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("riaz_metronome_dialog"),
            color = DeepMidnight,
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🪕", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "रियाज़ और बह्र मेट्रोनोम • Ghazal Riaz",
                                color = AntiqueGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Classical Syllable Rhythm & Tarannum Practice",
                                color = Color.LightGray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Metronome Dial
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(if (isRunning) pulseSize else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (currentBeat == 0 && isRunning) AntiqueGold.copy(alpha = 0.35f) else VelvetRose.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                3.dp,
                                if (currentBeat == 0 && isRunning) AntiqueGold else MysticTeal.copy(alpha = 0.6f)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${bpm.toInt()}",
                            color = AntiqueGold,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "BPM (लहजा)",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (isRunning) "Beat ${currentBeat + 1} of ${selectedBahr.beatsCount}" else "Paused",
                            color = if (currentBeat == 0 && isRunning) AntiqueGold else Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive BPM Slider
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vilambit (Slow 50)", color = Color.Gray, fontSize = 10.sp)
                        Text("Madhya (Medium 80)", color = AntiqueGold, fontSize = 10.sp)
                        Text("Drut (Fast 140)", color = VelvetRose, fontSize = 10.sp)
                    }
                    Slider(
                        value = bpm,
                        onValueChange = { bpm = it },
                        valueRange = 45f..140f,
                        colors = SliderDefaults.colors(
                            thumbColor = AntiqueGold,
                            activeTrackColor = AntiqueGold,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }

                // Play / Pause Toggle Button
                Button(
                    onClick = { isRunning = !isRunning },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) VelvetRose else MysticTeal
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(46.dp)
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isRunning) "Stop Metronome" else "Start Metronome",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Classical Bahr Selector List
                Text("Select Classical Ghazal Bahr (बह्र चुनाव):", color = AntiqueGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bahrPresets.forEach { bahr ->
                        val isSelected = selectedBahr.nameHindi == bahr.nameHindi
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E1528) else Color(0xFF15101F)
                            ),
                            border = BorderStroke(1.dp, if (isSelected) AntiqueGold else Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBahr = bahr }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(bahr.nameHindi, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(bahr.taalsName, color = MysticTeal, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(bahr.meterFormula, color = AntiqueGold, fontSize = 11.sp, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                                Text("Syllable Map: ${bahr.syllablesPattern}", color = Color.LightGray, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
