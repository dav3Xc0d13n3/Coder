package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ProjectEntity

@Composable
fun ProjectListScreen(
  projects: List<ProjectEntity>,
  selectedProjectId: Long?,
  onSelectProject: (Long) -> Unit,
  onCreateProject: (String, String, String) -> Unit,
  onNavigateToEditor: () -> Unit
) {
  var showCreateDialog by remember { mutableStateOf(false) }
  var newName by remember { mutableStateOf("") }
  var newDesc by remember { mutableStateOf("") }
  var newLang by remember { mutableStateOf("Kotlin") }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showCreateDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "New Project")
      }
    }
  ) { padding ->
    LazyColumn(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Text(
          "Code Projects",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          "Manage, build, and refactor code repositories",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
      }

      items(projects) { project ->
        val isSelected = project.id == selectedProjectId
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors =
            CardDefaults.cardColors(
              containerColor =
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            ),
          onClick = {
            onSelectProject(project.id)
            onNavigateToEditor()
          }
        ) {
          Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(16.dp))
              Column {
                Text(
                  text = project.name,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = project.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
              Text(project.language, modifier = Modifier.padding(6.dp))
            }
          }
        }
      }
    }
  }

  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      title = { Text("Create New Project") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newDesc,
            onValueChange = { newDesc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newLang,
            onValueChange = { newLang = it },
            label = { Text("Language / Stack (Kotlin, Python, React, Docker)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newName.isNotBlank()) {
              onCreateProject(newName, newDesc, newLang)
              showCreateDialog = false
              newName = ""
              newDesc = ""
            }
          }
        ) {
          Text("Create")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
      },
      shape = RoundedCornerShape(24.dp)
    )
  }
}
