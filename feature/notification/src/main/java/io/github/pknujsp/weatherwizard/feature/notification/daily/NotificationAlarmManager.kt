package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.feature.alarm.AppAlarmManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationReceiver
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

class NotificationAlarmManager(context: Context) {
    private val appAlarmManager: AppAlarmManager = AppAlarmManager(context)

    fun schedule(context: Context, notificationId: Long, hour: Int, minute: Int) {
        val now = ZonedDateTime.now().withSecond(0).withNano(0).let {
            if (hour < it.hour || (hour == it.hour && minute < it.minute)) {
                it.plusDays(1)
            } else {
                it
            }
        }
        val timeInMillis = now.toInstant().toEpochMilli()
        val pendingIntent = getPendingIntent(notificationId, context, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (pendingIntent != null) {
            appAlarmManager.schedule(timeInMillis, pendingIntent)
        }
    }

    fun unSchedule(context: Context, notificationId: Long) {
        val pendingIntent = getPendingIntent(notificationId, context, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null) {
            appAlarmManager.unSchedule(pendingIntent)
        }
    }

    private fun getPendingIntent(notificationId: Long, context: Context, flags: Int): PendingIntent? {
        val requestId = NotificationType.DAILY.notificationId + notificationId.toInt()
        return PendingIntent.getBroadcast(context, requestId, Intent(context, DailyNotificationReceiver::class.java)
            .apply {
                putExtras(DailyNotificationReceiver.bundleOf(notificationId))
            }, flags)
    }
}