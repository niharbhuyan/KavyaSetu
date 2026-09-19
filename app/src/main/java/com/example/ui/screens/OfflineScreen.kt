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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.PoemCategory
import com.example.data.model.Shayari
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.CategorizePoemDialog
import com.example.ui.components.PoetrySettingsDialog
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
    val savedShayaris by viewModel.savedShayaris.collectAsStateWithLifecycle()
    val downloadedShayaris by viewModel.downloadedShayaris.collectAsStateWithLifecycle()
    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsStateWithLifecycle()
    val poetryFontSizeSp by viewModel.poetryFontSizeSp.collectAsStateWithLifecycle()
    val poetryLineHeightMult by viewModel.poetryLineHeightMult.collectAsStateWithLifecycle()
    val poetryFontFamilyType by viewModel.poetryFontFamilyType.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()

    var vaultFilterTab by remember { mutableStateOf("saved") } // "saved" or "downloaded"
    var selectedCategory by remember { mutableStateOf(PoemCategory.ALL) }
    var selectedLanguage by remember { mutableStateOf(Language.ALL) }
    var tagSearchQuery by remember { mutableStateOf("") }
    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }
    var categorizeShayari by remember { mutableStateOf<Shayari?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val sourceList = if (vaultFilterTab == "saved") savedShayaris else downloadedShayaris

    val categoryCounts = remember(sourceList) {
        PoemCategory.entries.associateWith { cat ->
            if (cat == PoemCategory.ALL) {
                sourceList.size
            } else {
                sourceList.count { it.getCategoryEnum() == cat || it.category.equals(cat.id, ignoreCase = true) }
            }
        }
    }

    val filteredList = sourceList.filter { item ->
        val matchLang = when (selectedLanguage) {
            Language.ALL -> true
            else -> item.language.equals(selectedLanguage.code, ignoreCase = true)
        }
        val itemCat = item.getCategoryEnum()
        val matchCategory = when (selectedCategory) {
            PoemCategory.ALL -> true
            else -> itemCat == selectedCategory || item.category.equals(selectedCategory.id, ignoreCase = true)
        }
        val matchTag = if (tagSearchQuery.isBlank()) {
            true
        } else {
            val q = tagSearchQuery.trim().lowercase().removePrefix("#")
            item.tags.contains(q, ignoreCase = true) ||
            item.category.contains(q, ignoreCase = true) ||
            itemCat.displayName.contains(q, ignoreCase = true) ||
            item.lines.contains(q, ignoreCase = true) ||
            item.author.contains(q, ignoreCase = true)
        }
        matchLang && matchCategory && matchTag
    }

    if (categorizeShayari != null) {
        CategorizePoemDialog(
            shayari = categorizeShayari!!,
            onDismiss = { categorizeShayari = null },
            onSaveCategory = { newCategory, newTags ->
                viewModel.updatePoemCategoryAndTags(categorizeShayari!!.id, newCategory, newTags)
                Toast.makeText(context, "Updated to ${newCategory.displayName} with tags", Toast.LENGTH_SHORT).show()
                categorizeShayari = null
            }
        )
    }

    if (showSettingsDialog) {
        PoetrySettingsDialog(
            initialFontSizeSp = poetryFontSizeSp,
            initialLineHeightMult = poetryLineHeightMult,
            initialFontFamily = poetryFontFamilyType,
            onDismiss = { showSettingsDialog = false },
            onApplySettings = { newSize, newMult, newFont ->
                viewModel.updatePoetryDisplaySettings(context, newSize, newMult, newFont)
                Toast.makeText(context, "Readability settings updated!", Toast.LENGTH_SHORT).show()
            }
        )
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
                                        text = "${sourceList.size} verses • ~${(downloadedShayaris.size * 2.2).toInt()} KB in SQLite",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { showSettingsDialog = true },
                                    modifier = Modifier.size(36.dp).testTag("open_font_settings_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = "Font & Readability Settings",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(20.dp)
                                    )
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

        // Vault View Selection (Saved in Vault vs All Downloaded Cache)
        item(key = "vault_tabs") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = { vaultFilterTab = "saved" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (vaultFilterTab == "saved") AntiqueGold else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = if (vaultFilterTab == "saved") DeepMidnight else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Saved Vault (${savedShayaris.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vaultFilterTab == "saved") DeepMidnight else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    onClick = { vaultFilterTab = "downloaded" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (vaultFilterTab == "downloaded") AntiqueGold else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = null,
                            tint = if (vaultFilterTab == "downloaded") DeepMidnight else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Downloaded (${downloadedShayaris.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vaultFilterTab == "downloaded") DeepMidnight else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // LAST READ BOOKMARK / RESUME READING BANNER
        if (readingProgress.hasBookmark) {
            item(key = "last_read_bookmark_banner") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AntiqueGold.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("last_read_bookmark_banner")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("📌", fontSize = 16.sp)
                                Text(
                                    text = "Pick Up Where You Left Off",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                            }
                            IconButton(
                                onClick = { viewModel.clearBookmark(context) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Bookmark",
                                    tint = AntiqueGold.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"${readingProgress.lastReadSnippet}\"",
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "— ${readingProgress.lastReadAuthor ?: "Master"} • ${readingProgress.lastReadCategory?.replaceFirstChar { it.uppercase() } ?: "Verse"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FilledTonalButton(
                                onClick = {
                                    val targetId = readingProgress.lastReadPoemId
                                    if (!targetId.isNullOrBlank()) {
                                        viewModel.openShayariDetailById(targetId)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = AntiqueGold,
                                    contentColor = DeepMidnight
                                )
                            ) {
                                Text("Resume Reading", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // CATEGORY FILTER SYSTEM FOR OFFLINE VAULT
        item(key = "category_filters") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Filter by Category & Theme",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (selectedCategory != PoemCategory.ALL || tagSearchQuery.isNotBlank()) {
                        TextButton(
                            onClick = {
                                selectedCategory = PoemCategory.ALL
                                tagSearchQuery = ""
                            }
                        ) {
                            Text("Reset", fontSize = 12.sp, color = AntiqueGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PoemCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        val count = categoryCounts[category] ?: 0
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = "${category.emoji} ${category.displayName} ($count)",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                selectedLabelColor = AntiqueGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) AntiqueGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag("vault_cat_chip_${category.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // TAG SEARCH BAR
                OutlinedTextField(
                    value = tagSearchQuery,
                    onValueChange = { tagSearchQuery = it },
                    placeholder = { Text("Search by tag (e.g. rain, courage, monsoon, heartbreak)...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Label, contentDescription = "Tag search", tint = AntiqueGold, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (tagSearchQuery.isNotBlank()) {
                            IconButton(onClick = { tagSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear tag", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tag_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    ),
                    singleLine = true
                )

                // Quick Clickable Tag Suggestions
                val suggestedTags = when (selectedCategory) {
                    PoemCategory.NATURE -> listOf("rain", "monsoon", "clouds", "flowers", "hills", "earth")
                    PoemCategory.LOVE -> listOf("love", "passion", "ghalib", "beauty", "heart", "ishq")
                    PoemCategory.SORROW -> listOf("sorrow", "dard", "heartbreak", "tears", "loss", "gham")
                    PoemCategory.INSPIRATION -> listOf("inspiration", "courage", "resilience", "storm", "defiance")
                    PoemCategory.LIFE -> listOf("life", "zindagi", "jeevan", "journey", "struggle", "destiny")
                    PoemCategory.PHILOSOPHY -> listOf("philosophy", "falsafa", "wisdom", "truth", "darshan", "time")
                    else -> listOf("rain", "courage", "monsoon", "heartbreak", "ghalib", "zindagi", "falsafa")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Tags:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    suggestedTags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (tagSearchQuery.contains(tag, ignoreCase = true)) AntiqueGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.5.dp, if (tagSearchQuery.contains(tag, ignoreCase = true)) AntiqueGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            onClick = {
                                tagSearchQuery = if (tagSearchQuery.contains(tag, ignoreCase = true)) "" else tag
                            }
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 11.sp,
                                color = if (tagSearchQuery.contains(tag, ignoreCase = true)) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Language Filter
        item(key = "offline_language_filters") {
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

        // Curated Content Packs Carousel
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
                        title = "Nature & Rain",
                        subtitle = "Monsoon, green hills & earth",
                        icon = "🍃",
                        onClick = {
                            viewModel.downloadCuratedPack("all")
                            selectedCategory = PoemCategory.NATURE
                            Toast.makeText(context, "Downloaded Nature verses pack", Toast.LENGTH_SHORT).show()
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

        // Shayaris List with typography settings applied
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
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
                            text = if (sourceList.isEmpty()) {
                                if (vaultFilterTab == "saved") "No saved poems in vault yet" else "No verses downloaded yet"
                            } else {
                                "No poems match '${selectedCategory.displayName}' or tag '${tagSearchQuery}'"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (sourceList.isEmpty()) {
                                "Tap the bookmark icon on any poem to save it to your offline vault."
                            } else {
                                "Try choosing another category or clearing your tag search query."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (selectedCategory != PoemCategory.ALL || tagSearchQuery.isNotBlank()) {
                            Button(
                                onClick = {
                                    selectedCategory = PoemCategory.ALL
                                    tagSearchQuery = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Clear Category & Tag Filters", color = DeepMidnight, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.downloadCuratedPack("all")
                                    Toast.makeText(context, "Downloaded starter poetry pack!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Download Curated Poetry Pack", color = DeepMidnight, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { shayari ->
                val isCurrentBookmark = readingProgress.lastReadPoemId == shayari.id
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (isCurrentBookmark) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            color = AntiqueGold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("📌", fontSize = 12.sp)
                                Text(
                                    text = "Your Reading Bookmark • Picked up here",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepMidnight
                                )
                            }
                        }
                    }

                    ShayariCard(
                        shayari = shayari,
                        onLikeClick = { viewModel.toggleLike(shayari) },
                        onSaveClick = { viewModel.toggleSave(shayari) },
                        onDownloadClick = { viewModel.toggleDownload(shayari) },
                        onCardClick = {
                            viewModel.recordPoemRead(context, shayari)
                            viewModel.openShayariDetail(shayari)
                        },
                        onReciteClick = { lines, lang ->
                            viewModel.recordPoemRead(context, shayari)
                            audioReciter.speak(lines, lang)
                        },
                        onOpenCardStudio = { cardStudioShayari = it },
                        poetryFontSizeSp = poetryFontSizeSp,
                        poetryLineHeightMult = poetryLineHeightMult,
                        poetryFontFamilyType = poetryFontFamilyType,
                        onEditCategoryClick = { categorizeShayari = it },
                        onAnalyzeWithGemini = {
                            viewModel.analyzeWithHighThinking(it.lines, it.language)
                            onNavigateToAiStudio()
                        }
                    )

                    // Quick Bookmark / Last-read button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                if (isCurrentBookmark) {
                                    viewModel.clearBookmark(context)
                                    Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.setBookmark(context, shayari)
                                    Toast.makeText(context, "Marked as your reading bookmark 📌", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isCurrentBookmark) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = AntiqueGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCurrentBookmark) "Bookmarked (Last Read)" else "Set Bookmark Here",
                                fontSize = 11.sp,
                                color = AntiqueGold
                            )
                        }
                    }
                }
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
