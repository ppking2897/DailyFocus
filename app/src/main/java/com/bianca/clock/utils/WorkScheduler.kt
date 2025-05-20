package com.bianca.clock.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bianca.clock.infrastructure.ResetTaskWorker
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object WorkScheduler {
    fun scheduleDailyTaskReset(context: Context) {
        val request = PeriodicWorkRequestBuilder<ResetTaskWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyTaskReset",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleTestResetWorker(context: Context) {
        val request = OneTimeWorkRequestBuilder<ResetTaskWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val nextRun = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        return ChronoUnit.MILLIS.between(now, nextRun)
    }
}
