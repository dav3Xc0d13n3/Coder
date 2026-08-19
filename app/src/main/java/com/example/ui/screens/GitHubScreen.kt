package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.GitRepoEntity

@Composable
fun GitHubScreen(
  repos: List<GitRepoEntity>,
  onGitAction: (String, String) -> Unit,
  onConnectRepo: (String, String, String, String) -> Unit,
  onCreateRepo: (String, String, String, String) -> Unit
) {
  var showCreateDialog by remember { mutableStateOf(false) }
  var showConnectDialog by remember { mutableStateOf(false) }

  // Create Repo States (Username & GHP)
  var createUsername by remember { mutableStateOf("") }
  var createRepoName by remember { mutableStateOf("") }
  var createGhpToken by remember { mutableStateOf("") }
  var createDescription by remember { mutableStateOf("") }

  // Connect Repo States
  var connectOwner by remember { mutableStateOf("") }
  var connectRepoName by remember { mutableStateOf("") }
  var connectUrl by remember { mutableStateOf("") }
  var connectToken by remember { mutableStateOf("") }

  Scaffold(
    floatingActionButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FloatingActionButton(
          onClick = { showCreateDialog = true },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
          Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Create Repo")
        }
        FloatingActionButton(
          onClick = { showConnectDialog = true },
          containerColor = MaterialTheme.colorScheme.secondary,
          contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
          Icon(imageVector = Icons.Default.CloudSync, contentDescription = "Connect Repo")
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Text(
          "GitHub Desktop Workspace",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          "Create repositories, manage GHP tokens, push & pull deltas",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (repos.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier.padding(24.dp).fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Source,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text("No repositories connected", style = MaterialTheme.typography.titleMedium)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "Create a new repository or connect an existing one using your GitHub username and GHP token.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      items(repos) { repo ->
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Source,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "${repo.owner}/${repo.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Branch: ${repo.currentBranch} | Remote: ${repo.remoteUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text("Connected", modifier = Modifier.padding(4.dp))
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { onGitAction("Commit & Push", repo.name) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Push")
              }
              OutlinedButton(
                onClick = { onGitAction("Pull Changes", repo.name) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Pull")
              }
            }
          }
        }
      }
    }
  }

  // Create New Repository Dialog
  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      title = { Text("Create New GitHub Repository") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = createUsername,
            onValueChange = { createUsername = it },
            label = { Text("GitHub Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = createRepoName,
            onValueChange = { createRepoName = it },
            label = { Text("Repository Name") },
            leadingIcon = { Icon(Icons.Default.Source, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = createGhpToken,
            onValueChange = { createGhpToken = it },
            label = { Text("GitHub Personal Access Token (GHP)") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = createDescription,
            onValueChange = { createDescription = it },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (createUsername.isNotBlank() && createRepoName.isNotBlank() && createGhpToken.isNotBlank()) {
              onCreateRepo(createUsername, createRepoName, createGhpToken, createDescription)
              showCreateDialog = false
              createUsername = ""
              createRepoName = ""
              createGhpToken = ""
              createDescription = ""
            }
          }
        ) {
          Text("Create Repository")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
      },
      shape = RoundedCornerShape(24.dp)
    )
  }

  // Connect Existing Repository Dialog
  if (showConnectDialog) {
    AlertDialog(
      onDismissRequest = { showConnectDialog = false },
      title = { Text("Connect Existing Repository") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = connectOwner,
            onValueChange = { connectOwner = it },
            label = { Text("GitHub Username / Owner") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = connectRepoName,
            onValueChange = { connectRepoName = it },
            label = { Text("Repository Name") },
            leadingIcon = { Icon(Icons.Default.Source, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = connectUrl,
            onValueChange = { connectUrl = it },
            label = { Text("Remote URL (https://github.com/...)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = connectToken,
            onValueChange = { connectToken = it },
            label = { Text("GitHub GHP Token") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (connectRepoName.isNotBlank() && connectOwner.isNotBlank() && connectToken.isNotBlank()) {
              onConnectRepo(connectRepoName, connectOwner, connectUrl.ifBlank { "https://github.com/$connectOwner/$connectRepoName.git" }, connectToken)
              showConnectDialog = false
              connectOwner = ""
              connectRepoName = ""
              connectUrl = ""
              connectToken = ""
            }
          }
        ) {
          Text("Connect & Clone")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConnectDialog = false }) { Text("Cancel") }
      },
      shape = RoundedCornerShape(24.dp)
    )
  }
}
