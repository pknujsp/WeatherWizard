package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import javax.inject.Inject

class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context, private val widgetRepository: WidgetRepository
) : AppComponentBackgroundService<WidgetUpdatedArgument>(context) {

    override val id: Int = "WidgetUpdateBackgroundService".hashCode()

    override suspend fun doWork(argument: WidgetUpdatedArgument): Result<Unit> {
        for (widgetId in argument.widgetIds) {
            widgetRepository.delete(widgetId)
        }
        return Result.success(Unit)
    }

}