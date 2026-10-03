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
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.ads.AdMobBanner
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import com.example.util.SocialShareHelper
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
import com.example.ui.components.AmbientSoundscapePlayer
import com.example.ui.components.CalligraphyStudioDialog
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.CategorizePoemDialog
import com.example.ui.components.DiwanPublisherDialog
import com.example.ui.components.KalamEUstaadDialog
import com.example.ui.components.LafzOMaaniDialog
import com.example.ui.components.MushairaStudioDialog
import com.example.ui.components.ShayariCard
import com.example.ui.components.TarannumModeDialog
import com.example.ui.components.VirtualMehfilDialog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.RoyalPlum
import com.example.ui.theme.VelvetRose
import com.example.ui.theme.MysticTeal

@Composable
fun FeedScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    onNavigateToSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shayaris by viewModel.displayedShayaris.collectAsStateWithLifecycle()
    val dailyPick by viewModel.dailyPick.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()
    val poetryFontSizeSp by viewModel.poetryFontSizeSp.collectAsStateWithLifecycle()
    val poetryLineHeightMult by viewModel.poetryLineHeightMult.collectAsStateWithLifecycle()
    val poetryFontFamilyType by viewModel.poetryFontFamilyType.collectAsStateWithLifecycle()

    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }
    var mushairaStudioShayari by remember { mutableStateOf<Shayari?>(null) }
    var categorizeShayari by remember { mutableStateOf<Shayari?>(null) }
    val ambientPlayer = remember { AmbientSoundscapePlayer() }

    var showVirtualMehfil by remember { mutableStateOf(false) }
    var showKalamEUstaad by remember { mutableStateOf(false) }
    var tarannumShayari by remember { mutableStateOf<Shayari?>(null) }
    var showCalligraphy by remember { mutableStateOf(false) }
    var showLafzOMaani by remember { mutableStateOf(false) }
    var showDiwanPublisher by remember { mutableStateOf(false) }
    var showTarhiMushaira by remember { mutableStateOf(false) }
    var showTakhallusStudio by remember { mutableStateOf(false) }
    var showRiyazJournal by remember { mutableStateOf(false) }
    var showRaatMehfil by remember { mutableStateOf(false) }
    var showDastaangoi by remember { mutableStateOf(false) }
    var showStoryReelStudio by remember { mutableStateOf(false) }
    var showRiazMetronome by remember { mutableStateOf(false) }
    var showDailyStreakDialog by remember { mutableStateOf(false) }
    val vmShowDailyStreak by viewModel.showDailyStreakDialog.collectAsStateWithLifecycle()
    val updateInfo by viewModel.appUpdateInfo.collectAsStateWithLifecycle()

    if (showDailyStreakDialog || vmShowDailyStreak) {
        com.example.ui.components.DailyPoeticStreakDialog(
            onDismiss = {
                showDailyStreakDialog = false
                viewModel.closeDailyStreakDialog()
            },
            onOpenDailyPick = {
                val pick = dailyPick ?: shayaris.firstOrNull()
                if (pick != null) {
                    viewModel.openShayariDetail(pick)
                }
            },
            onNavigateToComposer = {
                onNavigateToAiStudio()
            }
        )
    }

    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsStateWithLifecycle()

    if (categorizeShayari != null) {
        CategorizePoemDialog(
            shayari = categorizeShayari!!,
            onDismiss = { categorizeShayari = null },
            onSaveCategory = { newCategory, newTags ->
                viewModel.updatePoemCategoryAndTags(categorizeShayari!!.id, newCategory, newTags)
                Toast.makeText(context, "Updated to ${newCategory.displayName}", Toast.LENGTH_SHORT).show()
                categorizeShayari = null
            }
        )
    }

    if (showVirtualMehfil) {
        VirtualMehfilDialog(
            allShayaris = shayaris,
            audioReciter = audioReciter,
            onDismiss = { showVirtualMehfil = false }
        )
    }

    if (showKalamEUstaad) {
        val initialText = dailyPick?.lines ?: shayaris.firstOrNull()?.lines ?: ""
        KalamEUstaadDialog(
            initialDraft = initialText,
            audioReciter = audioReciter,
            onDismiss = { showKalamEUstaad = false }
        )
    }

    if (tarannumShayari != null) {
        TarannumModeDialog(
            shayari = tarannumShayari!!,
            audioReciter = audioReciter,
            onDismiss = { tarannumShayari = null }
        )
    }

    if (showCalligraphy) {
        val initialVerse = dailyPick?.lines ?: shayaris.firstOrNull()?.lines ?: ""
        CalligraphyStudioDialog(
            initialVerse = initialVerse,
            onDismiss = { showCalligraphy = false }
        )
    }

    if (showLafzOMaani) {
        val initialVerse = dailyPick?.lines ?: shayaris.firstOrNull()?.lines ?: ""
        LafzOMaaniDialog(
            initialCoupletToScan = initialVerse,
            audioReciter = audioReciter,
            onDismiss = { showLafzOMaani = false }
        )
    }

    if (showStoryReelStudio) {
        val sampleSher = dailyPick?.lines ?: shayaris.firstOrNull()?.lines ?: "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले।\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले॥"
        val samplePoet = dailyPick?.poet ?: shayaris.firstOrNull()?.poet ?: "मिर्ज़ा असदुल्लाह ख़ान 'ग़ालिब'"
        com.example.ui.components.VideoStoryStudioDialog(
            initialPoemText = sampleSher,
            initialPoetName = samplePoet,
            audioReciter = audioReciter,
            onDismiss = { showStoryReelStudio = false }
        )
    }

    if (showRiazMetronome) {
        com.example.ui.components.PoeticRiazMetronomeDialog(
            onDismiss = { showRiazMetronome = false }
        )
    }

    if (showDiwanPublisher) {
        DiwanPublisherDialog(
            allShayaris = shayaris,
            onDismiss = { showDiwanPublisher = false }
        )
    }

    if (cardStudioShayari != null) {
        CardStudioDialog(
            shayari = cardStudioShayari!!,
            onDismiss = { cardStudioShayari = null }
        )
    }

    if (mushairaStudioShayari != null) {
        MushairaStudioDialog(
            shayari = mushairaStudioShayari!!,
            ambientPlayer = ambientPlayer,
            onDismiss = { mushairaStudioShayari = null }
        )
    }

    if (showTarhiMushaira) {
        com.example.ui.components.TarhiMushairaDialog(
            audioReciter = audioReciter,
            onDismiss = { showTarhiMushaira = false }
        )
    }

    if (showTakhallusStudio) {
        com.example.ui.components.TakhallusStudioDialog(
            onDismiss = { showTakhallusStudio = false }
        )
    }

    if (showRiyazJournal) {
        com.example.ui.components.RiyazJournalDialog(
            onDismiss = { showRiyazJournal = false }
        )
    }

    if (showRaatMehfil) {
        com.example.ui.components.RaatMehfilDialog(
            ambientPlayer = ambientPlayer,
            onDismiss = { showRaatMehfil = false }
        )
    }

    if (showDastaangoi) {
        com.example.ui.components.DastaangoiMehfilDialog(
            allShayaris = shayaris,
            audioReciter = audioReciter,
            ambientPlayer = ambientPlayer,
            onDismiss = { showDastaangoi = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Universal Local Database Search Bar Trigger
        item(key = "feed_search_trigger_bar") {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSearch() }
                    .testTag("feed_search_trigger_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Local Database",
                        tint = AntiqueGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Search verses, poets (Ghalib, Faiz...), or language...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AntiqueGold.copy(alpha = 0.22f),
                        border = BorderStroke(0.5.dp, AntiqueGold.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "Room DB",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

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

        // Automated In-App Update Notice Banner
        if (updateInfo?.isUpdateAvailable == true) {
            val nonNullInfo = updateInfo!!
            item(key = "auto_update_banner") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.checkForAppUpdate(context, force = true) }
                        .testTag("feed_auto_update_banner"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF14241B),
                    border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.6f)),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SystemUpdate,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Update Available: ${nonNullInfo.latestVersionName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Auto-OTA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Tap to review What's New & auto-update seamlessly",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.checkForAppUpdate(context, force = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Update", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Daily Poetic Streak Engagement Banner
        item(key = "daily_poetic_streak_banner") {
            val streakState by viewModel.poeticStreakState.collectAsStateWithLifecycle()
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDailyStreakDialog = true }
                    .testTag("feed_daily_streak_banner"),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1B1124),
                border = BorderStroke(1.2.dp, AntiqueGold.copy(alpha = 0.5f)),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(1.5.dp, AntiqueGold),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🔥 ${streakState.currentStreak}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AntiqueGold
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${streakState.currentStreak}-Day Poetic Streak",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AntiqueGold.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = streakState.streakTitle,
                                        fontSize = 10.sp,
                                        color = AntiqueGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (streakState.isCompletedToday) "✨ Today's verse recorded! Streak secured." else "⏳ Read or compose today's verse to protect your streak.",
                                fontSize = 11.sp,
                                color = if (streakState.isCompletedToday) Color(0xFF4ADE80) else AntiqueGold.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VelvetRose.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "Streak ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VelvetRose,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
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
                                        text = "Daily Pick",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        color = AntiqueGold
                                    )
                                    Text(
                                        text = "Featured Poem of the Day • 24h Cycle",
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
                                Spacer(modifier = Modifier.width(2.dp))
                                IconButton(
                                    onClick = {
                                        val pick = dailyPick ?: shayaris.firstOrNull()
                                        if (pick != null) {
                                            SocialShareHelper.sharePoem(context, pick)
                                        }
                                    },
                                    modifier = Modifier.size(32.dp).testTag("share_daily_pick_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Daily Pick to Social Media",
                                        tint = AntiqueGold,
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
                                        text = "24h Cycle Sync",
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
                                    onClick = {
                                        SocialShareHelper.sharePoem(context, pick)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f).testTag("daily_share_button")
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", fontSize = 11.sp)
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
                                                Toast.makeText(context, "Widget ready! Long-press your home screen to add the Daily Pick Widget.", Toast.LENGTH_LONG).show()
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

        // Cultural Poetry & Classical Studio Suite
        item(key = "cultural_mushaira_hub") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Classical Poetry Studio & Arts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    Text(
                        text = "محفل و کلام",
                        fontSize = 12.sp,
                        color = VelvetRose
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Virtual Mehfil
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showVirtualMehfil = true }
                                .testTag("hub_virtual_mehfil_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1230)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🌙", fontSize = 22.sp)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Green.copy(alpha = 0.15f)
                                    ) {
                                        Text("LIVE", fontSize = 9.sp, color = Color(0xFF81C784), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Virtual Mehfil", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                Text("Midnight salon with live applause & shama", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 2. Kalam-e-Ustaad (Master AI Persona)
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showKalamEUstaad = true }
                                .testTag("hub_kalam_ustaad_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF231024)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🎭", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Kalam-e-Ustaad", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VelvetRose)
                                Text("Ghalib & Upendra Bhanja islah review", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 3. Tarannum Mode
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable {
                                    tarannumShayari = dailyPick ?: shayaris.firstOrNull()
                                }
                                .testTag("hub_tarannum_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141F30)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🪕", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Tarannum Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                Text("Melodic ghazal chanting with Raga scales", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 4. Qalam Calligraphy Studio
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showCalligraphy = true }
                                .testTag("hub_calligraphy_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF261D15)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFD4A373).copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📜", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Qalam Studio", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8C88B))
                                Text("Nastaliq & Odia palm-leaf reed brush", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 5. Lafz-o-Maani Etymology
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showLafzOMaani = true }
                                .testTag("hub_lafz_maani_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF152226)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🔍", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Lafz-o-Maani", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                Text("Arabic, Persian & Sanskrit roots lexicon", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 5B. 9:16 Video Story Reel Studio
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showStoryReelStudio = true }
                                .testTag("hub_story_reel_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF250D24)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🎴", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("9:16 Reel Studio", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VelvetRose)
                                Text("Acoustic scores & HD status video cards", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 5C. Poetic Riaz Metronome
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showRiazMetronome = true }
                                .testTag("hub_riaz_metronome_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MysticTeal.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🪕", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Ghazal Metronome", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MysticTeal)
                                Text("Classical syllable rhythm & bahr practice", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 6. Poet's Diwan Publisher
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showDiwanPublisher = true }
                                .testTag("hub_diwan_publisher_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF281122)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📖", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Diwan Publisher", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VelvetRose)
                                Text("Publish & export illustrated PDF e-book", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 7. Tarhi Mushaira (Daily Verse Challenge)
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showTarhiMushaira = true }
                                .testTag("hub_tarhi_mushaira_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1A2E)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🎯", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Tarhi Mushaira", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                Text("Daily classical hemistich challenge", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 8. Takhallus Studio (Digital Seal / Mohar)
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showTakhallusStudio = true }
                                .testTag("hub_takhallus_studio_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1F18)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE8C88B).copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🪶", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Takhallus Studio", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8C88B))
                                Text("Design personal digital poet seal", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 9. Personal Riyaz (Rhyme Finder & Drafts)
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showRiyazJournal = true }
                                .testTag("hub_riyaz_journal_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2421)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF57CC99).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("📓", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Personal Riyaz", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF57CC99))
                                Text("Qafiya dictionary & poetry drafts", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 10. Raat Ki Mehfil (Night Owl Mode)
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showRaatMehfil = true }
                                .testTag("hub_raat_mehfil_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFF6D688).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🕯️", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Raat Ki Mehfil", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF6D688))
                                Text("Amber candle glow & night sanctuary", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
                            }
                        }
                    }

                    // 11. Dastaangoi (Live Audio Storytelling Theatre)
                    item {
                        Card(
                            modifier = Modifier
                                .width(155.dp)
                                .clickable { showDastaangoi = true }
                                .testTag("hub_dastaangoi_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1322)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🎙️", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Dastaangoi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                                Text("Live oral recitation & historical tales", fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
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

        // Shayaris List with AdMob Banners
        itemsIndexed(
            items = shayaris,
            key = { _, item -> item.id }
        ) { index, shayari ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    onOpenMushairaStudio = { mushairaStudioShayari = it },
                    poetryFontSizeSp = poetryFontSizeSp,
                    poetryLineHeightMult = poetryLineHeightMult,
                    poetryFontFamilyType = poetryFontFamilyType,
                    onEditCategoryClick = { categorizeShayari = it },
                    onAnalyzeWithGemini = {
                        viewModel.analyzeWithHighThinking(it.lines, it.language)
                        onNavigateToAiStudio()
                    }
                )

                // Inline AdMob Banner every 4 cards
                if (index > 0 && (index + 1) % 4 == 0) {
                    AdMobBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
