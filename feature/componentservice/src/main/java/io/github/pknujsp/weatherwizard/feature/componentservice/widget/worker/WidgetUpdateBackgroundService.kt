package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context,
    @CoDispatcher(CoDispatcherType.SINGLE) private val dispatcher: CoroutineDispatcher,
    private val widgetRepository: WidgetRepository,
    private val widgetManager: WidgetManager,
    private val appSettingsRepository: SettingsRepository,
    private val appAlarmManager: AppAlarmManager
) : AppComponentBackgroundService<WidgetUpdatedArgument>(context) {

    private val updateAppWidgetViews = AppWidgetViewUpdater(widgetManager,
        widgetRepository,
        featureStateManager,
        appSettingsRepository,
        WidgetViewCacheManagerFactory.getInstance(dispatcher))

    override suspend fun doWork(argument: WidgetUpdatedArgument): Result<Unit> {
        when (argument.action) {
            WidgetUpdatedArgument.SCHEDULE_TO_AUTO_REFRESH -> {
                val autoRefreshInterval = RefreshInterval.default
                AppWidgetAutoRefreshScheduler(widgetManager).scheduleAutoRefresh(context, appAlarmManager, autoRefreshInterval)
            }

            WidgetUpdatedArgument.UPDATE_ALL, WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS -> {
                updateAppWidgetViews(argument, context)
            }

            WidgetUpdatedArgument.DELETE -> {
                for (widgetId in argument.widgetIds) {
                    widgetRepository.delete(widgetId)
                }
            }
        }

        return Result.success(Unit)
    }

}