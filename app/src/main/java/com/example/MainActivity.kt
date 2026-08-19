package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.StudioViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.LiveProcessDialog
import com.example.ui.components.Screen
import com.example.ui.screens.*
import com.example.ui.theme.AICodingStudioTheme

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AICodingStudioTheme {
        val navController = rememberNavController()
        val viewModel: StudioViewModel = viewModel()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

        val projects by viewModel.projects.collectAsState()
        val providers by viewModel.aiProviders.collectAsState()
        val repos by viewModel.gitRepos.collectAsState()
        val chatLogs by viewModel.chatLogs.collectAsState()
        val selectedProjectId by viewModel.selectedProjectId.collectAsState()
        val files by viewModel.projectFiles.collectAsState()
        val activeFile by viewModel.activeFile.collectAsState()
        val discoveredModels by viewModel.discoveredModels.collectAsState()

        val isProcessRunning by viewModel.isProcessRunning.collectAsState()
        val processTitle by viewModel.processTitle.collectAsState()
        val processSteps by viewModel.processSteps.collectAsState()

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            TopAppBar(
              title = { Text("AI Coding Studio") },
              actions = {
                IconButton(onClick = { navController.navigate(Screen.Terminal.route) }) {
                  Icon(imageVector = Icons.Default.Terminal, contentDescription = "Terminal")
                }
                IconButton(onClick = { navController.navigate(Screen.Analytics.route) }) {
                  Icon(imageVector = Icons.Default.Analytics, contentDescription = "Analytics")
                }
                IconButton(onClick = { navController.navigate(Screen.AIProviders.route) }) {
                  Icon(imageVector = Icons.Default.SmartToy, contentDescription = "AI Providers")
                }
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                  Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
              }
            )
          },
          bottomBar = {
            BottomNavBar(
              currentRoute = currentRoute,
              onNavigate = { route ->
                if (route != currentRoute) {
                  navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              }
            )
          }
        ) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
          ) {
            composable(Screen.Dashboard.route) {
              DashboardScreen(
                projects = projects,
                providers = providers,
                repos = repos,
                chatLogs = chatLogs,
                onNavigate = { route -> navController.navigate(route) }
              )
            }
            composable(Screen.Projects.route) {
              ProjectListScreen(
                projects = projects,
                selectedProjectId = selectedProjectId,
                onSelectProject = { id -> viewModel.selectProject(id) },
                onCreateProject = { name, desc, lang ->
                  viewModel.createProject(name, desc, lang)
                },
                onNavigateToEditor = { navController.navigate(Screen.Editor.route) }
              )
            }
            composable(Screen.Editor.route) {
              CodeEditorScreen(
                files = files,
                activeFile = activeFile,
                onSelectFile = { file -> viewModel.selectFile(file) },
                onUpdateContent = { file, content ->
                  viewModel.updateFileContent(file, content)
                },
                onRunTerminal = { navController.navigate(Screen.Terminal.route) }
              )
            }
            composable(Screen.AIAssistant.route) {
              AIAssistantScreen(
                providers = providers,
                chatLogs = chatLogs,
                onRunAI = { prov, key, model, task, prompt, cb ->
                  viewModel.runAIAction(prov, key, model, task, prompt, cb)
                }
              )
            }
            composable(Screen.AIProviders.route) {
              AIProvidersScreen(
                providers = providers,
                discoveredModels = discoveredModels,
                onSaveProvider = { prov, key, model ->
                  viewModel.saveAIProvider(prov, key, model)
                }
              )
            }
            composable(Screen.GitHub.route) {
              GitHubScreen(
                repos = repos,
                onGitAction = { action, repoName ->
                  viewModel.runGitAction(action, repoName)
                },
                onConnectRepo = { name, owner, url, token ->
                  viewModel.connectRepo(name, owner, url, token)
                },
                onCreateRepo = { username, repoName, ghpToken, desc ->
                  viewModel.createGitHubRepository(username, repoName, ghpToken, desc)
                }
              )
            }
            composable(Screen.Snippets.route) {
              CodeSnippetsScreen(
                onInsertSnippet = { snippetCode ->
                  activeFile?.let { file ->
                    viewModel.updateFileContent(file, file.content + "\n\n" + snippetCode)
                  }
                  navController.navigate(Screen.Editor.route)
                }
              )
            }
            composable(Screen.Analytics.route) {
              AnalyticsScreen(chatLogs = chatLogs)
            }
            composable(Screen.Terminal.route) { TerminalScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
          }

          if (isProcessRunning) {
            LiveProcessDialog(
              title = processTitle,
              steps = processSteps,
              onDismiss = { viewModel.dismissProcess() }
            )
          }
        }
      }
    }
  }
}
