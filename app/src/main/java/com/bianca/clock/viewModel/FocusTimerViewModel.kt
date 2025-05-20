package com.bianca.clock.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.clock.infrastructure.room.TaskEntity
import com.bianca.clock.viewModel.repo.ITaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class FocusTimerViewModel @Inject constructor(private val repository: ITaskRepository) :
    ViewModel() {

    private var _timeDefault = (1 * 2) // 2 秒，測試用

    private val _timeLeft = MutableStateFlow(_timeDefault)
    val timeLeft: StateFlow<Int> = _timeLeft

    private var timerJob: Job? = null

    val isRunning = MutableStateFlow(false)

    private val quotes = listOf(
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
            triggerSound()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        isRunning.value = false
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timeLeft.value = _timeDefault
        isRunning.value = false
    }

    // 🔄 UI 專用任務資料：強制每次更新都給新實例，確保動畫觸發
    private val _uiTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val uiTasks: StateFlow<List<TaskEntity>> = _uiTasks

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect { dbTasks ->
                _uiTasks.value = dbTasks // ✅ 提供新 list 實體以觸發動畫
            }
        }
    }

    fun addTask(name: String, tag: String = "未分類", repeatDaily: Boolean = false) {
        viewModelScope.launch {
            val newTask = TaskEntity(name = name, tag = tag, repeatDaily = repeatDaily)
            repository.insertTask(newTask)
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = !task.isDone)
            repository.updateTask(updatedTask)
            // ❌ 不直接改 _uiTasks，資料將自 Room 更新回來，自然會觸發動畫
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    private val _playSoundEvent = MutableSharedFlow<Unit>()
    val playSoundEvent = _playSoundEvent.asSharedFlow()

    fun triggerSound() {
        viewModelScope.launch {
            _playSoundEvent.emit(Unit)
        }
    }

    fun updateRepeatFlag(taskId: Int, repeat: Boolean) {
        viewModelScope.launch {
            repository.updateRepeatFlag(taskId, repeat)
        }
    }
}
