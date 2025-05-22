package com.bianca.clock.infrastructure

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bianca.clock.R
import com.bianca.clock.ui.theme.YellowSun
import com.bianca.clock.viewModel.repo.ITaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ResetTaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ITaskRepository,
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Log.d("ResetTaskWorker", "Running daily reset worker")
        return try {
            repository.resetDailyTasksIfNeeded()

            // 建立並發送通知提示用戶
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            val channelId = "reset_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "每日任務重設",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_work_focus) // ✅ 自訂小圖示
                .setContentTitle("每日任務重設完成")
                .setContentText("已自動將每日重複任務重新啟用。")
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1001, notification)

            Result.success()
        } catch (e: Exception) {
            Log.e("ResetTaskWorker", "Error resetting tasks", e)
            Result.retry()
        }
    }
}