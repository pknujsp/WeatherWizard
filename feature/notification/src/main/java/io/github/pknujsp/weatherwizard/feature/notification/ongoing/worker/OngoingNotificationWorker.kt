package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

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
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationViewState
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.FeatureStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RetryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.DailyNotificationWorker
import io.github.pknujsp.weatherwizard.feature.notification.manager.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationUiModelMapper
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.util.NotificationIconGenerator


@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val remoteViewsModel: OngoingNotificationRemoteViewModel
) : CoroutineWorker(context, params) {

    private val appNotificationManager = AppNotificationManager(context)

    companion object : IWorker {
        override val name: String get() = "OngoingNotificationWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK, FeatureType.POST_NOTIFICATION_PERMISSION)
    }


    override suspend fun doWork(): Result {
        if (!checkFeatureStateAndNotify(DailyNotificationWorker.requiredFeatures)) {
            return Result.success()
        }

        val notificationEntity = remoteViewsModel.loadNotification()

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE))) {
            return Result.success()
        }

        val uiState = remoteViewsModel.load(notificationEntity)

        val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
            RemoteViewsCreatorManager.createRemoteViewsCreator(uiState.notificationType)

        val retryPendingIntent = appNotificationManager.getRefreshPendingIntent(context,
            NotificationType.ONGOING,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            OngoingNotificationReceiver::class)

        val notificationState = if (uiState.isSuccessful) {
            val model = OngoingNotificationUiModelMapper().mapToUiModel(uiState.model!!, remoteViewsModel.units)

            val smallRemoteView = creator.createSmallContentView(model, context)
            val bigRemoteView = creator.createBigContentView(model, context)

            NotificationViewState(
                true,
                notificationType = NotificationType.ONGOING,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                icon = NotificationIconGenerator.createIcon(context, uiState.notificationIconType!!, uiState.model, remoteViewsModel.units),
            )
        } else {
            NotificationViewState(
                false,
                failedContentRemoteViews = RetryRemoteViewCreator.createView(context,
                    context.getString(R.string.refresh),
                    retryPendingIntent,
                    RemoteViewCreator.NOTIFICATION),
                notificationType = NotificationType.ONGOING,
            )
        }
        appNotificationManager.notifyNotification(NotificationType.ONGOING, context, notificationState)
        return Result.success()
    }


    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>): Boolean {
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = FeatureStateRemoteViewCreator.createView(context, state.featureType, RemoteViewCreator.NOTIFICATION)
                val notificationViewState = NotificationViewState(
                    false,
                    failedContentRemoteViews = remoteViews,
                    notificationType = NotificationType.ONGOING,
                )
                appNotificationManager.notifyNotification(NotificationType.ONGOING, context, notificationViewState)
                false
            }

            else -> true
        }
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appNotificationManager.createForegroundNotification(context, NotificationType.WORKING)
    }

}