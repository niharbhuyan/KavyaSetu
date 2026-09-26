package com.example.ads

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
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
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Publisher ID: ca-app-pub-4880243637225183
    const val PUBLISHER_ID = "ca-app-pub-4880243637225183"

    // Google's official sample/test Banner Ad Unit ID
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Production-ready Banner Unit
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
        if (isRunningInEmulator) {
            Log.d(TAG, "Running in virtualized/emulator environment; bypassing measurement service binding.")
            isInitialized = true
            return
        }
        try {
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob MobileAds initialized successfully: $status")
                isInitialized = true
            }
        } catch (e: Throwable) {
            Log.w(TAG, "AdMob initialization bypassed or unavailable: ${e.message}")
            isInitialized = true
        }
    }
}

/**
 * Standard AdMob Banner Composable for Kavya Setu
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.TEST_BANNER_AD_UNIT_ID
) {
    val isInspection = LocalInspectionMode.current

    if (isInspection || AdMobManager.isRunningInEmulator) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF190F24).copy(alpha = 0.7f))
                .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("admob_banner_container"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFD4AF37).copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, Color(0xFFD4AF37).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Ad",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Text(
                    text = "Kavya Setu • Cultural Poetry & Arts Hub",
                    color = Color(0xFFD4AF37).copy(alpha = 0.85f),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF190F24).copy(alpha = 0.7f))
            .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(vertical = 4.dp)
            .testTag("admob_banner_container"),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("AdMobBanner", "Banner ad loaded successfully")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.w("AdMobBanner", "Banner ad failed to load: ${error.message} (code: ${error.code})")
                        }
                    }
                    val request = AdRequest.Builder().build()
                    loadAd(request)
                }
            }
        )
    }
}
