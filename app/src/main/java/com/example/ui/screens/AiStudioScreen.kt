package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.PoetryStyle
import com.example.data.model.PromptLibraryData
import com.example.data.model.PromptMood
import com.example.data.model.ShayariPrompt
import com.example.ui.components.InlinePromptSuggestions
import com.example.ui.components.PromptLibraryDialog
import com.example.data.remote.GeminiClient
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
import com.example.ui.components.KalamEUstaadDialog
import com.example.ui.components.LafzOMaaniDialog
import com.example.ui.components.MeterAnalysisResult
import com.example.ui.components.PoeticMeterAnalyzer
import com.example.ui.components.SherBaaziGameDialog
import com.example.util.SocialShareHelper
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Casino
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AiStudioScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSherBaaziDialog by remember { mutableStateOf(false) }
    var showKalamEUstaad by remember { mutableStateOf(false) }
    var showLafzOMaani by remember { mutableStateOf(false) }
    val allShayaris by viewModel.allShayaris.collectAsStateWithLifecycle()

    val tabs = listOf("Compose (Flash)", "High Thinking (Pro)", "Rhymes (Lite)", "Card Art (3 Pro)", "Meter & Beher")

    if (showSherBaaziDialog) {
        SherBaaziGameDialog(
            allShayaris = allShayaris,
            audioReciter = audioReciter,
            onDismiss = { showSherBaaziDialog = false }
        )
    }

    if (showKalamEUstaad) {
        val sampleVerse = allShayaris.firstOrNull()?.lines ?: ""
        KalamEUstaadDialog(
            initialDraft = sampleVerse,
            audioReciter = audioReciter,
            onDismiss = { showKalamEUstaad = false }
        )
    }

    if (showLafzOMaani) {
        LafzOMaaniDialog(
            audioReciter = audioReciter,
            onDismiss = { showLafzOMaani = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_studio_screen")
    ) {
        // Tab Row
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = AntiqueGold
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Sher Baazi Antakshari Quick Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { showSherBaaziDialog = true }
                .testTag("sher_baazi_banner"),
            shape = RoundedCornerShape(14.dp),
            color = VelvetRose.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "⚔️", fontSize = 20.sp)
                    Column {
                        Text(
                            text = "Play Sher Baazi (Couplet Duel)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = VelvetRose
                        )
                        Text(
                            text = "Classical Antakshari match against AI Sukhanwar",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VelvetRose
                ) {
                    Text(
                        text = "Play Now",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Classical Mentorship & Lexicon Quick Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showKalamEUstaad = true },
                color = Color(0xFF261224),
                border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎭", fontSize = 16.sp)
                    Column {
                        Text("Kalam-e-Ustaad", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VelvetRose)
                        Text("Master Islah & Critique", fontSize = 9.sp, color = Color.LightGray)
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showLafzOMaani = true },
                color = Color(0xFF142226),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔍", fontSize = 16.sp)
                    Column {
                        Text("Lafz-o-Maani", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGold)
                        Text("Roots & Lexicon", fontSize = 9.sp, color = Color.LightGray)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> ComposeTab(viewModel, audioReciter)
                1 -> HighThinkingTab(viewModel)
                2 -> RhymeAssistantTab(viewModel)
                3 -> ImageGeneratorTab()
                4 -> MeterTutorTab(audioReciter)
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComposeTab(viewModel: MainViewModel, audioReciter: AudioReciter) {
    val context = LocalContext.current
    var topic by remember { mutableStateOf("") }
    var penName by remember { mutableStateOf("Sukhanwar") }
    var selectedLang by remember { mutableStateOf("hindi") }
    var selectedEmotion by remember { mutableStateOf("ishq") }
    var showPromptLibraryDialog by remember { mutableStateOf(false) }
    var inlineSelectedMood by remember { mutableStateOf<PromptMood?>(null) }
    var activePromptTitle by remember { mutableStateOf<String?>(null) }
    var showIslahForComposed by remember { mutableStateOf(false) }

    val isComposing by viewModel.isComposing.collectAsStateWithLifecycle()
    val resultText by viewModel.composedResult.collectAsStateWithLifecycle()
    val errorText by viewModel.compositionError.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedPoetryStyle.collectAsStateWithLifecycle()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Compose with Gemini 3.5 Flash",
                style = MaterialTheme.typography.titleLarge,
                color = AntiqueGold,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Generate authentic couplets with metrical cadence in English, Hindi, or Odia.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Language Selector
            Text("Select Language:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Hindi" to "hindi", "Odia" to "odia", "English" to "english").forEach { (label, code) ->
                    FilterChip(
                        selected = selectedLang == code,
                        onClick = { selectedLang = code },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AntiqueGold,
                            selectedLabelColor = DeepMidnight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Emotion Selector
            Text("Select Emotion:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Emotion.entries.forEach { emo ->
                    FilterChip(
                        selected = selectedEmotion == emo.code,
                        onClick = { selectedEmotion = emo.code },
                        label = { Text("${emo.emoji} ${emo.englishLabel}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VelvetRose.copy(alpha = 0.2f),
                            selectedLabelColor = VelvetRose
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Poetry Style Filter Selector (Ghazal, Haiku, Free Verse, etc.)
            Text("Select Poetry Style / काव्य विधा:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PoetryStyle.entries.forEach { style ->
                    FilterChip(
                        selected = selectedStyle == style,
                        onClick = { viewModel.selectPoetryStyle(style) },
                        label = { Text("${style.emoji} ${style.displayName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                            selectedLabelColor = AntiqueGold
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(selectedStyle.emoji, fontSize = 15.sp)
                    Text(
                        text = "${selectedStyle.displayName}: ${selectedStyle.description}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inline Prompt Muse / Categorized Prompt Suggestions
            InlinePromptSuggestions(
                selectedMood = inlineSelectedMood,
                onMoodSelected = { inlineSelectedMood = it },
                onSelectPrompt = { prompt ->
                    topic = prompt.promptText
                    selectedEmotion = prompt.emotionCode
                    activePromptTitle = prompt.title
                    Toast.makeText(context, "Loaded: ${prompt.title}", Toast.LENGTH_SHORT).show()
                },
                onOpenFullLibrary = { showPromptLibraryDialog = true },
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (activePromptTitle != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AntiqueGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Using Prompt: $activePromptTitle",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AntiqueGold
                        )
                        IconButton(
                            onClick = { activePromptTitle = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear prompt tag",
                                modifier = Modifier.size(14.dp),
                                tint = AntiqueGold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            OutlinedTextField(
                value = topic,
                onValueChange = {
                    topic = it
                    if (activePromptTitle != null) activePromptTitle = null
                },
                label = { Text("Topic / Prompt / Inspiration") },
                placeholder = { Text("e.g., Rain on midnight streets, unforgotten smile, journey") },
                trailingIcon = {
                    IconButton(
                        onClick = { showPromptLibraryDialog = true },
                        modifier = Modifier.testTag("open_prompt_dialog_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Open Prompt Library",
                            tint = AntiqueGold
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("ai_compose_topic_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showPromptLibraryDialog = true },
                    modifier = Modifier.testTag("open_full_prompt_library_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = AntiqueGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Browse Full Library (${PromptLibraryData.prompts.size} Prompts)",
                        fontSize = 12.sp,
                        color = AntiqueGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = penName,
                onValueChange = { penName = it },
                label = { Text("Your Pen Name (Takhallus)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.composeWithGemini(
                        topic = topic.ifBlank { "Deep love and longing" },
                        emotion = selectedEmotion,
                        language = selectedLang,
                        penName = penName,
                        style = selectedStyle,
                        context = context
                    )
                },
                enabled = !isComposing,
                modifier = Modifier.fillMaxWidth().testTag("compose_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight)
            ) {
                if (isComposing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = DeepMidnight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Composing ${selectedStyle.displayName}...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compose ${selectedStyle.displayName}")
                }
            }

            if (errorText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        }
    }

    // Result Card
    if (resultText != null) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✨ Newly Composed",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${selectedStyle.emoji} ${selectedStyle.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AntiqueGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                SocialShareHelper.shareGeminiShayari(
                                    context = context,
                                    lines = resultText!!,
                                    emotion = selectedEmotion,
                                    language = selectedLang,
                                    author = penName,
                                    topic = topic
                                )
                            },
                            modifier = Modifier.testTag("ai_share_icon_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share Couplet to Social Media", tint = AntiqueGold)
                        }
                        IconButton(onClick = { audioReciter.speak(resultText!!, selectedLang) }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Recite", tint = AntiqueGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resultText!!,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 30.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val watermarkedText = "🖋️ *Kavya Setu • AI Composed Verse*\n━━━━━━━━━━━━━━━━━━━━\n\n${resultText!!}\n\n— Composed on Kavya Setu (Built by Nihar Sales)\n#KavyaSetu #GeminiAI"
                            clipboard.setPrimaryClip(ClipData.newPlainText("AI Shayari", watermarkedText))
                            Toast.makeText(context, "Copied with Kavya Setu watermark!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(0.9f).testTag("ai_copy_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            SocialShareHelper.shareGeminiShayari(
                                context = context,
                                lines = resultText!!,
                                emotion = selectedEmotion,
                                language = selectedLang,
                                author = penName,
                                topic = topic
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                        modifier = Modifier.weight(1.1f).testTag("ai_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = DeepMidnight)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepMidnight)
                    }

                    Button(
                        onClick = {
                            viewModel.publishNewShayari(
                                lines = resultText!!,
                                emotion = selectedEmotion,
                                language = selectedLang,
                                author = penName,
                                penName = penName
                            ) {
                                Toast.makeText(context, "Published to Community Feed!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                        modifier = Modifier.weight(1.2f).testTag("ai_publish_button")
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publish", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FilledTonalButton(
                    onClick = { showIslahForComposed = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎭 Seek Master's Islah (Ustaad Review)", fontWeight = FontWeight.Bold, color = VelvetRose)
                }
            }
        }
    }

    if (showIslahForComposed && resultText != null) {
        KalamEUstaadDialog(
            initialDraft = resultText!!,
            audioReciter = audioReciter,
            onDismiss = { showIslahForComposed = false }
        )
    }

    if (showPromptLibraryDialog) {
        PromptLibraryDialog(
            onDismissRequest = { showPromptLibraryDialog = false },
            onSelectPrompt = { prompt, autoComposeNow ->
                topic = prompt.promptText
                selectedEmotion = prompt.emotionCode
                activePromptTitle = prompt.title
                showPromptLibraryDialog = false
                Toast.makeText(context, "Loaded: \"${prompt.title}\"", Toast.LENGTH_SHORT).show()
                if (autoComposeNow) {
                    viewModel.composeWithGemini(
                        topic = prompt.promptText,
                        emotion = prompt.emotionCode,
                        language = selectedLang,
                        penName = penName
                    )
                }
            }
        )
    }
}

@Composable
private fun HighThinkingTab(viewModel: MainViewModel) {
    var inputVerse by remember {
        mutableStateOf("हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले")
    }
    var lang by remember { mutableStateOf("hindi") }
    val isAnalyzing by viewModel.isAnalyzingWithHighThinking.collectAsStateWithLifecycle()
    val analysis by viewModel.highThinkingAnalysis.collectAsStateWithLifecycle()
    val error by viewModel.analysisError.collectAsStateWithLifecycle()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = AntiqueGold)
                Text(
                    text = "High Thinking Mode (Gemini 3.1 Pro)",
                    style = MaterialTheme.typography.titleLarge,
                    color = AntiqueGold,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Uses Gemini 3.1 Pro with ThinkingLevel.HIGH for deep meter analysis (Behr, Radif, Qaafiya, Takhallus & philosophical nuance).",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = inputVerse,
                onValueChange = { inputVerse = it },
                label = { Text("Enter Poetry / Couplet to Analyze") },
                modifier = Modifier.fillMaxWidth().testTag("high_thinking_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.analyzeWithHighThinking(inputVerse, lang) },
                enabled = !isAnalyzing && inputVerse.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = DeepMidnight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deep Thinking in Progress...")
                } else {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze with High Thinking")
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        }
    }

    if (analysis != null) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🧠 Literary & Metrical Breakdown", fontWeight = FontWeight.Bold, color = AntiqueGold, fontSize = 16.sp)

                AnalysisRow("Behr / Meter", analysis!!.behrMeter)
                AnalysisRow("Radif (Refrain)", analysis!!.radif)
                AnalysisRow("Qaafiya (Rhyme)", analysis!!.qaafiya)
                AnalysisRow("Takhallus / Voice", analysis!!.takhallus)
                AnalysisRow("Ras / Emotion", analysis!!.emotionalWeight)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Scholarly Commentary:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AntiqueGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(analysis!!.literaryCommentary, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisRow(title: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
private fun RhymeAssistantTab(viewModel: MainViewModel) {
    var seedWord by remember { mutableStateOf("आस") }
    var language by remember { mutableStateOf("hindi") }
    val isSearching by viewModel.isSearchingRhymes.collectAsStateWithLifecycle()
    val rhymes by viewModel.rhymingSuggestions.collectAsStateWithLifecycle()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Fast Rhymes (Gemini 3.1 Flash-Lite)",
                style = MaterialTheme.typography.titleLarge,
                color = AntiqueGold,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Rapid low-latency rhyme and qaafiya finder for poets while writing.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = seedWord,
                onValueChange = { seedWord = it },
                label = { Text("Base Word / Qaafiya Seed") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.findRhymes(seedWord, language) },
                enabled = !isSearching && seedWord.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = DeepMidnight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finding Rhymes...")
                } else {
                    Text("Find Rhyming Words")
                }
            }

            if (rhymes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Suggested Rhymes & Qaafiyas:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rhymes.forEach { word ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = word,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageGeneratorTab() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var prompt by remember { mutableStateOf("Golden moonlight illuminating a vintage ink bottle and rose petals on dark velvet") }
    var selectedSize by remember { mutableStateOf("1K") } // "1K", "2K", "4K"
    var isGenerating by remember { mutableStateOf(false) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = AntiqueGold)
                Text(
                    text = "Aesthetic Card Backgrounds",
                    style = MaterialTheme.typography.titleLarge,
                    color = AntiqueGold,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Model: gemini-3-pro-image-preview. Generates atmospheric art with selectable resolution (1K, 2K, 4K) for wallpapers and poetry cards.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Visual Style & Emotion Description") },
                modifier = Modifier.fillMaxWidth().testTag("ai_image_prompt_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // User affordance for 1K, 2K, 4K
            Text("Image Resolution:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1K", "2K", "4K").forEach { size ->
                    FilterChip(
                        selected = selectedSize == size,
                        onClick = { selectedSize = size },
                        label = { Text(size) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AntiqueGold,
                            selectedLabelColor = DeepMidnight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        errorMessage = null
                        val res = GeminiClient.generateCardBackground(prompt, selectedSize)
                        res.onSuccess {
                            generatedBitmap = it
                            Toast.makeText(context, "$selectedSize Image generated successfully!", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            errorMessage = it.message ?: "Image generation failed."
                        }
                        isGenerating = false
                    }
                },
                enabled = !isGenerating && prompt.isNotBlank(),
                modifier = Modifier.fillMaxWidth().testTag("generate_image_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = DeepMidnight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Synthesizing $selectedSize Art...")
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate $selectedSize Artwork")
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        }
    }

    if (generatedBitmap != null) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Generated $selectedSize Wallpaper",
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    bitmap = generatedBitmap!!.asImageBitmap(),
                    contentDescription = "Generated art",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun MeterTutorTab(audioReciter: AudioReciter) {
    var coupletText by remember {
        mutableStateOf(
            "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले"
        )
    }

    val sampleCouplets = listOf(
        "Mirza Ghalib" to "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले",
        "Kabir Das" to "बड़ा हुआ तो क्या हुआ जैसे पेड़ खजूर\nपंथी को छाया नहीं फल लागे अति दूर",
        "Upendra Bhanja (Odia)" to "କୋଟି ବ୍ରହ୍ମାଣ୍ଡ ସୁନ୍ଦରୀ ଗୋ ତୋ ରୂପ ଅନୁପମା\nନୟନ ତୋଷିଣୀ ମନ ମୋହିନୀ ରମା",
        "Allama Iqbal" to "ख़ुदी को कर बुलंद इतना कि हर तक़दीर से पहले\nख़ुदा बंदे से ख़ुद पूछे बता तेरी रज़ा क्या है"
    )

    val analysis = remember(coupletText) {
        PoeticMeterAnalyzer.analyzeCouplet(coupletText)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Interactive Poetic Meter & Beher Tutor",
                style = MaterialTheme.typography.titleLarge,
                color = AntiqueGold,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Analyze Beher, Chhanda, Matra weights, Qafiya & Radif cadence balance",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Presets row
            Text(
                text = "Load Classic Meter Reference:",
                fontSize = 11.sp,
                color = AntiqueGold,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sampleCouplets.forEach { (poet, verse) ->
                    FilterChip(
                        selected = coupletText.trim() == verse.trim(),
                        onClick = { coupletText = verse },
                        label = { Text(poet, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AntiqueGold.copy(alpha = 0.2f),
                            selectedLabelColor = AntiqueGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = coupletText,
                onValueChange = { coupletText = it },
                label = { Text("Couplet / Sher (Misra 1 & Misra 2)") },
                placeholder = { Text("Line 1 (Misra-e-Ula)\nLine 2 (Misra-e-Sani)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("meter_couplet_input"),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { audioReciter.speak(coupletText, "hindi") }
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Recite Rhythm Aloud", color = AntiqueGold, fontSize = 12.sp)
                }
            }
        }
    }

    // Analysis Result Card
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.5.dp, VelvetRose.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cadence & Symmetry Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = VelvetRose
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (analysis.symmetryScore >= 80) AntiqueGold else VelvetRose
                ) {
                    Text(
                        text = "${analysis.symmetryScore}% Balanced",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepMidnight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { analysis.symmetryScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AntiqueGold,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Identified Meter Tag
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AntiqueGold.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Identified Meter / Beher:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = analysis.detectedBeherOrChhanda,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Syllable & Matra breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Line 1
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Misra 1 (Line 1)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = AntiqueGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Syllables: ${analysis.line1Syllables}", fontSize = 13.sp)
                        Text("• Matras: ${analysis.line1Matras}", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Weight Pattern:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = analysis.line1Pattern,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = VelvetRose
                        )
                    }
                }

                // Line 2
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Misra 2 (Line 2)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = AntiqueGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Syllables: ${analysis.line2Syllables}", fontSize = 13.sp)
                        Text("• Matras: ${analysis.line2Matras}", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Weight Pattern:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = analysis.line2Pattern,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = VelvetRose
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Qafiya & Radif Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Qafiya (Rhyme Pair)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (analysis.identifiedQafiya != null)
                                "${analysis.identifiedQafiya.first} ⟷ ${analysis.identifiedQafiya.second}"
                            else "Not matched",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VelvetRose
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Radif (Refrain)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = analysis.identifiedRadif ?: "None (Aazaad)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = analysis.rhythmBalanceComment,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (analysis.poeticSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tutor Suggestions:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AntiqueGold
                )
                analysis.poeticSuggestions.forEach { tip ->
                    Text(
                        text = "• $tip",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
