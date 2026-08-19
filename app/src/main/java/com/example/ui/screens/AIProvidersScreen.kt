package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ai.AIModel
import com.example.data.AIProviderEntity

@Composable
fun AIProvidersScreen(
  providers: List<AIProviderEntity>,
  discoveredModels: List<AIModel>,
  onSaveProvider: (String, String, String) -> Unit
) {
  var selectedProvider by remember { mutableStateOf<AIProviderEntity?>(null) }
  var apiKeyInput by remember { mutableStateOf("") }
  var selectedModelInput by remember { mutableStateOf("") }

  LaunchedEffect(providers) {
    if (providers.isNotEmpty() && selectedProvider == null) {
      selectedProvider = providers.first()
      apiKeyInput = providers.first().apiKey
      selectedModelInput = providers.first().selectedModel
    }
  }

  LazyColumn(
    modifier =
      Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        "AI Provider Management",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        "Connect AI providers with automated model discovery",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Configure: ${selectedProvider?.providerName ?: "Select Provider"}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("API Key") },
            leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = selectedModelInput,
            onValueChange = { selectedModelInput = it },
            label = { Text("Model ID (e.g. Gemini 2.5 Pro, Nemotron 70B)") },
            leadingIcon = { Icon(imageVector = Icons.Default.SmartToy, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              selectedProvider?.let {
                onSaveProvider(it.providerName, apiKeyInput, selectedModelInput)
              }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Validate & Discover Models")
          }
        }
      }
    }

    item {
      Text(
        "Supported AI Providers",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    items(providers) { provider ->
      val isCurrent = selectedProvider?.providerName == provider.providerName
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
          CardDefaults.cardColors(
            containerColor =
              if (isCurrent) MaterialTheme.colorScheme.primaryContainer
              else MaterialTheme.colorScheme.surface
          ),
        onClick = {
          selectedProvider = provider
          apiKeyInput = provider.apiKey
          selectedModelInput = provider.selectedModel
        }
      ) {
        Row(
          modifier = Modifier.padding(16.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.SmartToy,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                text = provider.providerName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Model: ${provider.selectedModel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          if (provider.isConnected) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Connected",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}
