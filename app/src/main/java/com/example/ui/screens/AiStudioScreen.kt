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
import com.example.data.model.PromptLibraryData
import com.example.data.model.PromptMood
import com.example.data.model.ShayariPrompt
import com.example.ui.components.InlinePromptSuggestions
import com.example.ui.components.PromptLibraryDialog
import com.example.data.remote.GeminiClient
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
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
    val tabs = listOf("Compose (Flash)", "High Thinking (Pro)", "Rhymes (Lite)", "Card Art (3 Pro)")

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
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
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

    val isComposing by viewModel.isComposing.collectAsStateWithLifecycle()
    val resultText by viewModel.composedResult.collectAsStateWithLifecycle()
    val errorText by viewModel.compositionError.collectAsStateWithLifecycle()

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
                        penName = penName
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
                    Text("Composing Couplet...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compose Couplet")
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
                    Text(
                        text = "✨ Newly Composed Verse",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    IconButton(onClick = { audioReciter.speak(resultText!!, selectedLang) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Recite", tint = AntiqueGold)
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("AI Shayari", resultText!!))
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
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
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publish to Feed")
                    }
                }
            }
        }
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
