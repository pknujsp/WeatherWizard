package io.github.pknujsp.weatherwizard.feature.notification.daily.worker

import android.app.PendingIntent
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.ui.notification.NotificationViewState
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.FeatureStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RetryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.notification.manager.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator

@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val viewModel: DailyNotificationRemoteViewModel
) : CoroutineWorker(context, params) {
    private val appNotificationManager = AppNotificationManager(context)
    private val retryPendingIntent
        get() = appNotificationManager.getRefreshPendingIntent(context,
            NotificationType.DAILY,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            DailyNotificationReceiver::class)

    companion object : IWorker {
        override val name: String = "DailyNotificationWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK, FeatureType.POST_NOTIFICATION_PERMISSION)
        const val NOTIFICATION_ID_KEY = "notificationId"
    }

    override suspend fun doWork(): Result {
        val inputDataMap = inputData.keyValueMap
        if (NOTIFICATION_ID_KEY !in inputDataMap) {
            return Result.success()
        }

        if (!checkFeatureStateAndNotify(requiredFeatures)) {
            return Result.success()
        }
        val notificationId = inputDataMap[NOTIFICATION_ID_KEY] as Long
        val notificationEntity = viewModel.loadNotification(notificationId)

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE))) {
            return Result.success()
        }

        val uiModel = viewModel.load(notificationEntity)
        val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
            RemoteViewsCreatorManager.createRemoteViewsCreator(uiModel.notificationType)

        if (uiModel.isSuccessful) {
            val model = DailyNotificationForecastUiModelMapper().mapToUiModel(uiModel.model!!, viewModel.units)
            val header = uiModel.header!!
            val smallRemoteView = creator.createSmallContentView(model, header, context)
            val bigRemoteView = creator.createBigContentView(model, header, context)

            val notificationViewState = NotificationViewState(
                true,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                notificationType = NotificationType.DAILY,
                refreshPendingIntent = retryPendingIntent,
            )
            appNotificationManager.notifyNotification(NotificationType.DAILY, context, notificationViewState)
        } else {

            val notificationViewState = NotificationViewState(
                false,
                failedContentRemoteViews = RetryRemoteViewCreator.createView(context,
                    context.getString(R.string.refresh),
                    retryPendingIntent,
                    RemoteViewCreator.NOTIFICATION),
                refreshPendingIntent = retryPendingIntent,
                notificationType = NotificationType.DAILY,
            )
            appNotificationManager.notifyNotification(NotificationType.DAILY, context, notificationViewState)
        }



        return Result.success()
    }


    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>): Boolean {
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = FeatureStateRemoteViewCreator.createView(context, state.featureType, RemoteViewCreator.NOTIFICATION)
                val notificationViewState = NotificationViewState(
                    false,
                    failedContentRemoteViews = remoteViews,
                    refreshPendingIntent = retryPendingIntent,
                    notificationType = NotificationType.DAILY,
                )
                appNotificationManager.notifyNotification(NotificationType.DAILY, context, notificationViewState)
                false
            }

            else -> true
        }
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
    }

}