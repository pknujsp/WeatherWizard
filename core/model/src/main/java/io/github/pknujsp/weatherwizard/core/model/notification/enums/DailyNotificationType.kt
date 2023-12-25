package io.github.pknujsp.weatherwizard.core.model.notification.enums

import io.github.pknujsp.weatherwizard.core.model.settings.BaseEnum
import io.github.pknujsp.weatherwizard.core.model.settings.IEnum
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType

enum class DailyNotificationType(val categories: Array<MajorWeatherEntityType>) : IEnum {
    CURRENT(categories = arrayOf(MajorWeatherEntityType.CURRENT_CONDITION)) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.current_weather
        override val key: Int = ordinal
    },
    AIR_QUALITY(categories = arrayOf(MajorWeatherEntityType.AIR_QUALITY)) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.air_quality
        override val key: Int = ordinal
    },
    FORECAST(categories = arrayOf(MajorWeatherEntityType.HOURLY_FORECAST, MajorWeatherEntityType.DAILY_FORECAST)) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.hourly_and_daily_forecast
        override val key: Int = ordinal
    };

    companion object : BaseEnum<DailyNotificationType> {
        override val default: DailyNotificationType = FORECAST
        override val key: String = "DailyNotificationType"
        override val enums: Array<DailyNotificationType> = entries.toTypedArray()
    }
}