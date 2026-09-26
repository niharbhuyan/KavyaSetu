package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.delay

data class DastaanChapter(
    val title: String,
    val era: String,
    val poet: String,
    val narrativeExcerpt: String,
    val featuredVerse: String,
    val mood: String
)

@Composable
fun DastaangoiMehfilDialog(
    allShayaris: List<Shayari>,
    audioReciter: AudioReciter,
    ambientPlayer: AmbientSoundscapePlayer,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dastaanChapters = remember {
        listOf(
            DastaanChapter(
                title = "The Courtyard of Delhi • दिल्ली की आख़िरी शमा",
                era = "1854 CE • Mughal Twilight",
                poet = "Mirza Asadullah Khan Ghalib",
                narrativeExcerpt = "Chandni Chowk was shrouded in the mist of winter. As the candle moved towards Ghalib, he took a deep breath, adjusting his kulah cap...",
                featuredVerse = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले",
                mood = "Philosophical Melancholy"
            ),
            DastaanChapter(
                title = "The Fragrance of Lucknow • लखनऊ की रंगीन रातें",
                era = "1870 CE • Avadh Renaissance",
                poet = "Mir Babar Ali Anees & Mir Taqi Mir",
                narrativeExcerpt = "Along the banks of Gomti, poets sat on silk carpets under chandeliers of silver. A voice echoed from the heart of the mehfil...",
                featuredVerse = "पत्ता पत्ता बूटा बूटा हाल हमारा जाने है\nजाने न जाने गुल ही न जाने बाग़ तो सारा जाने है",
                mood = "Sublime Devotion & Romance"
            ),
            DastaanChapter(
                title = "The Breeze of Utkala • କବିସୂର୍ଯ୍ୟଙ୍କ ଚମ୍ପୂ ମହଫିଲ",
                era = "1830 CE • Classical Odisha Court",
                poet = "Kabisurya Baladev Ratha",
                narrativeExcerpt = "In the sunlit temple courtyards of Puri, the Champu lyrics blended with the sound of the mardala drum and fragrant champak flowers...",
                featuredVerse = "ଚମ୍ପୂର ମଧୁର ସ୍ୱରରେ ମୋହିତ ହୁଏ ହୃଦୟ ମୋର\nଭାବର ଗଭୀରତାରେ ପ୍ରେମର ପ୍ରକାଶ ଚିରନ୍ତନ",
                mood = "Rhythmic Classical Grace"
            )
        )
    }

    var selectedChapterIndex by remember { mutableIntStateOf(0) }
    val currentChapter = dastaanChapters[selectedChapterIndex]

    var isLiveDastaanPlaying by remember { mutableStateOf(false) }
    var audienceWahWahCount by remember { mutableIntStateOf(142) }
    var currentSpeakerLine by remember { mutableStateOf("Narrator is preparing the historical prologue...") }

    LaunchedEffect(isLiveDastaanPlaying, selectedChapterIndex) {
        if (isLiveDastaanPlaying) {
            ambientPlayer.play(AmbientSoundPreset.TANPURA)
            currentSpeakerLine = currentChapter.narrativeExcerpt
            audioReciter.speak(currentChapter.narrativeExcerpt, "hindi")
            delay(5500)
            if (isLiveDastaanPlaying) {
                currentSpeakerLine = "Now reciting the legendary couplet..."
                audioReciter.speak(currentChapter.featuredVerse, "hindi")
            }
        } else {
            audioReciter.stop()
            ambientPlayer.stop()
        }
    }

    Dialog(
        onDismissRequest = {
            audioReciter.stop()
            ambientPlayer.stop()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DeepMidnight,
            border = BorderStroke(1.2.dp, AntiqueGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎙️", fontSize = 24.sp)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dastaangoi Live Mehfil",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isLiveDastaanPlaying) VelvetRose else Color.DarkGray
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.FiberManualRecord,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(8.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isLiveDastaanPlaying) "ON AIR" else "READY",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Oral poetry storytelling theatre with live classical audio backdrop",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    IconButton(onClick = {
                        audioReciter.stop()
                        ambientPlayer.stop()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chapter Selection
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dastaanChapters.indices.toList()) { index ->
                        val chap = dastaanChapters[index]
                        val isSelected = index == selectedChapterIndex
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AntiqueGold else Color(0xFF1B1828),
                            border = BorderStroke(1.dp, if (isSelected) AntiqueGold else Color.Gray.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clickable {
                                    selectedChapterIndex = index
                                    isLiveDastaanPlaying = false
                                    audioReciter.stop()
                                    ambientPlayer.stop()
                                }
                                .testTag("dastaan_chapter_$index")
                        ) {
                            Text(
                                text = chap.title.split("•").first().trim(),
                                color = if (isSelected) DeepMidnight else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Visual Reading Progress across Dastaan Chapters with Share button
                val dastaanProgress = (selectedChapterIndex + 1).toFloat() / dastaanChapters.size.toFloat()
                PoetryReadingProgressBar(
                    progress = dastaanProgress,
                    poemTypeLabel = "Dastaan Chapter",
                    poemMetrics = PoemReadingMetrics(
                        lineCount = 12,
                        coupletCount = 3,
                        wordCount = 180,
                        estimatedSeconds = 120,
                        isLongNazmOrGhazal = true
                    ),
                    modifier = Modifier.padding(bottom = 6.dp),
                    onSharePoem = {
                        val chapter = dastaanChapters.getOrNull(selectedChapterIndex)
                        val text = if (chapter != null) {
                            "📖 *Dastaangoi Mehfil — ${chapter.title}*\n\n${chapter.narrativeExcerpt}\n\n🖋️ *Sher:* \n${chapter.featuredVerse}\n\n— Era: ${chapter.era} • Poet: ${chapter.poet}\nvia Kavya Setu #Dastaangoi #Poetry"
                        } else {
                            "Dastaangoi Mehfil via Kavya Setu"
                        }
                        com.example.util.SocialShareHelper.shareViaChooser(context, text, "Dastaangoi Chapter")
                    },
                    onCopyPoem = {
                        val chapter = dastaanChapters.getOrNull(selectedChapterIndex)
                        val text = if (chapter != null) {
                            "📖 Dastaangoi Mehfil — ${chapter.title}\n\n${chapter.narrativeExcerpt}\n\n${chapter.featuredVerse}\n\n— Poet: ${chapter.poet} (${chapter.era})\nvia Kavya Setu"
                        } else {
                            "Dastaangoi Mehfil via Kavya Setu"
                        }
                        com.example.util.SocialShareHelper.copyToClipboard(context, text, "Dastaangoi Chapter")
                    }
                )

                // Stage Presentation Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171322)),
                    border = BorderStroke(1.2.dp, AntiqueGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentChapter.era,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Mood: ${currentChapter.mood}",
                                    fontSize = 10.sp,
                                    color = VelvetRose
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = currentChapter.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "By ${currentChapter.poet}",
                                fontSize = 12.sp,
                                color = AntiqueGold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Historical Narrative Text
                            Text(
                                text = "“${currentChapter.narrativeExcerpt}”",
                                fontSize = 12.5.sp,
                                lineHeight = 19.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Couplet Highlight Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AntiqueGold.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentChapter.featuredVerse,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }

                        // Bottom Live Bar & Wah-Wah
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { isLiveDastaanPlaying = !isLiveDastaanPlaying },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isLiveDastaanPlaying) VelvetRose else AntiqueGold
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("dastaan_play_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (isLiveDastaanPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isLiveDastaanPlaying) Color.White else DeepMidnight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isLiveDastaanPlaying) "Pause Dastaan" else "Listen to Dastaangoi",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLiveDastaanPlaying) Color.White else DeepMidnight,
                                        fontSize = 12.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { audienceWahWahCount++ },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, VelvetRose),
                                    modifier = Modifier.testTag("dastaan_wah_wah_btn")
                                ) {
                                    Text("👏 वाह-वाह! ($audienceWahWahCount)", color = VelvetRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
