package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.everyweather.core.FeatureStateManager
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.widgetnotification.model.AppComponentCoroutineService
import io.github.pknujsp.everyweather.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.everyweather.core.widgetnotification.model.IWorker
import io.github.pknujsp.everyweather.core.widgetnotification.model.NotificationViewState
import io.github.pknujsp.everyweather.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.everyweather.core.widgetnotification.model.RemoteViewUiModelMapperManager
import io.github.pknujsp.everyweather.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.everyweather.core.widgetnotification.notification.util.NotificationIconGenerator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager

@HiltWorker
class OngoingNotificationCoroutineService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val remoteViewsModel: OngoingNotificationRemoteViewModel,
) : AppComponentCoroutineService<OngoingNotificationServiceArgument>(context, params, Companion) {

    private val pendingIntentToRefresh: PendingIntent by lazy {
        ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.OngoingNotification(),
            actionString = AppComponentServiceReceiver.ACTION_REFRESH).pendingIntent!!
    }

    companion object : IWorker {
        override val name: String = "OngoingNotificationWorker"
        override val requiredFeatures: Array<FeatureType> = arrayOf(
            FeatureType.NETWORK,
            FeatureType.POST_NOTIFICATION_PERMISSION,
        )
    }

    override suspend fun doWork(context: Context, argument: OngoingNotificationServiceArgument): Result {
        appNotificationManager.notifyLoadingNotification(NotificationType.ONGOING)
        start(context, argument)
        Log.d("OngoingNotificationCoroutineService", "doWork")
        return Result.success()
    }

    private suspend fun start(context: Context, argument: OngoingNotificationServiceArgument) {
        if (!checkFeatureStateAndNotify(requiredFeatures, context)) {
            return
        }
        val notificationEntity = remoteViewsModel.loadNotification()

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE,
                FeatureType.BACKGROUND_LOCATION_PERMISSION), context)) {
            return
        }

        val uiState = remoteViewsModel.load(notificationEntity)
        Log.d("OngoingNotificationCoroutineService", "uiState: $uiState")
        val notificationState = if (uiState.isSuccessful) {
            val uiModelMapper = RemoteViewUiModelMapperManager.getByOngoingNotificationType(uiState.notificationType)

            val header = uiState.header!!
            val model = uiModelMapper.mapToUiModel(uiState.model!!, remoteViewsModel.units)

            val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
                RemoteViewsCreatorManager.getByOngoingNotificationType(uiState.notificationType)
            val smallRemoteView = creator.createSmallContentView(model, header, context)
            val bigRemoteView = creator.createBigContentView(model, header, context)

            NotificationViewState(true,
                notificationType = NotificationType.ONGOING,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                icon = NotificationIconGenerator.createIcon(context,
                    uiState.notificationIconType!!,
                    uiState.model!!,
                    remoteViewsModel.units),
                refreshPendingIntent = pendingIntentToRefresh)
        } else {
            NotificationViewState(false,
                smallFailedContentRemoteViews = UiStateRemoteViewCreator.createView(
                    context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    pendingIntentToRefresh,
                ),
                bigFailedContentRemoteViews = UiStateRemoteViewCreator.createView(
                    context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    UiStateRemoteViewCreator.ViewSizeType.BIG,
                    pendingIntentToRefresh,
                ),
                notificationType = NotificationType.ONGOING,
                refreshPendingIntent = pendingIntentToRefresh)
        }
        appNotificationManager.notifyNotification(NotificationType.ONGOING, notificationState.toExtendNotification())
    }

    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>, context: Context): Boolean {
        return when (val state = featureStateManager.retrieveFeaturesState(featureTypes, context)) {
            is FeatureStateManager.FeatureState.Unavailable -> {
                val pendingIntent = state.featureType.getPendingIntent(context)

                val smallRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    pendingIntent,
                    visibilityOfCompleteButton = true)

                val bigRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.BIG,
                    pendingIntent,
                    visibilityOfCompleteButton = true)

                val notificationViewState = NotificationViewState(false,
                    smallFailedContentRemoteViews = smallRemoteViews,
                    bigFailedContentRemoteViews = bigRemoteViews,
                    notificationType = NotificationType.ONGOING,
                    refreshPendingIntent = pendingIntentToRefresh)
                appNotificationManager.notifyNotification(NotificationType.ONGOING, notificationViewState.toExtendNotification())
                false
            }

            else -> true
        }
    }

}