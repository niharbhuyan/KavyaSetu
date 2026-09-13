package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.VelvetRose

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShayariCard(
    shayari: Shayari,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onReciteClick: (String, String) -> Unit,
    onOpenCardStudio: (Shayari) -> Unit,
    onAnalyzeWithGemini: (Shayari) -> Unit,
    onDownloadClick: (() -> Unit)? = null,
    onCardClick: (() -> Unit)? = null,
    isAnalyzing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTranslations by remember { mutableStateOf(false) }
    val emotion = Emotion.fromCode(shayari.emotion)
    val lang = Language.fromCode(shayari.language)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onCardClick != null) Modifier.clickable { onCardClick() } else Modifier)
            .animateContentSize()
            .testTag("shayari_card_${shayari.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (shayari.isDailyPick) AntiqueGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (shayari.isDailyPick) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Daily Pick badge, Emotion & Language Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (shayari.isDailyPick) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("☀️", fontSize = 12.sp)
                                Text(
                                    "Morning Pick",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                            }
                        }
                    }

                    // Emotion Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "${emotion.emoji} ${emotion.englishLabel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Moderation Status Badge if pending or flagged
                    if (shayari.moderationStatus == "FLAGGED") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VelvetRose.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = VelvetRose,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    "Auto-Flagged",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VelvetRose
                                )
                            }
                        }
                    } else if (shayari.moderationStatus == "PENDING") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF39C12).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFF39C12).copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("⏳", fontSize = 11.sp)
                                Text(
                                    "Under Review",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF39C12)
                                )
                            }
                        }
                    }
                }

                // Language tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${lang.scriptSample} ${lang.displayName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quotation decorative mark
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = AntiqueGold.copy(alpha = 0.45f),
                modifier = Modifier.size(28.dp)
            )

            // Shayari lines in rich display
            Text(
                text = shayari.lines,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    lineHeight = 32.sp,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.Serif
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("shayari_lines_text")
            )

            // Author & Pen Name Signature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "— ${shayari.author}${if (shayari.penName.isNotBlank()) " '${shayari.penName}'" else ""}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = AntiqueGold
                )
            }

            // Translations view toggle
            if (shayari.translationEnglish.isNotBlank() || shayari.translationHindi.isNotBlank() || shayari.translationOdia.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showTranslations = !showTranslations }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Translations",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (showTranslations) "Hide Translations" else "Multilingual Translations (English • हिंदी • ଓଡ଼ିଆ)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                AnimatedVisibility(visible = showTranslations) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (shayari.translationEnglish.isNotBlank()) {
                            Text(
                                text = "🇬🇧 English: ${shayari.translationEnglish}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                        if (shayari.translationHindi.isNotBlank()) {
                            Text(
                                text = "🇮🇳 हिंदी: ${shayari.translationHindi}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                        if (shayari.translationOdia.isNotBlank()) {
                            Text(
                                text = "🇮🇳 ଓଡ଼ିଆ: ${shayari.translationOdia}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Poetic analysis box if available
            if (shayari.poeticAnalysis.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = shayari.poeticAnalysis,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Bar: Like, Save, Recite Audio, Visual Card Studio, Copy, Social Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button & count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.testTag("like_button_${shayari.id}")
                    ) {
                        Icon(
                            imageVector = if (shayari.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (shayari.isLiked) VelvetRose else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${shayari.likesCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (shayari.isLiked) VelvetRose else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Recite button
                IconButton(
                    onClick = { onReciteClick(shayari.lines, shayari.language) },
                    modifier = Modifier.testTag("recite_button_${shayari.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Recite Shayari",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bookmark / Save button
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier.testTag("save_button_${shayari.id}")
                ) {
                    Icon(
                        imageVector = if (shayari.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save to favorites",
                        tint = if (shayari.isSaved) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Offline Download button
                if (onDownloadClick != null) {
                    IconButton(
                        onClick = {
                            onDownloadClick()
                            val msg = if (shayari.isDownloaded) "Removed from offline storage" else "Downloaded for offline access"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("download_button_${shayari.id}")
                    ) {
                        Icon(
                            imageVector = if (shayari.isDownloaded) Icons.Default.FileDownloadDone else Icons.Default.Download,
                            contentDescription = if (shayari.isDownloaded) "Downloaded Offline" else "Download for Offline",
                            tint = if (shayari.isDownloaded) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Copy to clipboard
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val textToCopy = "${shayari.lines}\n\n— ${shayari.author}\n#Shayari #Poetry #Multilingual"
                        clipboard.setPrimaryClip(ClipData.newPlainText("Shayari", textToCopy))
                        Toast.makeText(context, "Shayari copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_button_${shayari.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy text",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Visual Card Studio dialog opener
                IconButton(
                    onClick = { onOpenCardStudio(shayari) },
                    modifier = Modifier.testTag("card_studio_button_${shayari.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Design Quote Card",
                        tint = AntiqueGold
                    )
                }

                // Direct Social Share Intent
                IconButton(
                    onClick = {
                        val shareText = "✨ *Shayari of the Soul*\n\n${shayari.lines}\n\n— ${shayari.author} (${shayari.language.replaceFirstChar { it.uppercase() }})\n\nShared via Shayari App"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            putExtra(Intent.EXTRA_SUBJECT, "Poetic Verse")
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Shayari via..."))
                    },
                    modifier = Modifier.testTag("share_button_${shayari.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Shayari",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
