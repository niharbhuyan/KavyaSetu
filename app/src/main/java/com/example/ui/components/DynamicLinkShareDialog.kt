package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.analytics.KavyaAnalytics
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.MysticTealSoft
import com.example.ui.theme.VelvetRose
import com.example.util.FirebaseDynamicLinkManager
import com.example.util.SocialShareHelper

/**
 * Dynamic Deep-Linking Share Dialog powered by Firebase Dynamic Links.
 * Allows users to share a direct app-opening link for any specific poem, ghazal, or couplet.
 */
@Composable
fun DynamicLinkShareDialog(
    shayari: Shayari,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var generatedLink by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(shayari.id) {
        isLoading = true
        try {
            val link = FirebaseDynamicLinkManager.generateShortDynamicLink(context, shayari)
            generatedLink = link
        } catch (e: Exception) {
            generatedLink = FirebaseDynamicLinkManager.buildDeterministicDynamicLink(context, shayari)
        } finally {
            isLoading = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
                .testTag("dynamic_link_share_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Dynamic Deep Link",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Firebase App-to-App Link",
                                fontSize = 11.sp,
                                color = AntiqueGold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Poem Snippet Preview Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = shayari.lines.trim(),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontFamily = FontFamily.Serif,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "— ${shayari.author}${if (!shayari.penName.isNullOrBlank()) " (${shayari.penName})" else ""}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AntiqueGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Informational badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MysticTeal.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, MysticTeal.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎯", fontSize = 16.sp)
                        Text(
                            text = "Recipients opening this link will launch Kavya Setu directly to this specific poem instance with audio recitation & translations ready.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Link Display / Loading box
                Text(
                    text = "Generated Deep Link:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DeepMidnight.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = "Creating Firebase short link...",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            Text(
                                text = generatedLink ?: "",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = AntiqueGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    val linkToCopy = generatedLink ?: return@IconButton
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Poem Deep Link", linkToCopy))
                                    Toast.makeText(context, "Deep link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    KavyaAnalytics.trackPoetryInteraction("copy_deep_link", shayari.category, shayari.language, shayari.id)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy link",
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Share Action Button
                Button(
                    onClick = {
                        val link = generatedLink ?: FirebaseDynamicLinkManager.buildDeterministicDynamicLink(context, shayari)
                        FirebaseDynamicLinkManager.sharePoemWithDynamicLink(context, shayari, link)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_dynamic_link_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = DeepMidnight
                    ),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Dynamic Link (All Apps)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick WhatsApp & Copy Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp
                    OutlinedButton(
                        onClick = {
                            val link = generatedLink ?: FirebaseDynamicLinkManager.buildDeterministicDynamicLink(context, shayari)
                            val text = "📜 ${shayari.lines.trim()}\n\n— ${shayari.author}\n\n✨ Open in Kavya Setu:\n$link"
                            SocialShareHelper.shareToWhatsApp(context, text)
                            KavyaAnalytics.trackPoetryInteraction("whatsapp_dynamic_link", shayari.category, shayari.language, shayari.id)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                        border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
                        enabled = !isLoading
                    ) {
                        Text("💬 WhatsApp", fontSize = 11.sp, maxLines = 1)
                    }

                    // Copy Full Message
                    OutlinedButton(
                        onClick = {
                            val link = generatedLink ?: FirebaseDynamicLinkManager.buildDeterministicDynamicLink(context, shayari)
                            val text = "📜 ${shayari.lines.trim()}\n\n— ${shayari.author}\n\n✨ Open in Kavya Setu:\n$link"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Poem with Deep Link", text))
                            Toast.makeText(context, "Poem with dynamic deep link copied! 📋", Toast.LENGTH_SHORT).show()
                            KavyaAnalytics.trackPoetryInteraction("copy_poem_with_link", shayari.category, shayari.language, shayari.id)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        enabled = !isLoading
                    ) {
                        Text("📋 Copy Text+Link", fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}
