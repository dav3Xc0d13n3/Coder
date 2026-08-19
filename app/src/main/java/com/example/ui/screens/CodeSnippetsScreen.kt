package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CodeSnippet(val title: String, val category: String, val code: String)

@Composable
fun CodeSnippetsScreen(onInsertSnippet: (String) -> Unit) {
  val snippets = listOf(
    CodeSnippet(
      title = "Jetpack Compose Scaffold",
      category = "Android UI",
      code = "@Composable\nfun SampleScreen() {\n    Scaffold(\n        topBar = { TopAppBar(title = { Text(\"Sample\") }) }\n    ) { padding ->\n        Box(modifier = Modifier.padding(padding))\n    }\n}"
    ),
    CodeSnippet(
      title = "Room Database Entity",
      category = "Database",
      code = "@Entity(tableName = \"notes\")\ndata class NoteEntity(@PrimaryKey val id: Long, val text: String)"
    ),
    CodeSnippet(
      title = "Retrofit Service",
      category = "Networking",
      code = "interface Api {\n    @GET(\"endpoint\")\n    suspend fun get(): Response\n}"
    )
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        "AI Code Snippets",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        "Reusable code templates and architecture patterns",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(snippets) { snippet ->
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
              Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(12.dp))
              Text(text = snippet.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
              Text(snippet.category, modifier = Modifier.padding(4.dp))
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = snippet.code,
              modifier = Modifier.padding(12.dp),
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = { onInsertSnippet(snippet.code) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Insert into Editor")
          }
        }
      }
    }
  }
}
