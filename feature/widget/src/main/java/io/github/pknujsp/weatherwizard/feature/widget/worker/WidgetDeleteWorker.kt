package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager


@HiltWorker
class WidgetDeleteWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val widgetRemoteViewModel: WidgetRemoteViewModel
) : CoroutineWorker(context, params) {
    companion object : IWorker {
        override val name: String get() = "WidgetDeleteWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf()
    }

    override suspend fun doWork(): Result {
        val appWidgetIds = inputData.getIntArray("appWidgetIds")!!
        widgetRemoteViewModel.deleteWidgets(appWidgetIds)
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return AppNotificationManager(context).createForegroundNotification(context, NotificationType.WORKING)
    }

}