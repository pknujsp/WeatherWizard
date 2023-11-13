package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.checkFeatureStateAndUpdateWidgets
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.FeatureStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RetryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.domain.weather.ResponseState
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.onFailure
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.onSuccess
import kotlin.properties.Delegates


@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val widgetRemoteViewModel: WidgetRemoteViewModel
) : CoroutineWorker(context, params) {

    private val gpsLocationManager: AppLocationManager by lazy {
        AppLocationManager.getInstance(context)
    }
    private val widgetManager: WidgetManager by lazy {
        WidgetManager.getInstance(context)
    }
    private val featureStateRemoteViewCreator: FeatureStateRemoteViewCreator by lazy {
        FeatureStateRemoteViewCreator()
    }
    private val retryRemoteViewCreator: RetryRemoteViewCreator by lazy {
        RetryRemoteViewCreator()
    }

    companion object : IWorker {
        override val name: String get() = "WidgetWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK)
    }

    override suspend fun doWork(): Result {
        println("WidgetWorker.doWork----------------------- ${inputData.keyValueMap}")

        val action = WidgetManager.Action.valueOf(inputData.getString("action")!!)
        val appWidgetIds = inputData.getIntArray("appWidgetIds")!!

        widgetRemoteViewModel.init()

        if (!checkFeatureStateAndUpdateWidgets(requiredFeatures, appWidgetIds)) {
            println("WidgetWorker: checkFeatureStateAndUpdateWidgets")
            return Result.success()
        }

        val excludeAppWidgetIds = mutableListOf<Int>()
        var excludeLocationType: LocationType? = null

        if (action == WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION) {
            excludeLocationType = LocationType.CustomLocation()
        }

        widgetRemoteViewModel.widgetEntities.forEach {
            if (!widgetManager.isBind(it.id)) {
                excludeAppWidgetIds.add(it.id)
            }
        }

        widgetRemoteViewModel.widgetIdsByLocationType<LocationType.CurrentLocation>().let {
            if (it.isNotEmpty() and !checkFeatureStateAndUpdateWidgets(arrayOf(FeatureType.LOCATION_PERMISSION,
                    FeatureType.LOCATION_SERVICE), it.toIntArray())) {
                when (val currentLocation = gpsLocationManager.getCurrentLocation()) {
                    is AppLocationManager.CurrentLocationResult.Success -> {
                        widgetRemoteViewModel.currentLocation = io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate(
                            currentLocation.location.latitude,
                            currentLocation.location.longitude)
                    }

                    is AppLocationManager.CurrentLocationResult.Failure -> {
                        excludeAppWidgetIds.addAll(it.toList())
                        updateRetryWidgets(it,
                            widgetManager.getUpdatePendingIntent(context, WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION))
                    }
                }
            }
        }

        val widgetStates = widgetRemoteViewModel.load(excludeAppWidgetIds, excludeLocationType)
        val failedWidgetIds = widgetStates.filter { it.state is ResponseState.Failure }.map { it.appWidgetId }.toIntArray()
        val retryPendingIntent =
            widgetManager.getUpdatePendingIntent(context, WidgetManager.Action.UPDATE_ONLY_WITH_WIDGETS, failedWidgetIds)

        var remoteView: RemoteViews by Delegates.notNull()

        widgetStates.forEach { model ->
            model.state.onSuccess {
                val creator: WidgetRemoteViewsCreator<UiModel> = widgetManager.remoteViewCreator(model.widgetType)
                remoteView = creator.createContentView(it, context)
            }.onFailure {
                remoteView = retryRemoteViewCreator.createView(context,
                    context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.refresh),
                    retryPendingIntent,
                    RemoteViewCreator.WIDGET)
            }

            widgetManager.updateWidget(model.appWidgetId, remoteView, context)
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return AppNotificationManager(context).createForegroundNotification(context, NotificationType.WORKING)
    }

    private fun checkFeatureStateAndUpdateWidgets(featureTypes: Array<FeatureType>, widgetIds: IntArray? = null): Boolean {
        return when (val state = context.checkFeatureStateAndUpdateWidgets(featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = featureStateRemoteViewCreator.createView(context, state.featureType, RemoteViewCreator.WIDGET)
                widgetIds ?: widgetManager.widgetIds.forEach {
                    widgetManager.updateWidget(it, remoteViews, context)
                }
                false
            }

            else -> true
        }
    }

    private fun updateRetryWidgets(widgetIds: List<Int>, pendingIntent: PendingIntent) {
        val remoteViews = retryRemoteViewCreator.createView(context,
            context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.again),
            pendingIntent,
            RemoteViewCreator.WIDGET)
        widgetIds.forEach {
            widgetManager.updateWidget(it, remoteViews, context)
        }
    }

}