package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UstaadCritiqueResult
import com.example.data.model.UstaadId
import com.example.data.model.UstaadPersona
import com.example.data.repository.UstaadMentorRepository
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.launch

@Composable
fun KalamEUstaadDialog(
    initialDraft: String = "",
    onDismiss: () -> Unit,
    onAdoptRevision: (String) -> Unit = {},
    audioReciter: AudioReciter? = null
) {
    var draftText by remember {
        mutableStateOf(
            if (initialDraft.isNotBlank()) initialDraft
            else "रात भर तारों से पूछा है ठिकाना तेरा,\nख़्वाब में भी न मिला कोई फ़साना तेरा।"
        )
    }
    var selectedPersona by remember { mutableStateOf(UstaadMentorRepository.personas.first()) }
    var critiqueResult by remember { mutableStateOf<UstaadCritiqueResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = DeepMidnight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AntiqueGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎭", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kalam-e-Ustaad • کلامِ استاد",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AntiqueGold
                            )
                            Text(
                                text = "Master Poetic Mentorship & Classical Islah (اصلاح)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_kalam_e_ustaad")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Persona Selection Carousel
                    item {
                        Text(
                            text = "Choose Your Classical Master (Ustaad):",
                            style = MaterialTheme.typography.labelMedium,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(UstaadMentorRepository.personas) { persona ->
                                val isSelected = persona.id == selectedPersona.id
                                Card(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .clickable {
                                            selectedPersona = persona
                                            critiqueResult = null
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) AntiqueGold.copy(alpha = 0.22f) else CharcoalElevated
                                    ),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AntiqueGold) else null
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(persona.emblem, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = persona.name.split(" ").take(2).joinToString(" "),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) AntiqueGold else Color.White,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = persona.eraAndOrigin.split("•").first().trim(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Active Persona Badge
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedPersona.emblem, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = selectedPersona.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AntiqueGold
                                    )
                                    Text(
                                        text = selectedPersona.nativeName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Text(
                                        text = selectedPersona.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VelvetRose,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Draft Input
                    item {
                        Text(
                            text = "Your Verse / Couplet Draft:",
                            style = MaterialTheme.typography.labelMedium,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = draftText,
                            onValueChange = { draftText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("draft_verse_input"),
                            minLines = 3,
                            maxLines = 5,
                            placeholder = {
                                Text("Enter your couplet lines here to seek the master's review...")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (draftText.isNotBlank()) {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch {
                                        val result = UstaadMentorRepository.getCritique(
                                            persona = selectedPersona,
                                            coupletText = draftText,
                                            language = "Multilingual Urdu/Hindi/Odia"
                                        )
                                        isLoading = false
                                        result.onSuccess {
                                            critiqueResult = it
                                        }.onFailure { err ->
                                            errorMessage = err.localizedMessage
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_islaah_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                            enabled = !isLoading && draftText.isNotBlank(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Consulting ${selectedPersona.name.split(" ").first()}...")
                            } else {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Seek Master's Islah (اصلاح • Review)")
                            }
                        }
                    }

                    // Critique Result
                    if (critiqueResult != null) {
                        val res = critiqueResult!!

                        item {
                            HorizontalDivider(
                                color = AntiqueGold.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Greeting Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = AntiqueGold.copy(alpha = 0.12f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = res.greeting,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontStyle = FontStyle.Italic,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = AntiqueGold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = res.critique,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Master's Revised Version
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, VelvetRose)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Master's Refined Couplet (کلامِ استاد):",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = VelvetRose
                                        )

                                        if (audioReciter != null) {
                                            IconButton(
                                                onClick = {
                                                    audioReciter.speak(res.revisedCouplet, "hindi")
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Recite Master's Version",
                                                    tint = AntiqueGold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = res.revisedCouplet,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        ),
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(res.revisedCouplet))
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AntiqueGold)
                                        ) {
                                            Text("Copy Verse", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                onAdoptRevision(res.revisedCouplet)
                                                onDismiss()
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = DeepMidnight)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Adopt Verse", color = DeepMidnight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Word Swaps / Islah Details
                        if (res.wordReplacements.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Word Replacements & Poetic Nuances (انتخابِ الفاظ):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AntiqueGold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (swap in res.wordReplacements) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = CharcoalElevated.copy(alpha = 0.7f)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = swap.originalWord,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = Color.LightGray
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Icon(
                                                        Icons.Default.SwapHoriz,
                                                        contentDescription = null,
                                                        tint = AntiqueGold,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = swap.suggestedWord,
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = AntiqueGold
                                                    )
                                                }
                                                if (swap.poeticReason.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = swap.poeticReason,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Meter & Cadence note
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DeepMidnight),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Wazn & Meter Rhythm:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AntiqueGold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = res.meterComment,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "“${res.encouragement}”",
                                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                        color = VelvetRose
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
