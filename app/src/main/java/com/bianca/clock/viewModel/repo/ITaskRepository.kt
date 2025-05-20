package com.bianca.clock.viewModel.repo

import android.content.Context
import com.bianca.clock.infrastructure.room.TaskDao
import com.bianca.clock.infrastructure.room.TaskEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import androidx.core.content.edit

interface ITaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteTaskById(id: Int)
    suspend fun resetDailyTasksIfNeeded()
    suspend fun markAsRepeat(taskId: Int)
    suspend fun shouldResetToday(context: Context): Boolean
    suspend fun saveResetTimestamp(context: Context)
    suspend fun updateRepeatFlag(taskId: Int, repeat: Boolean)
    suspend fun getIncompleteRepeatTasks(): List<TaskEntity>
}

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
) : ITaskRepository {
    private val prefsName = "task_prefs"
    private val keyLastReset = "last_reset_date"

    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)

    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    override suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)

    override suspend fun resetDailyTasksIfNeeded() {
        taskDao.resetRepeatDailyTasks()
    }

    override suspend fun markAsRepeat(taskId: Int) {
        taskDao.markTaskAsRepeat(taskId)
    }

    override suspend fun shouldResetToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val last = prefs.getString(keyLastReset, null)
        val today = LocalDate.now().toString()
        return last != today
    }

    override suspend fun saveResetTimestamp(context: Context) {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit { putString(keyLastReset, LocalDate.now().toString()) }
    }

    override suspend fun updateRepeatFlag(taskId: Int, repeat: Boolean) {
        taskDao.updateRepeatFlag(taskId, repeat)
    }

    override suspend fun getIncompleteRepeatTasks(): List<TaskEntity> = taskDao.getIncompleteRepeatTasks()
}