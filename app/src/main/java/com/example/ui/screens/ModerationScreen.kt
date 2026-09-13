package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ModerationCheckResult
import com.example.data.model.ModerationSeverity
import com.example.data.model.Shayari
import com.example.ui.MainViewModel
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.VelvetRose

@Composable
fun ModerationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allSubmissions by viewModel.allModerationSubmissions.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var rejectingShayari by remember { mutableStateOf<Shayari?>(null) }
    var rejectionReason by remember { mutableStateOf("Commercial Spam / Advertising") }

    val pendingList = allSubmissions.filter { it.moderationStatus == "PENDING" }
    val flaggedList = allSubmissions.filter { it.moderationStatus == "FLAGGED" }
    val approvedList = allSubmissions.filter { it.moderationStatus == "APPROVED" }
    val rejectedList = allSubmissions.filter { it.moderationStatus == "REJECTED" }

    // Dialog for custom rejection
    if (rejectingShayari != null) {
        val shayariToReject = rejectingShayari!!
        val reasons = listOf(
            "Commercial Spam / Advertising",
            "Abusive or Vulgar Language",
            "Harassment or Hate Speech",
            "Copyright / Low Quality Content",
            "Other Policy Violation"
        )
        AlertDialog(
            onDismissRequest = { rejectingShayari = null },
            title = {
                Text(
                    "Reject Submission",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "Select violation category for this rejection:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = rejectionReason == r,
                                onClick = { rejectionReason = r }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(r, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectSubmission(shayariToReject.id, rejectionReason)
                        Toast.makeText(context, "Submission rejected", Toast.LENGTH_SHORT).show()
                        rejectingShayari = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VelvetRose)
                ) {
                    Text("Confirm Rejection", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingShayari = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("moderation_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header: Moderation Console
        item(key = "mod_header") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF281427),
                                    Color(0xFF1B142F),
                                    Color(0xFF0F0C1E)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AntiqueGold.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Content Moderation Console",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Multi-language automated filters & admin review queue",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatPill(
                                label = "Pending",
                                count = pendingList.size,
                                color = Color(0xFFF39C12),
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                label = "Flagged",
                                count = flaggedList.size,
                                color = VelvetRose,
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                label = "Approved",
                                count = approvedList.size,
                                color = Color(0xFF2ECC71),
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                label = "Rejected",
                                count = rejectedList.size,
                                color = Color(0xFF95A5A6),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Tabs Row: Pending, Flagged, Approved, Rejected, Keyword Scanner
        item(key = "mod_tabs") {
            val tabs = listOf(
                "Pending (${pendingList.size})",
                "Flagged (${flaggedList.size})",
                "Approved (${approvedList.size})",
                "Rejected (${rejectedList.size})",
                "Rule Scanner"
            )
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = AntiqueGold
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> { // Pending
                if (pendingList.isEmpty()) {
                    item {
                        EmptyModerationState(
                            message = "No pending submissions awaiting review.",
                            icon = Icons.Default.Check
                        )
                    }
                } else {
                    items(pendingList, key = { it.id }) { item ->
                        ModerationItemCard(
                            shayari = item,
                            onApprove = {
                                viewModel.approveSubmission(item.id)
                                Toast.makeText(context, "Submission approved for public feed", Toast.LENGTH_SHORT).show()
                            },
                            onReject = { rejectingShayari = item },
                            onFlag = {
                                viewModel.flagSubmission(item.id, "Flagged by reviewer for escalation")
                                Toast.makeText(context, "Marked as Flagged", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            1 -> { // Flagged
                if (flaggedList.isEmpty()) {
                    item {
                        EmptyModerationState(
                            message = "Zero flagged submissions! The repository is clean.",
                            icon = Icons.Default.Shield
                        )
                    }
                } else {
                    items(flaggedList, key = { it.id }) { item ->
                        ModerationItemCard(
                            shayari = item,
                            onApprove = {
                                viewModel.approveSubmission(item.id)
                                Toast.makeText(context, "Flag overridden: Approved", Toast.LENGTH_SHORT).show()
                            },
                            onReject = { rejectingShayari = item },
                            onFlag = {
                                Toast.makeText(context, "Already flagged", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            2 -> { // Approved
                items(approvedList, key = { it.id }) { item ->
                    ModerationItemCard(
                        shayari = item,
                        onApprove = null,
                        onReject = { rejectingShayari = item },
                        onFlag = {
                            viewModel.flagSubmission(item.id, "Flagged post-approval")
                            Toast.makeText(context, "Flagged", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            3 -> { // Rejected
                if (rejectedList.isEmpty()) {
                    item {
                        EmptyModerationState(
                            message = "No rejected submissions.",
                            icon = Icons.Default.Check
                        )
                    }
                } else {
                    items(rejectedList, key = { it.id }) { item ->
                        ModerationItemCard(
                            shayari = item,
                            onApprove = {
                                viewModel.approveSubmission(item.id)
                                Toast.makeText(context, "Reinstated & Approved", Toast.LENGTH_SHORT).show()
                            },
                            onReject = null,
                            onFlag = null
                        )
                    }
                }
            }
            4 -> { // Rule & Keyword Scanner
                item {
                    AutomatedScannerTool(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = color.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun ModerationItemCard(
    shayari: Shayari,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?,
    onFlag: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mod_item_${shayari.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(
            1.dp,
            when (shayari.moderationStatus) {
                "FLAGGED" -> VelvetRose.copy(alpha = 0.7f)
                "PENDING" -> Color(0xFFF39C12).copy(alpha = 0.5f)
                "APPROVED" -> Color(0xFF2ECC71).copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Status badge & language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val (badgeBg, badgeTextColor, badgeLabel) = when (shayari.moderationStatus) {
                        "FLAGGED" -> Triple(VelvetRose.copy(alpha = 0.2f), VelvetRose, "⚠️ Auto-Flagged")
                        "PENDING" -> Triple(Color(0xFFF39C12).copy(alpha = 0.2f), Color(0xFFF39C12), "⏳ Pending Review")
                        "APPROVED" -> Triple(Color(0xFF2ECC71).copy(alpha = 0.2f), Color(0xFF2ECC71), "✅ Approved")
                        else -> Triple(Color(0xFF95A5A6).copy(alpha = 0.2f), Color(0xFF95A5A6), "❌ Rejected")
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = badgeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "• ${shayari.language.replaceFirstChar { it.uppercase() }}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "ID: ${shayari.id.take(10)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Couplet text
            Text(
                text = shayari.lines,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "— ${shayari.author}${if (shayari.penName.isNotBlank()) " '${shayari.penName}'" else ""}",
                fontSize = 12.sp,
                color = AntiqueGold
            )

            // Automated Diagnostic Report
            if (!shayari.moderationReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    border = BorderStroke(
                        1.dp,
                        if (shayari.moderationStatus == "FLAGGED") VelvetRose.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (shayari.moderationStatus == "FLAGGED") VelvetRose else AntiqueGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Automated Policy Check Log:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = shayari.moderationReason ?: "",
                            fontSize = 12.sp,
                            color = if (shayari.moderationStatus == "FLAGGED") VelvetRose else MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Admin Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onFlag != null) {
                    IconButton(
                        onClick = onFlag,
                        modifier = Modifier.size(36.dp).testTag("mod_flag_${shayari.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Flag for Audit",
                            tint = Color(0xFFF39C12),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                if (onReject != null) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VelvetRose),
                        border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("mod_reject_${shayari.id}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (onApprove != null) {
                    Button(
                        onClick = onApprove,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("mod_approve_${shayari.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AutomatedScannerTool(viewModel: MainViewModel) {
    val input by viewModel.testScanInput.collectAsStateWithLifecycle()
    val result by viewModel.testScanResult.collectAsStateWithLifecycle()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Policy,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Automated Keyword & Pattern Scanner",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Test verse text against automated profanity, commercial spam, URL, phone number, and hate speech filters in English, Hindi, and Odia.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = input,
                onValueChange = { viewModel.runModerationScan(it) },
                label = { Text("Paste or type couplet to test...") },
                placeholder = { Text("e.g. Visit http://spam.xyz or call 9876543210 for free shayari") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("moderation_scanner_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = {
                            viewModel.runModerationScan("Join our WhatsApp group wa.me/shayari or call 9876543210 for paid promo!")
                        }
                    ) {
                        Text("Try Spam", fontSize = 11.sp)
                    }
                    FilledTonalButton(
                        onClick = {
                            viewModel.runModerationScan("हज़ारों ख़्वाहिशें ऐसी कि हर ख़्वाहिश पे दम निकले\nबहुत निकले मिरे अरमान लेकिन फिर भी कम निकले")
                        }
                    ) {
                        Text("Try Clean", fontSize = 11.sp)
                    }
                }

                if (input.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearModerationScan() }) {
                        Text("Clear", color = VelvetRose)
                    }
                }
            }

            // Diagnostic Results
            if (result != null) {
                val res = result!!
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Scan Diagnostic Results:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Risk Score Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calculated Risk Score: ${(res.riskScore * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (res.severity) {
                            ModerationSeverity.HIGH_RISK -> VelvetRose
                            ModerationSeverity.SUSPICIOUS -> Color(0xFFF39C12)
                            ModerationSeverity.SAFE -> Color(0xFF27AE60)
                        }
                    )
                    Text(
                        text = "Severity: ${res.severity.label}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { res.riskScore },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when (res.severity) {
                        ModerationSeverity.HIGH_RISK -> VelvetRose
                        ModerationSeverity.SUSPICIOUS -> Color(0xFFF39C12)
                        ModerationSeverity.SAFE -> Color(0xFF27AE60)
                    },
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "System Action: Auto-Assign Status → [ ${res.suggestedStatus} ]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold
                )

                if (res.detectedIssues.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detected Policy Violations:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VelvetRose
                    )
                    res.detectedIssues.forEach { issue ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ", color = VelvetRose, fontSize = 14.sp)
                            Text(issue, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✓ Zero policy violations found. Content is safe for public distribution.",
                        fontSize = 12.sp,
                        color = Color(0xFF27AE60)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyModerationState(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AntiqueGold,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
