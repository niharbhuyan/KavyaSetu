package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.PoeticStreakManager
import com.example.notification.StreakReminderScheduler
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

@Composable
fun DailyPoeticStreakDialog(
    onDismiss: () -> Unit,
    onOpenDailyPick: () -> Unit,
    onNavigateToComposer: () -> Unit
) {
    val context = LocalContext.current
    val streakState by PoeticStreakManager.streakState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var isReminderOn by remember { mutableStateOf(PoeticStreakManager.isReminderEnabled(context)) }
    val (savedHour, savedMinute) = remember { PoeticStreakManager.getReminderTime(context) }
    var selectedTiming by remember { mutableStateOf(if (savedHour < 12) "morning" else "evening") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("daily_poetic_streak_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DeepMidnight,
            border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.6f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Daily Poetic Streak",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "रोज़ाना रियाज़ • Daily Couplet Reading",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_streak_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Flame & Streak Badge Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("streak_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF3B1528),
                                        Color(0xFF1E1128),
                                        Color(0xFF130D20)
                                    )
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Flame Circle with Streak Number
                            Surface(
                                shape = CircleShape,
                                color = AntiqueGold.copy(alpha = 0.25f),
                                border = BorderStroke(2.dp, AntiqueGold),
                                modifier = Modifier
                                    .size(90.dp)
                                    .testTag("streak_flame_badge")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "🔥",
                                            fontSize = 28.sp
                                        )
                                        Text(
                                            text = "${streakState.currentStreak}",
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AntiqueGold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${streakState.currentStreak} Days Streak",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Title & Subtitle Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AntiqueGold.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "${streakState.streakTitle} • ${streakState.streakSubtitle}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AntiqueGold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "\"${streakState.motivationalQuote}\"",
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Today's Status Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (streakState.isCompletedToday) Color(0xFF143825) else Color(0xFF3A2810),
                    border = BorderStroke(
                        1.dp,
                        if (streakState.isCompletedToday) Color(0xFF2EBD6E) else AntiqueGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("streak_today_status_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (streakState.isCompletedToday) Color(0xFF2EBD6E).copy(alpha = 0.2f) else AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (streakState.isCompletedToday) Icons.Default.Check else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (streakState.isCompletedToday) Color(0xFF2EBD6E) else AntiqueGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (streakState.isCompletedToday) {
                                    "✨ Today's Poetic Goal Completed!"
                                } else {
                                    "⏳ Today's Streak Pending"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (streakState.isCompletedToday) Color(0xFF2EBD6E) else AntiqueGold
                            )
                            Text(
                                text = if (streakState.isCompletedToday) {
                                    "You've read or composed today. Your streak is protected!"
                                } else {
                                    "Read today's Daily Pick or compose a couplet to extend your streak."
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 7-Day Weekly History Tracker
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Last 7 Days Journey",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        streakState.weeklyHistory.forEach { day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = when {
                                        day.isCompleted -> AntiqueGold
                                        day.isToday -> AntiqueGold.copy(alpha = 0.2f)
                                        else -> Color(0xFF252033)
                                    },
                                    border = BorderStroke(
                                        width = if (day.isToday) 2.dp else 1.dp,
                                        color = if (day.isToday) AntiqueGold else Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (day.isCompleted) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = DeepMidnight,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (day.isToday) {
                                            Text("🔥", fontSize = 16.sp)
                                        } else {
                                            Text(
                                                text = "•",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = day.dayLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (day.isToday) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Today's Counters & Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E1730),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.2f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📖 Read Today", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${streakState.todayReadCount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E1730),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.2f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("✍️ Composed Today", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${streakState.todayComposedCount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E1730),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.2f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏆 Best Streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${streakState.longestStreak}d",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Milestone Progression
                val nextM = streakState.nextMilestone
                if (nextM != null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1B1428),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Next Poetic Milestone",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${nextM.badgeEmoji} ${nextM.title} (${nextM.daysRequired}d)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val progress = (streakState.currentStreak.toFloat() / nextM.daysRequired).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = AntiqueGold,
                                trackColor = Color(0xFF332A44)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val daysLeft = nextM.daysRequired - streakState.currentStreak
                            Text(
                                text = "$daysLeft more active day${if (daysLeft > 1) "s" else ""} to unlock this title!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // WorkManager Daily Reminder Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF18152B),
                    border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workmanager_reminder_settings_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = VelvetRose,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Daily WorkManager Reminders",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Automated daily prompt to preserve streak & read Daily Pick",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isReminderOn,
                                onCheckedChange = { enabled ->
                                    isReminderOn = enabled
                                    val (h, m) = if (selectedTiming == "morning") 8 to 0 else 19 to 30
                                    StreakReminderScheduler.updateSettings(context, enabled, h, m)
                                    Toast.makeText(
                                        context,
                                        if (enabled) "WorkManager daily reminder scheduled" else "Reminders disabled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AntiqueGold,
                                    checkedTrackColor = VelvetRose.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.testTag("workmanager_reminder_switch")
                            )
                        }

                        if (isReminderOn) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedTiming == "morning",
                                    onClick = {
                                        selectedTiming = "morning"
                                        StreakReminderScheduler.scheduleDailyReminder(context, 8, 0)
                                        Toast.makeText(context, "Reminder set for 8:00 AM daily", Toast.LENGTH_SHORT).show()
                                    },
                                    label = { Text("🌅 Morning (8:00 AM)", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AntiqueGold.copy(alpha = 0.2f),
                                        selectedLabelColor = AntiqueGold
                                    )
                                )

                                FilterChip(
                                    selected = selectedTiming == "evening",
                                    onClick = {
                                        selectedTiming = "evening"
                                        StreakReminderScheduler.scheduleDailyReminder(context, 19, 30)
                                        Toast.makeText(context, "Reminder set for 7:30 PM daily", Toast.LENGTH_SHORT).show()
                                    },
                                    label = { Text("🌙 Evening (7:30 PM)", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AntiqueGold.copy(alpha = 0.2f),
                                        selectedLabelColor = AntiqueGold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    StreakReminderScheduler.triggerInstantTestReminder(context)
                                    Toast.makeText(context, "🔔 WorkManager reminder enqueued! Check your notification tray.", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("test_streak_workmanager_button"),
                                border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = VelvetRose,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Trigger Instant WorkManager Test Reminder", fontSize = 12.sp, color = VelvetRose)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenDailyPick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("streak_read_daily_pick_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = DeepMidnight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Read Daily Pick", color = DeepMidnight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onNavigateToComposer()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("streak_compose_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compose Verse", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, PoeticStreakManager.getStreakShareText())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Daily Poetic Streak"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("streak_share_button"),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Streak Achievement 🔗", color = AntiqueGold, fontSize = 12.sp)
                }
            }
        }
    }
}
