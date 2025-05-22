package com.bianca.clock

import app.cash.turbine.test
import com.bianca.clock.infrastructure.room.TaskEntity
import com.bianca.clock.viewModel.FocusTimerViewModel
import com.bianca.clock.viewModel.repo.ITaskRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
class FocusTimerViewModelTest {

    private lateinit var repository: ITaskRepository
    private lateinit var viewModel: FocusTimerViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk(relaxed = true)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // region Repository 互動測試
    @Nested
    @Tag("repo")
    inner class RepositoryInteractionTests {

        @Test
        fun `init triggers getAllTasks collect`() = runTest {
            val taskList = listOf(TaskEntity(id = 1, name = "T1"))
            every { repository.getAllTasks() } returns flowOf(taskList)
            viewModel = FocusTimerViewModel(repository)
            assertEquals(taskList, viewModel.uiTasks.value)
        }

        @Test
        fun `addTask calls insertTask on repository`() = runTest {
            coEvery { repository.insertTask(any()) } just Runs
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            viewModel.addTask("Reading", "學習", false)
            coVerify { repository.insertTask(match { it.name == "Reading" && it.tag == "學習" }) }
        }

        @Test
        fun `toggleTask calls updateTask on repository with toggled isDone`() = runTest {
            coEvery { repository.updateTask(any()) } just Runs
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            val task = TaskEntity(id = 2, name = "寫Code", isDone = false)
            viewModel.toggleTask(task)
            coVerify { repository.updateTask(match { it.id == 2 && it.isDone }) }
        }

        @Test
        fun `deleteTask calls deleteTask on repository`() = runTest {
            coEvery { repository.deleteTask(any()) } just Runs
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            val task = TaskEntity(id = 3, name = "運動")
            viewModel.deleteTask(task)
            coVerify { repository.deleteTask(task) }
        }

        @Test
        fun `updateRepeatFlag calls updateRepeatFlag on repository`() = runTest {
            coEvery { repository.updateRepeatFlag(any(), any()) } just Runs
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            viewModel.updateRepeatFlag(5, true)
            coVerify { repository.updateRepeatFlag(5, true) }
        }

        @Test
        fun `setTimerLength updates default and left time`() = runTest {
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            viewModel.setTimerLength(10)
            assertEquals(10 * 60, viewModel.timeDefault.value)
            assertEquals(10 * 60, viewModel.timeLeft.value)
        }
    }
    // endregion

    // region Flow 狀態/事件測試
    @Nested
    @Tag("flow")
    inner class FlowStateEventTests {

        @Test
        fun `uiTasks emits updated task list when repository changes`() = runTest {
            val flow = MutableStateFlow<List<TaskEntity>>(emptyList())
            every { repository.getAllTasks() } returns flow
            viewModel = FocusTimerViewModel(repository)

            viewModel.uiTasks.test {
                assertEquals(emptyList<TaskEntity>(), awaitItem())
                val newTask = TaskEntity(id = 1, name = "Test", tag = "A")
                flow.value = listOf(newTask)
                assertEquals(listOf(newTask), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `timeLeft emits decreasing values when timer starts`() = runTest {
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            viewModel.setTimerLength(1)
            viewModel.startTimer()
            viewModel.timeLeft.test {
                assertEquals(60, awaitItem())
                assertEquals(59, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `playSoundEvent emits after timer finishes`() = runTest {
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            viewModel.setTimerLength(0)
            viewModel.playSoundEvent.test {
                viewModel.startTimer()
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `quoteToShow emits after timer finishes`() = runTest {
            every { repository.getAllTasks() } returns MutableStateFlow(emptyList())
            viewModel = FocusTimerViewModel(repository)
            viewModel.setTimerLength(0)
            viewModel.startTimer()
            viewModel.quoteToShow.test {
                val quote = awaitItem()
                assertTrue(!quote.isNullOrBlank())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
    // endregion
}
