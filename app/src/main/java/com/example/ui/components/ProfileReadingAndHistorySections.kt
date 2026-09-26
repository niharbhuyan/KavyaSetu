package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReadingProgressState
import com.example.data.model.GeneratedPoemItem
import com.example.data.model.PoetryStyle
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.MysticTealSoft
import com.example.ui.theme.SoftGold
import com.example.ui.theme.VelvetRose
import com.example.util.SocialShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun GeneratedPoemItem.getStyleEnum(): PoetryStyle {
    return PoetryStyle.entries.firstOrNull { it.id.equals(this.styleId, ignoreCase = true) }
        ?: PoetryStyle.GHAZAL
}

@Composable
fun ReadingProgressSection(
    readingProgress: ReadingProgressState,
    onResumeReading: (String) -> Unit,
    onClearBookmark: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val progressFraction = if (readingProgress.dailyGoal > 0) {
        (readingProgress.versesReadToday.toFloat() / readingProgress.dailyGoal).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Reading Goal & Streak Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Daily Reading Goal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Nurture your daily poetic contemplation",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Streak Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VelvetRose.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = VelvetRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${readingProgress.readingStreakDays}d Streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VelvetRose
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${readingProgress.versesReadToday} of ${readingProgress.dailyGoal} couplets read today",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AntiqueGold
                    )
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    color = AntiqueGold,
                    trackColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Goal adjust chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Target:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf(3, 5, 10, 15).forEach { goal ->
                        FilterChip(
                            selected = readingProgress.dailyGoal == goal,
                            onClick = { onUpdateDailyGoal(goal) },
                            label = { Text("$goal / day", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                selectedLabelColor = AntiqueGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = AntiqueGold.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                // Stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ReadingStatItem(value = "${readingProgress.totalVersesRead}", label = "Total Read")
                    ReadingStatItem(value = "${readingProgress.versesReadToday}", label = "Today's Verses")
                    ReadingStatItem(
                        value = if (progressFraction >= 1f) "Achieved! 🏆" else "In Progress",
                        label = "Goal Status"
                    )
                }
            }
        }

        // Active Bookmark / Last Read Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (readingProgress.hasBookmark) AntiqueGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                1.dp,
                if (readingProgress.hasBookmark) AntiqueGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                                Text("📌", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Bookmark & Last Read",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = if (readingProgress.hasBookmark) "Pick up exactly where you left off" else "No active bookmark",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (readingProgress.hasBookmark) {
                        IconButton(
                            onClick = onClearBookmark,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Bookmark",
                                tint = AntiqueGold.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (readingProgress.hasBookmark) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "\"${readingProgress.lastReadSnippet}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "— ${readingProgress.lastReadAuthor ?: "Unknown"} • ${readingProgress.lastReadCategory?.replaceFirstChar { it.uppercase() } ?: "Poetry"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AntiqueGold
                                )
                                Button(
                                    onClick = {
                                        val poemId = readingProgress.lastReadPoemId
                                        if (!poemId.isNullOrBlank()) {
                                            onResumeReading(poemId)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AntiqueGold,
                                        contentColor = DeepMidnight
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Resume Reading", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 Tip: Tap 'Set Bookmark Here' on any couplet in the Offline Vault to mark your position and resume whenever you return.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GeneratedPoemHistorySection(
    history: List<GeneratedPoemItem>,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    onDeletePoem: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedStyleFilter by remember { mutableStateOf<PoetryStyle?>(null) }
    var showClearConfirmation by remember { mutableStateOf(false) }

    val filteredHistory = remember(history, selectedStyleFilter) {
        if (selectedStyleFilter == null) {
            history
        } else {
            history.filter { it.getStyleEnum() == selectedStyleFilter }
        }
    }

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear Generated Poem History?") },
            text = { Text("This will remove all ${history.size} verses from your AI composition history. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearConfirmation = false
                        Toast.makeText(context, "Poem history cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VelvetRose)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Quick Stats
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
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
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Gemini AI Composition History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "${history.size} poems composed across Ghazal, Haiku & Free Verse",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (history.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Clear History",
                                tint = VelvetRose.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Filter by style
                if (history.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Filter by Poetry Style:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStyleFilter == null,
                            onClick = { selectedStyleFilter = null },
                            label = { Text("All (${history.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                selectedLabelColor = AntiqueGold
                            )
                        )
                        PoetryStyle.entries.forEach { style ->
                            val count = history.count { it.getStyleEnum() == style }
                            if (count > 0) {
                                FilterChip(
                                    selected = selectedStyleFilter == style,
                                    onClick = { selectedStyleFilter = style },
                                    label = { Text("${style.emoji} ${style.displayName} ($count)", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                        selectedLabelColor = AntiqueGold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of Generated Poems
        if (filteredHistory.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (history.isEmpty()) "No poems generated yet." else "No poems matching this style.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Use the Gemini AI Studio composer to craft verses in Ghazal, Haiku, Free Verse, Rubai, or Doha styles.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onNavigateToAiStudio,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = DeepMidnight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compose in AI Studio", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            filteredHistory.forEach { poem ->
                val styleEnum = poem.getStyleEnum()
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().testTag("generated_poem_card_${poem.id}")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Style Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AntiqueGold.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "${styleEnum.emoji} ${styleEnum.displayName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                // Language Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = VelvetRose.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = poem.language.replaceFirstChar { it.uppercase() },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VelvetRose,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = try {
                                    dateFormatter.format(Date(poem.timestamp))
                                } catch (e: Exception) {
                                    ""
                                },
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (poem.topic.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Topic: \"${poem.topic}\"",
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = AntiqueGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Poem Content
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = poem.content,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pen Name: ${poem.penName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Action Buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { audioReciter.speak(poem.content, poem.language) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Recite",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Poem", poem.content))
                                        Toast.makeText(context, "Copied poem to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        SocialShareHelper.shareGeminiShayari(
                                            context = context,
                                            lines = poem.content,
                                            emotion = poem.emotion,
                                            language = poem.language,
                                            author = poem.penName,
                                            topic = poem.topic
                                        )
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeletePoem(poem.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = VelvetRose.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountPreferencesSection(
    dailyPushEnabled: Boolean,
    onToggleDailyPush: (Boolean) -> Unit,
    onTestPush: () -> Unit,
    fontSize: Float,
    lineHeightMult: Float,
    fontFamily: String,
    onUpdateTypography: (Float, Float, String) -> Unit,
    fcmToken: String?,
    onTriggerHourlySync: (() -> Unit)? = null,
    onOpenBetaTesting: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentFontSize by remember(fontSize) { mutableFloatStateOf(fontSize) }
    var currentLineHeight by remember(lineHeightMult) { mutableFloatStateOf(lineHeightMult) }
    var currentFontFamily by remember(fontFamily) { mutableStateOf(fontFamily) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Google Play Beta Testing & Early Access Card
        if (onOpenBetaTesting != null) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MysticTeal.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBetaTesting() }
                    .testTag("card_beta_testing_preferences")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MysticTeal.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = MysticTealSoft,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Google Play Beta Testing",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MysticTealSoft
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MysticTeal.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "INTERNAL TRACK",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MysticTealSoft,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Get early access to future builds & experimental Gemini AI features",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = onOpenBetaTesting,
                        colors = ButtonDefaults.buttonColors(containerColor = MysticTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Join Track", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Hourly Auto-Sync & Background Refresh Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Hourly Auto-Refresh & Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Active • Rotates picks & refreshes every 60 min",
                                fontSize = 12.sp,
                                color = Color(0xFF81C784)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Kavya Setu automatically recalculates trending poetry, rotates featured couplets, syncs community verses from Firestore, and updates your homescreen widget on an hourly schedule.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onTriggerHourlySync?.invoke()
                        Toast.makeText(context, "Executed immediate auto-sync & hourly refresh! ⚡", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold.copy(alpha = 0.25f),
                        contentColor = AntiqueGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("trigger_hourly_sync_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync & Refresh Now (Manual Trigger)", fontWeight = FontWeight.Bold)
                }
            }
        }
        // FCM Push Notification Preference Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Daily Morning Push Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Firebase Cloud Messaging (FCM)",
                                fontSize = 12.sp,
                                color = AntiqueGold
                            )
                        }
                    }

                    Switch(
                        checked = dailyPushEnabled,
                        onCheckedChange = onToggleDailyPush,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepMidnight,
                            checkedTrackColor = AntiqueGold,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.testTag("fcm_daily_push_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Receive your hand-curated 'Daily Pick' poetry verse every morning at 8:00 AM directly in your Android notification shade in Hindi, Odia, and English.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Test Notification Button
                Button(
                    onClick = {
                        onTestPush()
                        Toast.makeText(context, "Dispatched Daily Pick push notification! 🌅", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = DeepMidnight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("test_fcm_push_button")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test 'Daily Pick' Notification Now", fontWeight = FontWeight.Bold)
                }

                if (!fcmToken.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "FCM Registration active • Ready for scheduled pushes",
                            fontSize = 11.sp,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }
        }

        // Poetry Reading & Typography Preferences Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = AntiqueGold
                    )
                    Text(
                        text = "Poetry Typography & Readability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Font Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Font Size", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("${currentFontSize.toInt()} sp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                }
                Slider(
                    value = currentFontSize,
                    onValueChange = {
                        currentFontSize = it
                        onUpdateTypography(currentFontSize, currentLineHeight, currentFontFamily)
                    },
                    valueRange = 14f..28f,
                    steps = 6,
                    colors = SliderDefaults.colors(thumbColor = AntiqueGold, activeTrackColor = AntiqueGold)
                )

                // Line Height Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Line Spacing", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(String.format(Locale.US, "%.1fx", currentLineHeight), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                }
                Slider(
                    value = currentLineHeight,
                    onValueChange = {
                        currentLineHeight = it
                        onUpdateTypography(currentFontSize, currentLineHeight, currentFontFamily)
                    },
                    valueRange = 1.2f..2.2f,
                    steps = 4,
                    colors = SliderDefaults.colors(thumbColor = AntiqueGold, activeTrackColor = AntiqueGold)
                )

                // Font Family Chips
                Spacer(modifier = Modifier.height(6.dp))
                Text("Font Family:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "serif" to "Classic Serif",
                        "sans" to "Modern Sans",
                        "monospace" to "Monospace"
                    ).forEach { (fam, label) ->
                        FilterChip(
                            selected = currentFontFamily == fam,
                            onClick = {
                                currentFontFamily = fam
                                onUpdateTypography(currentFontSize, currentLineHeight, currentFontFamily)
                            },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                selectedLabelColor = AntiqueGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Live Typography Preview:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nବହୁତ ନିକଳେ ମେରେ ଅରମାନ ଲେକିନ ଫିର ଭି କମ ନିକଳେ...",
                            fontSize = currentFontSize.sp,
                            lineHeight = (currentFontSize * currentLineHeight).sp,
                            fontFamily = when (currentFontFamily) {
                                "sans" -> FontFamily.SansSerif
                                "monospace" -> FontFamily.Monospace
                                else -> FontFamily.Serif
                            },
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
