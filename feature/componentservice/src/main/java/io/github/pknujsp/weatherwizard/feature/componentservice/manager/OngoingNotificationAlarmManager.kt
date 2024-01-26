package io.github.pknujsp.weatherwizard.feature.componentservice.manager

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppComponentManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.weatherwizard.core.common.manager.AppComponentManagerInitializer
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAutoRefreshScheduler
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager

private class OngoingNotificationAlarmManagerImpl(
    private val context: Context
) : OngoingNotificationAlarmManager {

    private val appAlarmManager: AppAlarmManager = AppComponentManagerFactory.getManager(context, AppAlarmManager::class)

    companion object {
        private val REQUEST_CODE = "OngoingNotificationAutoRefreshScheduler".hashCode()
    }

    override fun getScheduleState(context: Context): AppAlarmManager.ScheduledState {
        val pendingIntent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.OngoingNotification(OngoingNotificationServiceArgument()),
            REQUEST_CODE,
            AppComponentServiceReceiver.ACTION_AUTO_REFRESH)

        return if (pendingIntent.pendingIntent == null) {
            AppAlarmManager.ScheduledState.NotScheduled
        } else {
            AppAlarmManager.ScheduledState.Scheduled(pendingIntent.pendingIntent)
        }
    }

    override fun scheduleAutoRefresh(context: Context, refreshInterval: RefreshInterval) {
        unScheduleAutoRefresh(context)
        if (refreshInterval == RefreshInterval.MANUAL) {
            return
        }

        val pendingIntent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.OngoingNotification(OngoingNotificationServiceArgument()),
            REQUEST_CODE,
            AppComponentServiceReceiver.ACTION_AUTO_REFRESH)
        appAlarmManager.scheduleRepeat(refreshInterval.interval, pendingIntent.pendingIntent!!)
    }

    override fun unScheduleAutoRefresh(context: Context) {
        val scheduleState = getScheduleState(context)
        Log.d("OngoingNotificationAutoRefreshScheduler", "unScheduleAutoRefresh: $scheduleState")
        if (scheduleState is AppAlarmManager.ScheduledState.Scheduled) {
            appAlarmManager.unschedule(scheduleState.pendingIntent)
            scheduleState.pendingIntent.cancel()
        }
    }
}

interface OngoingNotificationAlarmManager : AppComponentManager, ComponentServiceAutoRefreshScheduler {
    companion object : AppComponentManagerInitializer {
        private var instance: OngoingNotificationAlarmManager? = null

        override fun getInstance(context: Context): OngoingNotificationAlarmManager = synchronized(this) {
            instance ?: OngoingNotificationAlarmManagerImpl(context).also { instance = it }
        }
    }
}