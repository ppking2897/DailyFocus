package com.bianca.clock.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bianca.clock.R
import com.bianca.clock.infrastructure.room.TaskEntity
import com.bianca.clock.ui.normal.TimerButton
import com.bianca.clock.viewModel.FocusTimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: FocusTimerViewModel = hiltViewModel()) {
    val time by viewModel.timeLeft.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val formattedTime = String.format("%02d:%02d", time / 60, time % 60)
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.playSoundEvent.collect {
            Log.d("HomeScreen", "Play sound event triggered")
            val player = MediaPlayer.create(context, R.raw.notification_over)
            player.setOnCompletionListener { it.release() }
            player.start()
        }
    }

    AddTaskDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = { name, tag ->
            Log.d("HomeScreen", "AddTaskDialog confirmed: name=$name, tag=$tag")
            viewModel.addTask(name, tag)
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("FocusFlow", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    IconButton(onClick = {
                        Log.d("HomeScreen", "Add task button clicked")
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "新增任務", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = innerPadding.calculateTopPadding() - 25.dp
                )
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerDisplay(time = formattedTime)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimerButton(
                    modifier = Modifier.weight(1f),
                    label = "開始",
                    onClick = {
                        Log.d("HomeScreen", "Timer start")
                        viewModel.startTimer()
                    },
                    enabled = !isRunning,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = Color.White
                )
                TimerButton(
                    modifier = Modifier.weight(1f),
                    label = "暫停",
                    onClick = {
                        Log.d("HomeScreen", "Timer paused")
                        viewModel.pauseTimer()
                    },
                    enabled = isRunning,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = Color.White
                )
                TimerButton(
                    modifier = Modifier.weight(1f),
                    label = "重設",
                    onClick = {
                        Log.d("HomeScreen", "Timer reset")
                        viewModel.resetTimer()
                    },
                    enabled = true,
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
//            QuoteBanner(quote = "保持微笑，專注就會自然發生。")
//            Spacer(modifier = Modifier.height(16.dp))

            val tasks by viewModel.uiTasks.collectAsState()
            Log.d("HomeScreen", "Total tasks: ${'$'}{tasks.size}")
            val (incomplete, complete) = remember(tasks) { tasks.partition { !it.isDone } }

            val listState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 16.dp)
            ) {

                item {
                    Text(
                        text = "代辦事項",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }
                items(incomplete, key = { it.id }) { task ->
                    Log.d("HomeScreen", "Render incomplete task: ${'$'}{task.id}, isDone=${'$'}{task.isDone}")
                    Box(Modifier.animateItem().animateContentSize()) {
                        TaskItemRow(task, viewModel){

                        }
                    }
                }
                if (complete.isNotEmpty()) {
                    item {
                        Text(
                            text = "已完成",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    items(complete, key = { it.id }) { task ->
                        Log.d("HomeScreen", "Render completed task: ${'$'}{task.id}, isDone=${'$'}{task.isDone}")
                        Box(Modifier.animateItem().animateContentSize()) {
                            TaskItemRow(task, viewModel){

                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_flower_smile),
                contentDescription = "可愛花朵",
                modifier = Modifier.size(80.dp)
            )
        }
    }

    val quote by viewModel.quoteToShow.collectAsState()
    if (quote != null) {
        Log.d("HomeScreen", "Quote shown: ${'$'}quote")
        FlowerQuoteBubble(
            quote = quote!!,
            imageRes = R.drawable.ic_flower_smile,
            onDismiss = { viewModel.clearQuote() }
        )
    }
}

@Composable
fun TaskItemRow(task: TaskEntity, viewModel: FocusTimerViewModel , checkBoxAction:() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = 5.dp , vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Checkbox(
                checked = task.isDone,
                onCheckedChange = {
                    Log.d("TaskItem", "Toggle task: ${'$'}{task.id} -> ${'$'}{!task.isDone}")
                    viewModel.toggleTask(task)
                    checkBoxAction()
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )

            Column {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
                if (task.tag.isNotEmpty()) {
                    Text(
                        text = "#${task.tag}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    Log.d("TaskItem", "Delete task: ${'$'}{task.id}")
                    viewModel.deleteTask(task)
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "刪除任務",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}



