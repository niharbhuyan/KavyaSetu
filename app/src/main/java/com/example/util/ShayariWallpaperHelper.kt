package com.example.util

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Shayari
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * High-Resolution AMOLED Wallpaper Generator & Lock Screen Customizer for Kavya Setu.
 */
object ShayariWallpaperHelper {
    private const val TAG = "ShayariWallpaperHelper"
    private const val WALLPAPER_WIDTH = 1080
    private const val WALLPAPER_HEIGHT = 2400

    /**
     * Generates a 1080x2400 True-Black AMOLED Wallpaper Bitmap featuring poetic typography,
     * golden Islamic/Kalinga ornamental borders, poet's takhallus, and translation.
     */
    fun generateWallpaper(context: Context, shayari: Shayari): Bitmap {
        val bitmap = Bitmap.createBitmap(WALLPAPER_WIDTH, WALLPAPER_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. True Black AMOLED Background with Deep Royal Vignette
        val bgPaint = Paint().apply {
            shader = RadialGradient(
                WALLPAPER_WIDTH / 2f,
                WALLPAPER_HEIGHT * 0.45f,
                WALLPAPER_HEIGHT * 0.65f,
                intArrayOf(Color.parseColor("#180D26"), Color.parseColor("#0C0614"), Color.parseColor("#000000")),
                floatArrayOf(0.0f, 0.5f, 1.0f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, WALLPAPER_WIDTH.toFloat(), WALLPAPER_HEIGHT.toFloat(), bgPaint)

        // 2. Ornamental Double Gold Borders
        val borderPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }

        val innerBorderPaint = Paint().apply {
            color = Color.parseColor("#66D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }

        val marginOuter = 40f
        val marginInner = 56f
        canvas.drawRoundRect(RectF(marginOuter, marginOuter, WALLPAPER_WIDTH - marginOuter, WALLPAPER_HEIGHT - marginOuter), 24f, 24f, borderPaint)
        canvas.drawRoundRect(RectF(marginInner, marginInner, WALLPAPER_WIDTH - marginInner, WALLPAPER_HEIGHT - marginInner), 16f, 16f, innerBorderPaint)

        // Corner Ornamental Accents
        val cornerPaint = Paint().apply {
            color = Color.parseColor("#F3E5AB")
            isAntiAlias = true
        }
        val cornerRadius = 6f
        canvas.drawCircle(marginInner, marginInner, cornerRadius, cornerPaint)
        canvas.drawCircle(WALLPAPER_WIDTH - marginInner, marginInner, cornerRadius, cornerPaint)
        canvas.drawCircle(marginInner, WALLPAPER_HEIGHT - marginInner, cornerRadius, cornerPaint)
        canvas.drawCircle(WALLPAPER_WIDTH - marginInner, WALLPAPER_HEIGHT - marginInner, cornerRadius, cornerPaint)

        // 3. Header Crest / Arch
        val crestPaint = TextPaint().apply {
            color = Color.parseColor("#D4AF37")
            textSize = 34f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("✦  K A V Y A   S E T U  ✦", WALLPAPER_WIDTH / 2f, 160f, crestPaint)

        val subCrestPaint = TextPaint().apply {
            color = Color.parseColor("#A09070")
            textSize = 24f
            typeface = Typeface.SERIF
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("काव्यसेतु • दीवान-ए-ग़ज़ल", WALLPAPER_WIDTH / 2f, 205f, subCrestPaint)

        // 4. Poetry Verses (Main Sher)
        val textPaint = TextPaint().apply {
            color = Color.parseColor("#FFF8E7")
            textSize = 58f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
            setShadowLayer(8f, 0f, 4f, Color.parseColor("#80000000"))
        }

        val contentWidth = WALLPAPER_WIDTH - 200
        val textLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(shayari.lines, 0, shayari.lines.length, textPaint, contentWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(24f, 1.4f)
                .setIncludePad(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(shayari.lines, textPaint, contentWidth, Layout.Alignment.ALIGN_CENTER, 1.4f, 24f, true)
        }

        val textTop = WALLPAPER_HEIGHT * 0.38f - (textLayout.height / 2f)
        canvas.save()
        canvas.translate(100f, textTop)
        textLayout.draw(canvas)
        canvas.restore()

        // 5. Poet's Takhallus (Pen Name)
        val poetPaint = TextPaint().apply {
            color = Color.parseColor("#F3E5AB")
            textSize = 42f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val poetY = textTop + textLayout.height + 70f
        canvas.drawText("— ${shayari.poet} —", WALLPAPER_WIDTH / 2f, poetY, poetPaint)

        // 6. English Poetic Meaning
        if (shayari.translationEnglish.isNotBlank()) {
            val transPaint = TextPaint().apply {
                color = Color.parseColor("#D0C4B0")
                textSize = 32f
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                isAntiAlias = true
            }
            val transLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(shayari.translationEnglish, 0, shayari.translationEnglish.length, transPaint, contentWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(14f, 1.3f)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(shayari.translationEnglish, transPaint, contentWidth, Layout.Alignment.ALIGN_CENTER, 1.3f, 14f, true)
            }
            canvas.save()
            canvas.translate(100f, poetY + 50f)
            transLayout.draw(canvas)
            canvas.restore()
        }

        // 7. Shahi Mohar Seal Stamp
        val sealY = WALLPAPER_HEIGHT - 220f
        val sealRadius = 55f
        val sealPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            isAntiAlias = true
        }
        canvas.drawCircle(WALLPAPER_WIDTH / 2f, sealY, sealRadius, sealPaint)
        canvas.drawCircle(WALLPAPER_WIDTH / 2f, sealY, sealRadius - 6f, Paint().apply {
            color = Color.parseColor("#66D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        })

        val sealTextPaint = TextPaint().apply {
            color = Color.parseColor("#F3E5AB")
            textSize = 20f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("शाही मोहर", WALLPAPER_WIDTH / 2f, sealY - 4f, sealTextPaint)
        canvas.drawText("काव्य", WALLPAPER_WIDTH / 2f, sealY + 22f, sealTextPaint)

        // 8. Footer Watermark
        val footerPaint = TextPaint().apply {
            color = Color.parseColor("#806650")
            textSize = 22f
            typeface = Typeface.SERIF
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Crafted for Poetry Lovers • Kavya Setu", WALLPAPER_WIDTH / 2f, WALLPAPER_HEIGHT - 90f, footerPaint)

        return bitmap
    }

    /**
     * Sets the generated bitmap as device wallpaper (Lock screen, Home screen, or Both).
     */
    suspend fun applyWallpaper(context: Context, bitmap: Bitmap, flag: Int): Boolean = withContext(Dispatchers.IO) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, flag)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error applying wallpaper: ${e.message}", e)
            false
        }
    }

    /**
     * Saves the generated wallpaper bitmap to device Pictures / MediaStore gallery.
     */
    suspend fun saveToGallery(context: Context, bitmap: Bitmap, title: String): Uri? = withContext(Dispatchers.IO) {
        val filename = "KavyaSetu_Wallpaper_${System.currentTimeMillis()}.png"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KavyaSetu")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext null
                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KavyaSetu")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save wallpaper to gallery: ${e.message}", e)
            null
        }
    }

    /**
     * Shares the generated wallpaper bitmap via Android Intent.
     */
    suspend fun shareWallpaper(context: Context, bitmap: Bitmap, shayari: Shayari) = withContext(Dispatchers.IO) {
        try {
            val cachePath = File(context.cacheDir, "wallpapers").apply { mkdirs() }
            val file = File(cachePath, "KavyaSetu_Wallpaper_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "✨ '${shayari.lines}' — ${shayari.poet}\n\nAMOLED Wallpaper generated via Kavya Setu • काव्यसेतु\nhttps://play.google.com/store/apps/details?id=com.niharsales.kavyasetu")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share AMOLED Wallpaper").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share wallpaper: ${e.message}", e)
        }
    }
}
