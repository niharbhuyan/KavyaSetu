package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdMobBanner
import com.example.data.model.Emotion
import com.example.data.model.SearchScope
import com.example.data.model.Shayari
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.CategorizePoemDialog
import com.example.ui.components.ShayariCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

/**
 * Universal Local Database Search & Literary Discovery Studio for Kavya Setu (काव्यसेतु).
 *
 * Facilitates multi-attribute search across:
 * - Couplet / Poem Title & Verses (Devanagari, Nastaliq, Odia, English)
 * - Poet Name (Mirza Ghalib, Faiz, Meer, Iqbal, Rahat Indori, Jaun Elia, Kabisurya, etc.)
 * - Language (Urdu, Hindi, Odia, English)
 * - Emotional realms & categories
 *
 * Real-time reactive auto-updates directly powered by Room DB.
 */
@Composable
fun ExploreScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchScope by viewModel.searchScope.collectAsStateWithLifecycle()
    val selectedSearchPoet by viewModel.selectedSearchPoet.collectAsStateWithLifecycle()
    val selectedSearchLanguage by viewModel.selectedSearchLanguage.collectAsStateWithLifecycle()
    val distinctPoets by viewModel.distinctPoets.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    val shayaris by viewModel.displayedShayaris.collectAsStateWithLifecycle()
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()
    val poetryFontSizeSp by viewModel.poetryFontSizeSp.collectAsStateWithLifecycle()
    val poetryLineHeightMult by viewModel.poetryLineHeightMult.collectAsStateWithLifecycle()
    val poetryFontFamilyType by viewModel.poetryFontFamilyType.collectAsStateWithLifecycle()

    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }
    var categorizeShayari by remember { mutableStateOf<Shayari?>(null) }
    val focusManager = LocalFocusManager.current

    if (categorizeShayari != null) {
        CategorizePoemDialog(
            shayari = categorizeShayari!!,
            onDismiss = { categorizeShayari = null },
            onSaveCategory = { newCategory, newTags ->
                viewModel.updatePoemCategoryAndTags(categorizeShayari!!.id, newCategory, newTags)
                categorizeShayari = null
            }
        )
    }

    if (cardStudioShayari != null) {
        CardStudioDialog(
            shayari = cardStudioShayari!!,
            onDismiss = { cardStudioShayari = null }
        )
    }

    val isFilterActive = searchQuery.isNotBlank() ||
            selectedSearchPoet != null ||
            selectedSearchLanguage != "all" ||
            selectedEmotion != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Search Bar & Scope Selector
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.onSearchQueryChanged(it)
                    },
                    placeholder = {
                        Text(
                            text = searchScope.hint,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Local Database",
                            tint = AntiqueGold
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.onSearchQueryChanged("")
                                },
                                modifier = Modifier.testTag("clear_search_query_btn")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = VelvetRose)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchQuery.isNotBlank()) {
                                viewModel.addRecentSearch(searchQuery)
                            }
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_bar"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Search Scope Chips (All, Poet Name, Title/Verse, Language)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("search_scope_chips_row")
                ) {
                    items(SearchScope.entries) { scope ->
                        val isSelected = searchScope == scope
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onSearchScopeSelected(scope) },
                            label = { Text("${scope.icon} ${scope.displayName}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.22f),
                                selectedLabelColor = AntiqueGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = AntiqueGold
                            ),
                            modifier = Modifier.testTag("scope_chip_${scope.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // 2. Language Quick Filter Chips (Urdu, Hindi, Odia, English)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🌐 Filter by Language (भाषा / زباں)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    if (selectedSearchLanguage != "all") {
                        TextButton(
                            onClick = { viewModel.onSearchLanguageSelected("all") },
                            modifier = Modifier.testTag("reset_language_filter_btn")
                        ) {
                            Text("Reset", color = VelvetRose, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("language_filter_chips_row")
                ) {
                    val languages = listOf(
                        Triple("all", "All Languages", "✨"),
                        Triple("urdu", "اردو (Urdu)", "شاعری"),
                        Triple("hindi", "हिंदी (Hindi)", "शायरी"),
                        Triple("odia", "ଓଡ଼ିଆ (Odia)", "କବିତା"),
                        Triple("english", "English", "Poetry")
                    )
                    items(languages) { (code, label, script) ->
                        val isSelected = selectedSearchLanguage.equals(code, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onSearchLanguageSelected(code) },
                            label = { Text("$script $label", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VelvetRose.copy(alpha = 0.25f),
                                selectedLabelColor = VelvetRose
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = VelvetRose
                            ),
                            modifier = Modifier.testTag("lang_chip_$code")
                        )
                    }
                }
            }
        }

        // 3. Popular Poet Filter Chips (Mirza Ghalib, Faiz, Meer, Iqbal, etc.)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "✍️ Filter by Poet (शायर / شعراء)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    if (selectedSearchPoet != null) {
                        TextButton(
                            onClick = { viewModel.onSearchPoetSelected(null) },
                            modifier = Modifier.testTag("reset_poet_filter_btn")
                        ) {
                            Text("All Poets", color = VelvetRose, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("poet_filter_chips_row")
                ) {
                    val popularPoets = listOf(
                        "Mirza Ghalib", "Faiz Ahmad Faiz", "Meer Taqi Meer", "Allama Iqbal",
                        "Rahat Indori", "Jaun Elia", "Kabisurya Baladev Kartha", "Dushyant Kumar",
                        "Parveen Shakir", "Sahir Ludhianvi", "Ahmad Faraz"
                    )
                    items(popularPoets) { poetName ->
                        val isSelected = selectedSearchPoet == poetName
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onSearchPoetSelected(poetName) },
                            label = { Text("✒️ $poetName", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.28f),
                                selectedLabelColor = AntiqueGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = AntiqueGold
                            ),
                            modifier = Modifier.testTag("poet_chip_${poetName.replace(" ", "_").lowercase()}")
                        )
                    }
                }
            }
        }

        // 4. Recent Search Queries
        if (recentSearches.isNotEmpty() && searchQuery.isBlank() && !isFilterActive) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Recent Searches",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(
                            onClick = { viewModel.clearRecentSearches() },
                            modifier = Modifier.testTag("clear_recent_searches_btn")
                        ) {
                            Text("Clear", color = VelvetRose, fontSize = 11.sp)
                        }
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recentSearches) { term ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.onSearchQueryChanged(term)
                                    }
                                    .testTag("recent_search_chip_$term")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = term, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $term",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.removeRecentSearch(term) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Active Search Summary Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFilterActive) AntiqueGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(0.5.dp, if (isFilterActive) AntiqueGold.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("search_summary_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${shayaris.size} verses found in local database",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AntiqueGold
                        )
                        val filterDescriptions = mutableListOf<String>()
                        if (searchQuery.isNotBlank()) filterDescriptions.add("Keyword: \"$searchQuery\" (${searchScope.displayName})")
                        if (selectedSearchPoet != null) filterDescriptions.add("Poet: $selectedSearchPoet")
                        if (selectedSearchLanguage != "all") filterDescriptions.add("Language: ${selectedSearchLanguage.uppercase()}")
                        if (selectedEmotion != null) filterDescriptions.add("Mood: ${selectedEmotion!!.englishLabel}")

                        if (filterDescriptions.isNotEmpty()) {
                            Text(
                                text = filterDescriptions.joinToString(" • "),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        } else {
                            Text(
                                text = "Auto-synced & live updated from Room database",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isFilterActive) {
                        TextButton(
                            onClick = { viewModel.clearAllSearchFilters() },
                            modifier = Modifier.testTag("clear_all_search_filters_btn")
                        ) {
                            Text("Reset All", color = VelvetRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Empty State when no results match
        if (shayaris.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .testTag("search_empty_state_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "📜", fontSize = 44.sp)
                        Text(
                            text = "No Verses Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                        Text(
                            text = "We couldn't find any couplets matching your current search criteria.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Suggested Searches:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Mirza Ghalib", "Faiz", "Ishq", "Hindi").forEach { term ->
                                OutlinedButton(
                                    onClick = { viewModel.onSearchQueryChanged(term) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold),
                                    border = BorderStroke(0.5.dp, AntiqueGold.copy(alpha = 0.5f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(term, fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.clearAllSearchFilters() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VelvetRose),
                            border = BorderStroke(1.dp, VelvetRose),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(top = 8.dp).testTag("empty_state_reset_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Search Filters")
                        }
                    }
                }
            }
        }

        // 7. Search Results list
        itemsIndexed(
            items = shayaris,
            key = { _, it -> it.id }
        ) { index, shayari ->
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ShayariCard(
                    shayari = shayari,
                    onLikeClick = { viewModel.toggleLike(shayari) },
                    onSaveClick = { viewModel.toggleSave(shayari) },
                    onDownloadClick = { viewModel.toggleDownload(shayari) },
                    onReciteClick = { lines, lang -> audioReciter.speak(lines, lang) },
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
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
