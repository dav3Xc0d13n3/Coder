package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FileEntity

@Composable
fun CodeEditorScreen(
  files: List<FileEntity>,
  activeFile: FileEntity?,
  onSelectFile: (FileEntity) -> Unit,
  onUpdateContent: (FileEntity, String) -> Unit,
  onRunTerminal: () -> Unit
) {
  var editableContent by remember(activeFile) { mutableStateOf(activeFile?.content ?: "") }
  var isSaved by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
  ) {
    // Editor Top Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(files) { file ->
            val isActive = file.id == activeFile?.id
            FilterChip(
              selected = isActive,
              onClick = { onSelectFile(file) },
              label = { Text(file.name) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Description,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
              }
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(
            onClick = {
              if (activeFile != null) {
                onUpdateContent(activeFile, editableContent)
                isSaved = true
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.Save,
              contentDescription = "Save",
              tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
          }
          IconButton(onClick = onRunTerminal) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Run",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    // Main Code Area
    Box(
      modifier = Modifier.fillMaxSize().weight(1f).padding(8.dp)
    ) {
      if (activeFile != null) {
        OutlinedTextField(
          value = editableContent,
          onValueChange = {
            editableContent = it
            isSaved = false
          },
          modifier = Modifier.fillMaxSize(),
          textStyle =
            TextStyle(
              fontFamily = FontFamily.Monospace,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface
            ),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
          shape = RoundedCornerShape(12.dp)
        )
      } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = null,
              modifier = Modifier.size(64.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              "No file selected",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
