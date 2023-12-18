package io.github.pknujsp.weatherwizard.feature.widget.activity

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.manager.IWorker
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationType
import java.util.concurrent.atomic.AtomicBoolean


@HiltWorker
class WidgetDeleteWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val widgetRemoteViewModel: DeleteWidgetRemoteViewModel
) : CoroutineWorker(context, params) {
    companion object : IWorker {
        override val name: String get() = "WidgetDeleteWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf()

        const val APP_WIDGET_IDS_KEY = "appWidgetIds"
        override val isRunning: AtomicBoolean = AtomicBoolean(false)
    }

    override suspend fun doWork(): Result {
        val inputDataMap = inputData.keyValueMap
        if (APP_WIDGET_IDS_KEY !in inputDataMap) {
            return Result.success()
        }

        val appWidgetIds = inputDataMap[APP_WIDGET_IDS_KEY] as IntArray
        widgetRemoteViewModel.deleteWidgets(appWidgetIds)
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return AppNotificationManager(context).createForegroundNotification(context, NotificationType.WORKING).run {
            ForegroundInfo(NotificationType.WORKING.notificationId, this)
        }
    }

}