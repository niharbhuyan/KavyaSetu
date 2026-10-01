package com.example.ads

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Lightweight Ad Management & House Banner Engine for Kavya Setu (काव्यसेतु).
 * Decoupled from heavy native adservices/measurement service binding to ensure
 * smooth, crash-free execution in all virtualized and hardware environments.
 */
object AdMobManager {
    private const val TAG = "AdMobManager"

    // Publisher ID: ca-app-pub-4880243637225183
    const val PUBLISHER_ID = "ca-app-pub-4880243637225183"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val PRODUCTION_BANNER_AD_UNIT_ID = "ca-app-pub-4880243637225183/8892147365"

    private var isInitialized = false

    val isRunningInEmulator: Boolean by lazy {
        BuildConfig.DEBUG ||
            Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk", ignoreCase = true) ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MODEL.contains("Android SDK built for x86", ignoreCase = true) ||
            Build.MODEL.contains("Cuttlefish", ignoreCase = true) ||
            Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||
            Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
            Build.HARDWARE.contains("cutf", ignoreCase = true) ||
            Build.HARDWARE.contains("vsoc", ignoreCase = true) ||
            Build.PRODUCT.contains("sdk", ignoreCase = true) ||
            Build.PRODUCT.contains("google_sdk", ignoreCase = true) ||
            Build.PRODUCT.contains("cf_", ignoreCase = true) ||
            Build.PRODUCT.contains("cvd", ignoreCase = true) ||
            Build.PRODUCT.contains("vbox", ignoreCase = true) ||
            Build.BOARD.contains("goldfish", ignoreCase = true) ||
            Build.BOARD.contains("cutf", ignoreCase = true) ||
            Build.HARDWARE.contains("emu", ignoreCase = true) ||
            Build.HARDWARE.contains("qemu", ignoreCase = true) ||
            Build.BRAND.startsWith("generic", ignoreCase = true) ||
            Build.DEVICE.startsWith("generic", ignoreCase = true) ||
            Build.DEVICE.contains("emulator", ignoreCase = true) ||
            Build.PRODUCT.contains("emulator", ignoreCase = true) ||
            Build.FINGERPRINT.contains("test-keys") ||
            Build.HOST.contains("android-build", ignoreCase = true) ||
            (Build.MANUFACTURER.contains("Google", ignoreCase = true) && Build.DEVICE.contains("emu", ignoreCase = true))
    }

    fun initialize(context: Context) {
        if (isInitialized) return
        Log.d(TAG, "AdMobManager initialized (clean zero-adservice mode).")
        isInitialized = true
    }

    /**
     * Elegant Non-Intrusive Banner Composable.
     */
    @Composable
    fun BannerAd(
        modifier: Modifier = Modifier,
        adUnitId: String = TEST_BANNER_AD_UNIT_ID
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(BorderStroke(0.5.dp, Color(0xFFD4AF37).copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                .testTag("kavya_banner_ad"),
            color = Color(0xFF0F172A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "✨ Kavya Setu • Rekhta, Ghalib & Odia Classics",
                    color = Color(0xFFD4AF37),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "AD",
                    color = Color.Gray,
                    fontSize = 9.sp,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.TEST_BANNER_AD_UNIT_ID
) {
    AdMobManager.BannerAd(modifier = modifier, adUnitId = adUnitId)
}
