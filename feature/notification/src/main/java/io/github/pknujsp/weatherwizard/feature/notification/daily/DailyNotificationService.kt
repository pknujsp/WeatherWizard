package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.DailyNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.NotificationViewState
import io.github.pknujsp.weatherwizard.feature.notification.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.worker.DailyNotificationForecastUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import javax.inject.Inject
import kotlin.properties.Delegates

class DailyNotificationService @Inject constructor(
    private val viewModel: DailyNotificationRemoteViewModel
) : AppComponentService<DailyNotificationServiceArgument> {
    private var appNotificationManager: AppNotificationManager by Delegates.notNull()

    companion object : IWorker {
        override val name: String = "DailyNotificationWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK,
                FeatureType.POST_NOTIFICATION_PERMISSION,
                FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION,
                FeatureType.BATTERY_OPTIMIZATION)
    }

    override suspend fun start(context: Context, argument: DailyNotificationServiceArgument) {
        appNotificationManager = AppNotificationManager(context)

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
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
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