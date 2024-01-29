package io.github.pknujsp.everyweather.feature.componentservice.manager

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import io.github.pknujsp.everyweather.core.common.manager.AppAlarmManager
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManager
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerInitializer
import io.github.pknujsp.everyweather.core.common.manager.WidgetManager
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.everyweather.core.widgetnotification.model.ComponentServiceAutoRefreshScheduler
import io.github.pknujsp.everyweather.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager

private class WidgetAlarmManagerImpl(private val context: Context) : WidgetAlarmManager {
    private val widgetManager: WidgetManager = AppComponentManagerFactory.getManager(context, WidgetManager::class)
    private val appAlarmManager: AppAlarmManager = AppComponentManagerFactory.getManager(context, AppAlarmManager::class)

    private companion object {
        val REQUEST_CODE = "AppWidgetAutoRefreshScheduler".hashCode()
    }

    override fun getScheduleState(): AppAlarmManager.ScheduledState {
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

    override fun scheduleAutoRefresh(refreshInterval: RefreshInterval) {
        unScheduleAutoRefresh()
        if (widgetManager.installedAllWidgetIds.isEmpty() || refreshInterval == RefreshInterval.MANUAL) {
            return
        }

        val pendingIntent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(LoadWidgetDataArgument.UPDATE_ALL)),
            REQUEST_CODE,
            actionString = AppComponentServiceReceiver.ACTION_AUTO_REFRESH)
        appAlarmManager.scheduleRepeat(refreshInterval.interval, pendingIntent.pendingIntent!!)

        Log.d("AppWidgetAutoRefreshScheduler", "scheduleWidgetAutoRefresh")
    }

    override fun unScheduleAutoRefresh() {
        val scheduleState = getScheduleState()
        if (scheduleState is AppAlarmManager.ScheduledState.Scheduled) {
            appAlarmManager.unschedule(scheduleState.pendingIntent)
            scheduleState.pendingIntent.cancel()
            Log.d("AppWidgetAutoRefreshScheduler", "unScheduleWidgetAutoRefresh")
        }
    }
}


interface WidgetAlarmManager : AppComponentManager, ComponentServiceAutoRefreshScheduler {
    companion object : AppComponentManagerInitializer {
        private var instance: WidgetAlarmManager? = null

        override fun getInstance(context: Context): WidgetAlarmManager = synchronized(this) {
            instance ?: WidgetAlarmManagerImpl(context).also { instance = it }
        }
    }
}