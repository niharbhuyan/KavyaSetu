package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.ContextCompat
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MushairaStudioDialog(
    shayari: Shayari,
    ambientPlayer: AmbientSoundscapePlayer,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val audioFile = remember(shayari.id) {
        File(context.filesDir, "mushaira_${shayari.id}.m4a")
    }

    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordingDurationSec by remember { mutableIntStateOf(0) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var hasExistingRecording by remember { mutableStateOf(audioFile.exists()) }

    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var selectedAmbient by remember { mutableStateOf(AmbientSoundPreset.OFF) }

    // Request Audio Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Microphone access granted. Tap Record to start.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Microphone permission is required to record recital.", Toast.LENGTH_LONG).show()
        }
    }

    // Timer coroutine for recording duration
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDurationSec = 0
            while (isRecording) {
                delay(1000)
                recordingDurationSec++
            }
        }
    }

    // Cleanup resources on dialog dismiss
    DisposableEffect(Unit) {
        onDispose {
            try {
                if (isRecording) {
                    mediaRecorder?.stop()
                    mediaRecorder?.release()
                }
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (ignored: Exception) { }
            ambientPlayer.stop()
        }
    }

    fun startRecording() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        try {
            if (audioFile.exists()) {
                audioFile.delete()
            }

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            isRecording = true

            // Optionally play gentle ambient drone while recording
            if (selectedAmbient != AmbientSoundPreset.OFF) {
                ambientPlayer.play(selectedAmbient)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to start recording: ${e.message}", Toast.LENGTH_LONG).show()
            isRecording = false
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            hasExistingRecording = audioFile.exists()
            Toast.makeText(context, "Recital captured in high fidelity!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving recording: ${e.message}", Toast.LENGTH_SHORT).show()
            isRecording = false
        }
    }

    fun startPlayback() {
        if (!audioFile.exists()) return
        try {
            mediaPlayer?.release()
            val player = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()
                setOnCompletionListener {
                    isPlaying = false
                    playbackProgress = 0f
                }
                start()
            }
            mediaPlayer = player
            isPlaying = true

            if (selectedAmbient != AmbientSoundPreset.OFF) {
                ambientPlayer.play(selectedAmbient)
            }

            // Track playback progress
            scope.launch {
                while (isPlaying && player.isPlaying) {
                    val current = player.currentPosition
                    val total = player.duration.coerceAtLeast(1)
                    playbackProgress = current.toFloat() / total.toFloat()
                    delay(100)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot play recording: ${e.message}", Toast.LENGTH_SHORT).show()
            isPlaying = false
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.pause()
            isPlaying = false
        } catch (ignored: Exception) { }
    }

    fun deleteRecording() {
        stopPlayback()
        if (audioFile.exists()) {
            audioFile.delete()
        }
        hasExistingRecording = false
        recordingDurationSec = 0
        playbackProgress = 0f
        Toast.makeText(context, "Recital deleted.", Toast.LENGTH_SHORT).show()
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        Text(text = "🎙️", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "Mushaira Recital Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "Record your voice recital with acoustic soundscapes",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Verse to Recite
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = shayari.lines,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "— ${shayari.author} ${if (shayari.penName.isNotBlank()) "'${shayari.penName}'" else ""}",
                            fontSize = 13.sp,
                            color = AntiqueGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Ambient Soundscape Selector
                Text(
                    text = "Acoustic Background Soundscape",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AntiqueGold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AmbientSoundPreset.entries.forEach { preset ->
                        val isSelected = selectedAmbient == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedAmbient = preset
                                if (isPlaying || isRecording) {
                                    ambientPlayer.play(preset)
                                }
                            },
                            label = { Text("${preset.emoji} ${preset.title}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                selectedLabelColor = AntiqueGold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsating Recording Indicator / Waveform
                val transition = rememberInfiniteTransition(label = "rec_pulse")
                val pulseScale by transition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isRecording) 1.25f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isRecording) VelvetRose else AntiqueGold.copy(alpha = 0.15f)
                        )
                        .clickable {
                            if (isRecording) {
                                stopRecording()
                            } else {
                                startRecording()
                            }
                        }
                        .testTag("mushaira_record_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                        tint = if (isRecording) Color.White else AntiqueGold,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isRecording) {
                        val minutes = recordingDurationSec / 60
                        val seconds = recordingDurationSec % 60
                        "🔴 Recording... %02d:%02d".format(minutes, seconds)
                    } else if (hasExistingRecording) {
                        "✨ Recital Recorded & Saved"
                    } else {
                        "Tap microphone to record recital"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isRecording) VelvetRose else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Playback controls if recording exists
                AnimatedVisibility(visible = hasExistingRecording) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Recorded Recital",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = AntiqueGold
                                )
                                IconButton(onClick = { deleteRecording() }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Recording",
                                        tint = VelvetRose
                                    )
                                }
                            }

                            Slider(
                                value = playbackProgress,
                                onValueChange = { /* read only visual progress */ },
                                colors = SliderDefaults.colors(
                                    thumbColor = VelvetRose,
                                    activeTrackColor = VelvetRose
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (isPlaying) stopPlayback() else startPlayback()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isPlaying) "Pause" else "Play Recital")
                                }

                                OutlinedButton(
                                    onClick = {
                                        deleteRecording()
                                        startRecording()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Re-record")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
