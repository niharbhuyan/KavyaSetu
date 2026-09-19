package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

data class PlayStoreAssetItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val resolution: String,
    val drawableResId: Int,
    val fileName: String,
    val policyNotice: String
)

object PlayStoreAssetHelper {

    val STORE_TITLE = "Kavya Setu: Poetry & Shayari"
    val SHORT_DESCRIPTION = "Multilingual poetry & shayari in Hindi, Odia, English with Gemini AI & audio."
    val PRIVACY_POLICY_URL = "https://raw.githack.com/niharsales/kavyasetu/main/privacy_policy.html"
    val DEVELOPER_EMAIL = "niharbhuyan@gmail.com"
    val FULL_DESCRIPTION = """
Kavya Setu (काव्य सेतु / କାବ୍ୟ ସେତୁ) is an Indian multilingual poetry and shayari platform celebrating the rich cultural tapestry of Hindi Kavita, Urdu Shayari, and Odia Chhanda.

✨ KEY HIGHLIGHTS:
• Curated Classical & Modern Verses: Timeless poetry from Mirza Ghalib, Allama Iqbal, Ramdhari Singh Dinkar, Jaun Elia, and Kantakabi Laxmikanta Mohapatra.
• Tarannum & Spoken Recitations: High-fidelity audio renditions accompanied by ambient sitar and tanpura ragas.
• Gemini AI Poetry Studio: Compose original Ghazals, Nazms, Haikus, and Dohas with metrical guidance (Bahr, Radif, Kafiya).
• Calligraphy Card Studio: Customize aesthetic cards with antique parchment and royal velvet textures for social sharing.
• Offline Poetry Vault: Fully offline-capable bookmarking and collection management.
• Virtual Mehfil & Mushaira: Real-time poetic gatherings with Wah-Wah audience reactions and rhyming challenges.
• Daily Pick Widget & Morning Reminders: Discover a handpicked couplet on your home screen every morning.

🛡️ POLICY COMPLIANCE:
• No intrusive permissions. Zero-permission media exports.
• Offline-first data privacy. No ads or promotional hype.
• Meets Google Play Store Title & Metadata standards (Title <= 30 chars, Short Desc <= 80 chars).
""".trimIndent()

    val assets = listOf(
        PlayStoreAssetItem(
            id = "icon",
            title = "High-Res App Icon",
            subtitle = "Google Play Store App Logo & Listing Icon",
            resolution = "512 × 512 px (32-bit PNG)",
            drawableResId = R.drawable.img_play_store_icon,
            fileName = "kavya_setu_play_store_512_icon.png",
            policyNotice = "Google Play Compliant: 512x512 32-bit PNG, centered safe zone, no rounded mask pre-baked."
        ),
        PlayStoreAssetItem(
            id = "feature_graphic",
            title = "Play Store Feature Graphic",
            subtitle = "Banner Hero for Store Presence & Promotions",
            resolution = "1024 × 500 px (PNG)",
            drawableResId = R.drawable.img_play_store_feature_graphic,
            fileName = "kavya_setu_feature_graphic_1024x500.png",
            policyNotice = "Google Play Compliant: 1024x500 px, no promotional words (no '#1', 'Free', ratings), centered brand elements."
        ),
        PlayStoreAssetItem(
            id = "screenshot_feed",
            title = "Screenshot 1: Home Feed & Audio",
            subtitle = "Daily Pick couplet & Tarannum audio recital player",
            resolution = "1080 × 1920 px",
            drawableResId = R.drawable.img_screenshot_feed,
            fileName = "kavya_setu_screenshot_1_home_feed.png",
            policyNotice = "Demonstrates primary core user journey: curated feed, Hindi/Urdu/Odia filters, and audio playback."
        ),
        PlayStoreAssetItem(
            id = "screenshot_composer",
            title = "Screenshot 2: Gemini AI Studio",
            subtitle = "Meter analysis, Ghazal & Haiku poetic generation",
            resolution = "1080 × 1920 px",
            drawableResId = R.drawable.img_screenshot_composer,
            fileName = "kavya_setu_screenshot_2_ai_composer.png",
            policyNotice = "Illustrates genuine Gemini AI poetic meter breakdown (Bahr, Radif, Kafiya rhymes)."
        ),
        PlayStoreAssetItem(
            id = "screenshot_studio",
            title = "Screenshot 3: Card Studio & Vault",
            subtitle = "Calligraphy card designer & reading streaks",
            resolution = "1080 × 1920 px",
            drawableResId = R.drawable.img_screenshot_studio,
            fileName = "kavya_setu_screenshot_3_card_studio.png",
            policyNotice = "Highlights aesthetic card exports, reading habits dashboard, and offline bookmarks."
        ),
        PlayStoreAssetItem(
            id = "screenshot_mehfil",
            title = "Screenshot 4: Virtual Mehfil",
            subtitle = "Audience Wah-Wah reactions & Sher-baazi duel",
            resolution = "1080 × 1920 px",
            drawableResId = R.drawable.img_screenshot_mehfil,
            fileName = "kavya_setu_screenshot_4_virtual_mehfil.png",
            policyNotice = "Showcases interactive poetic symposiums, ambient acoustics, and rhyming competitions."
        )
    )

    /**
     * Saves a drawable resource directly into device's public Pictures/KavyaSetu_PlayStore folder
     * using MediaStore without requiring runtime storage permissions on Android 10+.
     */
    fun saveAssetToGallery(context: Context, item: PlayStoreAssetItem): Boolean {
        return try {
            val bitmap = BitmapFactory.decodeResource(context.resources, item.drawableResId)
                ?: return false

            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, item.fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/KavyaSetu_PlayStore")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                resolver.openOutputStream(imageUri).use { outStream ->
                    if (outStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                Toast.makeText(context, "Saved ${item.title} to Pictures/KavyaSetu_PlayStore", Toast.LENGTH_SHORT).show()
                true
            } else {
                // Fallback to cache/files dir
                saveToInternalFiles(context, item, bitmap)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not save asset: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun saveToInternalFiles(context: Context, item: PlayStoreAssetItem, bitmap: Bitmap) {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "KavyaSetu_PlayStore")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, item.fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        Toast.makeText(context, "Saved to ${file.name}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Saves all 6 visual assets to device storage.
     */
    fun saveAllAssetsToDevice(context: Context): Int {
        var count = 0
        for (item in assets) {
            if (saveAssetToGallery(context, item)) {
                count++
            }
        }
        Toast.makeText(
            context,
            "✨ All $count Play Store assets downloaded successfully to Pictures/KavyaSetu_PlayStore!",
            Toast.LENGTH_LONG
        ).show()
        return count
    }

    /**
     * Shares an individual asset via Android's native share sheet.
     */
    fun shareAsset(context: Context, item: PlayStoreAssetItem) {
        try {
            val bitmap = BitmapFactory.decodeResource(context.resources, item.drawableResId) ?: return
            val cacheDir = File(context.cacheDir, "play_store_shares").apply { if (!exists()) mkdirs() }
            val file = File(cacheDir, item.fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, item.title)
                putExtra(Intent.EXTRA_TEXT, "${item.title} (${item.resolution}) for Kavya Setu Play Store publication.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share ${item.title}"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing asset: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares all 6 assets simultaneously via Intent.ACTION_SEND_MULTIPLE.
     */
    fun shareAllAssets(context: Context) {
        try {
            val cacheDir = File(context.cacheDir, "play_store_shares").apply { if (!exists()) mkdirs() }
            val uris = ArrayList<Uri>()

            for (item in assets) {
                val bitmap = BitmapFactory.decodeResource(context.resources, item.drawableResId) ?: continue
                val file = File(cacheDir, item.fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                uris.add(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file))
            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/png"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                putExtra(Intent.EXTRA_SUBJECT, "Kavya Setu — Google Play Store Media Kit & Assets")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Kavya Setu Google Play Store Publication Package (App Icon, Feature Graphic, 4 Screenshots, Metadata)."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share All Play Store Assets"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing assets: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
