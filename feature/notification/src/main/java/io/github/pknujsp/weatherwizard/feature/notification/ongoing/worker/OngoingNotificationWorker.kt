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
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationViewState
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.UiStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.common.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.DailyNotificationWorker
import io.github.pknujsp.weatherwizard.feature.notification.manager.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.util.NotificationIconGenerator


@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val remoteViewsModel: OngoingNotificationRemoteViewModel
) : CoroutineWorker(context, params) {

    private val appNotificationManager = AppNotificationManager(context)

    private val retryPendingIntent
        get() = appNotificationManager.getRefreshPendingIntent(context,
            NotificationType.ONGOING,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            OngoingNotificationReceiver::class)

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

        appNotificationManager.notifyLoadingNotification(NotificationType.ONGOING, context)
        val uiState = remoteViewsModel.load(notificationEntity)

        val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
            RemoteViewsCreatorManager.createRemoteViewsCreator(uiState.notificationType)

        val notificationState = if (uiState.isSuccessful) {
            val model = OngoingNotificationRemoteViewUiModelMapper().mapToUiModel(uiState.model!!, remoteViewsModel.units)
            val header = uiState.header!!
            val smallRemoteView = creator.createSmallContentView(model, header, context)
            val bigRemoteView = creator.createBigContentView(model, header, context)

            NotificationViewState(true,
                notificationType = NotificationType.ONGOING,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                icon = NotificationIconGenerator.createIcon(context, uiState.notificationIconType!!, uiState.model, remoteViewsModel.units),
                refreshPendingIntent = retryPendingIntent)
        } else {
            NotificationViewState(false,
                smallFailedContentRemoteViews = UiStateRemoteViewCreator.createView(context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    retryPendingIntent,
                    UiStateRemoteViewCreator.ViewSizeType.SMALL),
                bigFailedContentRemoteViews = UiStateRemoteViewCreator.createView(context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    retryPendingIntent,
                    UiStateRemoteViewCreator.ViewSizeType.BIG),
                notificationType = NotificationType.ONGOING,
                refreshPendingIntent = retryPendingIntent)
        }
        appNotificationManager.notifyNotification(NotificationType.ONGOING, context, notificationState)
        return Result.success()
    }

    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>): Boolean {
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val smallRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    visibilityOfCompleteButton = true)
                val bigRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfCompleteButton = true)

                val notificationViewState = NotificationViewState(false,
                    smallFailedContentRemoteViews = smallRemoteViews,
                    bigFailedContentRemoteViews = bigRemoteViews,
                    notificationType = NotificationType.ONGOING,
                    refreshPendingIntent = retryPendingIntent)
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