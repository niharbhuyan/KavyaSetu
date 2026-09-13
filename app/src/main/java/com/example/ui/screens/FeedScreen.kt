package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.ShayariCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.RoyalPlum
import com.example.ui.theme.VelvetRose

@Composable
fun FeedScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shayaris by viewModel.displayedShayaris.collectAsStateWithLifecycle()
    val dailyPick by viewModel.dailyPick.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()

    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }

    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsStateWithLifecycle()

    if (cardStudioShayari != null) {
        CardStudioDialog(
            shayari = cardStudioShayari!!,
            onDismiss = { cardStudioShayari = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Offline Active Alert
        if (isOfflineSimulated) {
            item(key = "offline_sim_banner") {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VelvetRose.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalWifiOff,
                            contentDescription = null,
                            tint = VelvetRose,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Offline Mode Active (Simulated)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VelvetRose
                            )
                            Text(
                                text = "Browsing downloaded couplets stored in local Room database.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(
                            onClick = { viewModel.toggleOfflineSimulation() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Go Online", fontSize = 12.sp, color = AntiqueGold)
                        }
                    }
                }
            }
        }

        // Shayari of the Day Feature Card
        item(key = "daily_morning_card") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_morning_banner"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF2E0F26),
                                    Color(0xFF1F143D),
                                    Color(0xFF0F0B1E)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
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
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.WbSunny,
                                            contentDescription = null,
                                            tint = AntiqueGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Shayari of the Day",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        color = AntiqueGold
                                    )
                                    Text(
                                        text = "Curated Daily Verse • Homescreen Ready",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        viewModel.triggerTestNotification(context)
                                        Toast.makeText(context, "Morning Notification sent!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp).testTag("trigger_notification_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Test Morning Notification",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                IconButton(
                                    onClick = {
                                        viewModel.refreshDailyPickWithWidget(context)
                                        Toast.makeText(context, "Refreshed daily pick and updated homescreen widget!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp).testTag("refresh_daily_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Daily Pick",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val pick = dailyPick ?: shayaris.firstOrNull()
                        if (pick != null) {
                            Text(
                                text = pick.lines,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    lineHeight = 26.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "— ${pick.author} (${pick.language.replaceFirstChar { it.uppercase() }})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AntiqueGold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Daily 6:00 AM Sync",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalButton(
                                    onClick = { viewModel.openShayariDetail(pick) },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1.1f).testTag("daily_details_button")
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Details", fontSize = 11.sp)
                                }

                                FilledTonalButton(
                                    onClick = { audioReciter.speak(pick.lines, pick.language) },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Recite", fontSize = 11.sp)
                                }

                                FilledTonalButton(
                                    onClick = { cardStudioShayari = pick },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Card", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.pinHomescreenWidget(context) { pinned ->
                                            if (pinned) {
                                                Toast.makeText(context, "Homescreen widget pinned successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Widget ready! Long-press your home screen to add the Shayari Widget.", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1.3f).testTag("pin_widget_button")
                                ) {
                                    Icon(Icons.Default.Widgets, contentDescription = null, tint = Color(0xFF140D24), modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Widget", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF140D24))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Filter Tabs (All • हिंदी • ଓଡ଼ିଆ • English)
        item(key = "language_filter_row") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Explore Languages",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Language.entries.forEach { lang ->
                        val isSelected = selectedLanguage == lang
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.onLanguageSelected(lang) }
                                .testTag("lang_chip_${lang.code}"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) AntiqueGold else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AntiqueGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = lang.scriptSample,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = lang.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) DeepMidnight else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Emotion Filter Chips
        item(key = "emotion_filter_row") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All Emotions" chip
                FilterChip(
                    selected = selectedEmotion == null,
                    onClick = { viewModel.onEmotionSelected(null) },
                    label = { Text("✨ All Emotions") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VelvetRose.copy(alpha = 0.2f),
                        selectedLabelColor = VelvetRose
                    )
                )

                Emotion.entries.forEach { emotion ->
                    val isSelected = selectedEmotion == emotion
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onEmotionSelected(if (isSelected) null else emotion) },
                        label = { Text(emotion.getDisplayName(selectedLanguage)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VelvetRose.copy(alpha = 0.2f),
                            selectedLabelColor = VelvetRose
                        )
                    )
                }
            }
        }

        // Shayaris List
        items(
            items = shayaris,
            key = { it.id }
        ) { shayari ->
            ShayariCard(
                shayari = shayari,
                onLikeClick = { viewModel.toggleLike(shayari) },
                onSaveClick = { viewModel.toggleSave(shayari) },
                onDownloadClick = { viewModel.toggleDownload(shayari) },
                onCardClick = { viewModel.openShayariDetail(shayari) },
                onReciteClick = { lines, lang ->
                    audioReciter.speak(lines, lang)
                },
                onOpenCardStudio = { cardStudioShayari = it },
                onAnalyzeWithGemini = {
                    viewModel.analyzeWithHighThinking(it.lines, it.language)
                    onNavigateToAiStudio()
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
