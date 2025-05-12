package com.bianca.clock.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Task.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Task.route) { TaskScreen() }
            composable(Screen.Habit.route) { HabitScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
        }
    }
}
