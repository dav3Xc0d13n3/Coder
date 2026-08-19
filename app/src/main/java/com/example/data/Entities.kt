package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val description: String,
  val language: String,
  val updatedAt: Long = System.currentTimeMillis(),
  val isFavorite: Boolean = false
)

@Entity(tableName = "files")
data class FileEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val projectId: Long,
  val name: String,
  val path: String,
  val content: String
)

@Entity(tableName = "ai_providers")
data class AIProviderEntity(
  @PrimaryKey val providerName: String, // e.g. "Google Gemini", "NVIDIA AI", "OpenAI", etc.
  val apiKey: String,
  val isConnected: Boolean,
  val selectedModel: String,
  val availableModelsJson: String // comma separated or JSON string
)

@Entity(tableName = "git_repos")
data class GitRepoEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val owner: String,
  val currentBranch: String,
  val remoteUrl: String,
  val isConnected: Boolean,
  val accessToken: String
)

@Entity(tableName = "chat_logs")
data class ChatLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val prompt: String,
  val response: String,
  val provider: String,
  val model: String,
  val timestamp: Long = System.currentTimeMillis()
)
