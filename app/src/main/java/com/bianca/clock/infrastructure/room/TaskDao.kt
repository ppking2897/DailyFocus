package com.bianca.clock.infrastructure.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 用於操作本地任務資料的 Data Access Object (DAO)。
 */
@Dao
interface TaskDao {

    /**
     * 取得所有任務清單，依建立時間由新到舊排序。
     *
     * @return Flow<List<TaskEntity>> 可觀察的任務資料流
     *
     * @see insertTask
     * @see updateTask
     * @see deleteTask
     */
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * 新增一筆任務資料；若主鍵已存在則覆蓋原資料。
     *
     * @param task 要插入的任務資料
     *
     * @see getAllTasks
     * @see updateTask
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    /**
     * 更新一筆既有的任務資料。
     *
     * @param task 欲更新的任務資料（ID 必須存在）
     *
     * @see insertTask
     * @see deleteTask
     */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * 刪除指定的任務資料（需提供完整 TaskEntity 物件）。
     *
     * @param task 欲刪除的任務資料
     *
     * @see deleteTaskById
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    /**
     * 依據任務 ID 直接刪除指定資料。
     *
     * @param id 任務主鍵 ID
     *
     * @see deleteTask
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    // TaskDao.kt
    @Query("UPDATE tasks SET isDone = 0 WHERE repeatDaily = 1")
    suspend fun resetRepeatDailyTasks()

    @Query("UPDATE tasks SET repeatDaily = 1 WHERE id = :taskId")
    suspend fun markTaskAsRepeat(taskId: Int)

    @Query("UPDATE tasks SET repeatDaily = :repeat WHERE id = :taskId")
    suspend fun updateRepeatFlag(taskId: Int, repeat: Boolean)

    @Query("SELECT * FROM tasks WHERE repeatDaily = 1 AND isDone = 0")
    suspend fun getIncompleteRepeatTasks(): List<TaskEntity>

}