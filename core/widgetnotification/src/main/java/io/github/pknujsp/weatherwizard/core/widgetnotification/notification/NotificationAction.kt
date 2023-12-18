package io.github.pknujsp.weatherwizard.core.widgetnotification.notification

import android.os.Bundle
import androidx.core.os.bundleOf
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.DailyNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument


sealed interface NotificationAction<T : ComponentServiceArgument> {
    val argument: T
    fun toMap(): Map<String, Any>
    fun toBundle(): Bundle

    companion object {
        const val key: String = "key"

        fun toInstance(bundle: Map<String, Any>): NotificationAction<out ComponentServiceArgument> = bundle[key]?.let {
            return when (it) {
                Ongoing::class.simpleName -> Ongoing(OngoingNotificationServiceArgument())
                Daily::class.simpleName -> Daily(DailyNotificationServiceArgument(bundle["notificationId"] as Long))
                else -> throw IllegalArgumentException("Unknown key: $it")
            }
        } ?: throw IllegalArgumentException("Unknown key: $key")

        fun toInstance(bundle: Bundle): NotificationAction<out ComponentServiceArgument> = bundle.getString(key)?.let {
            return when (it) {
                Ongoing::class.simpleName -> Ongoing(OngoingNotificationServiceArgument())
                Daily::class.simpleName -> Daily(DailyNotificationServiceArgument(bundle.getLong("notificationId")))
                else -> throw IllegalArgumentException("Unknown key: $it")
            }
        } ?: throw IllegalArgumentException("Unknown key: $key")

    }

    data class Ongoing(override val argument: OngoingNotificationServiceArgument = OngoingNotificationServiceArgument()) :
        NotificationAction<OngoingNotificationServiceArgument> {
        override fun toMap(): Map<String, Any> = mapOf(key to this::class.simpleName!!)
        override fun toBundle(): Bundle = bundleOf(key to this::class.simpleName!!)
    }

    data class Daily(override val argument: DailyNotificationServiceArgument) : NotificationAction<DailyNotificationServiceArgument> {
        override fun toMap(): Map<String, Any> = mapOf("notificationId" to argument.notificationId, key to this::class.simpleName!!)
        override fun toBundle(): Bundle = bundleOf("notificationId" to argument.notificationId, key to this::class.simpleName!!)
    }
}