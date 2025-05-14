package com.bianca.clock.model

/**
 * 🧾 欄位說明：
 * 欄位名稱	型別	說明
 * id	Int	每個任務的唯一識別碼（通常遞增）
 * name	String	任務的文字內容
 * isDone	Boolean	是否已完成（true = 勾選）
 */

data class TaskItemData(
    val id: Int,
    val name: String,
    val isDone: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String? = null,
    val repeatDaily: Boolean = false
)