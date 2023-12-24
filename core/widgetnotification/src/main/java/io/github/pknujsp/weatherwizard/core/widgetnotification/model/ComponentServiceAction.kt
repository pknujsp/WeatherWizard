package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import android.os.Bundle
import android.util.Log
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


sealed interface ComponentServiceAction<T : ComponentServiceArgument> {
    val argument: T

    companion object {
        private const val KEY: String = "KEY"

        private fun <T : ComponentServiceArgument> KClass<T>.toArgument(get: (String) -> Any, contains: (String) -> Boolean): T {
            val properties = primaryConstructor!!.parameters.filter { contains(it.name!!) }.associateWith { get(it.name!!) }
            return primaryConstructor!!.callBy(properties)
        }

        @Suppress("DEPRECATION")
        private fun <T : ComponentServiceArgument> Bundle.toArgument(argumentClass: KClass<T>): T = argumentClass.toArgument(get = { key ->
            get(key)!!
        }, contains = { key ->
            containsKey(key)
        })

        private fun <T : ComponentServiceArgument> Map<String, Any>.toArgument(argumentClass: KClass<T>): T =
            argumentClass.toArgument(get = { key ->
                get(key)!!
            }, contains = { key ->
                containsKey(key)
            })

        fun toInstance(map: Map<String, Any>): ComponentServiceAction<out ComponentServiceArgument> = map[KEY]!!.let {
            Log.d("ComponentServiceAction", "toInstance: $map")
            when (it) {
                LoadWidgetDataArgument::class.simpleName -> LoadWidgetData(map.toArgument(LoadWidgetDataArgument::class))
                DailyNotificationServiceArgument::class.simpleName -> DailyNotification(map.toArgument(DailyNotificationServiceArgument::class))
                OngoingNotificationServiceArgument::class.simpleName -> OngoingNotification(map.toArgument(
                    OngoingNotificationServiceArgument::class))

                else -> throw IllegalArgumentException("Unknown key: $it")
            }
        }

        fun toInstance(bundle: Bundle): ComponentServiceAction<out ComponentServiceArgument> = bundle.getString(KEY)!!.let {
            when (it) {
                LoadWidgetDataArgument::class.simpleName -> LoadWidgetData(bundle.toArgument(LoadWidgetDataArgument::class))
                DailyNotificationServiceArgument::class.simpleName -> DailyNotification(bundle.toArgument(DailyNotificationServiceArgument::class))
                OngoingNotificationServiceArgument::class.simpleName -> OngoingNotification(bundle.toArgument(
                    OngoingNotificationServiceArgument::class))

                else -> throw IllegalArgumentException("Unknown key: $it")
            }
        }
    }

     class OngoingNotification(override val argument: OngoingNotificationServiceArgument = OngoingNotificationServiceArgument()) :
        ComponentServiceAction<OngoingNotificationServiceArgument>

     class DailyNotification(override val argument: DailyNotificationServiceArgument) :
        ComponentServiceAction<DailyNotificationServiceArgument>

     class LoadWidgetData(override val argument: LoadWidgetDataArgument) : ComponentServiceAction<LoadWidgetDataArgument>

}