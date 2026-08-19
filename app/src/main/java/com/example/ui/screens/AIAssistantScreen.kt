package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AIProviderEntity
import com.example.data.ChatLogEntity

@Composable
fun AIAssistantScreen(
  providers: List<AIProviderEntity>,
  chatLogs: List<ChatLogEntity>,
  onRunAI: (String, String, String, String, String, (String) -> Unit) -> Unit
) {
  val activeProvider = providers.firstOrNull { it.isConnected } ?: providers.firstOrNull()
  var prompt by remember { mutableStateOf("") }
  var selectedTask by remember { mutableStateOf("Generate Code") }
  val tasks = listOf("Generate Code", "Debug Code", "Refactor Code", "Explain Code", "Generate Tests")
  var aiOutput by remember { mutableStateOf("") }

  Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)
  ) {
    Text(
      "AI Coding Assistant",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      "Powered by ${activeProvider?.providerName ?: "AI Provider"} (${activeProvider?.selectedModel ?: "Default Model"})",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Task Selector
    ScrollableTabRow(
      selectedTabIndex = tasks.indexOf(selectedTask),
      edgePadding = 0.dp,
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
      indicator = {},
      divider = {}
    ) {
      tasks.forEach { task ->
        Tab(
          selected = selectedTask == task,
          onClick = { selectedTask = task },
          text = { Text(task) },
          modifier =
            Modifier.padding(horizontal = 4.dp)
              .background(
                if (selectedTask == task) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
              )
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Prompt Input
    OutlinedTextField(
      value = prompt,
      onValueChange = { prompt = it },
      label = { Text("Describe what you want to build or fix...") },
      modifier = Modifier.fillMaxWidth().height(110.dp),
      shape = RoundedCornerShape(16.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    Button(
      onClick = {
        if (prompt.isNotBlank() && activeProvider != null) {
          onRunAI(
            activeProvider.providerName,
            activeProvider.apiKey,
            activeProvider.selectedModel,
            selectedTask,
            prompt
          ) { result ->
            aiOutput = result
          }
        }
      },
      modifier = Modifier.fillMaxWidth().height(50.dp),
      shape = RoundedCornerShape(16.dp)
    ) {
      Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Execute AI Action")
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Response Box / Recent Logs
    if (aiOutput.isNotBlank()) {
      Card(
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
          Text("AI Response Output:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          LazyColumn {
            item {
              Text(
                text = aiOutput,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          Text("Recent AI Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(chatLogs.take(5)) { log ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(text = log.prompt, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = log.response.take(120) + "...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
