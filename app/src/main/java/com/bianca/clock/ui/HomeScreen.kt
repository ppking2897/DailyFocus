package com.bianca.clock.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bianca.clock.R
import com.bianca.clock.viewModel.FocusTimerViewModel

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(viewModel: FocusTimerViewModel = hiltViewModel()) {

    val time by viewModel.timeLeft.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    val formattedTime = String.format("%02d:%02d", time / 60, time % 60)

    var showDialog by remember { mutableStateOf(false) }

    val tasks by viewModel.tasks.collectAsState()

    AddTaskDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = { name, tag ->
            viewModel.addTask(name, tag)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FocusFlow") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "新增任務")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F8FF)) // 淡藍動漫風
                .padding(start = 24.dp, end = 24.dp, top = innerPadding.calculateTopPadding()-25.dp)
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
                Button(onClick = { viewModel.startTimer() }, enabled = !isRunning) {
                    Text("開始")
                }
                Button(onClick = { viewModel.pauseTimer() }, enabled = isRunning) {
                    Text("暫停")
                }
                Button(onClick = { viewModel.resetTimer() }) {
                    Text("重設")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            QuoteBanner(quote = "保持微笑，專注就會自然發生。")

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItemRow(
                        task = task,
                        onCheckedChange = { viewModel.toggleTask(task) },
                        onDeleteClick = { viewModel.deleteTask(task) }
                    )
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

    val infiniteTransition = rememberInfiniteTransition(label = "quote-bg")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "gradientOffset"
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFE0B2), Color(0xFFB3E5FC), Color(0xFFD1C4E9)),
        start = Offset(0f, animatedOffset),
        end = Offset(animatedOffset, 0f)
    )

    AnimatedVisibility(
        visible = quote != null,
        enter = fadeIn(animationSpec = tween(500)) +
                scaleIn(initialScale = 0.8f, animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(400)) +
                scaleOut(targetScale = 0.8f, animationSpec = tween(400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { viewModel.clearQuote() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(animatedBrush, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = quote ?: "",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = Color.Black
                )
            }
        }
    }
}