package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultItemUploadStatus
import com.example.data.model.VaultSyncProgressState
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.VelvetRose

/**
 * Animated UI status banner and live progress bar dashboard for Offline Vault synchronization.
 * Triggers automatically when the device regains connectivity (or on manual trigger),
 * displaying individual animated progress bars as each poem/couplet is uploaded to Firestore.
 */
@Composable
fun OfflineVaultSyncAnimationCard(
    syncState: VaultSyncProgressState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible = syncState.isSyncing || syncState.isCompleted

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeIn(animationSpec = tween(300)),
        exit = shrinkVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "vault_sync_rotation")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin_angle"
        )

        val animatedOverallProgress by animateFloatAsState(
            targetValue = syncState.overallProgress.coerceIn(0f, 1f),
            animationSpec = tween(300, easing = FastOutSlowInEasing),
            label = "overall_progress_anim"
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                1.5.dp,
                if (syncState.isCompleted) Color(0xFF2ECC71).copy(alpha = 0.6f) else AntiqueGold.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("offline_vault_sync_animation_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (syncState.isCompleted) Color(0xFF0F2B1D) else Color(0xFF1E1A29),
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row: Pulsing Cloud icon, Title, Status Badge, and Dismiss
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (syncState.isCompleted) Color(0xFF2ECC71).copy(alpha = 0.2f) else AntiqueGold.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (syncState.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = "Sync Complete",
                                            tint = Color(0xFF2ECC71),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.CloudUpload,
                                            contentDescription = "Syncing to Firestore",
                                            tint = AntiqueGold,
                                            modifier = Modifier
                                                .size(22.dp)
                                                .rotate(if (syncState.isSyncing) rotationAngle else 0f)
                                        )
                                    }
                                }
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (syncState.isCompleted) "Vault Synced with Cloud" else "Offline Vault Synchronizing",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (syncState.isCompleted) Color(0xFF2ECC71) else AntiqueGold
                                    )
                                    if (syncState.isConnectivityRegained) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF2ECC71).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "⚡ Online",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2ECC71),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (syncState.isCompleted) {
                                        "${syncState.successCount} of ${syncState.totalItems} poems verified in Firestore"
                                    } else {
                                        "Uploading item ${syncState.currentItemIndex + 1} of ${syncState.totalItems} to Firestore"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp).testTag("dismiss_vault_sync_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Overall Progress Bar & Percentage
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (syncState.isCompleted) "All items synchronized" else "Overall Vault Upload",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${(animatedOverallProgress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (syncState.isCompleted) Color(0xFF2ECC71) else AntiqueGold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { animatedOverallProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("vault_overall_progress_bar"),
                            color = if (syncState.isCompleted) Color(0xFF2ECC71) else AntiqueGold,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    }

                    // Individual Items List with per-item progress bars
                    if (syncState.items.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Items Uploading to Firestore Database:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )

                            // Show up to 4 items or scrollable if more
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                syncState.items.forEachIndexed { index, item ->
                                    VaultIndividualItemProgressRow(
                                        item = item,
                                        isCurrent = syncState.isSyncing && syncState.currentItemIndex == index
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

/**
 * Individual poem item row showing per-item progress bar and upload status to Firestore.
 */
@Composable
fun VaultIndividualItemProgressRow(
    item: VaultItemUploadStatus,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = item.progress.coerceIn(0f, 1f),
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "item_progress_${item.shayariId}"
    )

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isCurrent) AntiqueGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
        border = BorderStroke(
            0.5.dp,
            if (item.isUploaded) Color(0xFF2ECC71).copy(alpha = 0.4f)
            else if (isCurrent) AntiqueGold.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("vault_item_upload_row_${item.shayariId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (item.isUploaded) "✓" else if (isCurrent) "⏳" else "•",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isUploaded) Color(0xFF2ECC71) else AntiqueGold
                    )
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.poet,
                        fontSize = 10.sp,
                        color = AntiqueGold,
                        maxLines = 1
                    )
                    if (item.isUploaded) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Uploaded to Firestore",
                            tint = Color(0xFF2ECC71),
                            modifier = Modifier.size(14.dp)
                        )
                    } else if (isCurrent) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = AntiqueGold,
                            strokeWidth = 1.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Queued",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Per-Item Animated Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .testTag("item_progress_bar_${item.shayariId}"),
                color = if (item.isUploaded) Color(0xFF2ECC71) else if (item.isFailed) VelvetRose else AntiqueGold,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
