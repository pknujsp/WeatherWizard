package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context,
    private val widgetRepository: WidgetRepository,
    appSettingsRepository: SettingsRepository,
) : AppComponentBackgroundService<WidgetUpdatedArgument>(context) {

    private val updateAppWidgetViews by lazy {
        AppWidgetViewUpdater(
            AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.WIDGET_MANAGER),
            widgetRepository,
            featureStateManager,
            appSettingsRepository.settings.replayCache.last().units,
        )
    }

    override suspend fun doWork(argument: WidgetUpdatedArgument): Result<Unit> {
        when (argument.action) {
            WidgetUpdatedArgument.DRAW, WidgetUpdatedArgument.DRAW_ALL -> {
                updateAppWidgetViews(context, argument.widgetIds.toList())
            }

            WidgetUpdatedArgument.DELETE -> {
                for (widgetId in argument.widgetIds) {
                    widgetRepository.delete(widgetId)
                }
            }

            WidgetUpdatedArgument.DELETE_ALL -> {
                widgetRepository.deleteAll()
            }
        }

        Log.d("WidgetUpdateBackgroundService", "widget doWork: ${argument.action}")
        return Result.success(Unit)
    }

}