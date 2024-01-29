package io.github.pknujsp.everyweather.feature.componentservice.manager

import android.content.Context
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManager
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import kotlin.reflect.KClass

object AppComponentServiceManagerFactory : AppComponentManagerFactory {

    val DAILY_NOTIFICATION_ALARM_MANAGER = DailyNotificationAlarmManager::class
    val ONGOING_NOTIFICATION_ALARM_MANAGER = OngoingNotificationAlarmManager::class
    val WIDGET_ALARM_MANAGER = WidgetAlarmManager::class

    override fun <T : AppComponentManager> getManager(context: Context, cls: KClass<T>): T {
        return when (cls) {
            DAILY_NOTIFICATION_ALARM_MANAGER -> DailyNotificationAlarmManager.getInstance(context) as T
            ONGOING_NOTIFICATION_ALARM_MANAGER -> OngoingNotificationAlarmManager.getInstance(context) as T
            WIDGET_ALARM_MANAGER -> WidgetAlarmManager.getInstance(context) as T
            else -> throw IllegalArgumentException("Unknown manager type: $cls")
        }
    }

}