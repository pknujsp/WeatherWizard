package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationViewState
import io.github.pknujsp.weatherwizard.core.common.manager.ServiceType
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.feature.UiStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.notification.daily.worker.DailyNotificationWorker
import io.github.pknujsp.weatherwizard.feature.notification.manager.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationRemoteViewUiModelMapper
import io.github.pknujsp.weatherwizard.feature.notification.remoteview.NotificationRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.notification.util.NotificationIconGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class OngoingNotificationService : LifecycleService() {

    @Inject lateinit var remoteViewsModel: OngoingNotificationRemoteViewModel
    @Inject @CoDispatcher(CoDispatcherType.IO) lateinit var dispatcher: CoroutineDispatcher
    private val appNotificationManager by lazy {
        AppNotificationManager(this@OngoingNotificationService)
    }
    private val retryPendingIntent
        get() = appNotificationManager.getRefreshPendingIntent(this,
            NotificationType.ONGOING,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            OngoingNotificationReceiver::class)

    companion object : IWorker {

        const val ACTION_START_LOCATION_SERVICE = "ACTION_START_LOCATION_SERVICE"
        const val ACTION_STOP_LOCATION_SERVICE = "ACTION_STOP_LOCATION_SERVICE"
        override val name: String get() = "OngoingNotificationWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK, FeatureType.POST_NOTIFICATION_PERMISSION)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun start() {
        lifecycleScope.launch(dispatcher) {
            startForeground(NotificationType.WORKING.notificationId,
                appNotificationManager.createForegroundNotification(this@OngoingNotificationService, NotificationType.WORKING))
            doWork()
            stop()
        }
    }

    private suspend fun doWork() {
        if (!checkFeatureStateAndNotify(DailyNotificationWorker.requiredFeatures)) {
            return
        }
        val notificationEntity = remoteViewsModel.loadNotification()

        if (notificationEntity.location.locationType is LocationType.CurrentLocation && !checkFeatureStateAndNotify(arrayOf(FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE))) {
            return
        }

        Log.d("OngoingNotificationService", "doWork: ${notificationEntity.location.locationType}")
        appNotificationManager.notifyLoadingNotification(NotificationType.ONGOING, this)
        val uiState = remoteViewsModel.load(notificationEntity)

        val creator: NotificationRemoteViewsCreator<RemoteViewUiModel> =
            RemoteViewsCreatorManager.createRemoteViewsCreator(uiState.notificationType)

        val notificationState = if (uiState.isSuccessful) {
            val model = OngoingNotificationRemoteViewUiModelMapper().mapToUiModel(uiState.model!!, remoteViewsModel.units)
            val header = uiState.header!!
            val smallRemoteView = creator.createSmallContentView(model, header, this)
            val bigRemoteView = creator.createBigContentView(model, header, this)

            NotificationViewState(true,
                notificationType = NotificationType.ONGOING,
                smallContentRemoteViews = smallRemoteView,
                bigContentRemoteViews = bigRemoteView,
                icon = NotificationIconGenerator.createIcon(this, uiState.notificationIconType!!, uiState.model, remoteViewsModel.units),
                refreshPendingIntent = retryPendingIntent)
        } else {
            NotificationViewState(false,
                smallFailedContentRemoteViews = UiStateRemoteViewCreator.createView(this,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    retryPendingIntent,
                    UiStateRemoteViewCreator.ViewSizeType.SMALL),
                bigFailedContentRemoteViews = UiStateRemoteViewCreator.createView(this,
                    R.string.title_failed_to_load_data,
                    R.string.failed_to_load_data,
                    R.string.refresh,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    retryPendingIntent,
                    UiStateRemoteViewCreator.ViewSizeType.BIG),
                notificationType = NotificationType.ONGOING,
                refreshPendingIntent = retryPendingIntent)
        }
        Log.d("OngoingNotificationService", "doWork: $notificationState")
        appNotificationManager.notifyNotification(NotificationType.ONGOING, this, notificationState)
    }

    private fun checkFeatureStateAndNotify(featureTypes: Array<FeatureType>): Boolean {
        return when (val state = FeatureStateChecker.checkFeatureState(this, featureTypes)) {
            is FeatureState.Unavailable -> {
                val smallRemoteViews = UiStateRemoteViewCreator.createView(this,
                    state.featureType,
                    RemoteViewCreator.ContainerType.NOTIFICATION_SMALL,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.SMALL,
                    visibilityOfCompleteButton = true)
                val bigRemoteViews = UiStateRemoteViewCreator.createView(this,
                    state.featureType,
                    RemoteViewCreator.ContainerType.NOTIFICATION_BIG,
                    viewSizeType = UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfCompleteButton = true)

                val notificationViewState = NotificationViewState(false,
                    smallFailedContentRemoteViews = smallRemoteViews,
                    bigFailedContentRemoteViews = bigRemoteViews,
                    notificationType = NotificationType.ONGOING,
                    refreshPendingIntent = retryPendingIntent)
                appNotificationManager.notifyNotification(NotificationType.ONGOING, this, notificationViewState)
                false
            }

            else -> true
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.run {
            Log.d("OngoingNotificationService", "onStartCommand: ${intent.action}")
            when (action) {
                ACTION_START_LOCATION_SERVICE -> start()
                ACTION_STOP_LOCATION_SERVICE -> stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }
}