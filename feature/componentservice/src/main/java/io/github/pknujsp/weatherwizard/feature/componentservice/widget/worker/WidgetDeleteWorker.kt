package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetServiceArgument


@HiltWorker
class WidgetDeleteWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val widgetRemoteViewModel: DeleteWidgetRemoteViewModel
) : AppComponentService<WidgetServiceArgument>(context, params, Companion) {

    override val isRequiredForegroundService: Boolean = false

    companion object : IWorker {
        override val name: String = "WidgetDeleteWorker"
        override val requiredFeatures: Array<FeatureType> = arrayOf()
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(context: Context, argument: WidgetServiceArgument): Result {
        widgetRemoteViewModel.deleteWidgets(argument.widgetIds)
        return Result.success()
    }

}