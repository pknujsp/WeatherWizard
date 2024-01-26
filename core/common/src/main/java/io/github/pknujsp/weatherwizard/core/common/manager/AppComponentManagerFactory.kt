package io.github.pknujsp.weatherwizard.core.common.manager

import android.appwidget.AppWidgetManager
import android.content.Context
import kotlin.reflect.KClass

interface AppComponentManagerFactory {
    companion object : AppComponentManagerFactory {
        val LOCATION_MANAGER = AppLocationManager::class
        val ALARM_MANAGER = AppAlarmManager::class
        val WIDGET_MANAGER = WidgetManager::class
        val NOTIFICATION_MANAGER = AppNotificationManager::class

        override fun <T : AppComponentManager> getManager(context: Context, cls: KClass<T>): T {
            return when (cls) {
                LOCATION_MANAGER -> AppLocationManager.getInstance(context) as T
                ALARM_MANAGER -> AppAlarmManager.getInstance(context) as T
                WIDGET_MANAGER -> WidgetManager.getInstance(context) as T
                NOTIFICATION_MANAGER -> AppNotificationManager.getInstance(context) as T
                else -> throw IllegalArgumentException("Unknown manager type: $cls")
            }
        }
    }

    fun <T : AppComponentManager> getManager(context: Context, cls: KClass<T>): T
}


interface AppComponentManager

interface AppComponentManagerInitializer {
    fun getInstance(context: Context): AppComponentManager
}