package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAutoRefreshScheduler
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider.BaseWidgetProvider
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider.SummaryWeatherWidgetProvider

class AppWidgetAutoRefreshScheduler(private val widgetManager: WidgetManager) : ComponentServiceAutoRefreshScheduler {

    private val baseWidgetProvider = SummaryWeatherWidgetProvider::class.java

    private companion object {
        const val TAG = "AppWidgetScheduler"
        val requestCode = TAG.hashCode()
    }

    private fun createAutoRefreshIntent(context: Context) = Intent(context, baseWidgetProvider).apply {
        action = BaseWidgetProvider.ACTION_SCHEDULE_TO_AUTO_REFRESH
    }

    override fun scheduleAutoRefresh(context: Context, appAlarmManager: AppAlarmManager, refreshInterval: RefreshInterval) {
        unScheduleAutoRefresh(context, appAlarmManager)
        if (widgetManager.installedAllWidgetIds.isEmpty()) {
            return
        }

        val pendingIntent = PendingIntent.getBroadcast(context,
            requestCode,
            createAutoRefreshIntent(context),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        appAlarmManager.scheduleExact(refreshInterval.interval, pendingIntent)
    }

    override fun unScheduleAutoRefresh(context: Context, appAlarmManager: AppAlarmManager) {
        val intent = createAutoRefreshIntent(context)
        val scheduleState = appAlarmManager.isScheduled(context, requestCode, intent)
        if (scheduleState is AppAlarmManager.ScheduledState.Scheduled) {
            appAlarmManager.unschedule(scheduleState.pendingIntent)
        }
    }
}