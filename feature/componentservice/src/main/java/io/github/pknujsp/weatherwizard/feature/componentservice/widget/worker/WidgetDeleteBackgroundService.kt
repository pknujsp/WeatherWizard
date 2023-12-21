package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetDeletedArgument
import javax.inject.Inject


class WidgetDeleteBackgroundService @Inject constructor(
    @ApplicationContext context: Context, private val widgetRepository: WidgetRepository
) : AppComponentBackgroundService<WidgetDeletedArgument>(context) {

    override val id: Int = "WidgetDeleteBackgroundService".hashCode()

    override suspend fun doWork(argument: WidgetDeletedArgument): Result<Unit> {
        for (widgetId in argument.widgetIds) {
            widgetRepository.delete(widgetId)
        }
        return Result.success(Unit)
    }

}