package com.bianca.clock.ui.normal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    trackHeight: Float = 16f,          // 軌道粗細（dp）
    thumbRadius: Float = 20f,          // 滑塊半徑（dp）
    activeColor: Color = Color(0xFF5EBE7E),
    inactiveColor: Color = Color(0xFFB6FFC4),
    thumbColor: Color = Color.Magenta,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
) {
    // Layout 參數
    val minValue = valueRange.start
    val maxValue = valueRange.endInclusive

    var sliderValue by remember { mutableStateOf(value) }

    // 轉換 value 到 x position
    fun valueToX(value: Float, width: Float): Float {
        return ((value - minValue) / (maxValue - minValue)) * width
    }
    // 轉換 x 到 value
    fun xToValue(x: Float, width: Float): Float {
        val percent = (x / width).coerceIn(0f, 1f)
        val exact = minValue + (maxValue - minValue) * percent
        // 分段邏輯（steps）
        return if (steps > 0) {
            val stepSize = (maxValue - minValue) / (steps + 1)
            val nearest = ((exact - minValue) / stepSize).roundToInt()
            minValue + nearest * stepSize
        } else exact
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val width = size.width.toFloat()
                    val x = (change.position.x).coerceIn(0f, width)
                    val newValue = xToValue(x, width)
                    sliderValue = newValue
                    onValueChange(newValue)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val centerY = size.height / 2

            // 計算滑塊的位置
            val x = valueToX(sliderValue, width)

            // 畫 inactive 軌道
            drawLine(
                color = inactiveColor,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = trackHeight
            )

            // 畫 active 軌道
            drawLine(
                color = activeColor,
                start = Offset(0f, centerY),
                end = Offset(x, centerY),
                strokeWidth = trackHeight
            )

            // 畫 thumb
            drawCircle(
                color = thumbColor,
                radius = thumbRadius,
                center = Offset(x, centerY)
            )

            // 畫分段點
            if (steps > 0) {
                val stepSize = (maxValue - minValue) / (steps + 1)
                for (i in 0..(steps + 1)) {
                    val px = valueToX(minValue + stepSize * i, width)
                    drawCircle(
                        color = Color.Gray,
                        radius = 4.dp.toPx(),
                        center = Offset(px, centerY)
                    )
                }
            }
        }
    }
}