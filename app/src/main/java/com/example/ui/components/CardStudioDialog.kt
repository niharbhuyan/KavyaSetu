package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.example.data.model.Shayari
import com.example.data.remote.GeminiClient
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.ParchmentLight
import com.example.ui.theme.ParchmentText
import com.example.ui.theme.RoyalPlum
import com.example.ui.theme.SoftGold
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.launch

enum class CardBackgroundTheme(val title: String, val brush: Brush, val textColor: Color, val accentColor: Color) {
    MIDNIGHT_GOLD(
        "Midnight Gold",
        Brush.linearGradient(listOf(Color(0xFF0F0C1B), Color(0xFF1B152A), Color(0xFF281E3B))),
        Color(0xFFFFFDF8),
        AntiqueGold
    ),
    VELVET_CRIMSON(
        "Velvet Crimson",
        Brush.linearGradient(listOf(Color(0xFF2B0716), Color(0xFF4A0E26), Color(0xFF1F0510))),
        Color(0xFFFFFAF0),
        Color(0xFFFF8DA1)
    ),
    MYSTIC_TEAL(
        "Mystic Indigo",
        Brush.linearGradient(listOf(Color(0xFF081B24), Color(0xFF0F303F), Color(0xFF07141B))),
        Color(0xFFF0FDF8),
        Color(0xFF57CC99)
    ),
    ANTIQUE_PARCHMENT(
        "Warm Parchment",
        Brush.linearGradient(listOf(Color(0xFFF9F4EB), Color(0xFFEFE6D5), Color(0xFFE5DAC6))),
        Color(0xFF281F19),
        Color(0xFF9E6E24)
    )
}

enum class CardFontChoice(val title: String, val fontFamily: FontFamily) {
    SERIF("Classical Serif", FontFamily.Serif),
    SANS("Modern Sans", FontFamily.SansSerif),
    MONO("Literary Type", FontFamily.Monospace)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardStudioDialog(
    shayari: Shayari,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTheme by remember { mutableStateOf(CardBackgroundTheme.MIDNIGHT_GOLD) }
    var selectedFont by remember { mutableStateOf(CardFontChoice.SERIF) }
    var selectedResolution by remember { mutableStateOf("1K") } // "1K", "2K", "4K"
    var aiGeneratedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isGeneratingAiImage by remember { mutableStateOf(false) }
    var aiImageError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Social Card Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, selectedTheme.accentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .background(selectedTheme.brush)
                        .testTag("preview_card_box"),
                    contentAlignment = Alignment.Center
                ) {
                    // Background AI Image if generated
                    if (aiGeneratedBitmap != null) {
                        Image(
                            bitmap = aiGeneratedBitmap!!.asImageBitmap(),
                            contentDescription = "AI Artwork Background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Dark overlay to keep text legible
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f))
                        )
                    }

                    // Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top decorative flourish
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✦ SHAYARI ✦",
                                fontSize = 11.sp,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = selectedTheme.accentColor
                            )
                            Text(
                                text = shayari.language.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedTheme.accentColor
                            )
                        }

                        // Center quote text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = selectedTheme.accentColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = shayari.lines,
                                fontSize = 19.sp,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = selectedFont.fontFamily,
                                fontWeight = FontWeight.Medium,
                                color = selectedTheme.textColor
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "— ${shayari.author}${if (shayari.penName.isNotBlank()) " '${shayari.penName}'" else ""}",
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                color = selectedTheme.accentColor
                            )
                        }

                        // Bottom watermark
                        Text(
                            text = "Crafted with Shayari Global",
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = selectedTheme.textColor.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Themes
                Text(
                    text = "Card Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardBackgroundTheme.entries.forEach { theme ->
                        FilterChip(
                            selected = selectedTheme == theme && aiGeneratedBitmap == null,
                            onClick = {
                                selectedTheme = theme
                                aiGeneratedBitmap = null
                            },
                            label = { Text(theme.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.2f),
                                selectedLabelColor = AntiqueGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: AI Wallpaper Generator with gemini-3-pro-image-preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AntiqueGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Gemini 3 Pro Image Wallpaper (1K / 2K / 4K)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                        }
                        Text(
                            text = "Generate ultra-high fidelity ambient art tailored to this couplet's emotion.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Resolution choice
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Resolution:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            listOf("1K", "2K", "4K").forEach { res ->
                                FilterChip(
                                    selected = selectedResolution == res,
                                    onClick = { selectedResolution = res },
                                    label = { Text(res) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AntiqueGold,
                                        selectedLabelColor = DeepMidnight
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    isGeneratingAiImage = true
                                    aiImageError = null
                                    val result = GeminiClient.generateCardBackground(
                                        themeDescription = "Poetry background depicting ${shayari.emotion} and mystical warmth",
                                        imageSize = selectedResolution
                                    )
                                    result.onSuccess {
                                        aiGeneratedBitmap = it
                                        Toast.makeText(context, "Generated $selectedResolution aesthetic wallpaper!", Toast.LENGTH_SHORT).show()
                                    }.onFailure {
                                        aiImageError = it.message ?: "Failed to generate AI image."
                                    }
                                    isGeneratingAiImage = false
                                }
                            },
                            enabled = !isGeneratingAiImage,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight)
                        ) {
                            if (isGeneratingAiImage) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = DeepMidnight, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating $selectedResolution Artwork...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate $selectedResolution Background")
                            }
                        }

                        if (aiImageError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = aiImageError!!,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Typography Style
                Text(
                    text = "Typography Style",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardFontChoice.entries.forEach { font ->
                        FilterChip(
                            selected = selectedFont == font,
                            onClick = { selectedFont = font },
                            label = { Text(font.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold.copy(alpha = 0.2f),
                                selectedLabelColor = AntiqueGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Share Card & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val formatted = "✨ *${shayari.lines}*\n\n— ${shayari.author}\n\n#Shayari #Art #${shayari.emotion}"
                            clipboard.setPrimaryClip(ClipData.newPlainText("Formatted Card", formatted))
                            Toast.makeText(context, "Card text copied!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Text")
                    }

                    Button(
                        onClick = {
                            val shareText = "🌹 *Shayari Card*\n\n${shayari.lines}\n\n— ${shayari.author} (${shayari.language.replaceFirstChar { it.uppercase() }})\n\nShared from Shayari App"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                putExtra(Intent.EXTRA_SUBJECT, "Shayari Card")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Card to..."))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Socially")
                    }
                }
            }
        }
    }
}
