package com.example.ui.components

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.util.ShayariWallpaperHelper
import kotlinx.coroutines.launch

@Composable
fun WallpaperStudioDialog(
    shayari: Shayari,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var wallpaperBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isGenerating by remember { mutableStateOf(true) }
    var isApplying by remember { mutableStateOf(false) }

    LaunchedEffect(shayari) {
        isGenerating = true
        val bmp = ShayariWallpaperHelper.generateWallpaper(context, shayari)
        wallpaperBitmap = bmp
        isGenerating = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .testTag("wallpaper_studio_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AMOLED Poetry Wallpaper",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "High-Res Lock Screen & Home Screen Art",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wallpaper Preview Frame (Aspect Ratio 9:18 for mobile mockup)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .aspectRatio(9f / 18f)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color.Black,
                    border = BorderStroke(2.dp, AntiqueGold.copy(alpha = 0.6f))
                ) {
                    if (isGenerating || wallpaperBitmap == null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AntiqueGold)
                        }
                    } else {
                        Image(
                            bitmap = wallpaperBitmap!!.asImageBitmap(),
                            contentDescription = "Wallpaper Preview",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Controls
                Text(
                    text = "Apply Wallpaper:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AntiqueGold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Lock Screen
                    Button(
                        onClick = {
                            wallpaperBitmap?.let { bmp ->
                                isApplying = true
                                scope.launch {
                                    val success = ShayariWallpaperHelper.applyWallpaper(context, bmp, WallpaperManager.FLAG_LOCK)
                                    isApplying = false
                                    if (success) {
                                        Toast.makeText(context, "🔒 Lock Screen wallpaper updated successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Wallpaper set action completed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = DeepMidnight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock Screen", fontSize = 11.sp, color = DeepMidnight, fontWeight = FontWeight.Bold)
                    }

                    // Home Screen
                    OutlinedButton(
                        onClick = {
                            wallpaperBitmap?.let { bmp ->
                                isApplying = true
                                scope.launch {
                                    val success = ShayariWallpaperHelper.applyWallpaper(context, bmp, WallpaperManager.FLAG_SYSTEM)
                                    isApplying = false
                                    if (success) {
                                        Toast.makeText(context, "🏠 Home Screen wallpaper updated successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Wallpaper set action completed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Home Screen", fontSize = 11.sp, color = AntiqueGold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save to Gallery
                    OutlinedButton(
                        onClick = {
                            wallpaperBitmap?.let { bmp ->
                                scope.launch {
                                    val uri = ShayariWallpaperHelper.saveToGallery(context, bmp, shayari.poet)
                                    if (uri != null) {
                                        Toast.makeText(context, "💾 Saved high-res wallpaper to Gallery!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to save wallpaper", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Image", fontSize = 11.sp)
                    }

                    // Share
                    OutlinedButton(
                        onClick = {
                            wallpaperBitmap?.let { bmp ->
                                scope.launch {
                                    ShayariWallpaperHelper.shareWallpaper(context, bmp, shayari)
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Art", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
