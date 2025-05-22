package com.bianca.clock.ui.normal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ImmersiveTimePickerDialog(
    show: Boolean,
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                var sliderValue by remember { mutableStateOf(initialMinutes.toFloat()) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("設定番茄鐘時長", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "${sliderValue.toInt()} 分鐘",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5EBE7E)
                    )
                    Spacer(Modifier.height(16.dp))
                    Slider(
                        modifier = Modifier.padding(start = 10.dp , end = 10.dp),
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 5f..60f,
                        steps = 10, // 5,10,15,...,60
                        colors = SliderDefaults.colors().copy(
                            thumbColor = Color.DarkGray,                // 你的主題色
                            activeTrackColor = Color(0xFF8FE9B5),          // 你想要的綠/其他
                            inactiveTrackColor = Color(0xFFB0BEC5),        // 灰色，明顯區分
                        )
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("取消", color = Color.White)
                        }
                        Button(onClick = { onConfirm(sliderValue.toInt()) }) {
                            Text("確認", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}