package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.pknujsp.weatherwizard.feature.notification.common.INotificationWorker

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object : INotificationWorker {
        override val name: String = "DailyNotificationWorker"
    }

    override suspend fun doWork(): Result {
        return Result.success()
    }
}