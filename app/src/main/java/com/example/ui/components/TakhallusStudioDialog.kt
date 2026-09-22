package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.PoetMoharSeal
import com.example.data.local.TakhallusSealManager
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

@Composable
fun TakhallusStudioDialog(
    initialPenName: String = "Parwaaz",
    onDismiss: () -> Unit
) {
    val currentSeal by TakhallusSealManager.currentSeal.collectAsState()

    var penNameInput by remember { mutableStateOf(currentSeal.takhallus.ifBlank { initialPenName }) }
    var selectedStyle by remember { mutableStateOf(currentSeal.sealStyle) }
    var selectedColor by remember { mutableLongStateOf(currentSeal.inkColorHex) }
    var subtitleInput by remember { mutableStateOf(currentSeal.subtitleTag) }

    val sealStyles = listOf(
        "MUGHAL_OVAL" to "Mughal Royal Oval",
        "ROYAL_ROUND" to "Sultani Round",
        "CALLIGRAPHIC_SHIELD" to "Calligraphic Shield",
        "WAX_HEXAGON" to "Imperial Wax Hexagon"
    )

    val colorOptions = listOf(
        0xFFE5B247 to "Antique Gold",
        0xFFD8315B to "Velvet Rose",
        0xFF38A3A5 to "Mystic Teal",
        0xFFE8C88B to "Amber Brass",
        0xFFD4A373 to "Sandalwood",
        0xFFFFFFFF to "Ivory White"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.2.dp, Color(selectedColor).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                        Text("🪶", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "Takhallus Studio • तख़ल्लुस मोहर",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(selectedColor)
                            )
                            Text(
                                text = "Design your personal digital poet seal for certificates & cards",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // The Digital Seal Live Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DeepMidnight)
                        .border(1.dp, Color(selectedColor).copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    PoetMoharView(
                        seal = PoetMoharSeal(
                            takhallus = penNameInput.ifBlank { "तख़ल्लुस" },
                            sealStyle = selectedStyle,
                            inkColorHex = selectedColor,
                            subtitleTag = subtitleInput
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input: Pen Name (Takhallus)
                Text(
                    text = "Takhallus (Poetic Pen Name)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = penNameInput,
                    onValueChange = { penNameInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("takhallus_input_field"),
                    placeholder = { Text("e.g. Parwaaz, Ghalib, Meer, Sahir...") },
                    leadingIcon = {
                        Icon(Icons.Default.Create, contentDescription = null, tint = Color(selectedColor))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(selectedColor),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Input: Subtitle Tag
                Text(
                    text = "Seal Inscription Tagline",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = subtitleInput,
                    onValueChange = { subtitleInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. काव्यसेतु • KAVYA SETU") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(selectedColor),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seal Style Selector
                Text(
                    text = "Mohar Architecture & Geometry",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sealStyles.forEach { (key, label) ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedStyle = key },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedStyle == key) Color(selectedColor).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (selectedStyle == key) Color(selectedColor) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedStyle == key) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = if (selectedStyle == key) Color(selectedColor) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ink Pigment Color
                Text(
                    text = "Calligraphic Ink Pigment",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(colorOptions) { (hex, name) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedColor = hex }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        2.dp,
                                        if (selectedColor == hex) Color.White else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColor == hex) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (hex == 0xFFFFFFFF) Color.Black else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = name, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Seal Button
                Button(
                    onClick = {
                        TakhallusSealManager.updateSeal(
                            takhallus = penNameInput,
                            style = selectedStyle,
                            colorHex = selectedColor,
                            subtitle = subtitleInput
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_seal_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(selectedColor)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Engrave Digital Seal (मोहर सहेजें)",
                        color = if (selectedColor == 0xFFFFFFFF) Color.Black else DeepMidnight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PoetMoharView(
    seal: PoetMoharSeal,
    modifier: Modifier = Modifier
) {
    val ink = Color(seal.inkColorHex)

    val shape = when (seal.sealStyle) {
        "ROYAL_ROUND" -> CircleShape
        "CALLIGRAPHIC_SHIELD" -> RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
        "WAX_HEXAGON" -> CutCornerShape(16.dp)
        else -> RoundedCornerShape(40.dp) // MUGHAL_OVAL
    }

    Box(
        modifier = modifier
            .size(width = 170.dp, height = 110.dp)
            .clip(shape)
            .background(ink.copy(alpha = 0.08f))
            .border(2.dp, ink, shape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, ink.copy(alpha = 0.5f), shape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✦ MOHAR ✦",
                    fontSize = 8.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ink.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = seal.takhallus,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = ink,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = seal.subtitleTag.uppercase(),
                    fontSize = 7.5.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium,
                    color = ink.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
