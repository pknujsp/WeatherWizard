package io.github.pknujsp.everyweather.feature.componentservice.manager

import android.app.PendingIntent
import android.content.Context
import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManager
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerInitializer
import io.github.pknujsp.everyweather.core.widgetnotification.model.DailyNotificationServiceArgument
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager
import java.time.ZonedDateTime
import kotlin.random.Random

private class DailyNotificationAlarmManagerImpl(
    private val context: Context,
) : DailyNotificationAlarmManager {
    private val appAlarmManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.ALARM_MANAGER)

    override fun schedule(
        notificationId: Long,
        hour: Int,
        minute: Int,
    ) {
        val now =
            ZonedDateTime.now().withSecond(0).let {
                if (hour < it.hour || (hour == it.hour && minute < it.minute)) {
                    it.plusDays(1)
                } else {
                    it
                }
            }.withHour(hour).withMinute(minute).withSecond(Random(System.currentTimeMillis()).nextInt(0, 10))

        getPendingIntent(notificationId, context, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)?.run {
            appAlarmManager.scheduleExact(now.toInstant().toEpochMilli(), this)
        }
    }

    override fun unSchedule(notificationId: Long) {
        getPendingIntent(notificationId, context, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.run {
            appAlarmManager.unschedule(this)
            cancel()
        }
    }

    override fun isScheduled(notificationId: Long): Boolean {
        return getPendingIntent(notificationId, context, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null
    }

    private fun getPendingIntent(
        notificationId: Long,
        context: Context,
        flags: Int,
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            (NotificationType.DAILY.hashCode() + notificationId.toInt()).hashCode(),
            ComponentPendingIntentManager.getIntent(
                context,
                DailyNotificationServiceArgument(notificationId),
                AppComponentServiceReceiver.ACTION_AUTO_REFRESH,
            ),
            flags,
        )
    }
}

interface DailyNotificationAlarmManager : AppComponentManager {
    companion object : AppComponentManagerInitializer {
        private var instance: DailyNotificationAlarmManager? = null

        override fun getInstance(context: Context): DailyNotificationAlarmManager =
            synchronized(this) {
                instance ?: DailyNotificationAlarmManagerImpl(context).also { instance = it }
            }
    }

    fun schedule(
        notificationId: Long,
        hour: Int,
        minute: Int,
    )

    fun unSchedule(notificationId: Long)

    fun isScheduled(notificationId: Long): Boolean
}
