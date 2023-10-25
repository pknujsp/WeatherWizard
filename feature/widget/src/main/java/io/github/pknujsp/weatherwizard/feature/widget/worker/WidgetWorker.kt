package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
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

    companion object : IWorker {
        override val name: String get() = "WidgetWorker"
    }

    override suspend fun doWork(): Result {
        println("WidgetWorker.doWork-----------------------")
        val action = WidgetManager.Action.valueOf(inputData.getString("action")!!)
        val appWidgetIds = inputData.getIntArray("appWidgetIds")!!

        when (action) {
            WidgetManager.Action.UPDATE -> {
                widgetRemoteViewModel.load()

                if (appWidgetIds.isNotEmpty() and widgetRemoteViewModel.isInitializng(appWidgetIds)) {
                    return Result.success()
                }

                if (widgetRemoteViewModel.hasCurrentLocationType()) {
                    when (val currentLocation = gpsLocationManager.getCurrentLocation()) {
                        is AppLocationManager.CurrentLocationResult.Success -> {
                            widgetRemoteViewModel.currentLocation =
                                currentLocation.location.latitude.toFloat() to currentLocation.location.longitude.toFloat()
                        }

                        is AppLocationManager.CurrentLocationResult.Failure -> {
                            return Result.success()
                        }
                    }
                }
                widgetRemoteViewModel.updateWidgets()
            }

            WidgetManager.Action.DELETE -> {
                widgetRemoteViewModel.deleteWidgets(appWidgetIds)
            }
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return AppNotificationManager(context).createForegroundNotification(context, NotificationType.WORKING)
    }
}