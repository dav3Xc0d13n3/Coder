package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AIProviderEntity
import com.example.data.ChatLogEntity
import com.example.data.GitRepoEntity
import com.example.data.ProjectEntity

@Composable
fun DashboardScreen(
  projects: List<ProjectEntity>,
  providers: List<AIProviderEntity>,
  repos: List<GitRepoEntity>,
  chatLogs: List<ChatLogEntity>,
  onNavigate: (String) -> Unit
) {
  val connectedProviders = providers.filter { it.isConnected }
  val activeProvider = connectedProviders.firstOrNull() ?: providers.firstOrNull()
  val activeRepo = repos.firstOrNull()

  LazyColumn(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                "AI Coding Studio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "VS Code + GitHub Desktop + AI Studio Workspace",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Box(
              modifier =
                Modifier.size(48.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Stats Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            StatCard(
              title = "Projects",
              value = projects.size.toString(),
              icon = Icons.Default.Folder,
              modifier = Modifier.weight(1f)
            )
            StatCard(
              title = "AI Providers",
              value = "${connectedProviders.size}/${providers.size}",
              icon = Icons.Default.SmartToy,
              modifier = Modifier.weight(1f)
            )
            StatCard(
              title = "Git Repos",
              value = repos.size.toString(),
              icon = Icons.Default.Source,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // Active AI & Model Badge
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Row(
          modifier = Modifier.padding(16.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier =
                Modifier.size(12.dp)
                  .clip(CircleShape)
                  .background(
                    if (activeProvider?.isConnected == true)
                      MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                  )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = activeProvider?.providerName ?: "No Provider Connected",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Model: ${activeProvider?.selectedModel ?: "Select in AI Providers"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          FilledTonalButton(onClick = { onNavigate("ai_providers") }) {
            Text("Manage AI")
          }
        }
      }
    }

    // Quick Action Hub
    item {
      Text(
        "Quick Workflows",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
          ActionChip(
            title = "AI Code Assistant",
            icon = Icons.Default.AutoAwesome,
            onClick = { onNavigate("ai_assistant") }
          )
        }
        item {
          ActionChip(
            title = "Code Editor",
            icon = Icons.Default.Code,
            onClick = { onNavigate("editor") }
          )
        }
        item {
          ActionChip(
            title = "GitHub Sync",
            icon = Icons.Default.CloudSync,
            onClick = { onNavigate("github") }
          )
        }
        item {
          ActionChip(
            title = "Terminal",
            icon = Icons.Default.Terminal,
            onClick = { onNavigate("terminal") }
          )
        }
      }
    }

    // Recent Projects
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Recent Projects",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        TextButton(onClick = { onNavigate("projects") }) { Text("View All") }
      }
    }

    items(projects.take(3)) { project ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { onNavigate("editor") }
      ) {
        Row(
          modifier = Modifier.padding(16.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                text = project.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text(project.language, modifier = Modifier.padding(4.dp))
          }
        }
      }
    }

    // Recent AI Logs
    if (chatLogs.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          "Recent AI Activity",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
      items(chatLogs.take(2)) { log ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = log.provider,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = log.model,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Prompt: ${log.prompt}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
      Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun ActionChip(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
  ElevatedCard(onClick = onClick, shape = RoundedCornerShape(16.dp)) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Spacer(modifier = Modifier.width(8.dp))
      Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
  }
}
