package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
  object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
  object Projects : Screen("projects", "Projects", Icons.Default.Folder)
  object Editor : Screen("editor", "Editor", Icons.Default.Code)
  object AIAssistant : Screen("ai_assistant", "AI Studio", Icons.Default.AutoAwesome)
  object GitHub : Screen("github", "GitHub", Icons.Default.CloudSync)
  object Snippets : Screen("snippets", "Snippets", Icons.Default.Description)
  object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
  object AIProviders : Screen("ai_providers", "Providers", Icons.Default.SmartToy)
  object Terminal : Screen("terminal", "Terminal", Icons.Default.Terminal)
  object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
  val items =
    listOf(
      Screen.Dashboard,
      Screen.Projects,
      Screen.Editor,
      Screen.AIAssistant,
      Screen.GitHub,
      Screen.Snippets,
      Screen.Analytics
    )

  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    items.forEach { screen ->
      NavigationBarItem(
        icon = { Icon(screen.icon, contentDescription = screen.title) },
        label = { Text(screen.title) },
        selected = currentRoute == screen.route,
        onClick = { onNavigate(screen.route) }
      )
    }
  }
}
