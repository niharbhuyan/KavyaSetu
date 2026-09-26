package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Anthology
import com.example.ui.components.AccountPreferencesSection
import com.example.ui.components.BetaTestingDialog
import com.example.ui.components.BetaTestingSection
import com.example.ui.components.GeneratedPoemHistorySection
import com.example.ui.components.PlayStoreMediaKitDialog
import com.example.ui.components.ReadingProgressSection
import com.example.util.PlayStoreAssetHelper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.ActivityType
import com.example.data.model.Emotion
import com.example.data.model.Language
import com.example.data.model.Shayari
import com.example.data.model.UserActivityItem
import com.example.ui.MainViewModel
import com.example.ui.components.AudioReciter
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.ChangePasswordDialog
import com.example.ui.components.DeleteAccountDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.ForgotPasswordDialog
import com.example.ui.components.ShayariCard
import com.example.ui.components.SignInDialog
import com.example.ui.components.SignUpDialog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.MysticTeal
import com.example.ui.theme.MysticTealSoft
import com.example.ui.theme.SoftGold
import com.example.ui.theme.VelvetRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    audioReciter: AudioReciter,
    onNavigateToAiStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val savedShayaris by viewModel.savedShayaris.collectAsStateWithLifecycle()
    val activities by viewModel.userActivities.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsStateWithLifecycle()
    val anthologies by viewModel.anthologies.collectAsStateWithLifecycle()
    val allShayaris by viewModel.allShayaris.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val generatedHistory by viewModel.generatedPoemHistory.collectAsStateWithLifecycle()
    val dailyPickNotificationEnabled by viewModel.dailyPickNotificationEnabled.collectAsStateWithLifecycle()
    val poetryFontSizeSp by viewModel.poetryFontSizeSp.collectAsStateWithLifecycle()
    val poetryLineHeightMult by viewModel.poetryLineHeightMult.collectAsStateWithLifecycle()
    val poetryFontFamilyType by viewModel.poetryFontFamilyType.collectAsStateWithLifecycle()
    val fcmToken by viewModel.fcmToken.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSavedLanguageFilter by remember { mutableStateOf("all") }
    var cardStudioShayari by remember { mutableStateOf<Shayari?>(null) }
    var showCreateAnthologyDialog by remember { mutableStateOf(false) }
    var selectedAnthologyForDetail by remember { mutableStateOf<Anthology?>(null) }
    var shayariForAnthologyPicker by remember { mutableStateOf<Shayari?>(null) }

    // Dialog state controllers
    var showSignInDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPlayStoreMediaKitDialog by remember { mutableStateOf(false) }
    var showBetaTestingDialog by remember { mutableStateOf(false) }

    if (showCreateAnthologyDialog) {
        CreateAnthologyDialog(
            onDismiss = { showCreateAnthologyDialog = false },
            onCreate = { title, desc, icon ->
                viewModel.createAnthology(title, desc, icon)
                showCreateAnthologyDialog = false
            }
        )
    }

    if (selectedAnthologyForDetail != null) {
        AnthologyDetailDialog(
            anthology = selectedAnthologyForDetail!!,
            allShayaris = allShayaris,
            audioReciter = audioReciter,
            onDismiss = { selectedAnthologyForDetail = null },
            onRemoveShayari = { sId ->
                viewModel.toggleShayariInAnthology(selectedAnthologyForDetail!!.id, sId)
            }
        )
    }

    if (shayariForAnthologyPicker != null) {
        AddToAnthologyDialog(
            shayari = shayariForAnthologyPicker!!,
            anthologies = anthologies,
            onDismiss = { shayariForAnthologyPicker = null },
            onToggleAnthology = { aId ->
                viewModel.toggleShayariInAnthology(aId, shayariForAnthologyPicker!!.id)
            }
        )
    }

    if (cardStudioShayari != null) {
        CardStudioDialog(
            shayari = cardStudioShayari!!,
            onDismiss = { cardStudioShayari = null }
        )
    }

    if (showSignInDialog) {
        SignInDialog(
            isLoading = isAuthLoading,
            onDismiss = { showSignInDialog = false },
            onSignIn = { email, pass ->
                viewModel.signInWithEmail(email, pass)
                showSignInDialog = false
            },
            onNavigateToSignUp = {
                showSignInDialog = false
                showSignUpDialog = true
            },
            onForgotPassword = {
                showSignInDialog = false
                showForgotPasswordDialog = true
            }
        )
    }

    if (showSignUpDialog) {
        SignUpDialog(
            isLoading = isAuthLoading,
            onDismiss = { showSignUpDialog = false },
            onSignUp = { email, pass, name, penName ->
                viewModel.signUpWithEmail(email, pass, name, penName)
                showSignUpDialog = false
            },
            onNavigateToSignIn = {
                showSignUpDialog = false
                showSignInDialog = true
            }
        )
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            isLoading = isAuthLoading,
            initialEmail = profile.email ?: "",
            onDismiss = { showForgotPasswordDialog = false },
            onSendReset = { email ->
                viewModel.sendPasswordReset(email)
                showForgotPasswordDialog = false
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isLoading = isAuthLoading,
            onDismiss = { showChangePasswordDialog = false },
            onUpdatePassword = { newPass ->
                viewModel.updatePassword(newPass)
                showChangePasswordDialog = false
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateUserProfile(updated)
                showEditProfileDialog = false
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            isLoading = isAuthLoading,
            onDismiss = { showDeleteAccountDialog = false },
            onConfirmDelete = {
                viewModel.deleteAccount()
                showDeleteAccountDialog = false
            }
        )
    }

    if (showPlayStoreMediaKitDialog) {
        PlayStoreMediaKitDialog(
            onDismiss = { showPlayStoreMediaKitDialog = false }
        )
    }

    if (showBetaTestingDialog) {
        BetaTestingDialog(
            onDismiss = { showBetaTestingDialog = false }
        )
    }

    val filteredSavedShayaris = remember(savedShayaris, selectedSavedLanguageFilter) {
        if (selectedSavedLanguageFilter == "all") {
            savedShayaris
        } else {
            savedShayaris.filter { it.language.equals(selectedSavedLanguageFilter, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feedback Banners
        if (authError != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VelvetRose.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, VelvetRose)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = VelvetRose)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = authError!!, color = VelvetRose, fontSize = 13.sp)
                        }
                        IconButton(onClick = { viewModel.clearAuthMessages() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = VelvetRose)
                        }
                    }
                }
            }
        }

        if (authSuccessMessage != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, AntiqueGold)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AntiqueGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = authSuccessMessage!!, color = AntiqueGold, fontSize = 13.sp)
                        }
                        IconButton(onClick = { viewModel.clearAuthMessages() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = AntiqueGold)
                        }
                    }
                }
            }
        }

        // Primary Poet Hero Profile Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar Badge
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(2.dp, AntiqueGold),
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = profile.displayName.take(1).uppercase(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Takhallus: '${profile.penName}'",
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                color = AntiqueGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Firebase Auth Status Indicator
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (profile.isGuest) AntiqueGold.copy(alpha = 0.15f) else VelvetRose.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        if (profile.isGuest) Icons.Default.AccountCircle else Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = if (profile.isGuest) AntiqueGold else VelvetRose
                                    )
                                    Text(
                                        text = if (profile.isGuest) "Guest Poet (Local Mode)" else (profile.email ?: "Firebase Account"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (profile.isGuest) AntiqueGold else VelvetRose
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = AntiqueGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "\"${profile.bio}\"",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Streak Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AntiqueGold.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "🔥", fontSize = 20.sp)
                                Column {
                                    Text(
                                        text = "${profile.streakDays} Day Reading Streak",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AntiqueGold
                                    )
                                    Text(
                                        text = "Engaging with daily wisdom across Hindi, Odia & English",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricItem(
                            value = "${readingProgress.readingStreakDays}d",
                            label = "Read Streak",
                            testTag = "metric_streak_count"
                        )
                        MetricItem(
                            value = "${readingProgress.totalVersesRead}",
                            label = "Verses Read",
                            testTag = "metric_read_count"
                        )
                        MetricItem(
                            value = "${generatedHistory.size}",
                            label = "AI Composed",
                            testTag = "metric_composed_count"
                        )
                        MetricItem(
                            value = "${savedShayaris.size}",
                            label = "Saved",
                            testTag = "metric_saved_count"
                        )
                    }
                }
            }
        }

        // Play Store Publication Quick-Access Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPlayStoreMediaKitDialog = true }
                    .testTag("banner_play_store_kit")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AntiqueGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🚀", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Google Play Store Media Kit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AntiqueGold
                            )
                            Text(
                                text = "512px Icon, 1024x500 Banner & 4 Screenshots",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { showPlayStoreMediaKitDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Open Kit", color = DeepMidnight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Beta Testing Quick-Access Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MysticTeal.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, MysticTeal.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTab = 7 }
                    .testTag("banner_beta_testing")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MysticTeal.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🧪", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Google Play Beta Testing",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MysticTealSoft
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MysticTeal.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "INTERNAL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MysticTealSoft,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Join early access track for upcoming builds & features",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { selectedTab = 7 },
                        colors = ButtonDefaults.buttonColors(containerColor = MysticTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Join Track", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Section Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = AntiqueGold
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Reading Progress") },
                    modifier = Modifier.testTag("tab_reading_progress")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("AI History (${generatedHistory.size})") },
                    modifier = Modifier.testTag("tab_ai_history")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Saved (${savedShayaris.size})") },
                    modifier = Modifier.testTag("tab_saved_shayari")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Anthologies (${anthologies.size})") },
                    modifier = Modifier.testTag("tab_anthologies")
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("Preferences & Auth") },
                    modifier = Modifier.testTag("tab_account_auth")
                )
                Tab(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    text = { Text("Activity & Insights") },
                    modifier = Modifier.testTag("tab_activity_insights")
                )
                Tab(
                    selected = selectedTab == 6,
                    onClick = { selectedTab = 6 },
                    text = { Text("Play Store Kit 🚀") },
                    modifier = Modifier.testTag("tab_play_store_kit")
                )
                Tab(
                    selected = selectedTab == 7,
                    onClick = { selectedTab = 7 },
                    text = { Text("Beta Testing 🧪") },
                    modifier = Modifier.testTag("tab_beta_testing")
                )
            }
        }

        // ================= TAB 0: READING PROGRESS =================
        if (selectedTab == 0) {
            item {
                ReadingProgressSection(
                    readingProgress = readingProgress,
                    onResumeReading = { poemId ->
                        viewModel.openShayariDetailById(poemId)
                    },
                    onClearBookmark = {
                        viewModel.clearBookmark(context)
                        Toast.makeText(context, "Bookmark cleared", Toast.LENGTH_SHORT).show()
                    },
                    onUpdateDailyGoal = { goal ->
                        viewModel.updateDailyGoal(context, goal)
                        Toast.makeText(context, "Daily reading goal set to $goal couplets", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // ================= TAB 1: AI POEM HISTORY =================
        if (selectedTab == 1) {
            item {
                GeneratedPoemHistorySection(
                    history = generatedHistory,
                    audioReciter = audioReciter,
                    onNavigateToAiStudio = onNavigateToAiStudio,
                    onDeletePoem = { id ->
                        viewModel.deleteGeneratedPoem(context, id)
                        Toast.makeText(context, "Poem removed from history", Toast.LENGTH_SHORT).show()
                    },
                    onClearHistory = {
                        viewModel.clearGeneratedPoemHistory(context)
                    }
                )
            }
        }

        // ================= TAB 2: SAVED SHAYARI =================
        if (selectedTab == 2) {
            // Language filters for saved verses
            if (savedShayaris.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "all" to "All (${savedShayaris.size})",
                            "hindi" to "हिंदी (${savedShayaris.count { it.language == "hindi" }})",
                            "odia" to "ଓଡ଼ିଆ (${savedShayaris.count { it.language == "odia" }})",
                            "english" to "English (${savedShayaris.count { it.language == "english" }})"
                        ).forEach { (langCode, label) ->
                            FilterChip(
                                selected = selectedSavedLanguageFilter == langCode,
                                onClick = { selectedSavedLanguageFilter = langCode },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AntiqueGold.copy(alpha = 0.25f),
                                    selectedLabelColor = AntiqueGold
                                )
                            )
                        }
                    }
                }
            }

            if (filteredSavedShayaris.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AntiqueGold.copy(alpha = 0.15f),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = AntiqueGold,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (savedShayaris.isEmpty()) "No saved couplets in your vault" else "No saved couplets in this language",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Tap the bookmark icon on any Shayari card in the Feed or Explore tab to save it to your personal vault for offline reading.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredSavedShayaris,
                    key = { it.id }
                ) { shayari ->
                    ShayariCard(
                        shayari = shayari,
                        onLikeClick = { viewModel.toggleLike(shayari) },
                        onSaveClick = { viewModel.toggleSave(shayari) },
                        onDownloadClick = { viewModel.toggleDownload(shayari) },
                        onCardClick = { viewModel.openShayariDetail(shayari) },
                        onReciteClick = { lines, lang ->
                            audioReciter.speak(lines, lang)
                            viewModel.recordUserActivity(
                                ActivityType.RECITED,
                                "Audio Recited",
                                "Listened to: \"${lines.take(25)}...\""
                            )
                        },
                        onOpenCardStudio = {
                            cardStudioShayari = it
                            viewModel.recordUserActivity(
                                ActivityType.CARD_EXPORT,
                                "Visual Card Studio",
                                "Created quote art for ${it.author}"
                            )
                        },
                        onAnalyzeWithGemini = {
                            viewModel.analyzeWithHighThinking(it.lines, it.language)
                            onNavigateToAiStudio()
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { shayariForAnthologyPicker = shayari }
                        ) {
                            Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add to Anthology", color = AntiqueGold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // ================= TAB 3: CURATED ANTHOLOGIES =================
        if (selectedTab == 3) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CollectionsBookmark, contentDescription = null, tint = AntiqueGold)
                                Text(
                                    text = "Curated Poetic Anthologies",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                            }
                            Button(
                                onClick = { showCreateAnthologyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Anthology", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Group verses into personalized anthologies for focused reading and sequential recital sessions.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (anthologies.isEmpty()) {
                item {
                    Text(
                        text = "No anthologies yet. Tap '+ New Anthology' to create your first themed playlist.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(anthologies, key = { it.id }) { anthology ->
                    val containedShayaris = remember(anthology.shayariIds, allShayaris) {
                        allShayaris.filter { anthology.shayariIds.contains(it.id) }
                    }
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("anthology_card_${anthology.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = AntiqueGold.copy(alpha = 0.15f),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = anthology.icon, fontSize = 20.sp)
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = anthology.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${containedShayaris.size} Couplets",
                                            fontSize = 12.sp,
                                            color = AntiqueGold,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.deleteAnthology(anthology.id) }
                                ) {
                                    Icon(Icons.Default.DeleteForever, contentDescription = "Delete anthology", tint = VelvetRose.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                                }
                            }

                            if (anthology.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = anthology.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }

                            if (containedShayaris.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        containedShayaris.take(2).forEach { s ->
                                            Text(
                                                text = "• \"${s.lines.lineSequence().firstOrNull() ?: s.lines}\"",
                                                fontSize = 11.sp,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                        if (containedShayaris.size > 2) {
                                            Text(
                                                text = "+ ${containedShayaris.size - 2} more verses...",
                                                fontSize = 10.sp,
                                                color = AntiqueGold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (containedShayaris.isNotEmpty()) {
                                            val fullSequence = containedShayaris.joinToString("\n\n... ... ...\n\n") { it.lines }
                                            audioReciter.speak(fullSequence, "hindi")
                                            viewModel.recordUserActivity(
                                                ActivityType.RECITED,
                                                "Anthology Recital",
                                                "Reciting: ${anthology.title}"
                                            )
                                        } else {
                                            Toast.makeText(context, "No verses in this anthology yet.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = AntiqueGold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recite All", fontSize = 12.sp, color = AntiqueGold)
                                }

                                Button(
                                    onClick = { selectedAnthologyForDetail = anthology },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VelvetRose.copy(alpha = 0.2f), contentColor = VelvetRose)
                                ) {
                                    Icon(Icons.Default.CollectionsBookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View Verses", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 5: ACTIVITY & INSIGHTS =================
        if (selectedTab == 5) {
            // Poetic Emotional Palette Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = VelvetRose)
                            Text(
                                text = "Emotional Resonance Insights",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                        }
                        Text(
                            text = "Analysis of the sentiments that inspire your reading and composition habits:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        EmotionalDistributionBar("❤️ Ishq (Love & Devotion)", 0.45f, AntiqueGold)
                        EmotionalDistributionBar("🥀 Dard (Solitude & Longing)", 0.25f, VelvetRose)
                        EmotionalDistributionBar("🦅 Hausla (Courage & Resilience)", 0.18f, Color(0xFFE5A93C))
                        EmotionalDistributionBar("🕊️ Sufi (Mystical Peace)", 0.12f, Color(0xFF64B5F6))
                    }
                }
            }

            // Language Engagement Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Multilingual Exploration Bridge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LanguageChip("हिंदी (Hindi)", "50%", AntiqueGold)
                            LanguageChip("ଓଡ଼ିଆ (Odia)", "35%", VelvetRose)
                            LanguageChip("English", "15%", SoftGold)
                        }
                    }
                }
            }

            // Activity Timeline Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = AntiqueGold)
                    Text(
                        text = "Recent Poetic Activity Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (activities.isEmpty()) {
                item {
                    Text(
                        text = "No recent activity recorded yet.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(activities, key = { it.id }) { activity ->
                    ActivityTimelineItem(activity = activity)
                }
            }
        }

        // ================= TAB 4: ACCOUNT PREFERENCES & AUTH =================
        if (selectedTab == 4) {
            // Daily Notifications & Reading Preferences
            item {
                AccountPreferencesSection(
                    dailyPushEnabled = dailyPickNotificationEnabled,
                    onToggleDailyPush = { enabled ->
                        viewModel.setDailyNotificationEnabled(context, enabled)
                    },
                    onTestPush = {
                        viewModel.triggerTestFcmPush(context)
                    },
                    fontSize = poetryFontSizeSp,
                    lineHeightMult = poetryLineHeightMult,
                    fontFamily = poetryFontFamilyType,
                    onUpdateTypography = { size, lh, fam ->
                        viewModel.updatePoetryDisplaySettings(context, size, lh, fam)
                    },
                    fcmToken = fcmToken,
                    onTriggerHourlySync = {
                        viewModel.triggerHourlySyncNow(context)
                    },
                    onOpenBetaTesting = {
                        selectedTab = 7
                    }
                )
            }

            // Firebase Auth Status Overview
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Firebase Auth Status",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                )
                                Text(
                                    text = if (profile.isGuest) "Unauthenticated Guest Session" else "Authenticated Cloud Account",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (profile.isGuest) Color(0xFF4A3E1E) else Color(0xFF1E3A2A)
                            ) {
                                Text(
                                    text = if (profile.isGuest) "GUEST" else "FIREBASE AUTH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (profile.isGuest) AntiqueGold else Color(0xFF81C784),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = AntiqueGold.copy(alpha = 0.2f))

                        AccountDetailRow(
                            label = "Poet UID",
                            value = profile.uid.take(16) + if (profile.uid.length > 16) "..." else ""
                        )

                        AccountDetailRow(
                            label = "Registered Email",
                            value = profile.email ?: "None (Guest mode)"
                        )

                        AccountDetailRow(
                            label = "Auth Provider",
                            value = when (profile.authProvider) {
                                "password" -> "Firebase Email & Password"
                                "google" -> "Google Identity Services"
                                else -> "Anonymous / Local Guest"
                            }
                        )

                        AccountDetailRow(
                            label = "Email Verified",
                            value = if (profile.isGuest) "N/A" else if (profile.isEmailVerified) "Verified ✓" else "Unverified ⚠️"
                        )

                        if (!profile.isGuest && !profile.isEmailVerified) {
                            OutlinedButton(
                                onClick = { viewModel.sendEmailVerification() },
                                modifier = Modifier.fillMaxWidth().testTag("send_email_verification_button")
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Resend Email Verification Link")
                            }
                        }
                    }
                }
            }

            // Authentication Actions: Sign In / Register OR Manage Account
            if (profile.isGuest) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, VelvetRose.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Sign In to Sync Your Poetry",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = VelvetRose
                            )
                            Text(
                                text = "Connect your account using Firebase Authentication to securely back up your saved verses, synchronized across devices, and share under your recognized pen name.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showSignInDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                                    modifier = Modifier.weight(1f).testTag("profile_signin_button")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sign In")
                                }

                                OutlinedButton(
                                    onClick = { showSignUpDialog = true },
                                    modifier = Modifier.weight(1f).testTag("profile_signup_button")
                                ) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Register")
                                }
                            }
                        }
                    }
                }
            } else {
                // Logged-in Account Management actions
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Account Security & Credentials",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )

                            OutlinedButton(
                                onClick = { showChangePasswordDialog = true },
                                modifier = Modifier.fillMaxWidth().testTag("change_password_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Firebase Password")
                            }

                            OutlinedButton(
                                onClick = { viewModel.signOut() },
                                modifier = Modifier.fillMaxWidth().testTag("sign_out_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out of Firebase")
                            }

                            Button(
                                onClick = { showDeleteAccountDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = VelvetRose.copy(alpha = 0.25f), contentColor = VelvetRose),
                                modifier = Modifier.fillMaxWidth().testTag("delete_account_button")
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp), tint = VelvetRose)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Firebase Account")
                            }
                        }
                    }
                }
            }

            // Morning Push Notifications Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AntiqueGold)
                            Text(
                                text = "Morning Couplet Notification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Scheduled daily at 8:00 AM. Delivers trending verses in Hindi, Odia, and English directly to your notification shade.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.triggerTestNotification(context)
                                Toast.makeText(context, "Morning Notification sent to status bar!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                            modifier = Modifier.fillMaxWidth().testTag("profile_test_notification_button")
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Morning Notification Now")
                        }
                    }
                }
            }

            // App Information & Creator Attribution
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DeepMidnight,
                                border = BorderStroke(1.5.dp, AntiqueGold),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Kavya Setu App Logo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                            Column {
                                Text(
                                    text = "Kavya Setu • काव्यसेतु • କାବ୍ୟସେତୁ",
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Built by Nihar Sales",
                                    fontWeight = FontWeight.SemiBold,
                                    color = VelvetRose,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Kavya Setu bridges hearts across cultures with native support for:\n• Hindi (Devanagari script)\n• Odia (ଓଡ଼ିଆ ଲିପି)\n• English\n\nFeatures full Gemini AI generation, Prompt Library categorized by mood, metrical analysis, visual card studio export, homescreen widget, content moderation, and morning push inspiration.",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // ================= TAB 6: PLAY STORE ASSETS & PUBLICATION =================
        if (selectedTab == 6) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AntiqueGold)
                                Text(
                                    text = "Google Play Store Media Kit",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = AntiqueGold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E3A2A)
                            ) {
                                Text(
                                    text = "100% COMPLIANT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF81C784),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "All required assets for Play Store publication have been crafted according to official Google Play policies:\n• 512×512 32-bit PNG App Icon (no rounded corner mask pre-baked)\n• 1024×500 PNG Feature Graphic (strictly no promotional buzzwords, ranking, or pricing)\n• 4 Phone Screenshots (1080×1920) showcasing real user journeys\n• Compliant Title (<= 30 chars) and Short Description (<= 80 chars)\n• Google Play Compliant Privacy Policy URL (hosted and offline in-app)\n• One-click export to phone gallery or native share sheet.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showPlayStoreMediaKitDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Open Asset Kit", color = DeepMidnight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { PlayStoreAssetHelper.saveAllAssetsToDevice(context) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AntiqueGold)
                            ) {
                                Text("Save All to Gallery", color = AntiqueGold, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 7: GOOGLE PLAY BETA TESTING =================
        if (selectedTab == 7) {
            item {
                BetaTestingSection(
                    onOpenPlayStoreKit = { selectedTab = 6 }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
private fun MetricItem(
    value: String,
    label: String,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = AntiqueGold
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AntiqueGold)
    }
}

@Composable
private fun EmotionalDistributionBar(title: String, percentage: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${(percentage * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage },
            color = color,
            trackColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun LanguageChip(name: String, percentage: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = percentage, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ActivityTimelineItem(activity: UserActivityItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = AntiqueGold.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = activity.type.emoji, fontSize = 20.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AntiqueGold
                    )
                    Text(
                        text = formatRelativeTime(activity.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = activity.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val elapsed = System.currentTimeMillis() - timestamp
    val minutes = elapsed / (1000 * 60)
    val hours = elapsed / (1000 * 60 * 60)
    val days = elapsed / (1000 * 60 * 60 * 24)

    return when {
        minutes < 2 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        else -> "${days}d ago"
    }
}

@Composable
fun CreateAnthologyDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String, icon: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("❤️") }
    val availableIcons = listOf("❤️", "🌧️", "🦚", "🌙", "🕊️", "🥀", "🦅", "📜", "☕", "🌌")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, AntiqueGold),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .testTag("create_anthology_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Anthology",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AntiqueGold)
                    }
                }

                Text(
                    text = "Pick an Emblem / Mood Icon:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    availableIcons.take(5).forEach { icon ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedIcon == icon) AntiqueGold.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (selectedIcon == icon) BorderStroke(2.dp, AntiqueGold) else null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedIcon = icon }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = icon, fontSize = 18.sp)
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    availableIcons.drop(5).forEach { icon ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedIcon == icon) AntiqueGold.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (selectedIcon == icon) BorderStroke(2.dp, AntiqueGold) else null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedIcon = icon }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = icon, fontSize = 18.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Anthology Title (e.g. Shaam-e-Ghazal)") },
                    modifier = Modifier.fillMaxWidth().testTag("anthology_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        focusedLabelColor = AntiqueGold
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description or Thematic Note") },
                    modifier = Modifier.fillMaxWidth().testTag("anthology_desc_input"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        focusedLabelColor = AntiqueGold
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onCreate(title, description, selectedIcon)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_anthology_button")
                    ) {
                        Text("Create", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AnthologyDetailDialog(
    anthology: Anthology,
    allShayaris: List<Shayari>,
    audioReciter: AudioReciter,
    onDismiss: () -> Unit,
    onRemoveShayari: (String) -> Unit
) {
    val verses = remember(anthology.shayariIds, allShayaris) {
        allShayaris.filter { anthology.shayariIds.contains(it.id) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, AntiqueGold),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .testTag("anthology_detail_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = anthology.icon, fontSize = 28.sp)
                        Column {
                            Text(
                                text = anthology.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = "${verses.size} couplets curated",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AntiqueGold)
                    }
                }

                if (anthology.description.isNotBlank()) {
                    Text(
                        text = anthology.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }

                if (verses.isNotEmpty()) {
                    Button(
                        onClick = {
                            val fullSequence = verses.joinToString("\n\n... ... ...\n\n") { it.lines }
                            audioReciter.speak(fullSequence, "hindi")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recite All Sequentially (Mushaira Mode)", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                if (verses.isEmpty()) {
                    Text(
                        text = "No verses assigned to this anthology yet.\nGo to the 'Saved' tab and tap 'Add to Anthology' under any verse!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(verses, key = { it.id }) { s ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = s.lines,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "— ${s.author} (${s.language})",
                                            fontSize = 11.sp,
                                            color = AntiqueGold
                                        )
                                    }
                                    IconButton(onClick = { onRemoveShayari(s.id) }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove from anthology",
                                            tint = VelvetRose,
                                            modifier = Modifier.size(18.dp)
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
}

@Composable
fun AddToAnthologyDialog(
    shayari: Shayari,
    anthologies: List<Anthology>,
    onDismiss: () -> Unit,
    onToggleAnthology: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, AntiqueGold),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .testTag("add_to_anthology_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add to Anthology",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AntiqueGold)
                    }
                }

                Text(
                    text = "\"${shayari.lines.lineSequence().firstOrNull() ?: shayari.lines}\"",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                if (anthologies.isEmpty()) {
                    Text(
                        text = "No anthologies created yet. Create one first in the Anthologies tab!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(anthologies, key = { it.id }) { anthology ->
                            val isIncluded = anthology.shayariIds.contains(shayari.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onToggleAnthology(anthology.id) }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = anthology.icon, fontSize = 20.sp)
                                    Column {
                                        Text(
                                            text = anthology.title,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${anthology.shayariIds.size} couplets",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Checkbox(
                                    checked = isIncluded,
                                    onCheckedChange = { onToggleAnthology(anthology.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AntiqueGold,
                                        checkmarkColor = DeepMidnight
                                    )
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold, contentColor = DeepMidnight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
