package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.TakhallusSealManager
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import com.example.util.SocialFormatPreset
import com.example.util.SocialShareHelper

/**
 * Interactive Dialog that allows users to customize formatting styles (WhatsApp, Instagram, Twitter/X,
 * Royal Parchment, Minimalist) and export/share poetry directly via Android's Intent system.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormattedSocialShareDialog(
    lines: String,
    author: String,
    language: String = "hindi",
    emotion: String? = null,
    style: String? = null,
    topic: String? = null,
    penName: String? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentSeal by TakhallusSealManager.currentSeal.collectAsState()

    var selectedPreset by remember { mutableStateOf(SocialFormatPreset.WHATSAPP_MARKDOWN) }
    var includeSeal by remember { mutableStateOf(true) }
    var includeMetadata by remember { mutableStateOf(true) }
    var includeHashtags by remember { mutableStateOf(true) }
    var includeWatermark by remember { mutableStateOf(true) }

    val formattedPreview = remember(
        lines, author, penName, language, emotion, style, topic,
        selectedPreset, includeSeal, includeMetadata, includeHashtags, includeWatermark, currentSeal
    ) {
        SocialShareHelper.buildCustomFormattedPoem(
            lines = lines,
            author = author,
            penName = penName,
            language = language,
            emotion = emotion,
            style = style,
            topic = topic,
            preset = selectedPreset,
            includeSeal = includeSeal,
            includeHashtags = includeHashtags,
            includeWatermark = includeWatermark,
            includeMetadata = includeMetadata,
            takhallusSealText = currentSeal.takhallus
        )
    }

    val charCount = formattedPreview.length
    val isTwitterOverLimit = selectedPreset == SocialFormatPreset.TWITTER_X && charCount > 280

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("formatted_social_share_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
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
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Export Formatted Verse",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Formatted text for social platforms & chat apps",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_share_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // 1. Format Preset Selector
                Text(
                    text = "Select Format Style:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SocialFormatPreset.entries.forEach { preset ->
                        val isSelected = selectedPreset == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPreset = preset },
                            label = { Text("${preset.iconEmoji} ${preset.title}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold,
                                selectedLabelColor = DeepMidnight
                            )
                        )
                    }
                }

                Text(
                    text = selectedPreset.description,
                    fontSize = 11.sp,
                    color = AntiqueGold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                // 2. Customization Toggles
                Text(
                    text = "Include in Export:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = includeSeal,
                        onClick = { includeSeal = !includeSeal },
                        label = { Text("🪶 Takhallus Seal", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VelvetRose.copy(alpha = 0.2f),
                            selectedLabelColor = VelvetRose
                        )
                    )
                    FilterChip(
                        selected = includeMetadata,
                        onClick = { includeMetadata = !includeMetadata },
                        label = { Text("🎭 Mood & Style", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = includeHashtags,
                        onClick = { includeHashtags = !includeHashtags },
                        label = { Text("#️⃣ Social Hashtags", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = includeWatermark,
                        onClick = { includeWatermark = !includeWatermark },
                        label = { Text("🖋️ Kavya Setu Tag", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Live Formatted Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Text Preview:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Character counter badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isTwitterOverLimit) MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isTwitterOverLimit) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = if (selectedPreset == SocialFormatPreset.TWITTER_X) "$charCount / 280 chars" else "$charCount characters",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isTwitterOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            text = formattedPreview,
                            fontFamily = if (selectedPreset == SocialFormatPreset.ROYAL_PARCHMENT) FontFamily.Monospace else FontFamily.Serif,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Primary System Intent Share Button
                Button(
                    onClick = {
                        SocialShareHelper.shareViaChooser(
                            context = context,
                            text = formattedPreview,
                            subject = "Poetry from Kavya Setu — $author",
                            title = "Share via Social App"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("primary_intent_share_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = DeepMidnight
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = DeepMidnight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share via Android Intent (All Apps)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Quick Direct Platform Launchers
                Text(
                    text = "Quick Send to App:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp
                    OutlinedButton(
                        onClick = { SocialShareHelper.shareToWhatsApp(context, formattedPreview) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_whatsapp_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF25D366)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f))
                    ) {
                        Text("💬 WhatsApp", fontSize = 11.sp, maxLines = 1)
                    }

                    // Twitter / X
                    OutlinedButton(
                        onClick = { SocialShareHelper.shareToTwitter(context, formattedPreview) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_twitter_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AntiqueGold
                        ),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
                    ) {
                        Text("🐦 X (Twitter)", fontSize = 11.sp, maxLines = 1)
                    }

                    // Telegram
                    OutlinedButton(
                        onClick = { SocialShareHelper.shareToTelegram(context, formattedPreview) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_telegram_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2AABEE)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2AABEE).copy(alpha = 0.5f))
                    ) {
                        Text("✈️ Telegram", fontSize = 11.sp, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Copy to Clipboard
                OutlinedButton(
                    onClick = {
                        SocialShareHelper.copyToClipboard(context, formattedPreview, "Formatted Poetry")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_formatted_poetry_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Formatted Text to Clipboard", fontSize = 12.sp)
                }
            }
        }
    }
}
