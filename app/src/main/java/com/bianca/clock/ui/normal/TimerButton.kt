package com.bianca.clock.ui.normal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bianca.clock.ui.theme.WorkClockTheme

@ExperimentalMaterial3Api
@Composable
fun TimerButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    containerColor: Color,
    disabledContainerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = textColor.copy(alpha = 0.4f)
        ),
        shape = ButtonDefaults.shape
    ) {
        Text(
            text = label,
            color = if (enabled) textColor else containerColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@ExperimentalMaterial3Api
@Composable
@Preview
fun TimerButtonPreview() {
    WorkClockTheme {
        TimerButton(
            label = "開始",
            onClick = {},
            enabled = false,
            containerColor = Color(0xFF6200EE),
            disabledContainerColor = Color(0xFFBB86FC),
            textColor = Color.White
        )
    }
}