package com.bianca.clock.ui

import android.content.Context
import android.content.res.Configuration
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.clock.ui.theme.WorkClockTheme

// 8️⃣ AddTaskDialog 加入每日重複開關與調整按鈕背景色
@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, tag: String, repeat: Boolean) -> Unit,
) {
    if (!showDialog) return

    var taskName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf("未分類") }
    var isRepeatDaily by remember { mutableStateOf(false) }

    val tagOptions = listOf("未分類", "閱讀", "工作", "運動", "放鬆")
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增任務", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            // 必須將此參數設定於此，才能關閉鍵盤
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                OutlinedTextField(
                    textStyle = TextStyle(MaterialTheme.colorScheme.onBackground),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("任務名稱", color = MaterialTheme.colorScheme.onBackground) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("分類", color = MaterialTheme.colorScheme.onBackground)
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(selectedTag, color = MaterialTheme.colorScheme.onBackground)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        tagOptions.forEach { tag ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        tag,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                },
                                onClick = {
                                    selectedTag = tag
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("每日重複", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isRepeatDaily,
                        onCheckedChange = {
                            isRepeatDaily = it
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (taskName.isNotBlank()) {
                        onConfirm(taskName.trim(), selectedTag, isRepeatDaily)
                    }else{
                        Toast.makeText(context, "請至少輸入一個字", Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("確認", color = MaterialTheme.colorScheme.onSecondary)
            }
        },
        dismissButton = {
            TextButton(
                shape = RoundedCornerShape(8.dp),
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )
            ) {
                Text("取消", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    )
}

@Composable
@Preview
fun AddTaskDialogPreview() {
    WorkClockTheme {
        var showDialog by remember { mutableStateOf(true) }
        if (showDialog) {
            AddTaskDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onConfirm = { name, tag, repeat ->
                    // Handle the confirmed task here
                    println("Task: $name, Tag: $tag, Repeat: $repeat")
                    showDialog = false
                }
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AddTaskDialogPreviewDark() {
    WorkClockTheme {
        var showDialog by remember { mutableStateOf(true) }
        if (showDialog) {
            AddTaskDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onConfirm = { name, tag, repeat ->
                    // Handle the confirmed task here
                    println("Task: $name, Tag: $tag, Repeat: $repeat")
                    showDialog = false
                }
            )
        }
    }
}