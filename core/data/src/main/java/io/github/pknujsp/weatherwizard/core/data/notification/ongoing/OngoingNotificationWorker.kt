package io.github.pknujsp.weatherwizard.core.data.notification.ongoing

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.pknujsp.weatherwizard.core.data.notification.IWorker

class OngoingNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object : IWorker {
        override val name: String = "OngoingNotificationWorker"
    }

    override suspend fun doWork(): Result {
        return Result.success()
    }
}