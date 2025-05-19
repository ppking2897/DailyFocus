//package com.bianca.clock.ui.normal
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.unit.dp
//import com.bianca.clock.infrastructure.room.TaskEntity
//import com.bianca.clock.viewModel.FocusTimerViewModel
//
//@Composable
//fun AnimatedTaskList(viewModel: FocusTimerViewModel) {
//    val tasks by viewModel.uiTasks.collectAsState()
//    val (incomplete, complete) = remember(tasks) {
//        tasks.partition { !it.isDone }
//    }
//
//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
//    ) {
//        items(incomplete, key = { it.id }) { task ->
//            TaskItemCard(task = task, viewModel = viewModel)
//        }
//
//        if (complete.isNotEmpty()) {
//            item {
//                Text(
//                    text = "已完成",
//                    style = MaterialTheme.typography.titleSmall,
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
//                )
//            }
//
//            items(complete, key = { it.id }) { task ->
//                TaskItemCard(task = task, viewModel = viewModel)
//            }
//        }
//    }
//}
//
//@Composable
//fun TaskItemCard(task: TaskEntity, viewModel: FocusTimerViewModel) {
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .animateItem()
//            .animateContentSize(),
//        shape = MaterialTheme.shapes.medium,
//        tonalElevation = 2.dp,
//        shadowElevation = 2.dp,
//        color = MaterialTheme.colorScheme.surface
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                Text(
//                    text = task.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
//                )
//                if (task.tag.isNotEmpty()) {
//                    Text(
//                        text = "#${task.tag}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Checkbox(
//                    checked = task.isDone,
//                    onCheckedChange = { viewModel.toggleTask(task) },
//                    colors = CheckboxDefaults.colors(
//                        checkedColor = MaterialTheme.colorScheme.primary
//                    )
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                IconButton(onClick = { viewModel.deleteTask(task) }) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "刪除任務",
//                        tint = MaterialTheme.colorScheme.error
//                    )
//                }
//            }
//        }
//    }
//}