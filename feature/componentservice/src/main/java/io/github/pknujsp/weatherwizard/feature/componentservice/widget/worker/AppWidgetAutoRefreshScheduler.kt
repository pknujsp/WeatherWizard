package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAutoRefreshScheduler
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager

class AppWidgetAutoRefreshScheduler(private val widgetManager: WidgetManager) : ComponentServiceAutoRefreshScheduler {

    private companion object {
        val REQUEST_CODE = "AppWidgetAutoRefreshScheduler".hashCode()
    }

    override fun getScheduleState(context: Context): AppAlarmManager.ScheduledState {
        val intent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(LoadWidgetDataArgument.UPDATE_ALL)),
            REQUEST_CODE,
            actionString = AppComponentServiceReceiver.ACTION_AUTO_REFRESH)

        return if (intent.pendingIntent == null) {
            AppAlarmManager.ScheduledState.NotScheduled
        } else {
            AppAlarmManager.ScheduledState.Scheduled(intent.pendingIntent)
        }
    }

    override fun scheduleAutoRefresh(context: Context, appAlarmManager: AppAlarmManager, refreshInterval: RefreshInterval) {
        unScheduleAutoRefresh(context, appAlarmManager)
        if (widgetManager.installedAllWidgetIds.isEmpty() || refreshInterval == RefreshInterval.MANUAL) {
            return
        }

        val pendingIntent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(LoadWidgetDataArgument.UPDATE_ALL)),
            REQUEST_CODE,
            actionString = AppComponentServiceReceiver.ACTION_AUTO_REFRESH)
        appAlarmManager.scheduleRepeat(refreshInterval.interval, pendingIntent.pendingIntent!!)
    }

    override fun unScheduleAutoRefresh(context: Context, appAlarmManager: AppAlarmManager) {
        val scheduleState = getScheduleState(context)
        Log.d("AppWidgetAutoRefreshScheduler", "unScheduleAutoRefresh: $scheduleState")
        if (scheduleState is AppAlarmManager.ScheduledState.Scheduled) {
            appAlarmManager.unschedule(scheduleState.pendingIntent)
            scheduleState.pendingIntent.cancel()
        }
    }
}