package io.github.pknujsp.weatherwizard.feature.main.notification

import android.app.PendingIntent
import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.manager.NotificationAlarmManager
import kotlinx.coroutines.flow.firstOrNull

class NotificationStarterImpl(
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val appAlarmManager: AppAlarmManager,
    private val appNotificationManager: AppNotificationManager,
    private val notificationAlarmManager: NotificationAlarmManager,
) : NotificationStarter {

    private suspend fun getOngoingNotification() = ongoingNotificationRepository.getOngoingNotification().let {
        if (it.isInitialized) it else null
    }

    private suspend fun getDailyNotifications() = dailyNotificationRepository.getDailyNotifications().firstOrNull()

    private suspend fun startOngoingNotification(context: Context) {
        getOngoingNotification()?.let {
            if (it.enabled && !appNotificationManager.isActiveNotification(NotificationType.ONGOING)) {
                context.sendBroadcast(ComponentPendingIntentManager.getIntent(context, OngoingNotificationServiceArgument()))

                if (it.data.refreshInterval != RefreshInterval.MANUAL) {
                    val action = ComponentServiceAction.OngoingNotification()
                    if (ComponentPendingIntentManager.getPendingIntent(context,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE,
                            action) == null) {
                        val pendingIntentToSchedule = ComponentPendingIntentManager.getPendingIntent(context,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                            action).pendingIntent!!
                        appAlarmManager.scheduleRepeat(it.data.refreshInterval.interval, pendingIntentToSchedule)
                    }
                }
            }
        }
    }

    private suspend fun startDailyNotifications(context: Context) {
        getDailyNotifications()?.forEach { dailyNotification ->
            if (dailyNotification.enabled && !notificationAlarmManager.isScheduled(context, dailyNotification.id)) {
                notificationAlarmManager.schedule(context, dailyNotification.id, dailyNotification.data.hour, dailyNotification.data.minute)
            }
        }
    }

    override suspend fun start(context: Context) {
        startOngoingNotification(context)
        startDailyNotifications(context)
    }
}


interface NotificationStarter {
    suspend fun start(context: Context)
}