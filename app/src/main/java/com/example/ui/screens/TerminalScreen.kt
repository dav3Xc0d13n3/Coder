package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TerminalLog(val command: String, val output: String, val isError: Boolean = false)

@Composable
fun TerminalScreen() {
  var currentCommand by remember { mutableStateOf("") }
  var terminalLogs by remember {
    mutableStateOf(
      listOf(
        TerminalLog("git status", "On branch main\nChanges not staged for commit:\n  modified:   MainActivity.kt"),
        TerminalLog("gradle assembleDebug", "BUILD SUCCESSFUL in 2.4s\nAPK generated at /app/build/outputs/apk/debug/app-debug.apk")
      )
    )
  }

  Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Integrated Terminal (Bash / Zsh)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Terminal Output Box
    Card(
      modifier = Modifier.fillMaxWidth().weight(1f),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(terminalLogs) { log ->
          Column {
            Text(
              text = "$ ${log.command}",
              style = MaterialTheme.typography.bodyMedium,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = log.output,
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              color = if (log.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Command Input Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = currentCommand,
        onValueChange = { currentCommand = it },
        placeholder = { Text("Enter terminal command (e.g., git commit, gradle build)...") },
        modifier = Modifier.weight(1f),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        shape = RoundedCornerShape(16.dp)
      )
      IconButton(
        onClick = {
          if (currentCommand.isNotBlank()) {
            val cmd = currentCommand
            val output =
              when {
                cmd.startsWith("git") -> "Executed Git command '$cmd': Success."
                cmd.startsWith("gradle") -> "Gradle build task executed successfully."
                cmd == "ls" -> "app/  gradle/  build.gradle.kts  settings.gradle.kts"
                else -> "Command executed: $cmd -> OK"
              }
            terminalLogs = terminalLogs + TerminalLog(cmd, output)
            currentCommand = ""
          }
        },
        modifier =
          Modifier.size(56.dp)
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Run",
          tint = MaterialTheme.colorScheme.onPrimary
        )
      }
    }
  }
}
