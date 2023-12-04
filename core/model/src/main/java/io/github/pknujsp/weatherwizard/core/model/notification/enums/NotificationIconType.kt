package io.github.pknujsp.weatherwizard.core.model.notification.enums

import io.github.pknujsp.weatherwizard.core.common.enum.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.enum.IEnum

enum class NotificationIconType : IEnum {
    TEMPERATURE {
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.temperature_icon
        override val key: Int = ordinal
    },
    ICON {
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.weather_condition_icon
        override val key: Int = ordinal
    };

    companion object : BaseEnum<NotificationIconType> {
        override val default: NotificationIconType = TEMPERATURE
        override val key: String get() = "NotificationIconType"
        override val enums: Array<NotificationIconType> = entries.toTypedArray()
    }
}