package io.github.pknujsp.weatherwizard.core.data.notification.daily

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.pknujsp.weatherwizard.core.data.notification.IWorker

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object : IWorker {
        override val name: String = "DailyNotificationWorker"
    }

    override suspend fun doWork(): Result {
        return Result.success()
    }
}