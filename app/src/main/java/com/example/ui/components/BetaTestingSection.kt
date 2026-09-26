package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.MysticTealSoft
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.SurfaceCardBorder

private const val DEVELOPER_EMAIL = "niharbhuyan@gmail.com"
private const val PACKAGE_NAME = "com.niharsales.kavyasetu"
private const val APP_VERSION = "v1.2.2"
private const val BUILD_NUMBER = "5"
// Official Google Play testing web opt-in URL scheme
private const val PLAY_TESTING_WEB_URL = "https://play.google.com/apps/testing/$PACKAGE_NAME"
private const val PLAY_INTERNAL_TEST_URL = "https://play.google.com/apps/internaltest/$PACKAGE_NAME"

@Composable
fun BetaTestingSection(
    modifier: Modifier = Modifier,
    onOpenPlayStoreKit: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var testerEmail by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var showFaqDetails by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("beta_testing_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. HERO BANNER: EARLY ACCESS BADGE ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.18f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = "Beta Testing Icon",
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Google Play Internal Testing",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Early Access & Pilot Ring",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MutedSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MysticTeal.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, MysticTealSoft.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "FAST-TRACK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MysticTealSoft,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Welcome to the Kavya Setu vanguard circle! As an internal tester, you receive pre-release builds within minutes of compilation, exploring upcoming Gemini AI composition models, new Odia/Urdu poetic meters, Tarannum melodies, and calligraphy features before anyone else.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                        fontSize = 13.sp
                    )
                )

                // Current Build Metadata Grid
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BuildMetaItem("Version", APP_VERSION)
                        BuildMetaItem("Build Code", BUILD_NUMBER)
                        BuildMetaItem("Channel", "Internal Track")
                        BuildMetaItem("Security", "Signed AAB")
                    }
                }
            }
        }

        // --- 2. STEP-BY-STEP ENROLLMENT GUIDE ---
        Text(
            text = "How to Join the Internal Track",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = AntiqueGold,
                fontSize = 15.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        StepGuideCard(
            stepNumber = "1",
            icon = Icons.Default.Email,
            title = "Submit Your Google Account",
            description = "Google Play distributes internal builds exclusively to registered Google/Gmail accounts added to the developer's console tester list."
        )

        StepGuideCard(
            stepNumber = "2",
            icon = Icons.Default.VerifiedUser,
            title = "Accept the Web/Android Invitation",
            description = "Click the Play Store internal testing invitation link. On the web page, click 'Accept Invitation' to register your device."
        )

        StepGuideCard(
            stepNumber = "3",
            icon = Icons.Default.PhoneAndroid,
            title = "Download or Update via Play Store",
            description = "Open the Google Play Store on your Android phone. You will see '(Early Access)' next to Kavya Setu with instant updates."
        )

        StepGuideCard(
            stepNumber = "4",
            icon = Icons.Default.BugReport,
            title = "Submit Private Tester Feedback",
            description = "Your feedback and bug reports go directly to the developer without impacting public Play Store star ratings."
        )

        // --- 3. INTERACTIVE REGISTRATION FORM ---
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Request Internal Tester Access",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold,
                            fontSize = 14.sp
                        )
                    )
                }

                Text(
                    text = "Enter your Google Play account email (Gmail or Google Workspace) to request immediate addition to the testing list:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )

                OutlinedTextField(
                    value = testerEmail,
                    onValueChange = {
                        testerEmail = it
                        if (emailError) emailError = false
                    },
                    label = { Text("Your Google Account Email") },
                    placeholder = { Text("e.g. yourname@gmail.com") },
                    isError = emailError,
                    supportingText = {
                        if (emailError) {
                            Text("Please enter a valid email address", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("The developer will add this email to Google Play Console within 24h", color = MutedSlate)
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedLabelColor = AntiqueGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tester_email")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (testerEmail.isBlank() || !testerEmail.contains("@") || !testerEmail.contains(".")) {
                                emailError = true
                                Toast.makeText(context, "Please enter a valid Google Account email", Toast.LENGTH_SHORT).show()
                            } else {
                                val subject = "Kavya Setu Internal Tester Request - $testerEmail"
                                val body = """
                                    Hello Nihar,
                                    
                                    I would like to join the Google Play Internal Testing track for Kavya Setu: Poetry & Shayari.
                                    
                                    • My Google Account Email: $testerEmail
                                    • Current App Version: $APP_VERSION (Build $BUILD_NUMBER)
                                    • Package: $PACKAGE_NAME
                                    
                                    Please add me to the Google Play Console Internal Testers list.
                                    
                                    Thank you!
                                """.trimIndent()

                                val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$DEVELOPER_EMAIL")
                                    putExtra(Intent.EXTRA_SUBJECT, subject)
                                    putExtra(Intent.EXTRA_TEXT, body)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(mailIntent, "Send Request via Email"))
                                } catch (e: Exception) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Tester Request", body))
                                    Toast.makeText(context, "Request copied! Send to $DEVELOPER_EMAIL", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_submit_tester_request")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = DeepMidnight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Email Request",
                            color = DeepMidnight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Developer Email", DEVELOPER_EMAIL))
                            Toast.makeText(context, "Developer email copied: $DEVELOPER_EMAIL", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AntiqueGold),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_copy_dev_email")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Contact",
                            color = AntiqueGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // --- 4. ACTION BUTTONS: OPEN PLAY STORE LINKS ---
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Direct Play Store Opt-in Links",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold,
                        fontSize = 14.sp
                    )
                )

                Text(
                    text = "Once your email has been approved by the developer, tap below to open the Google Play Internal Test opt-in invitation:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_TESTING_WEB_URL))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open browser", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MysticTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_open_optin_link")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Open Web Opt-in",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Play Store Testing Link", PLAY_TESTING_WEB_URL))
                            Toast.makeText(context, "Testing URL copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MysticTeal),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_copy_optin_link")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = MysticTealSoft,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy URL",
                            color = MysticTealSoft,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // --- 5. FREQUENTLY ASKED QUESTIONS (EXPANDABLE) ---
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFaqDetails = !showFaqDetails }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Internal Tester FAQ & Guidelines",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = if (showFaqDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle FAQ",
                        tint = AntiqueGold
                    )
                }

                AnimatedVisibility(
                    visible = showFaqDetails,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        HorizontalDivider(color = SurfaceCardBorder)

                        FaqItem(
                            question = "Is participation in internal testing free?",
                            answer = "Yes, 100% free of charge. You will receive all experimental AI models, audio features, and calligraphy themes without fees."
                        )

                        FaqItem(
                            question = "Will I lose my saved poems or bookmarked verses?",
                            answer = "No. All poems, anthologies, and reading streaks are securely persisted in your local Room Database. Test builds seamlessly upgrade over previous versions."
                        )

                        FaqItem(
                            question = "How quickly are internal builds released?",
                            answer = "Google Play Internal Testing builds deploy directly within 5 to 15 minutes of upload by the developer, completely bypassing standard days-long review queues."
                        )

                        FaqItem(
                            question = "How do I opt out or return to the public version?",
                            answer = "You can leave the testing program at any time by revisiting the opt-in web link and clicking 'Leave the program', or by reinstalling the stable app from Google Play."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildMetaItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MutedSlate
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold
        )
    }
}

@Composable
private fun StepGuideCard(
    stepNumber: String,
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = AntiqueGold.copy(alpha = 0.15f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stepNumber,
                        color = AntiqueGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "• $question",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold
        )
        Text(
            text = answer,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
