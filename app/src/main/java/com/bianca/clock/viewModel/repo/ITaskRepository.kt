package com.bianca.clock.viewModel.repo

import com.bianca.clock.infrastructure.room.TaskDao
import com.bianca.clock.infrastructure.room.TaskEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteTaskById(id: Int)
}

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : ITaskRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)

    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    override suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)
}