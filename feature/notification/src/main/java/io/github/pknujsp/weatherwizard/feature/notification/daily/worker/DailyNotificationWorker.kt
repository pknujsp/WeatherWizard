package io.github.pknujsp.weatherwizard.feature.notification.daily.worker

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.permission.PermissionType
import io.github.pknujsp.weatherwizard.core.common.permission.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.notification.daily.forecast.DailyNotificationForecastUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.onFailure
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.feature.notification.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.manager.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val viewModel: DailyNotificationRemoteViewModel
) : CoroutineWorker(context, params) {
    private val appNotificationManager = AppNotificationManager(context)

    companion object : IWorker {
        override val name: String = "DailyNotificationWorker"
        override val id: UUID
            get() = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        val notificationId = inputData.getLong("notificationId", 0L)
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)) {
                Result.success()
            }

            viewModel.init(notificationId)
            appNotificationManager.notifyNotification(NotificationType.DAILY, context, load())
            Result.success()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
    }

    private suspend fun load(): RemoteViewUiModel {
        return when (val notifiationDataType = viewModel.notificationInfo.data.getType()) {
            DailyNotificationType.FORECAST -> loadHourlyForecast(RemoteViewsCreatorManager.createRemoteViewsCreator(
                notifiationDataType))

            else -> throw IllegalArgumentException("Unknown notification type: $notifiationDataType")
        }
    }

    private suspend inline fun loadHourlyForecast(
        remoteViewCreator: NotificationRemoteViewsCreator<DailyNotificationForecastUiModel>
    ): RemoteViewUiModel {
        val result = viewModel.loadHourlyForecast()

        val remoteViewUiModel = RemoteViewUiModel(result is UiState.Success)

        result.onSuccess {
            val smallContentRemoteViews = remoteViewCreator.createSmallContentView(it, context)
            val bigContentRemoteViews = remoteViewCreator.createBigContentView(it, context)
            remoteViewUiModel.apply {
                this.smallContentRemoteViews = smallContentRemoteViews
                this.bigContentRemoteViews = bigContentRemoteViews
            }
        }.onFailure {

        }

        return remoteViewUiModel
    }

    private suspend inline fun loadDailyForecast(
        remoteViewCreator: NotificationRemoteViewsCreator<DailyNotificationForecastUiModel>
    ): RemoteViewUiModel {
        val result = viewModel.loadHourlyForecast()

        val remoteViewUiModel = RemoteViewUiModel(result is UiState.Success)

        result.onSuccess {
            val smallContentRemoteViews = remoteViewCreator.createSmallContentView(it, context)
            val bigContentRemoteViews = remoteViewCreator.createBigContentView(it, context)
            remoteViewUiModel.apply {
                this.smallContentRemoteViews = smallContentRemoteViews
                this.bigContentRemoteViews = bigContentRemoteViews
            }
        }.onFailure {

        }

        return remoteViewUiModel
    }
}