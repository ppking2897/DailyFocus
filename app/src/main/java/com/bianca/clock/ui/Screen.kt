package com.bianca.clock.ui

sealed class Screen(val route: String, val title: String) {
    data object Task : Screen("task", "任務")
    data object Habit : Screen("habit", "習慣")
    data object Stats : Screen("stats", "統計")
}