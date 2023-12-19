package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily

import android.content.Context
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
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.DailyNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.NotificationViewState
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewUiModelMapperManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator

@HiltWorker
class DailyNotificationService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val viewModel: DailyNotificationRemoteViewModel,
    private val featureStatusManager: FeatureStatusManager
) : AppComponentService<DailyNotificationServiceArgument>(context, params, Companion) {

    companion object : IWorker {
        override val name: String = "DailyNotificationWorker"
        override val requiredFeatures: Array<FeatureType> = arrayOf(
            FeatureType.NETWORK,
            FeatureType.POST_NOTIFICATION_PERMISSION,
            FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION,
        )
        override val workerId: Int = name.hashCode()
    }


    override suspend fun doWork(context: Context, argument: DailyNotificationServiceArgument): Result {
        start(context, argument)
        return Result.success()
    }

    private suspend fun start(context: Context, argument: DailyNotificationServiceArgument) {
        if (!checkFeatureStateAndNotify(requiredFeatures, context)) {
            return
        }

        val notificationId = argument.notificationId
        val notificationEntity = viewModel.loadNotification(notificationId)

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE), context)) {
            return
        }

        val uiModel = viewModel.load(notificationEntity)

        if (uiModel.isSuccessful) {
            val uiModelMapper = RemoteViewUiModelMapperManager.getByDailyNotificationType(uiModel.notificationType)
            val model = uiModelMapper.mapToUiModel(uiModel.model!!, viewModel.units)
            val header = uiModel.header!!

            val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
                RemoteViewsCreatorManager.getByDailyNotificationType(uiModel.notificationType)
            val smallRemoteView = creator.createSmallContentView(model, header, context)
            val bigRemoteView = creator.createBigContentView(model, header, context)

            val notificationViewState = NotificationViewState(
                true,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                notificationType = NotificationType.DAILY,
            )
            appNotificationManager.notifyNotification(NotificationType.DAILY, context, notificationViewState)
        } else {

            val notificationViewState = NotificationViewState(
                false,
                smallFailedContentRemoteViews = UiStateRemoteViewCreator.createView(context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    UiStateRemoteViewCreator.ViewSizeType.SMALL),
                bigContentRemoteViews = UiStateRemoteViewCreator.createView(context,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    UiStateRemoteViewCreator.ViewSizeType.BIG),
                notificationType = NotificationType.DAILY,
            )
            appNotificationManager.notifyNotification(NotificationType.DAILY, context, notificationViewState)
        }
    }


    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>, context: Context): Boolean {
        return when (val state = featureStatusManager.status(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val smallRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType.failedReason,
                    io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    visibilityOfCompleteButton = false,
                    visibilityOfActionButton = false)
                val bigRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType.failedReason,
                    io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfCompleteButton = false,
                    visibilityOfActionButton = false)

                val notificationViewState = NotificationViewState(
                    false,
                    smallFailedContentRemoteViews = smallRemoteViews,
                    bigFailedContentRemoteViews = bigRemoteViews,
                    notificationType = NotificationType.DAILY,
                )
                appNotificationManager.notifyNotification(NotificationType.DAILY, context, notificationViewState)
                false
            }

            else -> true
        }
    }
}