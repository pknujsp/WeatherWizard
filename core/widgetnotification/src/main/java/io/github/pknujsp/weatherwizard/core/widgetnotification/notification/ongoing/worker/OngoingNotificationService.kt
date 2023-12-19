package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.worker

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.NotificationViewState
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.OngoingNotificationRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.util.NotificationIconGenerator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import kotlin.properties.Delegates

@HiltWorker
class OngoingNotificationService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val remoteViewsModel: OngoingNotificationRemoteViewModel,
    private val featureStatusManager: FeatureStatusManager
) : AppComponentService<OngoingNotificationServiceArgument>(context, params, Companion) {

    private var retryPendingIntent: PendingIntent by Delegates.notNull()

    companion object : IWorker {
        override val name: String = "OngoingNotificationWorker"
        override val requiredFeatures: Array<FeatureType> = arrayOf(
            FeatureType.NETWORK,
            FeatureType.POST_NOTIFICATION_PERMISSION,
        )
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(context: Context, argument: OngoingNotificationServiceArgument): Result {
        start(context, argument)
        return Result.success()
    }

    private suspend fun start(context: Context, argument: OngoingNotificationServiceArgument) {
        retryPendingIntent = appNotificationManager.getRefreshPendingIntent(
            context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.OngoingNotification(),
        )

        if (!checkFeatureStateAndNotify(requiredFeatures, context)) {
            return
        }

        val notificationEntity = remoteViewsModel.loadNotification()

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE), context)) {
            return
        }

        appNotificationManager.notifyLoadingNotification(NotificationType.ONGOING, context)
        val uiState = remoteViewsModel.load(notificationEntity)

        val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
            RemoteViewsCreatorManager.getByNotificationType(uiState.notificationType)

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
                smallFailedContentRemoteViews = UiStateRemoteViewCreator.createView(
                    context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    retryPendingIntent,
                ),
                bigFailedContentRemoteViews = UiStateRemoteViewCreator.createView(
                    context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    UiStateRemoteViewCreator.ViewSizeType.BIG,
                    retryPendingIntent,
                ),
                notificationType = NotificationType.ONGOING,
                refreshPendingIntent = retryPendingIntent)
        }
        appNotificationManager.notifyNotification(NotificationType.ONGOING, context, notificationState)
        Log.d("OngoingNotificationService", "notify $notificationState")
    }

    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>, context: Context): Boolean {
        return when (val state = featureStatusManager.status(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val smallRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType.failedReason,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    state.featureType.getPendingIntent(context),
                    visibilityOfCompleteButton = true)
                val bigRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType.failedReason,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.BIG,
                    state.featureType.getPendingIntent(context),
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

}