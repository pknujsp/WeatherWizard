package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.pknujsp.weatherwizard.feature.notification.common.INotificationWorker
import java.util.UUID

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object : INotificationWorker {
        override val name: String = "DailyNotificationWorker"
        override val id: UUID
            get() = TODO("Not yet implemented")
    }

    override suspend fun doWork(): Result {
        return Result.success()
    }
}