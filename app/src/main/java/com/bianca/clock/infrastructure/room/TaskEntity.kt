package com.bianca.clock.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val isDone: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String = "未分類",
    val repeatDaily: Boolean = false
)