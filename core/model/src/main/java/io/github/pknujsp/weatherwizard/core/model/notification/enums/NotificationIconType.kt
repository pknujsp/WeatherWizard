package io.github.pknujsp.weatherwizard.core.model.notification.enums

import io.github.pknujsp.weatherwizard.core.model.settings.BaseEnum
import io.github.pknujsp.weatherwizard.core.model.settings.IEnum

enum class NotificationIconType : IEnum {
    TEMPERATURE {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.temperature_icon
        override val icon: Int? = null
        override val key: Int = ordinal
    },
    ICON {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_condition_icon
        override val icon: Int? = null
        override val key: Int = ordinal
    };

    companion object : BaseEnum<NotificationIconType> {
        override val default: NotificationIconType = TEMPERATURE
        override val key: String get() = "NotificationIconType"
        override val enums: Array<NotificationIconType> = entries.toTypedArray()
    }
}