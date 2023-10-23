package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val widgetRemoteViewModel: WidgetRemoteViewModel
) : CoroutineWorker(context, params) {

    companion object : IWorker {
        override val name: String get() = "WidgetWorker"
        override val id: UUID get() = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val action = WidgetManager.Action.valueOf(inputData.getString("action")!!)
            val appWidgetIds = inputData.getIntArray("appWidgetIds")!!

            when (action) {
                WidgetManager.Action.UPDATE -> {
                    widgetRemoteViewModel.updateWidgets(appWidgetIds)
                }

                WidgetManager.Action.DELETE -> {
                    widgetRemoteViewModel.deleteWidgets(appWidgetIds)
                }
            }

            Result.success()
        }
    }
}