package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.RaatMehfilManager
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

@Composable
fun RaatMehfilDialog(
    ambientPlayer: AmbientSoundscapePlayer,
    onDismiss: () -> Unit
) {
    val themeConfig by RaatMehfilManager.themeConfig.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DeepMidnight,
            border = BorderStroke(1.2.dp, Color(0xFFE8C88B).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                        Text("🕯️", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "Raat Ki Mehfil • रात की महफ़िल",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE8C88B)
                            )
                            Text(
                                text = "Nocturnal sanctuary with warm amber candle glow & night drone",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Status Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B152A)),
                    border = BorderStroke(1.dp, Color(0xFFE8C88B).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (themeConfig.midnightHourActive || themeConfig.isNightModeForced) Color(0xFF3B2E10) else Color(0xFF141F30)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (themeConfig.midnightHourActive || themeConfig.isNightModeForced) "🌙" else "☀️",
                                fontSize = 20.sp
                            )
                        }

                        Column {
                            Text(
                                text = if (themeConfig.midnightHourActive) {
                                    "Midnight Hour Active (10 PM - 5 AM)"
                                } else if (themeConfig.isNightModeForced) {
                                    "Nocturnal Mode Forced Always ON"
                                } else {
                                    "Daytime / Regular Lighting"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFE8C88B)
                            )
                            Text(
                                text = "Auto-updated every hour by Kavya Setu background engine.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Setting 1: Force Night Owl Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221C34)),
                    border = BorderStroke(0.6.dp, Color.Gray.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Force Night Owl Theme",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Keep deep midnight palette active 24/7 regardless of current hour",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = themeConfig.isNightModeForced,
                            onCheckedChange = { RaatMehfilManager.toggleForcedNightMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFE8C88B),
                                checkedTrackColor = Color(0xFF3B2E10)
                            ),
                            modifier = Modifier.testTag("raat_force_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Setting 2: Amber Candle Glow
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221C34)),
                    border = BorderStroke(0.6.dp, Color.Gray.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Warm Amber Candle Glow (शमा की रोशनी)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Soft yellow-amber tint for eye comfort during late night recitation",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = themeConfig.amberCandleGlow,
                            onCheckedChange = { RaatMehfilManager.toggleAmberGlow(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFE8C88B),
                                checkedTrackColor = Color(0xFF3B2E10)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Setting 3: Crickets / Tanpura Ambient Sound
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221C34)),
                    border = BorderStroke(0.6.dp, Color.Gray.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Night Crickets & Acoustic Drone",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Subtle background soundscape for immersive late night composition",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = themeConfig.cricketsAmbientEnabled,
                            onCheckedChange = { enabled ->
                                RaatMehfilManager.toggleCricketsAmbient(enabled)
                                if (enabled) {
                                    ambientPlayer.play(AmbientSoundPreset.NIGHT_CRICKETS)
                                } else {
                                    ambientPlayer.stop()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFE8C88B),
                                checkedTrackColor = Color(0xFF3B2E10)
                            )
                        )
                    }
                }
            }
        }
    }
}
