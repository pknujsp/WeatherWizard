package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.AppWidgetAutoRefreshScheduler

class WidgetStarterImpl(
    private val widgetManager: WidgetManager,
    private val appAlarmManager: AppAlarmManager,
    private val settingsRepository: SettingsRepository,
) : WidgetStarter {

    private fun scheduleAutoRefresh(context: Context, refreshInterval: RefreshInterval) {
        AppWidgetAutoRefreshScheduler(widgetManager).run {
            if (isScheduled(context, appAlarmManager) is AppAlarmManager.ScheduledState.NotScheduled) {
                scheduleAutoRefresh(context, appAlarmManager, refreshInterval)
            }
        }
    }

    override fun redrawWidgets(context: Context) {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { widgetProvider ->
            // 위젯 뷰 새로고침, 데이터 업데이트 X
            val intent = Intent().apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                component = widgetProvider
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toIntArray())
            }
            context.sendBroadcast(intent)

            // 위젯 자동 업데이트 예약
            val refreshInterval = settingsRepository.settings.replayCache.last().widgetAutoRefreshInterval
            scheduleAutoRefresh(context, refreshInterval)
        }
    }

}


interface WidgetStarter {
    fun redrawWidgets(context: Context)
}