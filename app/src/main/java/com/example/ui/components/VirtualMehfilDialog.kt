package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MehfilAttendee
import com.example.data.model.MehfilComment
import com.example.data.model.MehfilReactionType
import com.example.data.model.Shayari
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FloatingParticle(
    val id: Long = Random.nextLong(),
    val emoji: String,
    val initialX: Float,
    val animProgress: Animatable<Float, *> = Animatable(0f)
)

@Composable
fun VirtualMehfilDialog(
    allShayaris: List<Shayari>,
    audioReciter: AudioReciter,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
    val scope = rememberCoroutineScope()

    var currentVerseIndex by remember { mutableIntStateOf(0) }
    val activeShayari = remember(currentVerseIndex, allShayaris) {
        if (allShayaris.isNotEmpty()) allShayaris[currentVerseIndex % allShayaris.size]
        else Shayari(
            id = "demo_mehfil",
            lines = "हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले,\nबहुत निकले मिरे अरमाँ लेकिन फिर भी कम निकले।",
            author = "Mirza Ghalib",
            language = "hindi",
            emotion = "ishq"
        )
    }

    // Attendees
    val attendees = remember {
        listOf(
            MehfilAttendee("1", "Ali Zafar", "🌙", "Delhi"),
            MehfilAttendee("2", "Soumya Das", "🦚", "Bhubaneswar"),
            MehfilAttendee("3", "Parveen S.", "🕊️", "Lucknow"),
            MehfilAttendee("4", "Kavita Rao", "✨", "Jaipur"),
            MehfilAttendee("5", "Nihar", "📜", "Cuttack")
        )
    }

    // Floating appreciations (petals, lanterns)
    val floatingParticles = remember { mutableStateListOf<FloatingParticle>() }

    // Live Mehfil comments
    val comments = remember {
        mutableStateListOf(
            MehfilComment("c1", "Ali Zafar", "🌙", "सुभानअल्लाह! क्या मतला कहा है!", MehfilReactionType.SUBHANALLAH),
            MehfilComment("c2", "Soumya Das", "🦚", "କାବ୍ୟର ମଧୁରତା ମନ ଛୁଇଁଗଲା!", MehfilReactionType.WAH_WAH),
            MehfilComment("c3", "Parveen S.", "🕊️", "मुकर्रर इरशाद! एक बार फिर!", MehfilReactionType.MUKARRAR)
        )
    }

    var commentInput by remember { mutableStateOf("") }

    // Candle Flame Animation
    val infiniteTransition = rememberInfiniteTransition(label = "candle")
    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameFlicker"
    )

    fun triggerReaction(type: MehfilReactionType) {
        // Haptic feedback
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(45)
            }
        } catch (e: Exception) {
            // ignore
        }

        // Add floating particle
        val p = FloatingParticle(
            emoji = type.iconEmoji,
            initialX = Random.nextFloat() * 240f - 120f
        )
        floatingParticles.add(p)
        scope.launch {
            p.animProgress.animateTo(1f, animationSpec = tween(1600, easing = LinearEasing))
            floatingParticles.remove(p)
        }

        // Add to live appreciation list
        comments.add(
            0,
            MehfilComment(
                id = System.currentTimeMillis().toString(),
                senderName = "You",
                avatarEmoji = "✨",
                text = "${type.arabicUrdu} ${type.title}",
                reactionType = type
            )
        )

        // If Mukarrar Irshad (Encore), recite verse again
        if (type == MehfilReactionType.MUKARRAR) {
            audioReciter.speak(activeShayari.lines, activeShayari.language)
        }
    }

    // Auto-recite when entering verse
    LaunchedEffect(currentVerseIndex) {
        delay(300)
        audioReciter.speak(activeShayari.lines, activeShayari.language)
    }

    DisposableEffect(Unit) {
        onDispose {
            audioReciter.stop()
        }
    }

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
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AntiqueGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🌙", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Virtual Mehfil • محفلِ سخن",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AntiqueGold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color.Green)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Live Midnight Room • 48 Poets Listening",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_virtual_mehfil")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Shama-e-Mehfil (Sacred Candle Stage)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Animated Shama Candle Flame
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(flameFlicker),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🕯️", fontSize = 32.sp)
                            }

                            Text(
                                text = "शमा-ए-महफ़िल • شمعِ محفل",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold,
                                fontSize = 10.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = activeShayari.lines,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    lineHeight = 26.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Reciting: ~ ${activeShayari.author} ~",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = AntiqueGold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Stage Controls
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        audioReciter.speak(activeShayari.lines, activeShayari.language)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AntiqueGold.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Recite",
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        currentVerseIndex = (currentVerseIndex + 1) % allShayaris.size.coerceAtLeast(1)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(VelvetRose.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.SkipNext,
                                        contentDescription = "Next Poet",
                                        tint = VelvetRose,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Classical Mehfil Etiquette Reactions (Wah Wah, Mukarrar Irshad, Subhanallah)
                    Text(
                        text = "Mehfil Adab & Daad (داد • Audience Appreciations):",
                        style = MaterialTheme.typography.labelSmall,
                        color = AntiqueGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (r in MehfilReactionType.entries) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { triggerReaction(r) },
                                colors = CardDefaults.cardColors(containerColor = CharcoalElevated),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(r.iconEmoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = r.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueGold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = r.arabicUrdu,
                                        fontSize = 9.sp,
                                        color = Color.LightGray,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Audience Chat & Stream
                    Text(
                        text = "Live Mehfil Appreciations (हाज़िरीन की दाद):",
                        style = MaterialTheme.typography.labelSmall,
                        color = AntiqueGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(comments) { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CharcoalElevated.copy(alpha = 0.6f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(c.avatarEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = c.senderName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AntiqueGold
                                    )
                                    Text(
                                        text = c.text,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                                if (c.reactionType != null) {
                                    Text(c.reactionType.iconEmoji, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Comment Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mehfil_comment_input"),
                            placeholder = { Text("Send your praise (e.g. कमाल मिसरा!)...", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntiqueGold,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    comments.add(
                                        0,
                                        MehfilComment(
                                            id = System.currentTimeMillis().toString(),
                                            senderName = "You",
                                            avatarEmoji = "✍️",
                                            text = commentInput.trim(),
                                            reactionType = MehfilReactionType.WAH_WAH
                                        )
                                    )
                                    commentInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(VelvetRose, CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Render floating animated petals/lanterns over the canvas
                for (particle in floatingParticles) {
                    val progress = particle.animProgress.value
                    val yOffset = -((progress * 420).dp)
                    val alpha = (1f - progress).coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset {
                                IntOffset(
                                    x = particle.initialX.dp.roundToPx(),
                                    y = yOffset.roundToPx()
                                )
                            }
                            .alpha(alpha)
                    ) {
                        Text(particle.emoji, fontSize = (22 + (progress * 10)).sp)
                    }
                }
            }
        }
    }
}
