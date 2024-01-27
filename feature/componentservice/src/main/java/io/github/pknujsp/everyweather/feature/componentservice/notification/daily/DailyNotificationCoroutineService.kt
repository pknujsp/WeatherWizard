package io.github.pknujsp.everyweather.feature.componentservice.notification.daily

import android.content.Context
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
import io.github.pknujsp.everyweather.core.widgetnotification.model.DailyNotificationServiceArgument
import io.github.pknujsp.everyweather.core.widgetnotification.model.IWorker
import io.github.pknujsp.everyweather.core.widgetnotification.model.NotificationViewState
import io.github.pknujsp.everyweather.core.widgetnotification.model.RemoteViewUiModelMapperManager
import io.github.pknujsp.everyweather.core.widgetnotification.notification.remoteview.DailyNotificationRemoteViewsCreator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.UiStateRemoteViewCreator

@HiltWorker
class DailyNotificationCoroutineService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val viewModel: DailyNotificationRemoteViewModel,
) : AppComponentCoroutineService<DailyNotificationServiceArgument>(context, params, Companion) {

    companion object : IWorker {
        override val name: String = "DailyNotificationWorker"
        override val requiredFeatures: Array<FeatureType> = arrayOf(
            FeatureType.NETWORK,
            FeatureType.POST_NOTIFICATION_PERMISSION,
            FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION,
        )

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
                FeatureType.LOCATION_SERVICE,
                FeatureType.BACKGROUND_LOCATION_PERMISSION), context)) {
            return
        }

        val uiModel = viewModel.load(notificationEntity)

        if (uiModel.isSuccessful) {
            val uiModelMapper = RemoteViewUiModelMapperManager.getByDailyNotificationType(uiModel.notificationType)
            val model = uiModelMapper.mapToUiModel(uiModel.model!!, viewModel.units)
            val header = uiModel.header!!

            val creator: DailyNotificationRemoteViewsCreator<RemoteViewUiModel> =
                RemoteViewsCreatorManager.getByDailyNotificationType(uiModel.notificationType)
            val smallRemoteView = creator.createSmallContentView(model, header, context)
            val bigRemoteView = creator.createBigContentView(model, header, context)

            val notificationViewState = NotificationViewState(
                true,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                notificationType = NotificationType.DAILY,
            )
            appNotificationManager.notifyNotification(NotificationType.DAILY, notificationViewState.toExtendNotification())
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
            appNotificationManager.notifyNotification(NotificationType.DAILY, notificationViewState.toExtendNotification())
        }
    }


    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>, context: Context): Boolean {
        return when (val state = featureStateManager.retrieveFeaturesState(featureTypes, context)) {
            is FeatureStateManager.FeatureState.Unavailable -> {
                val smallRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    visibilityOfCompleteButton = false,
                    visibilityOfActionButton = false)
                val bigRemoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfCompleteButton = false,
                    visibilityOfActionButton = false)

                val notificationViewState = NotificationViewState(
                    false,
                    smallFailedContentRemoteViews = smallRemoteViews,
                    bigFailedContentRemoteViews = bigRemoteViews,
                    notificationType = NotificationType.DAILY,
                )
                appNotificationManager.notifyNotification(NotificationType.DAILY, notificationViewState.toExtendNotification())
                false
            }

            else -> true
        }
    }
}