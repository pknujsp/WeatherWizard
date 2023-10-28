package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.app.PendingIntent
import android.content.Context
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
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.FeatureStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RetryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager


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
        println("WidgetWorker.doWork-----------------------")

        val action = WidgetManager.Action.valueOf(inputData.getString("action")!!)
        val appWidgetIds = inputData.getIntArray("appWidgetIds")!!

        when (action) {
            WidgetManager.Action.UPDATE, WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION -> {
                widgetRemoteViewModel.init()

                if (appWidgetIds.isNotEmpty() and widgetRemoteViewModel.isInitializng(appWidgetIds)) {
                    println("WidgetWorker.doWork: isInitializng")
                    return Result.success()
                }
                if (!checkFeatureStateAndUpdateWidgets(requiredFeatures, appWidgetIds)) {
                    println("WidgetWorker.doWork: checkFeatureStateAndUpdateWidgets")
                    return Result.success()
                }

                val excludeAppWidgetIds = mutableListOf<Int>()
                var excludeAppWidgetType: WidgetType? = null
                var excludeLocationType: LocationType? = null

                widgetRemoteViewModel.widgetIdsByLocationType<LocationType.CurrentLocation>().let {
                    if (action == WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION) {
                        excludeLocationType = LocationType.CurrentLocation
                    }

                    if (!checkFeatureStateAndUpdateWidgets(arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE), it)) {
                        when (val currentLocation = gpsLocationManager.getCurrentLocation()) {
                            is AppLocationManager.CurrentLocationResult.Success -> {
                                widgetRemoteViewModel.currentLocation = io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate(
                                    currentLocation.location.latitude,
                                    currentLocation.location.longitude)
                            }

                            is AppLocationManager.CurrentLocationResult.Failure -> {
                                excludeAppWidgetIds.addAll(it.toList())
                                updateRetryWidgets(it, widgetManager.getUpdatePendingIntent(context, it))
                            }
                        }
                    }
                }

                val widgetStates = widgetRemoteViewModel.load(excludeAppWidgetIds, excludeLocationType)
                widgetStates.forEach { widgetState ->

                }
            }

            WidgetManager.Action.DELETE -> {
                widgetRemoteViewModel.deleteWidgets(appWidgetIds)
            }

            else -> {

            }
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

    private fun updateRetryWidgets(widgetIds: IntArray, pendingIntent: PendingIntent) {
        val remoteViews = retryRemoteViewCreator.createView(context,
            context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.again),
            pendingIntent,
            RemoteViewCreator.WIDGET)
        widgetIds.forEach {
            widgetManager.updateWidget(it, remoteViews, context)
        }
    }
}