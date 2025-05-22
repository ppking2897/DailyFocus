package com.bianca.clock

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bianca.clock.ui.HomeScreen
import com.bianca.clock.ui.normal.CameraPermissionRationaleDialog
import com.bianca.clock.ui.normal.PermissionHandler
import com.bianca.clock.ui.theme.WorkClockTheme
import com.bianca.clock.utils.WorkScheduler
import com.bianca.clock.viewModel.repo.ITaskRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@AndroidEntryPoint


class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: ITaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            WorkScheduler.scheduleTestResetWorker(applicationContext)
        } else {
            WorkScheduler.scheduleDailyTaskReset(applicationContext)
        }

        lifecycleScope.launch {
            //檢查是否今天尚未重設
            //如果是，就立即呼叫 resetDailyTasksIfNeeded() 並記錄執行時間
            if (repository.shouldResetToday(applicationContext)) {
                repository.resetDailyTasksIfNeeded()
                repository.saveResetTimestamp(applicationContext)
            }
        }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            var isShowSettingOperationDialog by remember { mutableStateOf(false) }
            val requiredPermissions = mutableListOf<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
//            requiredPermissions.add(Manifest.permission.CAMERA)

            if (isShowSettingOperationDialog) {
                CameraPermissionRationaleDialog(){
                    isShowSettingOperationDialog = false
                }
            }

            PermissionHandler(
                permissions = requiredPermissions,
                onAllGranted = {
                    // 權限已獲得，進入主畫面
                    isShowSettingOperationDialog = false
                },
                onDenied = { deniedList ->
                    // 權限被拒絕，處理提示或關閉功能
                    isShowSettingOperationDialog = true
                }
            )
            WorkClockTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WorkClockTheme {
        Greeting("Android")
    }
}