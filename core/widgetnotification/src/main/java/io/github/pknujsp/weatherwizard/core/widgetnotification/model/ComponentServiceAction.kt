package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import android.os.Bundle
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


sealed interface ComponentServiceAction<T : ComponentServiceArgument> {
    val argument: T

    companion object {
        private const val KEY: String = "KEY"

        private fun <T : ComponentServiceArgument> KClass<T>.toArgument(get: (String) -> Any): T {
            val properties = primaryConstructor!!.parameters.associateWith { get(it.name!!) }
            return primaryConstructor!!.callBy(properties)
        }

        @Suppress("DEPRECATION")
        private fun <T : ComponentServiceArgument> Bundle.toArgument(argumentClass: KClass<T>): T = argumentClass.toArgument { key ->
            get(key)!!
        }


        private fun <T : ComponentServiceArgument> Map<String, Any>.toArgument(argumentClass: KClass<T>): T =
            argumentClass.toArgument { key ->
                get(key)!!
            }


        fun toInstance(map: Map<String, Any>): ComponentServiceAction<out ComponentServiceArgument> = map[KEY]!!.let {
            when (it) {
                WidgetServiceArgument::class.simpleName -> Widget(map.toArgument(WidgetServiceArgument::class))
                DailyNotificationServiceArgument::class.simpleName -> DailyNotification(map.toArgument(DailyNotificationServiceArgument::class))
                OngoingNotificationServiceArgument::class.simpleName -> OngoingNotification(map.toArgument(
                    OngoingNotificationServiceArgument::class))

                else -> throw IllegalArgumentException("Unknown key: $it")
            }
        }

        fun toInstance(bundle: Bundle): ComponentServiceAction<out ComponentServiceArgument> = bundle.getString(KEY)!!.let {
            when (it) {
                WidgetServiceArgument::class.simpleName -> Widget(bundle.toArgument(WidgetServiceArgument::class))
                DailyNotificationServiceArgument::class.simpleName -> DailyNotification(bundle.toArgument(DailyNotificationServiceArgument::class))
                OngoingNotificationServiceArgument::class.simpleName -> OngoingNotification(bundle.toArgument(
                    OngoingNotificationServiceArgument::class))

                else -> throw IllegalArgumentException("Unknown key: $it")
            }
        }
    }

    data class OngoingNotification(override val argument: OngoingNotificationServiceArgument = OngoingNotificationServiceArgument()) :
        ComponentServiceAction<OngoingNotificationServiceArgument>

    data class DailyNotification(override val argument: DailyNotificationServiceArgument) :
        ComponentServiceAction<DailyNotificationServiceArgument>

    data class Widget(override val argument: WidgetServiceArgument) : ComponentServiceAction<WidgetServiceArgument>
}