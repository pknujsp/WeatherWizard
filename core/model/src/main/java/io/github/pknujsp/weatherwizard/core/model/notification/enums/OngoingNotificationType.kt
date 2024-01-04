package io.github.pknujsp.weatherwizard.core.model.notification.enums

import io.github.pknujsp.weatherwizard.core.model.settings.BaseEnum
import io.github.pknujsp.weatherwizard.core.model.settings.IEnum
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType

enum class OngoingNotificationType(val categories: Array<MajorWeatherEntityType>) : IEnum {
    CURRENT_HOURLY_FORECAST(categories = arrayOf(MajorWeatherEntityType.CURRENT_CONDITION, MajorWeatherEntityType.HOURLY_FORECAST)) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.current_weather_and_hourly_forecast
        override val icon: Int? = null
        override val key: Int = ordinal
    };

    companion object : BaseEnum<OngoingNotificationType> {
        override val default: OngoingNotificationType = CURRENT_HOURLY_FORECAST
        override val key: String = "OngoingNotificationType"
        override val enums: Array<OngoingNotificationType> = entries.toTypedArray()
    }
}