package com.bianca.clock.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.clock.infrastructure.room.TaskEntity
import com.bianca.clock.viewModel.repo.ITaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 名稱	用法
 * _tasks	可變的任務資料狀態（私有）
 * tasks	公開給 UI 用的唯讀版本
 * nextTaskId	用來確保每個任務的 ID 唯一
 * addTask()	加入新任務，支援分類與每日重複設定
 * toggleTask()	點擊 Checkbox 時切換 isDone 狀態
 */

@HiltViewModel
class FocusTimerViewModel @Inject constructor(private val repository: ITaskRepository) :
    ViewModel() {


    private var _timeDefault = (1 * 2) // 25 分鐘 in 秒

    private val _timeLeft = MutableStateFlow(_timeDefault) // 25 分鐘 in 秒
    val timeLeft: StateFlow<Int> = _timeLeft

    private var timerJob: Job? = null

    val isRunning = MutableStateFlow(false)

    private val quotes = listOf(
//        "你剛才的專注，是對時間最深的尊重。",
//        "心有方向，時間才有重量。",
//        "專注，不是做很多事，而是不做其他事。",
//        "完成，是因為你選擇了它，而不是剛好有空。",
//        "讓每一段番茄，帶你靠近目標一點點。",
        "你已經完成一段工作了\n但是還很多工作等著你。",
    )

    private val _quoteToShow = MutableStateFlow<String?>(null)
    val quoteToShow: StateFlow<String?> = _quoteToShow

    private fun showRandomQuote() {
        _quoteToShow.value = quotes.random()
    }

    fun clearQuote() {
        _quoteToShow.value = null
    }

    fun startTimer() {
        if (isRunning.value) return
        isRunning.value = true
        if (_timeLeft.value <= 0)
            _timeLeft.value = _timeDefault
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1
            }
            isRunning.value = false
            showRandomQuote()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        isRunning.value = false
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timeLeft.value = 25 * 60
        isRunning.value = false
    }


    // ✅ 任務資料接收：直接觀察 Flow
    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ✅ 加入任務
    fun addTask(name: String, tag: String = "未分類", repeatDaily: Boolean = false) {
        viewModelScope.launch {
            val newTask = TaskEntity(name = name, tag = tag, repeatDaily = repeatDaily)
            repository.insertTask(newTask)
        }
    }

    // ✅ 勾選任務切換
    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(isDone = !task.isDone)
            repository.updateTask(updated)
        }
    }

    // ✅ 刪除任務
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}