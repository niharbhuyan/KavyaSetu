package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.core.content.FileProvider
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.VelvetRose
// AudioReciter is in com.example.ui.components (same package)
import java.io.File
import java.io.FileOutputStream

enum class ReelTheme(val title: String, val bgGradient: List<Color>, val fontColor: Color, val accentColor: Color) {
    ROYAL_MUGHAL("Royal Mughal", listOf(Color(0xFF1A0B2E), Color(0xFF0F061D)), AntiqueGold, VelvetRose),
    AMOLED_NOIR("Amoled Noir", listOf(Color(0xFF0A0A0A), Color(0xFF141414)), Color.White, AntiqueGold),
    MYSTIC_SUFI("Mystic Sufi", listOf(Color(0xFF0B2424), Color(0xFF051313)), Color(0xFFE0F2F1), MysticTeal),
    ROSE_GULZAR("Rose Gulzar", listOf(Color(0xFF2C0B18), Color(0xFF17040B)), Color(0xFFFFD1DC), VelvetRose)
}

enum class AmbientSound(val label: String, val icon: String, val desc: String) {
    SITAR("Sitar Melody", "🪕", "Acoustic Vilambit Raag Yaman strumming"),
    BANSURI("Bansuri Flute", "🎋", "Pahadi meditative bamboo flute notes"),
    SANTOOR("Kashmir Santoor", "🌊", "Gentle cascading acoustic strikes"),
    NONE("Silent Recital", "🔇", "Pure voice with zero background acoustics")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoStoryStudioDialog(
    initialPoemText: String = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले।\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले॥",
    initialPoetName: String = "मिर्ज़ा असदुल्लाह ख़ान 'ग़ालिब'",
    audioReciter: AudioReciter? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var poemText by remember { mutableStateOf(initialPoemText) }
    var poetName by remember { mutableStateOf(initialPoetName) }
    var selectedTheme by remember { mutableStateOf(ReelTheme.ROYAL_MUGHAL) }
    var selectedSound by remember { mutableStateOf(AmbientSound.SITAR) }
    var includeWaxSeal by remember { mutableStateOf(true) }
    var isPlayingPreview by remember { mutableStateOf(false) }

    // Pulse animation for live 9:16 reel preview
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("video_story_studio_dialog"),
            color = DeepMidnight,
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎴", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "काव्य रील स्टूडियो • 9:16 Story Studio",
                                color = AntiqueGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "HD Vertical Video Cards for Instagram, WhatsApp & Status",
                                color = Color.LightGray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Studio Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 9:16 Canvas Reel Card Live Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .scale(if (isPlayingPreview) pulseScale else 1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.verticalGradient(selectedTheme.bgGradient))
                            .border(BorderStroke(1.5.dp, selectedTheme.accentColor.copy(alpha = 0.7f)), RoundedCornerShape(18.dp))
                            .padding(18.dp)
                    ) {
                        // Ornamental corner watermarks
                        Text(
                            text = "﷽",
                            color = selectedTheme.accentColor.copy(alpha = 0.35f),
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = poemText,
                                color = selectedTheme.fontColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "— $poetName",
                                color = selectedTheme.accentColor,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Bottom status bar & Shahi Mohar (royal seal)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(selectedSound.icon, fontSize = 12.sp)
                                Text(
                                    text = selectedSound.label,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 9.sp
                                )
                            }

                            if (includeWaxSeal) {
                                Surface(
                                    shape = CircleShape,
                                    color = VelvetRose.copy(alpha = 0.85f),
                                    border = BorderStroke(1.dp, AntiqueGold),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("मोहर", fontSize = 9.sp, color = AntiqueGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Theme selector row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("1. Visual Palette & Mood", color = AntiqueGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReelTheme.values().forEach { theme ->
                                val isSelected = selectedTheme == theme
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedTheme = theme },
                                    color = if (isSelected) theme.accentColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                                    border = BorderStroke(1.dp, if (isSelected) theme.accentColor else Color.White.copy(alpha = 0.15f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = theme.title,
                                            color = if (isSelected) theme.accentColor else Color.White.copy(alpha = 0.8f),
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Ambient Acoustic Score
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("2. Acoustic Score & Recital Ambient", color = AntiqueGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AmbientSound.values().forEach { sound ->
                                val isSelected = selectedSound == sound
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedSound = sound },
                                    color = if (isSelected) MysticTeal.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                                    border = BorderStroke(1.dp, if (isSelected) MysticTeal else Color.White.copy(alpha = 0.15f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(sound.icon, fontSize = 14.sp)
                                        Text(
                                            text = sound.label,
                                            color = if (isSelected) MysticTeal else Color.White.copy(alpha = 0.7f),
                                            fontSize = 8.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Options toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Shahi Mohar (शाही मोहर)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("Stamp personalized royal wax watermark", color = Color.LightGray, fontSize = 10.sp)
                        }
                        Switch(
                            checked = includeWaxSeal,
                            onCheckedChange = { includeWaxSeal = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AntiqueGold, checkedTrackColor = AntiqueGold.copy(alpha = 0.4f))
                        )
                    }

                    // Audio Reciter Preview Button
                    if (audioReciter != null) {
                        Button(
                            onClick = {
                                isPlayingPreview = true
                                audioReciter.speak(poemText.replace("\n", " "), "hi")
                                Handler(Looper.getMainLooper()).postDelayed({
                                    isPlayingPreview = false
                                }, 7000)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isPlayingPreview) "Playing Audio Reel Preview..." else "Preview Recital with Voice", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: Export 9:16 High-Res Story Card
                Button(
                    onClick = {
                        try {
                            // Render 9:16 Bitmap (1080 x 1920 HD Standard)
                            val width = 1080
                            val height = 1920
                            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)

                            // Background gradient
                            val p = Paint()
                            val topColor = when (selectedTheme) {
                                ReelTheme.ROYAL_MUGHAL -> 0xFF1A0B2E.toInt()
                                ReelTheme.AMOLED_NOIR -> 0xFF0A0A0A.toInt()
                                ReelTheme.MYSTIC_SUFI -> 0xFF0B2424.toInt()
                                ReelTheme.ROSE_GULZAR -> 0xFF2C0B18.toInt()
                            }
                            val bottomColor = when (selectedTheme) {
                                ReelTheme.ROYAL_MUGHAL -> 0xFF0F061D.toInt()
                                ReelTheme.AMOLED_NOIR -> 0xFF141414.toInt()
                                ReelTheme.MYSTIC_SUFI -> 0xFF051313.toInt()
                                ReelTheme.ROSE_GULZAR -> 0xFF17040B.toInt()
                            }
                            p.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), topColor, bottomColor, Shader.TileMode.CLAMP)
                            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), p)
                            p.shader = null

                            // Border
                            p.style = Paint.Style.STROKE
                            p.strokeWidth = 14f
                            p.color = when (selectedTheme) {
                                ReelTheme.ROYAL_MUGHAL -> 0xFFD4AF37.toInt()
                                ReelTheme.AMOLED_NOIR -> 0xFFD4AF37.toInt()
                                ReelTheme.MYSTIC_SUFI -> 0xFF2E8B57.toInt()
                                ReelTheme.ROSE_GULZAR -> 0xFFC71585.toInt()
                            }
                            canvas.drawRoundRect(RectF(40f, 40f, (width - 40).toFloat(), (height - 40).toFloat()), 30f, 30f, p)

                            // Header Text
                            p.style = Paint.Style.FILL
                            p.color = 0xFFD4AF37.toInt()
                            p.textSize = 54f
                            p.textAlign = Paint.Align.CENTER
                            p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            canvas.drawText("काव्यसेतु • KAVYA SETU", (width / 2).toFloat(), 200f, p)

                            // Couplet lines
                            p.color = when (selectedTheme) {
                                ReelTheme.AMOLED_NOIR -> 0xFFFFFFFF.toInt()
                                ReelTheme.MYSTIC_SUFI -> 0xFFE0F2F1.toInt()
                                ReelTheme.ROSE_GULZAR -> 0xFFFFD1DC.toInt()
                                else -> 0xFFD4AF37.toInt()
                            }
                            p.textSize = 62f
                            p.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)

                            val lines = poemText.split("\n")
                            var startY = 850f
                            for (line in lines) {
                                canvas.drawText(line, (width / 2).toFloat(), startY, p)
                                startY += 120f
                            }

                            // Poet attribution
                            p.textSize = 48f
                            p.color = 0xFFC71585.toInt()
                            p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                            canvas.drawText("— $poetName", (width / 2).toFloat(), startY + 60f, p)

                            // Watermark / Seal
                            if (includeWaxSeal) {
                                p.style = Paint.Style.FILL
                                p.color = 0xFF8B0000.toInt()
                                canvas.drawCircle((width / 2).toFloat(), 1650f, 70f, p)
                                p.color = 0xFFD4AF37.toInt()
                                p.textSize = 34f
                                p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                canvas.drawText("शाही मोहर", (width / 2).toFloat(), 1662f, p)
                            }

                            // Save to cache and trigger share
                            val cachePath = File(context.cacheDir, "images")
                            cachePath.mkdirs()
                            val stream = FileOutputStream(File(cachePath, "kavya_story_reel.png"))
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            stream.close()

                            val imagePath = File(context.cacheDir, "images/kavya_story_reel.png")
                            val contentUri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", imagePath)

                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                                putExtra(Intent.EXTRA_STREAM, contentUri)
                                putExtra(Intent.EXTRA_TEXT, "✨ Created with Kavya Setu — 9:16 Vertical Reel Studio\n$poemText\n— $poetName")
                                type = "image/png"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share 9:16 Video Story Card"))
                            Toast.makeText(context, "9:16 Story Card Rendered Successfully!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_story_card_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = DeepMidnight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export & Share 9:16 Story Card", color = DeepMidnight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
