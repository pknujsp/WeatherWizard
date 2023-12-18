package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentService
import io.github.pknujsp.weatherwizard.feature.notification.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.NotificationViewState
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.OngoingNotificationRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.util.NotificationIconGenerator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import javax.inject.Inject
import kotlin.properties.Delegates

class OngoingNotificationService @Inject constructor(
    private val remoteViewsModel: OngoingNotificationRemoteViewModel
) : AppComponentService<OngoingNotificationServiceArgument> {

    private var appNotificationManager: AppNotificationManager by Delegates.notNull()
    private var retryPendingIntent: PendingIntent by Delegates.notNull()

    companion object : IWorker {
        override val name: String get() = "OngoingNotificationWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK,
                FeatureType.POST_NOTIFICATION_PERMISSION,
                FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION,
                FeatureType.BATTERY_OPTIMIZATION)
    }


    override suspend fun start(context: Context, argument: OngoingNotificationServiceArgument) {
        appNotificationManager = AppNotificationManager(context)
        retryPendingIntent = appNotificationManager.getRefreshPendingIntent(
            context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            NotificationAction.Ongoing(),
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

        val creator: io.github.pknujsp.weatherwizard.core.widgetnotification.notification.remoteview.NotificationRemoteViewsCreator<RemoteViewUiModel> =
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
                icon = NotificationIconGenerator.createIcon(context,
                    uiState.notificationIconType!!,
                    uiState.model!!,
                    remoteViewsModel.units),
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
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
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