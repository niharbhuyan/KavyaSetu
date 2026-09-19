package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight

/**
 * Aesthetic Watermark badge displaying the Kavya Setu App Logo and Name.
 * Suitable for screen footers, card corners, and full-app overlays.
 */
@Composable
fun AppWatermarkBadge(
    modifier: Modifier = Modifier,
    logoSize: Dp = 18.dp,
    textColor: Color = AntiqueGold,
    alpha: Float = 0.85f,
    showSubtext: Boolean = true
) {
    Surface(
        modifier = modifier.alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        color = DeepMidnight.copy(alpha = 0.75f),
        border = BorderStroke(0.8.dp, AntiqueGold.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = DeepMidnight,
                border = BorderStroke(0.6.dp, AntiqueGold.copy(alpha = 0.8f)),
                modifier = Modifier.size(logoSize)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Kavya Setu Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Column {
                Text(
                    text = "Kavya Setu • काव्यसेतु",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 0.3.sp
                )
                if (showSubtext) {
                    Text(
                        text = "କାବ୍ୟସେତୁ • Built by Nihar Sales",
                        fontSize = 8.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

/**
 * Subtle watermark overlay for entire screens or preview containers.
 * Places a soft signature badge at the bottom-end of the container.
 */
@Composable
fun FullAppWatermarkOverlay(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomEnd,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        AppWatermarkBadge(
            modifier = Modifier
                .align(alignment)
                .padding(end = 12.dp, bottom = 8.dp),
            logoSize = 16.dp,
            alpha = 0.65f,
            showSubtext = false
        )
    }
}
