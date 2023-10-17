package io.github.pknujsp.weatherwizard.feature.notification.ongoing

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
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewsEntity
import io.github.pknujsp.weatherwizard.feature.notification.common.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.common.INotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val remoteViewsModel: OngoingNotificationRemoteViewsModel
) : CoroutineWorker(context, params) {
    private val appNotificationManager = AppNotificationManager(context)

    companion object : INotificationWorker {
        override val name: String get() = "OngoingNotificationWorker"
        override val id: UUID get() = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !context.checkSelfPermission(PermissionType.NOTIFICATION)) {
                Result.success()
            }

            appNotificationManager.notifyLoadingNotification(NotificationType.ONGOING, context)
            val result = remoteViewsModel.load()
            result.onSuccess {
                it.refreshPendingIntent = appNotificationManager.getRefreshPendingIntent(context, NotificationType.ONGOING)

                val remoteViewsCreator = OngoingNotificationRemoteViewsCreator()
                val smallContentRemoteViews = remoteViewsCreator.createSmallContentView(it, context)
                val bigContentRemoteViews = remoteViewsCreator.createBigContentView(it, context)

                val entity = RemoteViewsEntity(true,
                    smallContentRemoteViews, bigContentRemoteViews, "${it.address} • ${it.time}", it.currentWeather.weatherIcon)
                appNotificationManager.notifyNotification(NotificationType.ONGOING, context, entity)
            }
            Result.success()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
    }

}