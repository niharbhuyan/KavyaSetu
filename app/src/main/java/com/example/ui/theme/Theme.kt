package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
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
