package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    ProjectEntity::class,
    FileEntity::class,
    AIProviderEntity::class,
    GitRepoEntity::class,
    ChatLogEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun projectDao(): ProjectDao
  abstract fun fileDao(): FileDao
  abstract fun aiProviderDao(): AIProviderDao
  abstract fun gitRepoDao(): GitRepoDao
  abstract fun chatLogDao(): ChatLogDao

  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE
        ?: synchronized(this) {
          val instance =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ai_coding_studio_db"
              )
              .addCallback(DatabaseCallback())
              .build()
          INSTANCE = instance
          instance
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          CoroutineScope(Dispatchers.IO).launch {
            populateInitialData(database)
          }
        }
      }

      suspend fun populateInitialData(database: AppDatabase) {
        val providerDao = database.aiProviderDao()
        val defaultProviders =
          listOf(
            AIProviderEntity(
              "Google Gemini",
              "",
              false,
              "Gemini 2.5 Pro",
              "Gemini 2.5 Pro,Gemini 2.5 Flash,Gemini Experimental,Gemini 1.5 Pro"
            ),
            AIProviderEntity(
              "NVIDIA AI",
              "",
              false,
              "Nemotron 70B",
              "Nemotron 70B,Llama 3.1 405B,DeepSeek R1,Qwen 2.5 72B"
            ),
            AIProviderEntity(
              "OpenAI",
              "",
              false,
              "GPT-4o",
              "GPT-4o,GPT-4o-mini,o1-reasoning,o3-mini"
            ),
            AIProviderEntity(
              "Anthropic Claude",
              "",
              false,
              "Claude 3.5 Sonnet",
              "Claude 3.5 Sonnet,Claude 3 Opus,Claude 3 Haiku"
            ),
            AIProviderEntity(
              "Groq",
              "",
              false,
              "Llama 3 70B",
              "Llama 3 70B,Mixtral 8x7B,Gemma 2 9B"
            ),
            AIProviderEntity(
              "OpenRouter",
              "",
              false,
              "Auto-Router",
              "DeepSeek R1,Claude 3.5 Sonnet,GPT-4o,Llama 3.1 405B"
            ),
            AIProviderEntity(
              "DeepSeek",
              "",
              false,
              "DeepSeek V3",
              "DeepSeek V3,DeepSeek R1"
            ),
            AIProviderEntity(
              "Together AI",
              "",
              false,
              "Llama 3.3 70B",
              "Llama 3.3 70B,Qwen 2.5 Coder 32B"
            ),
            AIProviderEntity(
              "Mistral AI",
              "",
              false,
              "Mistral Large 2",
              "Mistral Large 2,Codestral,Mistral Small"
            ),
            AIProviderEntity(
              "Custom OpenAI-Compatible",
              "",
              false,
              "Custom Model",
              "Custom Model"
            )
          )
        for (provider in defaultProviders) {
          providerDao.insertOrUpdateProvider(provider)
        }

        val projectDao = database.projectDao()
        val fileDao = database.fileDao()

        val projId =
          projectDao.insertProject(
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
            content = "package com.example\n\nimport android.os.Bundle\nimport androidx.activity.ComponentActivity\nimport androidx.activity.compose.setContent\n\nclass MainActivity : ComponentActivity() {\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        setContent { AICodingStudioApp() }\n    }\n}"
          )
        )
        fileDao.insertFile(
          FileEntity(
            projectId = projId,
            name = "build.gradle.kts",
            path = "app/build.gradle.kts",
            content = "plugins {\n    alias(libs.plugins.android.application)\n}\n\nandroid {\n    namespace = \"com.example\"\n}"
          )
        )
      }
    }
  }
}
