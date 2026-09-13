package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.VelvetRose

@Composable
fun OfflineScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val downloadedShayaris by viewModel.downloadedShayaris.collectAsStateWithLifecycle()
    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsStateWithLifecycle()
    var selectedLanguage by remember { mutableStateOf(Language.ALL) }
    var selectedEmotion by remember { mutableStateOf<Emotion?>(null) }
    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredList = downloadedShayaris.filter { item ->
        val matchLang = when (selectedLanguage) {
            Language.ALL -> true
            else -> item.language.equals(selectedLanguage.code, ignoreCase = true)
        }
        val matchEmotion = selectedEmotion == null || item.emotion.equals(selectedEmotion?.code, ignoreCase = true)
        matchLang && matchEmotion
    }

    if (cardStudioShayari != null) {
        CardStudioDialog(
            shayari = cardStudioShayari!!,
            onDismiss = { cardStudioShayari = null }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Offline Storage?") },
            text = {
                Text(
                    "This will remove ${downloadedShayaris.size} couplets from your local offline cache. They will still remain accessible in the online feed.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearDownloadedStorage()
                        Toast.makeText(context, "Offline cache cleared", Toast.LENGTH_SHORT).show()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VelvetRose)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("offline_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Storage Efficiency & Offline Simulator Hero
        item(key = "offline_hero") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF14202B),
                                    Color(0xFF121B32),
                                    Color(0xFF0F0F23)
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
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = AntiqueGold.copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FolderSpecial,
                                            contentDescription = null,
                                            tint = AntiqueGold,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Offline Poetry Vault",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${downloadedShayaris.size} couplets • ~${(downloadedShayaris.size * 2.2).toInt()} KB in SQLite",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            if (downloadedShayaris.isNotEmpty()) {
                                IconButton(
                                    onClick = { showClearDialog = true },
                                    modifier = Modifier.size(36.dp).testTag("clear_offline_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Clear cache",
                                        tint = VelvetRose,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Offline Mode Simulator Toggle Box
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                            border = BorderStroke(
                                1.dp,
                                if (isOfflineSimulated) VelvetRose.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isOfflineSimulated) Icons.Default.SignalWifiOff else Icons.Default.Wifi,
                                        contentDescription = null,
                                        tint = if (isOfflineSimulated) VelvetRose else Color(0xFF2ECC71),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isOfflineSimulated) "Offline Mode Active (Simulated)" else "Online Mode (Full Network)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isOfflineSimulated) "Restricted to local Room database" else "Toggle to test offline access behavior",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Switch(
                                    checked = isOfflineSimulated,
                                    onCheckedChange = { viewModel.toggleOfflineSimulation() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AntiqueGold,
                                        checkedTrackColor = AntiqueGold.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("offline_mode_toggle")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Download Packs Center
        item(key = "offline_packs") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Download Curated Content Packs",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OfflinePackCard(
                        title = "Favorite Verses",
                        subtitle = "All your bookmarked couplets",
                        icon = "⭐",
                        onClick = {
                            viewModel.downloadAllFavorites()
                            Toast.makeText(context, "Downloading all saved favorites...", Toast.LENGTH_SHORT).show()
                        }
                    )
                    OfflinePackCard(
                        title = "Hindi Classics",
                        subtitle = "Ghalib, Indori & Mir",
                        icon = "🇮🇳",
                        onClick = {
                            viewModel.downloadCuratedPack("hindi")
                            Toast.makeText(context, "Downloaded Hindi Classics Pack", Toast.LENGTH_SHORT).show()
                        }
                    )
                    OfflinePackCard(
                        title = "Odia Kabyadhara",
                        subtitle = "Bhakti, Prem & Gitika",
                        icon = "🌺",
                        onClick = {
                            viewModel.downloadCuratedPack("odia")
                            Toast.makeText(context, "Downloaded Odia Kabyadhara Pack", Toast.LENGTH_SHORT).show()
                        }
                    )
                    OfflinePackCard(
                        title = "Love & Ishq",
                        subtitle = "Passionate romantic ghazals",
                        icon = "❤️",
                        onClick = {
                            viewModel.downloadCuratedPack("romance")
                            Toast.makeText(context, "Downloaded Ishq Pack", Toast.LENGTH_SHORT).show()
                        }
                    )
                    OfflinePackCard(
                        title = "Complete Archive",
                        subtitle = "All curated verses in database",
                        icon = "📚",
                        onClick = {
                            viewModel.downloadCuratedPack("all")
                            Toast.makeText(context, "Downloaded Full Archive", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Filter by language inside offline storage
        item(key = "offline_filters") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Language.entries.forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLanguage = lang },
                        label = { Text("${lang.scriptSample} ${lang.displayName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                            selectedLabelColor = AntiqueGold
                        )
                    )
                }
            }
        }

        // Downloaded Shayaris List
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (downloadedShayaris.isEmpty()) "No verses downloaded yet" else "No downloaded verses match this filter",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Download your favorite shayaris or tap a pack above to enjoy poetry without internet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.downloadCuratedPack("all")
                                Toast.makeText(context, "Downloaded starter poetry pack!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Download All Curated Verses", color = DeepMidnight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { shayari ->
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
        }
    }
}

@Composable
private fun OfflinePackCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Download",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AntiqueGold
                )
            }
        }
    }
}
