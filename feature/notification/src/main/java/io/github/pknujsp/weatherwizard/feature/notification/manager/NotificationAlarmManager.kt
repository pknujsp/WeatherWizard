package io.github.pknujsp.weatherwizard.feature.notification.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.DailyNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationAction
import io.github.pknujsp.weatherwizard.feature.notification.NotificationServiceReceiver
import java.time.ZonedDateTime
import kotlin.random.Random

class NotificationAlarmManager(context: Context) {
    private val appAlarmManager: AppAlarmManager = AppAlarmManager(context)

    fun schedule(context: Context, notificationId: Long, hour: Int, minute: Int) {
        val now = ZonedDateTime.now().withSecond(0).let {
            if (hour < it.hour || (hour == it.hour && minute < it.minute)) {
                it.plusDays(1)
            } else {
                it
            }
        }.withHour(hour).withMinute(minute).withSecond(Random(System.currentTimeMillis()).nextInt(0, 10))

        getPendingIntent(notificationId, context, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)?.run {
            appAlarmManager.schedule(now.toInstant().toEpochMilli(), this)
        }
    }

    fun unSchedule(context: Context, notificationId: Long) {
        getPendingIntent(notificationId, context, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.run {
            appAlarmManager.unSchedule(this)
            cancel()
        }
    }

    fun isScheduled(context: Context, notificationId: Long): Boolean {
        return getPendingIntent(notificationId, context, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null
    }

    private fun getPendingIntent(notificationId: Long, context: Context, flags: Int): PendingIntent? {
        return PendingIntent.getBroadcast(context,
            pendingIntentRequestFactory.requestId(notificationId.toInt()),
            Intent(context, NotificationServiceReceiver::class.java).apply {
                action = NotificationService.ACTION_PROCESS
                putExtras(NotificationAction.Daily(DailyNotificationServiceArgument(notificationId)).toBundle())
            },
            flags)
    }
}