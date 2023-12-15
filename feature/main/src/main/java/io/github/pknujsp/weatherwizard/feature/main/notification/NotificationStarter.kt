package io.github.pknujsp.weatherwizard.feature.main.notification

import android.app.PendingIntent
import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationType
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker.OngoingNotificationReceiver
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class NotificationStarterImpl(
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    private val dailyNotificationRepository: DailyNotificationRepository
) : NotificationStarter {

    private suspend fun getOngoingNotification() = ongoingNotificationRepository.getOngoingNotification().let {
        if (it.isInitialized) it else null
    }

    private suspend fun getDailyNotifications() = dailyNotificationRepository.getDailyNotifications().firstOrNull()

    private var appAlarmManager: AppAlarmManager? = null
    private var appNotificationManager: AppNotificationManager? = null
    private var notificationAlarmManager: NotificationAlarmManager? = null

    private suspend fun startOngoingNotification(context: Context) {
        getOngoingNotification()?.let {
            if (it.enabled) {
                if (appNotificationManager == null) {
                    appNotificationManager = AppNotificationManager(context)
                }

                if (!appNotificationManager!!.isActiveNotification(NotificationType.ONGOING)) {
                    val pendingIntent = appNotificationManager!!.getRefreshPendingIntent(context,
                        NotificationType.ONGOING,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                        OngoingNotificationReceiver::class)

                    pendingIntent.send()
                    if (it.data.refreshInterval != RefreshInterval.MANUAL) {
                        if (appAlarmManager == null) {
                            appAlarmManager = AppAlarmManager(context)
                        }
                        appAlarmManager!!.scheduleRepeat(it.data.refreshInterval.interval, pendingIntent)
                    }
                }
            }
        }
    }

    private suspend fun startDailyNotifications(context: Context) {
        getDailyNotifications()?.let {
            if (notificationAlarmManager == null) {
                notificationAlarmManager = NotificationAlarmManager(context)
            }
            it.forEach { dailyNotification ->
                if (dailyNotification.enabled) {
                    if (!notificationAlarmManager!!.isScheduled(context, dailyNotification.id)) {
                        notificationAlarmManager!!.schedule(context,
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
            val ongoing = launch { startOngoingNotification(context) }
            val daily = launch { startDailyNotifications(context) }

            joinAll(ongoing, daily)

            appAlarmManager = null
            appNotificationManager = null
            notificationAlarmManager = null
        }
    }
}


interface NotificationStarter {
    suspend fun start(context: Context)
}