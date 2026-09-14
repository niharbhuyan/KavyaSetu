package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PoeticWordDefinition
import com.example.data.repository.LafzOMaaniData
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

@Composable
fun LafzOMaaniDialog(
    initialQuery: String = "",
    initialCoupletToScan: String = "",
    audioReciter: AudioReciter? = null,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedWord by remember { mutableStateOf<PoeticWordDefinition?>(null) }

    val clipboardManager = LocalClipboardManager.current

    val scannedWords = remember(initialCoupletToScan) {
        if (initialCoupletToScan.isNotBlank()) {
            LafzOMaaniData.findMatchingWordsInText(initialCoupletToScan)
        } else emptyList()
    }

    val displayedWords = remember(searchQuery, selectedCategory, scannedWords) {
        val baseList = if (searchQuery.isBlank() && scannedWords.isNotEmpty() && selectedCategory == "All") {
            scannedWords
        } else {
            LafzOMaaniData.search(searchQuery)
        }

        if (selectedCategory == "All") baseList
        else baseList.filter { it.categoryTag.equals(selectedCategory, ignoreCase = true) }
    }

    val categories = listOf("All", "Love", "Grief", "Mysticism", "Aesthetics")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = DeepMidnight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AntiqueGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔍", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lafz-o-Maani • لَفْظ و مَعْنٰی",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AntiqueGold
                            )
                            Text(
                                text = "Poetic Etymology, Roots & Rhyming Lexicon",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_lafz_o_maani")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // If scanned from verse, show notification pill
                if (scannedWords.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AntiqueGold.copy(alpha = 0.15f))
                            .border(1.dp, AntiqueGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "✨ Scanned from Couplet: ${scannedWords.size} classical words detected!",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_lafz_input"),
                    placeholder = { Text("Search word (e.g. Hijr, Bismil, Visal, Manamohana)...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AntiqueGold)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Categories
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AntiqueGold else CharcoalElevated)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DeepMidnight else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Words List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedWords) { word ->
                        val isExpanded = expandedWord?.wordLatin == word.wordLatin
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedWord = if (isExpanded) null else word
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isExpanded) CharcoalElevated else CharcoalElevated.copy(alpha = 0.7f)
                            ),
                            border = if (isExpanded) androidx.compose.foundation.BorderStroke(1.2.dp, AntiqueGold) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = word.wordLatin,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = AntiqueGold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${word.wordHindi} • ${word.wordUrdu}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray
                                        )
                                        if (word.wordOdia.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "• ${word.wordOdia}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = VelvetRose
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AntiqueGold.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = word.originLanguage,
                                            fontSize = 9.sp,
                                            color = AntiqueGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Literal: ${word.literalMeaning}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )

                                // Expanded view
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 10.dp)) {
                                        HorizontalDivider(color = AntiqueGold.copy(alpha = 0.2f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Poetic Nuance
                                        Text(
                                            text = "Deep Poetic Nuance:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = AntiqueGold
                                        )
                                        Text(
                                            text = word.poeticNuance,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White,
                                            lineHeight = 17.sp
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Root derivation
                                        Text(
                                            text = "Etymology & Root Derivation:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = VelvetRose
                                        )
                                        Text(
                                            text = word.rootDerivation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.LightGray
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Rhyming Companions (Ham-Qafiya)
                                        Text(
                                            text = "Rhyming Companions (Ham-Qafiya • هم قافیہ):",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = AntiqueGold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            items(word.rhymingCompanions) { comp ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(DeepMidnight)
                                                        .border(1.dp, AntiqueGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(comp, fontSize = 10.sp, color = AntiqueGold)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Example Couplet
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = DeepMidnight),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Master Verse Citation:",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = AntiqueGold
                                                    )
                                                    if (audioReciter != null) {
                                                        IconButton(
                                                            onClick = {
                                                                audioReciter.speak(word.exampleCouplet, "hindi")
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.AutoMirrored.Filled.VolumeUp,
                                                                contentDescription = "Recite",
                                                                tint = AntiqueGold,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = word.exampleCouplet,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.Serif,
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "~ ${word.exampleAuthor} ~",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                                                    color = VelvetRose
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Copy Action
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val text = "${word.wordLatin} (${word.wordHindi} / ${word.wordUrdu})\nOrigin: ${word.originLanguage}\nMeaning: ${word.literalMeaning}\nPoetic Nuance: ${word.poeticNuance}\nRoot: ${word.rootDerivation}\nExample:\n${word.exampleCouplet}\n~ ${word.exampleAuthor}"
                                                    clipboardManager.setText(AnnotatedString(text))
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.ContentCopy,
                                                    contentDescription = "Copy Word Card",
                                                    tint = AntiqueGold,
                                                    modifier = Modifier.size(16.dp)
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
        }
    }
}
