package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.model.DiwanConfig
import com.example.data.model.DiwanCoverStyle
import com.example.data.model.Shayari
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DiwanPdfGenerator {

    suspend fun generateDiwanPdf(
        context: Context,
        config: DiwanConfig,
        shayaris: List<Shayari>
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val pdfDoc = PdfDocument()
            val pageWidth = 595 // Standard A4 points
            val pageHeight = 842
            var pageNumber = 1

            // 1. Title / Cover Page
            val coverPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            val coverPage = pdfDoc.startPage(coverPageInfo)
            drawCoverPage(context, coverPage.canvas, pageWidth, pageHeight, config)
            pdfDoc.finishPage(coverPage)

            // 2. Dedication Page
            if (config.dedication.isNotBlank()) {
                val dedPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
                val dedPage = pdfDoc.startPage(dedPageInfo)
                drawDedicationPage(dedPage.canvas, pageWidth, pageHeight, config)
                pdfDoc.finishPage(dedPage)
            }

            // 3. Couplet Pages (2 couplets per page for spacious, elegant layout)
            val chunkedShayaris = shayaris.chunked(2)
            for ((chunkIndex, chunk) in chunkedShayaris.withIndex()) {
                val versePageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
                val versePage = pdfDoc.startPage(versePageInfo)
                drawCoupletsPage(
                    context = context,
                    canvas = versePage.canvas,
                    w = pageWidth,
                    h = pageHeight,
                    config = config,
                    chunk = chunk,
                    pageNumber = pageNumber - 1,
                    totalVersePages = chunkedShayaris.size + (if (config.dedication.isNotBlank()) 2 else 1)
                )
                pdfDoc.finishPage(versePage)
            }

            // Write out to cache directory
            val outputDir = File(context.cacheDir, "diwan_books")
            if (!outputDir.exists()) outputDir.mkdirs()
            val cleanTitle = config.bookTitle.replace(Regex("[^a-zA-Z0-9_]"), "_").take(24)
            val fileName = "Diwan_${cleanTitle}_${System.currentTimeMillis()}.pdf"
            val file = File(outputDir, fileName)

            FileOutputStream(file).use { out ->
                pdfDoc.writeTo(out)
            }
            pdfDoc.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun drawCoverPage(context: Context, canvas: Canvas, w: Int, h: Int, config: DiwanConfig) {
        val bgPaint = Paint().apply {
            color = when (config.coverStyle) {
                DiwanCoverStyle.ROYAL_VELVET -> Color.rgb(42, 16, 23)
                DiwanCoverStyle.MIDNIGHT_INDIGO -> Color.rgb(13, 27, 42)
                DiwanCoverStyle.ODIA_PALM_LEAF -> Color.rgb(61, 46, 30)
                else -> Color.rgb(247, 242, 231) // Mughal Parchment
            }
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val accentColor = when (config.coverStyle) {
            DiwanCoverStyle.ROYAL_VELVET -> Color.rgb(224, 187, 104)
            DiwanCoverStyle.MIDNIGHT_INDIGO -> Color.rgb(224, 225, 221)
            DiwanCoverStyle.ODIA_PALM_LEAF -> Color.rgb(232, 200, 139)
            else -> Color.rgb(180, 140, 60)
        }

        val borderPaint = Paint().apply {
            color = accentColor
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }

        // Double ornate border
        canvas.drawRect(24f, 24f, w - 24f, h - 24f, borderPaint)
        borderPaint.strokeWidth = 1f
        canvas.drawRect(32f, 32f, w - 32f, h - 32f, borderPaint)

        // Title Paint
        val titlePaint = Paint().apply {
            color = accentColor
            textSize = 28f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = accentColor
            alpha = 210
            textSize = 15f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val authorPaint = Paint().apply {
            color = accentColor
            textSize = 18f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = accentColor
            alpha = 180
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        var cy = h * 0.28f
        canvas.drawText("✦ ✧ ✦", w / 2f, cy, titlePaint)

        cy += 50f
        canvas.drawText(config.bookTitle, w / 2f, cy, titlePaint)

        if (config.subtitle.isNotBlank()) {
            cy += 30f
            canvas.drawText(config.subtitle, w / 2f, cy, subtitlePaint)
        }

        cy += 40f
        canvas.drawText("— ❦ —", w / 2f, cy, subtitlePaint)

        cy += 70f
        canvas.drawText("By Poet", w / 2f, cy - 20f, subtitlePaint)
        canvas.drawText(config.poetName, w / 2f, cy + 10f, authorPaint)

        if (config.takhallus.isNotBlank() && config.includeTakhallusSeal) {
            cy += 80f
            drawTakhallusSeal(canvas, w / 2f, cy, config.takhallus, accentColor)
        }

        val dateStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Diwan Collection • Published $dateStr", w / 2f, h - 86f, footerPaint)

        // Draw App Logo and Name Watermark on Cover
        try {
            val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.app_logo)
            if (logoBmp != null) {
                val logoSize = 28
                val scaled = Bitmap.createScaledBitmap(logoBmp, logoSize, logoSize, true)
                canvas.drawBitmap(scaled, (w / 2f) - (logoSize / 2f), h - 68f, null)
            }
        } catch (ignored: Exception) {}

        val watermarkPaint = Paint().apply {
            color = accentColor
            alpha = 230
            textSize = 10.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Kavya Setu • काव्यसेतु • କାବ୍ୟସେତୁ (Built by Nihar Sales)", w / 2f, h - 28f, watermarkPaint)
    }

    private fun drawDedicationPage(canvas: Canvas, w: Int, h: Int, config: DiwanConfig) {
        val bgPaint = Paint().apply { color = Color.rgb(250, 248, 242) }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val borderPaint = Paint().apply {
            color = Color.rgb(200, 170, 110)
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        canvas.drawRect(36f, 36f, w - 36f, h - 36f, borderPaint)

        val headingPaint = Paint().apply {
            color = Color.rgb(80, 50, 20)
            textSize = 20f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(60, 40, 20)
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        var cy = h * 0.35f
        canvas.drawText("Dedication", w / 2f, cy, headingPaint)
        cy += 20f
        canvas.drawText("— ✧ —", w / 2f, cy, headingPaint)
        cy += 45f

        val lines = config.dedication.split("\n")
        for (line in lines) {
            canvas.drawText(line.trim(), w / 2f, cy, bodyPaint)
            cy += 24f
        }
    }

    private fun drawCoupletsPage(
        context: Context,
        canvas: Canvas,
        w: Int,
        h: Int,
        config: DiwanConfig,
        chunk: List<Shayari>,
        pageNumber: Int,
        totalVersePages: Int
    ) {
        val bgPaint = Paint().apply { color = Color.rgb(252, 250, 245) }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val borderPaint = Paint().apply {
            color = Color.rgb(215, 185, 125)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
            isAntiAlias = true
        }
        canvas.drawRect(32f, 32f, w - 32f, h - 32f, borderPaint)

        // Header and Footer
        val headerPaint = Paint().apply {
            color = Color.rgb(150, 120, 80)
            textSize = 9f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText(config.bookTitle.uppercase(), 44f, 50f, headerPaint)

        // Watermark on page bottom
        val watermarkPaint = Paint().apply {
            color = Color.rgb(160, 130, 85)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("Kavya Setu • काव्यसेतु (Built by Nihar Sales)", 44f, h - 45f, watermarkPaint)

        // Draw small app logo next to watermark text
        try {
            val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.app_logo)
            if (logoBmp != null) {
                val logoSize = 14
                val scaled = Bitmap.createScaledBitmap(logoBmp, logoSize, logoSize, true)
                canvas.drawBitmap(scaled, 235f, h - 56f, null)
            }
        } catch (ignored: Exception) {}

        val pageNumPaint = Paint().apply {
            color = Color.rgb(150, 120, 80)
            textSize = 9f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("Page $pageNumber", w - 44f, h - 45f, pageNumPaint)

        val coupletPaint = Paint().apply {
            color = Color.rgb(30, 25, 20)
            textSize = 15f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val translationPaint = Paint().apply {
            color = Color.rgb(90, 75, 60)
            textSize = 11.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val metaPaint = Paint().apply {
            color = Color.rgb(170, 130, 70)
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Draw up to two couplets per page
        val sectionHeights = listOf(h * 0.28f, h * 0.65f)
        for ((idx, shayari) in chunk.withIndex()) {
            var yPos = sectionHeights.getOrElse(idx) { h * 0.5f }

            canvas.drawText("— ❦ —", w / 2f, yPos - 35f, metaPaint)

            // Draw couplet lines
            val lines = shayari.lines.lines().filter { it.isNotBlank() }
            for (line in lines) {
                canvas.drawText(line.trim(), w / 2f, yPos, coupletPaint)
                yPos += 24f
            }

            // Author & Pen name
            yPos += 12f
            val authorLabel = if (shayari.penName.isNotBlank()) {
                "~ ${shayari.author} '${shayari.penName}' ~"
            } else {
                "~ ${shayari.author} ~"
            }
            canvas.drawText(authorLabel, w / 2f, yPos, metaPaint)

            // Optional English / Odia Translation
            if (config.includeTranslations && shayari.translationEnglish.isNotBlank()) {
                yPos += 22f
                val transLines = shayari.translationEnglish.lines().filter { it.isNotBlank() }
                for (tLine in transLines.take(2)) {
                    canvas.drawText("“${tLine.trim()}”", w / 2f, yPos, translationPaint)
                    yPos += 18f
                }
            }
        }
    }

    private fun drawTakhallusSeal(canvas: Canvas, cx: Float, cy: Float, takhallus: String, colorInt: Int) {
        val radius = 34f
        val sealPaint = Paint().apply {
            color = colorInt
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawCircle(cx, cy, radius, sealPaint)
        sealPaint.strokeWidth = 0.8f
        canvas.drawCircle(cx, cy, radius - 4f, sealPaint)

        val textPaint = Paint().apply {
            color = colorInt
            textSize = 10f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("तख़ल्लुस", cx, cy - 8f, textPaint)
        canvas.drawText(takhallus, cx, cy + 10f, textPaint)
    }

    fun sharePdf(context: Context, pdfFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Diwan Poetry Book: ${pdfFile.nameWithoutExtension}")
                putExtra(Intent.EXTRA_TEXT, "📖 Published Poetry Diwan compiled with Kavya Setu • काव्यसेतु • କାବ୍ୟସେତୁ (Built by Nihar Sales).\n#KavyaSetu #Diwan #PoetryCollection")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Poetry Diwan (PDF)"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
