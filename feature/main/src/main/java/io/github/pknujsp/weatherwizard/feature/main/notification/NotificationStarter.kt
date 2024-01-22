package io.github.pknujsp.weatherwizard.feature.main.notification

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.manager.NotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.OngoingNotificationAutoRefreshScheduler
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
                val scheduler = OngoingNotificationAutoRefreshScheduler()
                scheduler.scheduleAutoRefresh(context, appAlarmManager, it.data.refreshInterval)
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