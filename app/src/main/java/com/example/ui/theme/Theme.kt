package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AntiqueGold,
    onPrimary = DeepMidnight,
    primaryContainer = Color(0xFF3B2E10),
    onPrimaryContainer = SoftGold,
    secondary = VelvetRose,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A1024),
    onSecondaryContainer = RoseSoft,
    tertiary = MysticTealSoft,
    background = DeepMidnight,
    onBackground = IvoryWhite,
    surface = RoyalPlum,
    onSurface = IvoryWhite,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = MutedSlate,
    outline = SurfaceCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGold,
    onPrimary = Color.White,
    primaryContainer = SoftGold,
    onPrimaryContainer = Color(0xFF281C03),
    secondary = VelvetRose,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = Color(0xFF3E0018),
    tertiary = MysticTeal,
    background = ParchmentLight,
    onBackground = ParchmentText,
    surface = ParchmentSurface,
    onSurface = ParchmentText,
    surfaceVariant = Color(0xFFEBE3D3),
    onSurfaceVariant = Color(0xFF5E5649),
    outline = Color(0xFFCFC4B2)
)

@Composable
fun ShayariTheme(
    darkTheme: Boolean = true, // Default to poetic luxury dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val raatMehfilConfig by com.example.data.local.RaatMehfilManager.themeConfig.collectAsState()
    val isNocturnal = darkTheme || raatMehfilConfig.isNightModeForced || raatMehfilConfig.midnightHourActive

    val baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isNocturnal) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isNocturnal -> DarkColorScheme
        else -> LightColorScheme
    }

    val finalColorScheme = if (raatMehfilConfig.amberCandleGlow) {
        baseScheme.copy(
            primary = Color(0xFFF3D279),
            background = Color(0xFF140F0A),
            surface = Color(0xFF1E1712),
            surfaceVariant = Color(0xFF2B2019)
        )
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = finalColorScheme,
        typography = Typography,
        content = content
    )
}

// Keep alias for compatibility with template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    ShayariTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
