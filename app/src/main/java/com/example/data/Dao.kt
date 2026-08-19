package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectEntity): Long

  @Update
  suspend fun updateProject(project: ProjectEntity)

  @Delete
  suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface FileDao {
  @Query("SELECT * FROM files WHERE projectId = :projectId")
  fun getFilesForProject(projectId: Long): Flow<List<FileEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFile(file: FileEntity): Long

  @Update
  suspend fun updateFile(file: FileEntity)

  @Delete
  suspend fun deleteFile(file: FileEntity)
}

@Dao
interface AIProviderDao {
  @Query("SELECT * FROM ai_providers")
  fun getAllProviders(): Flow<List<AIProviderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProvider(provider: AIProviderEntity)

  @Query("SELECT * FROM ai_providers WHERE providerName = :name")
  suspend fun getProvider(name: String): AIProviderEntity?
}

@Dao
interface GitRepoDao {
  @Query("SELECT * FROM git_repos")
  fun getAllRepos(): Flow<List<GitRepoEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRepo(repo: GitRepoEntity): Long

  @Delete
  suspend fun deleteRepo(repo: GitRepoEntity)
}

@Dao
interface ChatLogDao {
  @Query("SELECT * FROM chat_logs ORDER BY timestamp DESC")
  fun getAllChatLogs(): Flow<List<ChatLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: ChatLogEntity)
}
