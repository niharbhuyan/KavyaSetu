package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DiwanConfig
import com.example.data.model.DiwanCoverStyle
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun DiwanPublisherDialog(
    allShayaris: List<Shayari>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bookTitle by remember { mutableStateOf("Diwan-e-Kavya • दीवान-ए-काव्य") }
    var subtitle by remember { mutableStateOf("Anthology of Multilingual Ghazals & Chhandas") }
    var poetName by remember { mutableStateOf("Nihar") }
    var takhallus by remember { mutableStateOf("Nihar (निहार)") }
    var dedication by remember { mutableStateOf("Dedicated to all seekers of timeless poetic beauty,\nand lovers of words that awaken the soul.") }
    var coverStyle by remember { mutableStateOf(DiwanCoverStyle.ROYAL_VELVET) }
    var includeTranslations by remember { mutableStateOf(true) }
    var includeTakhallusSeal by remember { mutableStateOf(true) }

    val selectedIds = remember {
        mutableStateListOf<String>().apply {
            addAll(allShayaris.take(8).map { it.id })
        }
    }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
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
                            Text("📖", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Poet's Diwan Publisher • دیوان",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AntiqueGold
                            )
                            Text(
                                text = "Export Custom Illustrated PDF & e-Book Anthology",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_diwan_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Book Metadata Fields
                    item {
                        Text("Book Identity:", style = MaterialTheme.typography.labelMedium, color = AntiqueGold)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = bookTitle,
                            onValueChange = { bookTitle = it },
                            label = { Text("Diwan / Book Title") },
                            modifier = Modifier.fillMaxWidth().testTag("diwan_title_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Subtitle / Edition") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = poetName,
                                onValueChange = { poetName = it },
                                label = { Text("Author / Compiler") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AntiqueGold,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            OutlinedTextField(
                                value = takhallus,
                                onValueChange = { takhallus = it },
                                label = { Text("Takhallus (Seal)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AntiqueGold,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dedication,
                            onValueChange = { dedication = it },
                            label = { Text("Dedication Message") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Cover Style Selection
                    item {
                        Text("Cover Aesthetic & Binding Style:", style = MaterialTheme.typography.labelMedium, color = AntiqueGold)
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(DiwanCoverStyle.entries) { style ->
                                val isSelected = style == coverStyle
                                Card(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clickable { coverStyle = style },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) AntiqueGold.copy(alpha = 0.2f) else CharcoalElevated
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AntiqueGold) else null
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = style.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) AntiqueGold else Color.White
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = style.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.LightGray,
                                            fontSize = 9.sp,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Publishing Toggles
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Include English / Odia Translations", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                    Switch(
                                        checked = includeTranslations,
                                        onCheckedChange = { includeTranslations = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AntiqueGold)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Stamp Poet's Takhallus Seal on Cover", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                    Switch(
                                        checked = includeTakhallusSeal,
                                        onCheckedChange = { includeTakhallusSeal = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AntiqueGold)
                                    )
                                }
                            }
                        }
                    }

                    // Verse Selection list
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Couplets to Include (${selectedIds.size} selected):",
                                style = MaterialTheme.typography.labelMedium,
                                color = AntiqueGold
                            )
                            Text(
                                text = if (selectedIds.size == allShayaris.size) "Deselect All" else "Select All",
                                style = MaterialTheme.typography.labelSmall,
                                color = VelvetRose,
                                modifier = Modifier.clickable {
                                    if (selectedIds.size == allShayaris.size) selectedIds.clear()
                                    else {
                                        selectedIds.clear()
                                        selectedIds.addAll(allShayaris.map { it.id })
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (s in allShayaris.take(16)) {
                                val isChecked = selectedIds.contains(s.id)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedIds.remove(s.id)
                                            else selectedIds.add(s.id)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isChecked) CharcoalElevated else CharcoalElevated.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = {
                                                if (it) selectedIds.add(s.id) else selectedIds.remove(s.id)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = AntiqueGold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = s.lines.lines().firstOrNull() ?: s.lines,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "~ ${s.author} (${s.language.uppercase()})",
                                                fontSize = 10.sp,
                                                color = AntiqueGold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: Generate PDF & Share
                if (generatedFile != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("✅ Diwan PDF Generated!", fontWeight = FontWeight.Bold, color = AntiqueGold, fontSize = 13.sp)
                                Text(generatedFile!!.name, color = Color.White, fontSize = 10.sp, maxLines = 1)
                            }
                            Button(
                                onClick = {
                                    DiwanPdfGenerator.sharePdf(context, generatedFile!!)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VelvetRose),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("share_diwan_pdf_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        val config = DiwanConfig(
                            bookTitle = bookTitle,
                            subtitle = subtitle,
                            poetName = poetName,
                            takhallus = takhallus,
                            dedication = dedication,
                            coverStyle = coverStyle,
                            selectedShayariIds = selectedIds.toSet(),
                            includeTranslations = includeTranslations,
                            includeTakhallusSeal = includeTakhallusSeal
                        )
                        val chosenShayaris = allShayaris.filter { selectedIds.contains(it.id) }
                        isGenerating = true
                        errorMessage = null
                        scope.launch {
                            val res = DiwanPdfGenerator.generateDiwanPdf(context, config, chosenShayaris)
                            isGenerating = false
                            res.onSuccess {
                                generatedFile = it
                                DiwanPdfGenerator.sharePdf(context, it)
                            }.onFailure { err ->
                                errorMessage = err.localizedMessage
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("compile_diwan_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                    enabled = !isGenerating && selectedIds.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DeepMidnight, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Typesetting Diwan Book (PDF)...", color = DeepMidnight, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = DeepMidnight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish & Export Diwan PDF", color = DeepMidnight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
