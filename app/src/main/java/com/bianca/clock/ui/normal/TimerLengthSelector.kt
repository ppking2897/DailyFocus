package com.bianca.clock.ui.normal

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun TimerLengthSelector(
    currentMinutes: Int,
    onMinutesChange: (Int) -> Unit
) {
    // 常用倒數時長
    val options = listOf(15, 20, 25, 30, 35, 40, 45, 50, 55, 60)
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text("番茄鐘時長：$currentMinutes 分鐘")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { min ->
                DropdownMenuItem(
                    text = { Text("$min 分鐘" , color = MaterialTheme.colorScheme.onBackground) },
                    onClick = {
                        onMinutesChange(min)
                        expanded = false
                    }
                )
            }
        }
    }
}