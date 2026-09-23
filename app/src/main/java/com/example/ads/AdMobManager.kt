package com.example.ads

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
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
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu") ||
            Build.PRODUCT.contains("sdk") ||
            Build.PRODUCT.contains("google_sdk")
    }

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob MobileAds initialized successfully: $status")
                isInitialized = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob MobileAds", e)
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

    if (isInspection) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF22162B)),
            contentAlignment = Alignment.Center
        ) {
            Text("AdMob Banner Preview", color = Color(0xFFD4AF37), fontSize = 12.sp)
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
                    // In emulators or container runtimes lacking hardware rendernodes,
                    // software layer prevents Mesa from trying to open /dev/dri/renderD128
                    if (AdMobManager.isRunningInEmulator) {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    }
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
