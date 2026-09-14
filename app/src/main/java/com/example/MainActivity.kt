package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Shayari
import com.example.notification.ShayariFirebaseMessagingService
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.components.AudioReciter
import com.example.ui.components.CardStudioDialog
import com.example.ui.components.FcmNotificationDialog
import com.example.ui.components.ShayariDetailDialog
import com.example.ui.components.VirtualMehfilDialog
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ModerationScreen
import com.example.ui.screens.OfflineScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.ShayariTheme
import com.example.ui.theme.VelvetRose

class MainActivity : ComponentActivity() {
    private var audioReciter: AudioReciter? = null
    private val pendingDeepLinkShayariId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioReciter = AudioReciter(this)
        handleDeepLinkIntent(intent)

        setContent {
            ShayariTheme(darkTheme = true) {
                MainAppContainer(
                    audioReciter = audioReciter!!,
                    incomingDeepLinkId = pendingDeepLinkShayariId.value,
                    onDeepLinkConsumed = { pendingDeepLinkShayariId.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        if (intent == null) return
        var shayariId = intent.getStringExtra(ShayariFirebaseMessagingService.EXTRA_SHAYARI_ID)
            ?: intent.getStringExtra("shayari_id")
            ?: intent.getStringExtra("id")

        val dataUri = intent.data
        if (shayariId == null && dataUri != null) {
            shayariId = dataUri.getQueryParameter("id")
                ?: dataUri.lastPathSegment?.takeIf { it != "detail" && it.isNotEmpty() }
        }

        if (!shayariId.isNullOrBlank()) {
            pendingDeepLinkShayariId.value = shayariId
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioReciter?.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    audioReciter: AudioReciter,
    incomingDeepLinkId: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val app = context.applicationContext as ShayariApplication
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(app.repository)
    )

    var currentNavigationTab by remember { mutableIntStateOf(0) }
    var showFcmDialog by remember { mutableStateOf(false) }
    var showVirtualMehfil by remember { mutableStateOf(false) }
    var detailCardStudioShayari by remember { mutableStateOf<Shayari?>(null) }

    val selectedDetail by viewModel.selectedDetailShayari.collectAsStateWithLifecycle()
    val fcmToken by viewModel.fcmToken.collectAsStateWithLifecycle()
    val isAnalyzingWithHighThinking by viewModel.isAnalyzingWithHighThinking.collectAsStateWithLifecycle()

    // Initialize FCM and retrieve token
    LaunchedEffect(Unit) {
        viewModel.initFcm(context)
    }

    // Process incoming deep link from FCM push notification or deep link URL
    LaunchedEffect(incomingDeepLinkId) {
        if (!incomingDeepLinkId.isNullOrBlank()) {
            viewModel.openShayariDetailById(incomingDeepLinkId)
            onDeepLinkConsumed()
        }
    }

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val downloadedCount by viewModel.downloadedCount.collectAsStateWithLifecycle()
    val pendingSubmissions by viewModel.pendingModerationSubmissions.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kavya Setu • काव्यसेतु • କାବ୍ୟସେତୁ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = AntiqueGold
                        )
                        Text(
                            text = "Built by Nihar Sales",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Offline Vault shortcut with badge
                    IconButton(
                        onClick = { currentNavigationTab = 2 },
                        modifier = Modifier.testTag("appbar_offline_action")
                    ) {
                        BadgedBox(
                            badge = {
                                if (downloadedCount > 0) {
                                    Badge(containerColor = AntiqueGold, contentColor = DeepMidnight) {
                                        Text("$downloadedCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderSpecial,
                                contentDescription = "Offline Vault",
                                tint = if (currentNavigationTab == 2) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Content Moderation console shortcut with badge
                    IconButton(
                        onClick = { currentNavigationTab = 3 },
                        modifier = Modifier.testTag("appbar_moderation_action")
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingSubmissions.isNotEmpty()) {
                                    Badge(containerColor = VelvetRose, contentColor = Color.White) {
                                        Text("${pendingSubmissions.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Content Moderation Console",
                                tint = if (currentNavigationTab == 3) VelvetRose else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Live Virtual Mehfil Room
                    IconButton(
                        onClick = { showVirtualMehfil = true },
                        modifier = Modifier.testTag("appbar_mehfil_action")
                    ) {
                        Text("🌙", fontSize = 18.sp)
                    }

                    // Firebase Cloud Messaging & Morning Notification Center
                    IconButton(
                        onClick = {
                            showFcmDialog = true
                        },
                        modifier = Modifier.testTag("appbar_notification_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "FCM Morning Push Center",
                            tint = AntiqueGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = AntiqueGold
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentNavigationTab == 0,
                    onClick = { currentNavigationTab = 0 },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Feed")
                    },
                    label = { Text("Feed", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepMidnight,
                        selectedTextColor = AntiqueGold,
                        indicatorColor = AntiqueGold,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_feed")
                )

                NavigationBarItem(
                    selected = currentNavigationTab == 1,
                    onClick = { currentNavigationTab = 1 },
                    icon = {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Studio")
                    },
                    label = { Text("AI Studio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepMidnight,
                        selectedTextColor = AntiqueGold,
                        indicatorColor = AntiqueGold,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_ai")
                )

                NavigationBarItem(
                    selected = currentNavigationTab == 2,
                    onClick = { currentNavigationTab = 2 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (downloadedCount > 0) {
                                    Badge(containerColor = AntiqueGold, contentColor = DeepMidnight) {
                                        Text("$downloadedCount", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Offline")
                        }
                    },
                    label = { Text("Offline", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepMidnight,
                        selectedTextColor = AntiqueGold,
                        indicatorColor = AntiqueGold,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_offline")
                )

                NavigationBarItem(
                    selected = currentNavigationTab == 3,
                    onClick = { currentNavigationTab = 3 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (pendingSubmissions.isNotEmpty()) {
                                    Badge(containerColor = VelvetRose, contentColor = Color.White) {
                                        Text("${pendingSubmissions.size}", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Moderation")
                        }
                    },
                    label = { Text("Mod Review", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepMidnight,
                        selectedTextColor = AntiqueGold,
                        indicatorColor = AntiqueGold,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_moderation")
                )

                NavigationBarItem(
                    selected = currentNavigationTab == 4,
                    onClick = { currentNavigationTab = 4 },
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepMidnight,
                        selectedTextColor = AntiqueGold,
                        indicatorColor = AntiqueGold,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        floatingActionButton = {
            if (currentNavigationTab == 0) {
                FloatingActionButton(
                    onClick = { currentNavigationTab = 1 },
                    containerColor = VelvetRose,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_compose")
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Compose with AI",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentNavigationTab) {
                0 -> FeedScreen(
                    viewModel = viewModel,
                    audioReciter = audioReciter,
                    onNavigateToAiStudio = { currentNavigationTab = 1 }
                )
                1 -> AiStudioScreen(
                    viewModel = viewModel,
                    audioReciter = audioReciter
                )
                2 -> OfflineScreen(
                    viewModel = viewModel,
                    audioReciter = audioReciter,
                    onNavigateToAiStudio = { currentNavigationTab = 1 }
                )
                3 -> ModerationScreen(
                    viewModel = viewModel
                )
                4 -> ProfileScreen(
                    viewModel = viewModel,
                    audioReciter = audioReciter,
                    onNavigateToAiStudio = { currentNavigationTab = 1 }
                )
            }

            // Deep Link Details Dialog
            if (selectedDetail != null) {
                val detail = selectedDetail!!
                ShayariDetailDialog(
                    shayari = detail,
                    onDismiss = { viewModel.closeShayariDetail() },
                    onLikeClick = { viewModel.toggleLike(detail) },
                    onSaveClick = { viewModel.toggleSave(detail) },
                    onDownloadClick = { viewModel.toggleDownload(detail) },
                    onReciteClick = { lines, lang -> audioReciter.speak(lines, lang) },
                    onOpenCardStudio = { shayariToRender ->
                        detailCardStudioShayari = shayariToRender
                    },
                    onPinWidget = { ctx ->
                        viewModel.pinHomescreenWidget(ctx) { pinned ->
                            if (pinned) {
                                Toast.makeText(ctx, "Widget pinned to home screen!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(ctx, "Widget ready! Long press home screen to add.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    onAnalyzeWithGemini = { shayariToAnalyze ->
                        viewModel.analyzeWithHighThinking(shayariToAnalyze.lines, shayariToAnalyze.language)
                    },
                    isAnalyzing = isAnalyzingWithHighThinking
                )
            }

            // Card Studio Dialog from Details View
            if (detailCardStudioShayari != null) {
                CardStudioDialog(
                    shayari = detailCardStudioShayari!!,
                    onDismiss = { detailCardStudioShayari = null }
                )
            }

            // FCM Configuration & Test Dialog
            if (showFcmDialog) {
                FcmNotificationDialog(
                    fcmToken = fcmToken,
                    onDismiss = { showFcmDialog = false },
                    onSendFcmPush = {
                        viewModel.triggerTestFcmPush(context)
                    },
                    onSendLocalAlarm = {
                        viewModel.triggerTestNotification(context)
                    },
                    onOpenDetailsDirectly = {
                        viewModel.openShayariDetailById("daily_pick")
                    }
                )
            }

            // Virtual Mehfil Dialog
            if (showVirtualMehfil) {
                val allShayaris by viewModel.allShayaris.collectAsStateWithLifecycle()
                VirtualMehfilDialog(
                    allShayaris = allShayaris,
                    audioReciter = audioReciter,
                    onDismiss = { showVirtualMehfil = false }
                )
            }
        }
    }
}
