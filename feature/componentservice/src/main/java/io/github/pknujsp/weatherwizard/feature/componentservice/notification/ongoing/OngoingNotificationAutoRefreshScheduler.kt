package io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing

import android.app.PendingIntent
import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAutoRefreshScheduler
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager

class OngoingNotificationAutoRefreshScheduler : ComponentServiceAutoRefreshScheduler {

    private companion object {
        const val TAG = "OngoingNotificationAutoRefreshScheduler"
        val requestCode = TAG.hashCode()
    }

    override fun isScheduled(context: Context, appAlarmManager: AppAlarmManager): AppAlarmManager.ScheduledState {
        val intent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.OngoingNotification(OngoingNotificationServiceArgument())).intent
        return appAlarmManager.isScheduled(context, requestCode, intent)
    }

    override fun scheduleAutoRefresh(context: Context, appAlarmManager: AppAlarmManager, refreshInterval: RefreshInterval) {
        unScheduleAutoRefresh(context, appAlarmManager)
        if (refreshInterval == RefreshInterval.MANUAL) {
            return
        }

        val pendingIntent = ComponentPendingIntentManager.getPendingIntent(context,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ComponentServiceAction.OngoingNotification(OngoingNotificationServiceArgument()))
        appAlarmManager.scheduleRepeat(refreshInterval.interval, pendingIntent.pendingIntent!!)
    }

    override fun unScheduleAutoRefresh(context: Context, appAlarmManager: AppAlarmManager) {
        val scheduleState = isScheduled(context, appAlarmManager)
        if (scheduleState is AppAlarmManager.ScheduledState.Scheduled) {
            appAlarmManager.unschedule(scheduleState.pendingIntent)
        }
    }
}


/*Intent(context, AppComponentServiceReceiver::class.java).run {
                this.action = AppComponentServiceReceiver.ACTION_PROCESS
                putExtras(OngoingNotificationServiceArgument().toBundle())
                context.sendBroadcast(this)
            }

            if (ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval != RefreshInterval.MANUAL) {
                val pendingIntentToSchedule = ComponentPendingIntentManager.getPendingIntent(context,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                    action).pendingIntent!!
                appAlarmManager.scheduleRepeat(ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval.interval,
                    pendingIntentToSchedule)
            }*/