package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import com.example.util.SocialShareHelper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.SoftGold
import com.example.ui.theme.VelvetRose

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShayariDetailDialog(
    shayari: Shayari,
    onDismiss: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onReciteClick: (String, String) -> Unit,
    onOpenCardStudio: (Shayari) -> Unit,
    onPinWidget: (Context) -> Unit,
    onAnalyzeWithGemini: (Shayari) -> Unit,
    isAnalyzing: Boolean = false
) {
    val context = LocalContext.current
    val audioReciter = remember { AudioReciter(context) }
    var showEnglishTranslation by remember { mutableStateOf(true) }
    val emotion = Emotion.fromCode(shayari.emotion)
    val lang = Language.fromCode(shayari.language)

    var showTarannum by remember { mutableStateOf(false) }
    var showLafzOMaani by remember { mutableStateOf(false) }
    var showCalligraphy by remember { mutableStateOf(false) }
    var showUstaadIslah by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val readingMetrics = remember(shayari.lines, shayari.translationEnglish) {
        calculatePoemReadingMetrics(shayari.lines, shayari.translationEnglish)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("shayari_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DeepMidnight,
            border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.6f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // PINNED AT THE TOP: Visual Reading Progress Bar with Native Android Share Action
                ScrollablePoetryReadingProgressBar(
                    scrollState = scrollState,
                    shayari = shayari,
                    onPoemFinished = {
                        com.example.data.local.ReadingProgressManager.recordPoemRead(context, shayari)
                    },
                    onSharePoem = {
                        SocialShareHelper.sharePoem(context, shayari)
                    },
                    onCopyPoem = {
                        SocialShareHelper.copyPoem(context, shayari)
                    }
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AntiqueGold.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (readingMetrics.isLongNazmOrGhazal) "Nazm Reader • ନଜ଼୍ମ ପାଠ" else "Shayari Details • ବିବରଣୀ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = if (readingMetrics.isLongNazmOrGhazal) "Long Poem • ${readingMetrics.coupletCount} Ash'aar • ${readingMetrics.formattedReadTime}" else "Deep Link View • Couplet of the Day",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Top Action Icons: Native Share Intent, Copy to Clipboard & Close Dialog
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    SocialShareHelper.sharePoem(context, shayari)
                                },
                                modifier = Modifier.size(36.dp).testTag("header_share_poem_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Poem to Social Apps",
                                    tint = AntiqueGold
                                )
                            }

                            IconButton(
                                onClick = {
                                    SocialShareHelper.copyPoem(context, shayari)
                                },
                                modifier = Modifier.size(36.dp).testTag("header_copy_poem_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Poem to Clipboard",
                                    tint = AntiqueGold
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp).testTag("close_detail_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Badges (Language, Emotion, Category, Tags, Era)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val category = shayari.getCategoryEnum()
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AntiqueGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${category.emoji} ${category.displayName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AntiqueGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${lang.scriptSample} ${lang.displayName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VelvetRose.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${emotion.emoji} ${emotion.englishLabel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = VelvetRose,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (shayari.tags.isNotBlank()) {
                        shayari.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (shayari.isDailyPick || shayari.isTrending) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (shayari.isDailyPick) "⭐ Shayari of the Day" else "🔥 Trending Verse",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Hero Couplet Box with Gradient Framing
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF1B122C),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2B1333),
                                        Color(0xFF18102A)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val stanzas = shayari.lines.split("\n\n").filter { it.isNotBlank() }
                            if (stanzas.size > 1) {
                                stanzas.forEachIndexed { sIndex, stanza ->
                                    if (sIndex > 0) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(0.85f),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            HorizontalDivider(modifier = Modifier.weight(1f), color = AntiqueGold.copy(alpha = 0.25f))
                                            Text(
                                                text = " ✦ Ash'ar ${sIndex + 1} ✦ ",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = AntiqueGold.copy(alpha = 0.8f),
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                            HorizontalDivider(modifier = Modifier.weight(1f), color = AntiqueGold.copy(alpha = 0.25f))
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                    }
                                    Text(
                                        text = stanza.trim(),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontFamily = FontFamily.Serif,
                                            lineHeight = 32.sp,
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = shayari.lines,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        lineHeight = 32.sp,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "— ${shayari.author}${if (shayari.penName.isNotBlank()) " '${shayari.penName}'" else ""}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = AntiqueGold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Translation & Meaning Card
                if (shayari.translationEnglish.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
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
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = null,
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "English Translation & Meaning",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AntiqueGold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = shayari.translationEnglish,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Primary Action Buttons Row (Recite, Share, Copy, Card Studio, Homescreen Widget)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onReciteClick(shayari.lines, shayari.language) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Recite", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = { SocialShareHelper.sharePoem(context, shayari) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("detail_primary_share_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = SoftGold, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Share", fontSize = 11.sp, color = SoftGold, fontWeight = FontWeight.SemiBold)
                    }

                    FilledTonalButton(
                        onClick = { SocialShareHelper.copyPoem(context, shayari) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("detail_primary_copy_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy to Clipboard", tint = SoftGold, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Copy", fontSize = 11.sp, color = SoftGold, fontWeight = FontWeight.SemiBold)
                    }

                    FilledTonalButton(
                        onClick = { onOpenCardStudio(shayari) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Studio", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onPinWidget(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.05f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Widgets, contentDescription = null, tint = DeepMidnight, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Widget", fontSize = 11.sp, color = DeepMidnight, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Classical & Cultural Poetry Suite
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF22152C),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Classical Poetry Studio & Arts:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Tarannum Singing
                            Button(
                                onClick = { showTarannum = true },
                                colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text("🪕 Tarannum", fontSize = 11.sp, maxLines = 1)
                            }

                            // Lafz-o-Maani Word Roots
                            Button(
                                onClick = { showLafzOMaani = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text("🔍 Lafz Roots", fontSize = 11.sp, color = DeepMidnight, fontWeight = FontWeight.Bold, maxLines = 1)
                            }

                            // Qalam Calligraphy
                            FilledTonalButton(
                                onClick = { showCalligraphy = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text("📜 Qalam", fontSize = 11.sp, maxLines = 1)
                            }

                            // Master Ustaad Islah
                            FilledTonalButton(
                                onClick = { showUstaadIslah = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text("🎭 Islah", fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Tool Row (Like, Save, Offline Download, Share, Copy, Gemini Analysis)
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = if (shayari.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (shayari.isLiked) VelvetRose else Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Save Bookmark
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = if (shayari.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (shayari.isSaved) AntiqueGold else Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Download for Offline
                    IconButton(onClick = onDownloadClick) {
                        Icon(
                            imageVector = if (shayari.isDownloaded) Icons.Default.FileDownloadDone else Icons.Default.Download,
                            contentDescription = "Offline Download",
                            tint = if (shayari.isDownloaded) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Copy Text
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "Shayari",
                                "🖋️ *Kavya Setu • काव्यसेतु*\n\n${shayari.lines}\n— ${shayari.author}\n\n— Watermark: Kavya Setu (Built by Nihar Sales)\n#KavyaSetu #Poetry"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Couplet copied with Kavya Setu watermark", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Share to Social Media
                    IconButton(
                        onClick = {
                            SocialShareHelper.sharePoem(context, shayari)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share to Social Media",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gemini AI Poetic Analysis Button
                OutlinedButton(
                    onClick = { onAnalyzeWithGemini(shayari) },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("detail_gemini_analysis_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = AntiqueGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini AI Analyzing Metaphor & Rhythm...", fontSize = 12.sp, color = AntiqueGold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Analyze with Gemini AI (Beher & Sentiment)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AntiqueGold
                        )
                    }
                }
            }
        }
    }
}

    if (showTarannum) {
        TarannumModeDialog(
            shayari = shayari,
            audioReciter = audioReciter,
            onDismiss = { showTarannum = false }
        )
    }

    if (showLafzOMaani) {
        LafzOMaaniDialog(
            initialCoupletToScan = shayari.lines,
            audioReciter = audioReciter,
            onDismiss = { showLafzOMaani = false }
        )
    }

    if (showCalligraphy) {
        CalligraphyStudioDialog(
            initialVerse = shayari.lines,
            onDismiss = { showCalligraphy = false }
        )
    }

    if (showUstaadIslah) {
        KalamEUstaadDialog(
            initialDraft = shayari.lines,
            audioReciter = audioReciter,
            onDismiss = { showUstaadIslah = false }
        )
    }
}
