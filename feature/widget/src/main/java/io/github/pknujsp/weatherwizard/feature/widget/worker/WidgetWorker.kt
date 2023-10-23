package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.GpsLocationManager
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val widgetRemoteViewModel: WidgetRemoteViewModel
) : CoroutineWorker(context, params) {

    private val gpsLocationManager: GpsLocationManager by lazy {
        GpsLocationManager(context)
    }

    companion object : IWorker {
        override val name: String get() = "WidgetWorker"
        override val id: UUID get() = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        val action = WidgetManager.Action.valueOf(inputData.getString("action")!!)
        val appWidgetIds = inputData.getIntArray("appWidgetIds")!!

        widgetRemoteViewModel.load()
        val widgetEntities = widgetRemoteViewModel.widgetEntities

        if (widgetEntities.any { it.content.getLocationType() is LocationType.CurrentLocation }) {
            when (val currentLocation = gpsLocationManager.getCurrentLocation()) {
                is GpsLocationManager.CurrentLocationResult.Success -> {
                    widgetRemoteViewModel.currentLocation =
                        currentLocation.location.latitude.toFloat() to currentLocation.location.longitude.toFloat()
                }

                is GpsLocationManager.CurrentLocationResult.Failure -> {

                }
            }
        }

        when (action) {
            WidgetManager.Action.UPDATE -> {
                widgetRemoteViewModel.updateWidgets()
            }

            WidgetManager.Action.DELETE -> {
                widgetRemoteViewModel.deleteWidgets(appWidgetIds)
            }
        }

        return Result.success()
    }
}