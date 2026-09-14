package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Emotion
import com.example.data.model.Shayari
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.ShayariCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.VelvetRose

@Composable
fun ExploreScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val shayaris by viewModel.displayedShayaris.collectAsStateWithLifecycle()
    val selectedEmotion by viewModel.selectedEmotion.collectAsStateWithLifecycle()
    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }

    if (cardStudioShayari != null) {
        CardStudioDialog(
            shayari = cardStudioShayari!!,
            onDismiss = { cardStudioShayari = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search verses, poets, emotions...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = AntiqueGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("explore_search_bar"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }

        // Quick Mood Filter Chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filter by Mood",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    if (selectedEmotion != null) {
                        TextButton(
                            onClick = { viewModel.onEmotionSelected(null) },
                            modifier = Modifier.testTag("clear_mood_filter_button")
                        ) {
                            Text("Reset", color = VelvetRose, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("mood_filter_chips_row")
                ) {
                    item {
                        FilterChip(
                            selected = selectedEmotion == null,
                            onClick = { viewModel.onEmotionSelected(null) },
                            label = { Text("✨ All Moods") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.2f),
                                selectedLabelColor = AntiqueGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedEmotion == null,
                                selectedBorderColor = AntiqueGold
                            ),
                            modifier = Modifier.testTag("mood_filter_all")
                        )
                    }
                    items(Emotion.entries) { emo ->
                        val isSelected = selectedEmotion == emo
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.onEmotionSelected(if (isSelected) null else emo)
                            },
                            label = { Text("${emo.emoji} ${emo.englishLabel}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VelvetRose.copy(alpha = 0.25f),
                                selectedLabelColor = VelvetRose
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = VelvetRose
                            ),
                            modifier = Modifier.testTag("mood_filter_${emo.code}")
                        )
                    }
                }
            }
        }

        // Emotion Category Grid
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Emotional Realms",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 2-column grid of emotion cards
                val chunkedEmotions = Emotion.entries.chunked(2)
                chunkedEmotions.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { emo ->
                            val isSelected = selectedEmotion == emo
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.onEmotionSelected(if (isSelected) null else emo)
                                    }
                                    .padding(vertical = 4.dp)
                                    .testTag("emotion_card_${emo.code}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) VelvetRose.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) VelvetRose else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = emo.emoji, fontSize = 24.sp)
                                    Column {
                                        Text(
                                            text = emo.englishLabel,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${emo.hindiLabel} • ${emo.odiaLabel}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (emo.subtitle.isNotBlank()) {
                                            Text(
                                                text = emo.subtitle,
                                                fontSize = 10.sp,
                                                color = AntiqueGold,
                                                maxLines = 1
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

        // Search/Filtered results header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results (${shayaris.size})"
                    else if (selectedEmotion != null) "${selectedEmotion!!.emoji} ${selectedEmotion!!.englishLabel} Collection (${shayaris.size})"
                    else "All Curated Verses (${shayaris.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("explore_results_header")
                )
                if (selectedEmotion != null) {
                    TextButton(
                        onClick = { viewModel.onEmotionSelected(null) },
                        modifier = Modifier.testTag("clear_filter_header_btn")
                    ) {
                        Text("Show All", color = VelvetRose, fontSize = 12.sp)
                    }
                }
            }
        }

        // Empty State
        if (shayaris.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (selectedEmotion != null) "${selectedEmotion!!.emoji}" else "🔍",
                        fontSize = 40.sp
                    )
                    Text(
                        text = if (selectedEmotion != null) "No verses found in ${selectedEmotion!!.englishLabel} yet"
                        else "No verses matching your search",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Try selecting another mood or clearing your search filter",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(
                        onClick = {
                            viewModel.onEmotionSelected(null)
                            viewModel.onSearchQueryChanged("")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold),
                        modifier = Modifier.testTag("explore_reset_filter_btn")
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        }

        // Shayaris items
        items(
            items = shayaris,
            key = { it.id }
        ) { shayari ->
            ShayariCard(
                shayari = shayari,
                onLikeClick = { viewModel.toggleLike(shayari) },
                onSaveClick = { viewModel.toggleSave(shayari) },
                onDownloadClick = { viewModel.toggleDownload(shayari) },
                onReciteClick = { lines, lang -> audioReciter.speak(lines, lang) },
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
