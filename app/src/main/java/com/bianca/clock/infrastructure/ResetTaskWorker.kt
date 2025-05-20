package com.bianca.clock.infrastructure

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bianca.clock.R
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
            val pendingTasks = repository.getIncompleteRepeatTasks()
            if (pendingTasks.isNotEmpty()) {
                val notificationManager = NotificationManagerCompat.from(applicationContext)
                val channelId = "daily_tasks"

                // Android 8+ 通知頻道
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "每日任務提醒",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    channel.description = "提醒你尚未完成的每日任務"
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(applicationContext, channelId)
                    .setSmallIcon(R.drawable.ic_flower_smile)
                    .setContentTitle("你有 ${pendingTasks.size} 項每日任務未完成")
                    .setContentText("記得今天也要保持專注喔！")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                notificationManager.notify(1001, notification)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ResetTaskWorker", "Error resetting tasks", e)
            Result.retry()
        }
    }
}