package io.github.pknujsp.weatherwizard.feature.main.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.feature.notification.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationAction
import io.github.pknujsp.weatherwizard.feature.notification.NotificationServiceReceiver
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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
                val intent = Intent(context, NotificationServiceReceiver::class.java).apply {
                    action = NotificationService.ACTION_PROCESS
                    putExtras(NotificationAction.Ongoing().toBundle())
                }
                context.sendBroadcast(intent)

                if (it.data.refreshInterval != RefreshInterval.MANUAL) {
                    val pendingIntent = appNotificationManager.getRefreshPendingIntent(context,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                        NotificationAction.Ongoing())

                    appAlarmManager.unScheduleRepeat(pendingIntent)
                    appAlarmManager.scheduleRepeat(it.data.refreshInterval.interval, pendingIntent)
                }
            }
        }
    }

    private suspend fun startDailyNotifications(context: Context) {
        getDailyNotifications()?.let {
            it.forEach { dailyNotification ->
                if (dailyNotification.enabled) {
                    if (!notificationAlarmManager.isScheduled(context, dailyNotification.id)) {
                        notificationAlarmManager.schedule(context,
                            dailyNotification.id,
                            dailyNotification.data.hour,
                            dailyNotification.data.minute)
                    }
                }
            }
        }
    }

    override suspend fun start(context: Context) {
        supervisorScope {
            val ongoing = async { startOngoingNotification(context) }
            val daily = async { startDailyNotifications(context) }

            ongoing.await()
            daily.await()
        }
    }
}


interface NotificationStarter {
    suspend fun start(context: Context)
}