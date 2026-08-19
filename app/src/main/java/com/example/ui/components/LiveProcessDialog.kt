package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.git.GitProcessStep

@Composable
fun LiveProcessDialog(
  title: String,
  steps: List<GitProcessStep>,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = {},
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
          modifier = Modifier.size(24.dp),
          strokeWidth = 2.dp,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)))
        
        Spacer(modifier = Modifier.height(4.dp))

        steps.forEach { step ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            when {
              step.isCompleted -> {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Completed",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
              step.isInProgress -> {
                CircularProgressIndicator(
                  modifier = Modifier.size(18.dp),
                  strokeWidth = 2.dp,
                  color = MaterialTheme.colorScheme.secondary
                )
              }
              else -> {
                Box(
                  modifier =
                    Modifier.size(18.dp)
                      .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                      )
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = step.title,
              style = MaterialTheme.typography.bodyMedium,
              color =
                if (step.isCompleted || step.isInProgress)
                  MaterialTheme.colorScheme.onSurface
                else
                  MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
          }
        }
      }
    },
    confirmButton = {
      val isAllDone = steps.all { it.isCompleted }
      if (isAllDone) {
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
          Text("Done")
        }
      }
    },
    shape = RoundedCornerShape(24.dp)
  )
}
