package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DarkGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.MysticTealSoft
import com.example.ui.theme.RoyalPlum
import com.example.ui.theme.SoftGold
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Metric helper describing the length and estimated reading commitment of a poem.
 */
data class PoemReadingMetrics(
    val lineCount: Int,
    val coupletCount: Int,
    val wordCount: Int,
    val estimatedSeconds: Int,
    val isLongNazmOrGhazal: Boolean
) {
    val formattedReadTime: String
        get() = if (estimatedSeconds < 45) {
            "~30 sec read"
        } else {
            "~${ceil(estimatedSeconds / 60.0).toInt()} min read"
        }
}

/**
 * Computes metrics for poetry reading based on contemplative cadence (approx 1.5s per line).
 */
fun calculatePoemReadingMetrics(poemText: String, translationText: String = ""): PoemReadingMetrics {
    val cleanLines = poemText.lines().map { it.trim() }.filter { it.isNotEmpty() }
    val lineCount = maxOf(1, cleanLines.size)
    val coupletCount = maxOf(1, ceil(lineCount / 2.0).toInt())

    val poemWords = poemText.split("\\s+".toRegex()).count { it.isNotBlank() }
    val translationWords = translationText.split("\\s+".toRegex()).count { it.isNotBlank() }
    val totalWords = poemWords + translationWords

    // Classical contemplative poetry reading speed (~50 words per minute + meter appreciation)
    val readSeconds = maxOf(25, (totalWords * 1.2).toInt() + (lineCount * 3))
    val isLong = lineCount >= 4 || totalWords >= 40

    return PoemReadingMetrics(
        lineCount = lineCount,
        coupletCount = coupletCount,
        wordCount = totalWords,
        estimatedSeconds = readSeconds,
        isLongNazmOrGhazal = isLong
    )
}

/**
 * A visual 'Reading Progress' bar pinned at the top of poetry reader screens.
 * Tracks how much of a long nazm or ghazal the reader has finished.
 * Features:
 * - Real-time scroll/reading progress percentage
 * - Animated gradient progress track (AntiqueGold -> VelvetRose -> MysticTeal)
 * - Estimated reading time and couplet/verse counter
 * - Integrated Share button triggering Android native Share Intent
 * - Completion celebration pill ("✨ Khatam / Finished")
 * - Jump to top shortcut
 */
@Composable
fun PoetryReadingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    poemMetrics: PoemReadingMetrics? = null,
    poemTypeLabel: String = "Nazm / Ghazal",
    onResetToTop: (() -> Unit)? = null,
    onPoemFinished: (() -> Unit)? = null,
    onSharePoem: (() -> Unit)? = null,
    onCopyPoem: (() -> Unit)? = null
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "poetryReadingProgress"
    )

    val percentInt = (animatedProgress * 100).roundToInt()
    val isFinished = clampedProgress >= 0.92f

    var hasNotifiedFinished by remember { mutableStateOf(false) }
    LaunchedEffect(isFinished) {
        if (isFinished && !hasNotifiedFinished) {
            hasNotifiedFinished = true
            onPoemFinished?.invoke()
        }
    }

    val progressGradient = Brush.horizontalGradient(
        colors = listOf(
            AntiqueGold,
            VelvetRose,
            MysticTealSoft
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("poetry_reading_progress_bar"),
        color = RoyalPlum.copy(alpha = 0.95f),
        border = BorderStroke(0.8.dp, AntiqueGold.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Informational Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Progress Icon + Percent or Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFinished) MysticTeal.copy(alpha = 0.25f)
                                else AntiqueGold.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFinished) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Finished Reading",
                                tint = MysticTealSoft,
                                modifier = Modifier.size(15.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Reading",
                                tint = AntiqueGold,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isFinished) "Finished Reading" else "Reading Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFinished) MysticTealSoft else AntiqueGold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isFinished) MysticTeal.copy(alpha = 0.2f) else DeepMidnight,
                        border = BorderStroke(
                            0.5.dp,
                            if (isFinished) MysticTealSoft.copy(alpha = 0.5f) else AntiqueGold.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "$percentInt%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFinished) MysticTealSoft else SoftGold,
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("reading_progress_percent_text")
                        )
                    }
                }

                // Right: Reading metrics & quick share & jump
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (poemMetrics != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepMidnight.copy(alpha = 0.6f),
                            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MutedSlate,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = poemMetrics.formattedReadTime,
                                    fontSize = 10.sp,
                                    color = MutedSlate
                                )
                                if (poemMetrics.isLongNazmOrGhazal) {
                                    Text(
                                        text = "• ${poemMetrics.coupletCount} Ash'aar",
                                        fontSize = 10.sp,
                                        color = AntiqueGold.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Native Android Share Button in Reader Bar
                    if (onSharePoem != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(0.7.dp, AntiqueGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onSharePoem() }
                                .testTag("reader_header_share_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Poem to Social Apps",
                                    tint = SoftGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Share",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SoftGold
                                )
                            }
                        }
                    }

                    // Copy to Clipboard Button in Reader Bar
                    if (onCopyPoem != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(0.7.dp, AntiqueGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onCopyPoem() }
                                .testTag("reader_header_copy_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Poem to Clipboard",
                                    tint = SoftGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Copy",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SoftGold
                                )
                            }
                        }
                    }

                    if (onResetToTop != null && clampedProgress > 0.15f) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AntiqueGold.copy(alpha = 0.15f))
                                .clickable { onResetToTop() }
                                .padding(4.dp)
                                .testTag("btn_reading_reset_top"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Scroll to Top",
                                tint = AntiqueGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Visual Progress Track (Capsule with Gradient Fill & Head Glow)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(DeepMidnight.copy(alpha = 0.8f))
                    .border(0.4.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(3.dp))
            ) {
                // Animated Progress Fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(progressGradient)
                )

                // Stanza Milestone Markers for Long Nazms / Ghazals
                if (poemMetrics != null && poemMetrics.coupletCount > 1) {
                    val count = minOf(6, poemMetrics.coupletCount)
                    for (i in 1 until count) {
                        val fraction = i.toFloat() / count.toFloat()
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxWidth(fraction)
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(width = 1.5.dp, height = 4.dp)
                                    .background(Color.White.copy(alpha = 0.35f))
                            )
                        }
                    }
                }
            }

            // Completion Banner (Animated when completed)
            AnimatedVisibility(
                visible = isFinished,
                enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                exit = fadeOut(tween(200)) + scaleOut(tween(200))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ Nazm Khatam • Reading Goal Updated! (+1 Verse)",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MysticTealSoft
                    )
                }
            }
        }
    }
}

/**
 * ScrollState-aware convenience wrapper for [PoetryReadingProgressBar].
 * Dynamically binds to vertical scroll position of poem reader screen.
 */
@Composable
fun ScrollablePoetryReadingProgressBar(
    scrollState: ScrollState,
    shayari: Shayari,
    modifier: Modifier = Modifier,
    onPoemFinished: (() -> Unit)? = null,
    onSharePoem: (() -> Unit)? = null,
    onCopyPoem: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val readingProgress by remember {
        derivedStateOf {
            if (scrollState.maxValue > 0) {
                (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    val metrics = remember(shayari.lines, shayari.translationEnglish) {
        calculatePoemReadingMetrics(shayari.lines, shayari.translationEnglish)
    }

    PoetryReadingProgressBar(
        progress = readingProgress,
        modifier = modifier,
        poemMetrics = metrics,
        poemTypeLabel = if (metrics.isLongNazmOrGhazal) "Long Nazm" else "Ghazal / Couplet",
        onResetToTop = {
            coroutineScope.launch {
                scrollState.animateScrollTo(0)
            }
        },
        onPoemFinished = onPoemFinished,
        onSharePoem = onSharePoem,
        onCopyPoem = onCopyPoem
    )
}

/**
 * LazyListState-aware convenience wrapper for [PoetryReadingProgressBar].
 * Used in screens with lazy list item scrolling (e.g. TarannumMode, Chapter Readers).
 */
@Composable
fun LazyListPoetryReadingProgressBar(
    lazyListState: LazyListState,
    totalItems: Int,
    poemMetrics: PoemReadingMetrics? = null,
    modifier: Modifier = Modifier,
    onPoemFinished: (() -> Unit)? = null,
    onSharePoem: (() -> Unit)? = null,
    onCopyPoem: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val readingProgress by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (totalItems <= 1 || visibleItems.isEmpty()) {
                0f
            } else {
                val lastVisibleItem = visibleItems.last()
                (lastVisibleItem.index.toFloat() / (totalItems - 1).toFloat()).coerceIn(0f, 1f)
            }
        }
    }

    PoetryReadingProgressBar(
        progress = readingProgress,
        modifier = modifier,
        poemMetrics = poemMetrics,
        onResetToTop = {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(0)
            }
        },
        onPoemFinished = onPoemFinished,
        onSharePoem = onSharePoem,
        onCopyPoem = onCopyPoem
    )
}
