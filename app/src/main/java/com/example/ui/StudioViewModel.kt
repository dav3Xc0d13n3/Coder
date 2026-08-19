package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIModel
import com.example.ai.AIProviderManager
import com.example.data.*
import com.example.git.GitManager
import com.example.git.GitProcessStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val projectDao = database.projectDao()
  private val fileDao = database.fileDao()
  private val providerDao = database.aiProviderDao()
  private val repoDao = database.gitRepoDao()
  private val chatLogDao = database.chatLogDao()

  val projects: StateFlow<List<ProjectEntity>> =
    projectDao.getAllProjects().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val aiProviders: StateFlow<List<AIProviderEntity>> =
    providerDao.getAllProviders().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val gitRepos: StateFlow<List<GitRepoEntity>> =
    repoDao.getAllRepos().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val chatLogs: StateFlow<List<ChatLogEntity>> =
    chatLogDao.getAllChatLogs().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  private val _selectedProjectId = MutableStateFlow<Long?>(null)
  val selectedProjectId: StateFlow<Long?> = _selectedProjectId.asStateFlow()

  val projectFiles: StateFlow<List<FileEntity>> =
    _selectedProjectId
      .flatMapLatest { id ->
        if (id != null) fileDao.getFilesForProject(id) else flowOf(emptyList())
      }
      .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  private val _activeFile = MutableStateFlow<FileEntity?>(null)
  val activeFile: StateFlow<FileEntity?> = _activeFile.asStateFlow()

  // Live Process Tracking state
  private val _isProcessRunning = MutableStateFlow(false)
  val isProcessRunning: StateFlow<Boolean> = _isProcessRunning.asStateFlow()

  private val _processTitle = MutableStateFlow("")
  val processTitle: StateFlow<String> = _processTitle.asStateFlow()

  private val _processSteps = MutableStateFlow<List<GitProcessStep>>(emptyList())
  val processSteps: StateFlow<List<GitProcessStep>> = _processSteps.asStateFlow()

  // Dynamic Models state
  private val _discoveredModels = MutableStateFlow<List<AIModel>>(emptyList())
  val discoveredModels: StateFlow<List<AIModel>> = _discoveredModels.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      if (providerDao.getAllProviders().first().isEmpty()) {
        val defaultProviders =
          listOf(
            AIProviderEntity("Google Gemini", "", false, "Gemini 2.5 Pro", "Gemini 2.5 Pro,Gemini 2.5 Flash,Gemini Experimental,Gemini 1.5 Pro"),
            AIProviderEntity("NVIDIA AI", "", false, "Nemotron 70B", "Nemotron 70B,Llama 3.1 405B,DeepSeek R1,Qwen 2.5 72B"),
            AIProviderEntity("OpenAI", "", false, "GPT-4o", "GPT-4o,GPT-4o-mini,o1-reasoning,o3-mini"),
            AIProviderEntity("Anthropic Claude", "", false, "Claude 3.5 Sonnet", "Claude 3.5 Sonnet,Claude 3 Opus,Claude 3 Haiku"),
            AIProviderEntity("Groq", "", false, "Llama 3 70B", "Llama 3 70B,Mixtral 8x7B,Gemma 2 9B"),
            AIProviderEntity("OpenRouter", "", false, "Auto-Router", "DeepSeek R1,Claude 3.5 Sonnet,GPT-4o,Llama 3.1 405B"),
            AIProviderEntity("DeepSeek", "", false, "DeepSeek V3", "DeepSeek V3,DeepSeek R1"),
            AIProviderEntity("Together AI", "", false, "Llama 3.3 70B", "Llama 3.3 70B,Qwen 2.5 Coder 32B"),
            AIProviderEntity("Mistral AI", "", false, "Mistral Large 2", "Mistral Large 2,Codestral,Mistral Small"),
            AIProviderEntity("Custom OpenAI-Compatible", "", false, "Custom Model", "Custom Model")
          )
        for (p in defaultProviders) {
          providerDao.insertOrUpdateProvider(p)
        }
      }
      if (projectDao.getAllProjects().first().isEmpty()) {
        val projId = projectDao.insertProject(
          ProjectEntity(
            name = "Android Mobile Workspace",
            description = "Kotlin Jetpack Compose Android client workspace",
            language = "Kotlin",
            isFavorite = true
          )
        )
        fileDao.insertFile(
          FileEntity(
            projectId = projId,
            name = "MainActivity.kt",
            path = "app/src/main/java/com/example/MainActivity.kt",
            content = "package com.example\n\nimport android.os.Bundle\nimport androidx.activity.ComponentActivity\nimport androidx.activity.compose.setContent\n\nclass MainActivity : ComponentActivity() {\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        setContent { }\n    }\n}"
          )
        )
      }
    }

    viewModelScope.launch {
      projects.collect { list ->
        if (list.isNotEmpty() && _selectedProjectId.value == null) {
          selectProject(list.first().id)
        }
      }
    }
  }

  fun selectProject(projectId: Long) {
    _selectedProjectId.value = projectId
    viewModelScope.launch {
      val files = fileDao.getFilesForProject(projectId).first()
      if (files.isNotEmpty()) {
        _activeFile.value = files.first()
      } else {
        _activeFile.value = null
      }
    }
  }

  fun selectFile(file: FileEntity) {
    _activeFile.value = file
  }

  fun updateFileContent(file: FileEntity, newContent: String) {
    viewModelScope.launch {
      fileDao.updateFile(file.copy(content = newContent))
      if (_activeFile.value?.id == file.id) {
        _activeFile.value = file.copy(content = newContent)
      }
    }
  }

  fun createProject(name: String, description: String, language: String) {
    viewModelScope.launch {
      val projId =
        projectDao.insertProject(
          ProjectEntity(name = name, description = description, language = language)
        )
      fileDao.insertFile(
        FileEntity(
          projectId = projId,
          name = "Main.$language",
          path = "src/Main.$language",
          content = "// Welcome to $name\n// Created with AI Coding Studio\n"
        )
      )
      selectProject(projId)
    }
  }

  fun saveAIProvider(providerName: String, apiKey: String, selectedModel: String) {
    viewModelScope.launch {
      _isProcessRunning.value = true
      _processTitle.value = "Authenticating & Discovering Models for $providerName"
      GitManager.executeGitActionWithProgress("API Connection") { steps ->
        _processSteps.value = steps
      }

      try {
        val models = AIProviderManager.validateAndDiscoverModels(providerName, apiKey)
        val modelsStr = models.joinToString(",") { it.id }
        providerDao.insertOrUpdateProvider(
          AIProviderEntity(
            providerName = providerName,
            apiKey = apiKey,
            isConnected = true,
            selectedModel = selectedModel.ifBlank { models.firstOrNull()?.id ?: "Default" },
            availableModelsJson = modelsStr
          )
        )
        _discoveredModels.value = models
      } catch (e: Exception) {
        providerDao.insertOrUpdateProvider(
          AIProviderEntity(
            providerName = providerName,
            apiKey = apiKey,
            isConnected = false,
            selectedModel = selectedModel,
            availableModelsJson = ""
          )
        )
      } finally {
        _isProcessRunning.value = false
      }
    }
  }

  fun runAIAction(
    providerName: String,
    apiKey: String,
    model: String,
    taskType: String,
    prompt: String,
    onResult: (String) -> Unit
  ) {
    viewModelScope.launch {
      _isProcessRunning.value = true
      _processTitle.value = "AI Inference ($taskType using $model)"
      GitManager.executeGitActionWithProgress("AI Processing") { steps ->
        _processSteps.value = steps
      }

      val result = AIProviderManager.executeAIRequest(providerName, apiKey, model, taskType, prompt)
      chatLogDao.insertLog(
        ChatLogEntity(
          prompt = prompt,
          response = result,
          provider = providerName,
          model = model
        )
      )
      _isProcessRunning.value = false
      onResult(result)
    }
  }

  fun runGitAction(actionName: String, repoName: String) {
    viewModelScope.launch {
      _isProcessRunning.value = true
      _processTitle.value = "Git Operation: $actionName ($repoName)"
      GitManager.executeGitActionWithProgress(actionName) { steps ->
        _processSteps.value = steps
      }
      _isProcessRunning.value = false
    }
  }

  fun dismissProcess() {
    _isProcessRunning.value = false
  }

  fun connectRepo(name: String, owner: String, remoteUrl: String, token: String) {
    viewModelScope.launch {
      _isProcessRunning.value = true
      _processTitle.value = "Cloning Repository $owner/$name"
      GitManager.executeGitActionWithProgress("Clone Repository") { steps ->
        _processSteps.value = steps
      }
      repoDao.insertRepo(
        GitRepoEntity(
          name = name,
          owner = owner,
          currentBranch = "main",
          remoteUrl = remoteUrl,
          isConnected = true,
          accessToken = token
        )
      )
      _isProcessRunning.value = false
    }
  }

  fun createGitHubRepository(username: String, repoName: String, ghpToken: String, description: String) {
    viewModelScope.launch {
      _isProcessRunning.value = true
      _processTitle.value = "Creating GitHub Repository '$username/$repoName' with GHP Key..."
      GitManager.executeGitActionWithProgress("Create Remote Repository") { steps ->
        _processSteps.value = steps
      }
      repoDao.insertRepo(
        GitRepoEntity(
          name = repoName,
          owner = username,
          currentBranch = "main",
          remoteUrl = "https://github.com/$username/$repoName.git",
          isConnected = true,
          accessToken = ghpToken
        )
      )
      _isProcessRunning.value = false
    }
  }
}
