package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AutoPublishScreen
import com.example.ui.screens.AvatarGalleryScreen
import com.example.ui.screens.CreateVideoWizardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ScriptAssistantScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.VoiceStudioScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.data.analytics.AppAnalytics

enum class MainTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Estúdio", Icons.Default.VideoLibrary),
    CREATE("Criar", Icons.Default.AddCircle),
    EDITOR("Editor", Icons.Default.ContentCut),
    PLAYER("Simulador", Icons.Default.PlayCircle),
    AUTO_PUBLISH("Minhas Redes", Icons.Default.Share)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Firebase Analytics for tracking video generation, shares, and voice cloning
        AppAnalytics.init(this)
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    var showVoiceStudioModal by remember { mutableStateOf(false) }
    var showScriptAssistantModal by remember { mutableStateOf(false) }
    var showAvatarGalleryModal by remember { mutableStateOf(false) }

    val canGoBack = showVoiceStudioModal || showScriptAssistantModal || showAvatarGalleryModal || currentTab != MainTab.HOME
    BackHandler(enabled = canGoBack) {
        if (showVoiceStudioModal) {
            showVoiceStudioModal = false
        } else if (showScriptAssistantModal) {
            showScriptAssistantModal = false
        } else if (showAvatarGalleryModal) {
            showAvatarGalleryModal = false
        } else if (currentTab != MainTab.HOME) {
            currentTab = MainTab.HOME
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceVariant,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                MainTab.values().forEach { tab ->
                    val isSelected = currentTab == tab && !showVoiceStudioModal && !showScriptAssistantModal && !showAvatarGalleryModal
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            showVoiceStudioModal = false
                            showScriptAssistantModal = false
                            showAvatarGalleryModal = false
                            currentTab = tab
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BrandSecondary,
                            indicatorColor = BrandPrimary,
                            unselectedIconColor = Color(0xFFAFA7CD),
                            unselectedTextColor = Color(0xFFAFA7CD)
                        ),
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (showVoiceStudioModal) {
            VoiceStudioScreen(
                viewModel = viewModel,
                onBack = { showVoiceStudioModal = false },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (showScriptAssistantModal) {
            ScriptAssistantScreen(
                viewModel = viewModel,
                onBack = { showScriptAssistantModal = false },
                onNavigateToCreate = {
                    showScriptAssistantModal = false
                    currentTab = MainTab.CREATE
                },
                onNavigateToEditor = {
                    showScriptAssistantModal = false
                    currentTab = MainTab.EDITOR
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (showAvatarGalleryModal) {
            AvatarGalleryScreen(
                viewModel = viewModel,
                onBackClick = { showAvatarGalleryModal = false },
                onSelectAndProceed = { _, _ ->
                    showAvatarGalleryModal = false
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            when (currentTab) {
                MainTab.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCreate = { currentTab = MainTab.CREATE },
                        onNavigateToPlayer = { currentTab = MainTab.PLAYER },
                        onNavigateToEditor = { currentTab = MainTab.EDITOR },
                        onNavigateToVoiceStudio = { showVoiceStudioModal = true },
                        onNavigateToScriptAssistant = { showScriptAssistantModal = true },
                        onNavigateToAvatarGallery = { showAvatarGalleryModal = true },
                        onNavigateToPublish = { currentTab = MainTab.AUTO_PUBLISH },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainTab.CREATE -> {
                    CreateVideoWizardScreen(
                        viewModel = viewModel,
                        onNavigateToScriptAssistant = { showScriptAssistantModal = true },
                        onNavigateToAvatarGallery = { showAvatarGalleryModal = true },
                        onVideoCreated = {
                            currentTab = MainTab.EDITOR
                        },
                        onBackToMenu = {
                            currentTab = MainTab.HOME
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainTab.EDITOR -> {
                    VideoEditorScreen(
                        viewModel = viewModel,
                        onNavigateToPlayer = { currentTab = MainTab.PLAYER },
                        onNavigateToScriptAssistant = { showScriptAssistantModal = true },
                        onNavigateToAvatarGallery = { showAvatarGalleryModal = true },
                        onNavigateToPublish = { currentTab = MainTab.AUTO_PUBLISH },
                        onBackToMenu = {
                            currentTab = MainTab.HOME
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainTab.PLAYER -> {
                    VideoPlayerScreen(
                        viewModel = viewModel,
                        onNavigateToEditor = { currentTab = MainTab.EDITOR },
                        onNavigateToPublish = { currentTab = MainTab.AUTO_PUBLISH },
                        onBackToMenu = {
                            currentTab = MainTab.HOME
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainTab.AUTO_PUBLISH -> {
                    AutoPublishScreen(
                        viewModel = viewModel,
                        onNavigateToEditor = { projectId ->
                            projectId?.let { viewModel.loadProjectIntoEditorById(it) }
                            currentTab = MainTab.EDITOR
                        },
                        onBackToMenu = {
                            currentTab = MainTab.HOME
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
