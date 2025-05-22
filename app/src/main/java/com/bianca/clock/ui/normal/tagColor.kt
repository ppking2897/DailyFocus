package com.bianca.clock.ui.normal

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ✅ 顏色對應工具函式
fun tagColor(tag: String): Color {
    return when (tag) {
        "閱讀" -> Color(0xFF81C784)
        "工作" -> Color(0xFF64B5F6)
        "運動" -> Color(0xFFFFB74D)
        "放鬆" -> Color(0xFFBA68C8)
        "未分類" -> Color(0xFFFF8A65)
        "每日重複" -> Color(0xFF7986CB)
        else -> Color(0xFF607D8B)
    }
}

// ✅ 篩選器樣式：顯示為色塊
@Composable
fun ColoredFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = if (selected) Color.White else Color.White
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor =  Color.Gray,
            selectedContainerColor = tagColor(label),
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

// ✅ 任務色條裝飾用 Modifier
fun Modifier.taskTagStripe(tag: String): Modifier {
    return this.then(
        Modifier.background(
            color = tagColor(tag)
        )
    )
}