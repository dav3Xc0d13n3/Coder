package com.example.git

import kotlinx.coroutines.delay

data class GitProcessStep(val title: String, val isCompleted: Boolean, val isInProgress: Boolean)

object GitManager {

  suspend fun executeGitActionWithProgress(
    actionName: String,
    onStepUpdate: (List<GitProcessStep>) -> Unit
  ) {
    val steps =
      mutableListOf(
        GitProcessStep("Connecting to GitHub API...", false, false),
        GitProcessStep("Validating repository & credentials...", false, false),
        GitProcessStep("Preparing workspace delta...", false, false),
        GitProcessStep("Executing $actionName...", false, false),
        GitProcessStep("Finalizing synchronization...", false, false),
        GitProcessStep("Completed successfully", false, false)
      )

    for (i in steps.indices) {
      // Set current step in progress
      val currentList = steps.mapIndexed { index, step ->
        when {
          index < i -> step.copy(isCompleted = true, isInProgress = false)
          index == i -> step.copy(isCompleted = false, isInProgress = true)
          else -> step.copy(isCompleted = false, isInProgress = false)
        }
      }
      onStepUpdate(currentList)
      delay(400)
    }

    // Mark all completed
    val finalList = steps.map { it.copy(isCompleted = true, isInProgress = false) }
    onStepUpdate(finalList)
    delay(300)
  }
}
