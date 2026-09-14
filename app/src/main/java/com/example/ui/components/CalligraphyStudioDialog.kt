package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.data.model.CalligraphyInk
import com.example.data.model.CalligraphyTexture
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import java.io.File
import java.io.FileOutputStream
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class DrawnStroke(
    val points: List<Offset>,
    val color: Color,
    val baseWidth: Float = 14f
)

@Composable
fun CalligraphyStudioDialog(
    initialVerse: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedInk by remember { mutableStateOf(CalligraphyInk.ZAFRAN_GOLD) }
    var selectedTexture by remember { mutableStateOf(CalligraphyTexture.TALA_PATRA) }
    var selectedTemplate by remember { mutableStateOf("इश्क़ (Ishq)") }
    var penWidth by remember { mutableStateOf(16f) }

    val strokes = remember { mutableStateListOf<DrawnStroke>() }
    var currentPoints = remember { mutableStateListOf<Offset>() }

    val tracingTemplates = listOf(
        "इश्क़ (Ishq)",
        "ख़्वाब (Khwab)",
        "محبت (Muhabbat)",
        "ପ୍ରେମ (Prema)",
        "କାବ୍ୟ (Kavya)",
        "نور (Noor)",
        "ଜୀବନ (Jeevana)",
        "Clear Guide"
    )

    fun shareCalligraphy() {
        try {
            val width = 800
            val height = 800
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val androidCanvas = android.graphics.Canvas(bitmap)

            // Background
            androidCanvas.drawColor(selectedTexture.bgColor.toArgb())

            // Ornate border
            val borderPaint = android.graphics.Paint().apply {
                color = selectedInk.color.toArgb()
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 6f
                isAntiAlias = true
            }
            androidCanvas.drawRect(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat(), borderPaint)
            borderPaint.strokeWidth = 2f
            androidCanvas.drawRect(32f, 32f, (width - 32).toFloat(), (height - 32).toFloat(), borderPaint)

            // Draw all strokes
            for (stroke in strokes) {
                if (stroke.points.size < 2) continue
                val strokePaint = android.graphics.Paint().apply {
                    color = stroke.color.toArgb()
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = stroke.baseWidth
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    strokeJoin = android.graphics.Paint.Join.ROUND
                    isAntiAlias = true
                }
                val path = android.graphics.Path()
                val p0 = stroke.points[0]
                path.moveTo(p0.x * (width / 400f), p0.y * (height / 400f))
                for (i in 1 until stroke.points.size) {
                    val pt = stroke.points[i]
                    path.lineTo(pt.x * (width / 400f), pt.y * (height / 400f))
                }
                androidCanvas.drawPath(path, strokePaint)
            }

            val cacheDir = File(context.cacheDir, "calligraphy_art")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, "Qalam_Art_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Handcrafted calligraphy created with Kavya Setu Qalam Studio.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Calligraphy Artwork"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.95f)
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
                            Text("📜", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Qalam & Lipi Studio • خطاطی",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AntiqueGold
                            )
                            Text(
                                text = "Nastaliq & Odia Palm-Leaf Calligraphy Canvas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_calligraphy_studio")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tracing Guide Carousel
                Text("Select Tracing Template:", style = MaterialTheme.typography.labelSmall, color = AntiqueGold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tracingTemplates) { tmpl ->
                        val isSelected = tmpl == selectedTemplate
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AntiqueGold else CharcoalElevated)
                                .clickable { selectedTemplate = tmpl }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = tmpl,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DeepMidnight else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Canvas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = selectedTexture.bgColor),
                    border = androidx.compose.foundation.BorderStroke(2.dp, selectedInk.color.copy(alpha = 0.7f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background watermark tracing guide
                        if (selectedTemplate != "Clear Guide") {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedTemplate.split(" ").first(),
                                    fontSize = 72.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color.Black.copy(alpha = 0.12f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Drawing Surface
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("calligraphy_canvas")
                                .pointerInput(selectedInk, penWidth) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentPoints.clear()
                                            currentPoints.add(offset)
                                        },
                                        onDrag = { change, _ ->
                                            currentPoints.add(change.position)
                                        },
                                        onDragEnd = {
                                            if (currentPoints.size > 1) {
                                                strokes.add(
                                                    DrawnStroke(
                                                        points = currentPoints.toList(),
                                                        color = selectedInk.color,
                                                        baseWidth = penWidth
                                                    )
                                                )
                                            }
                                            currentPoints.clear()
                                        }
                                    )
                                }
                        ) {
                            // Completed strokes
                            for (stroke in strokes) {
                                if (stroke.points.size < 2) continue
                                val path = Path()
                                path.moveTo(stroke.points[0].x, stroke.points[0].y)
                                for (i in 1 until stroke.points.size) {
                                    path.lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                                drawPath(
                                    path = path,
                                    color = stroke.color,
                                    style = Stroke(
                                        width = stroke.baseWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }

                            // Active stroke in progress
                            if (currentPoints.size > 1) {
                                val currentPath = Path()
                                currentPath.moveTo(currentPoints[0].x, currentPoints[0].y)
                                for (i in 1 until currentPoints.size) {
                                    currentPath.lineTo(currentPoints[i].x, currentPoints[i].y)
                                }
                                drawPath(
                                    path = currentPath,
                                    color = selectedInk.color,
                                    style = Stroke(
                                        width = penWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toolbar: Ink Palette & Texture
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Inks
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ink:", style = MaterialTheme.typography.labelSmall, color = AntiqueGold)
                        for (ink in CalligraphyInk.entries) {
                            val isSelected = ink == selectedInk
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(ink.color)
                                    .clickable { selectedInk = ink }
                                    .border(
                                        if (isSelected) 2.5.dp else 1.dp,
                                        if (isSelected) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                            )
                        }
                    }

                    // Actions: Undo, Clear
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) },
                            enabled = strokes.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo", tint = AntiqueGold)
                        }
                        IconButton(
                            onClick = { strokes.clear() },
                            enabled = strokes.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = VelvetRose)
                        }
                    }
                }

                // Textures Row & Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(CalligraphyTexture.entries) { tex ->
                            val isSelected = tex == selectedTexture
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AntiqueGold.copy(alpha = 0.3f) else CharcoalElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) AntiqueGold else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedTexture = tex }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(tex.displayName, fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { shareCalligraphy() },
                        colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("export_calligraphy_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Art", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
