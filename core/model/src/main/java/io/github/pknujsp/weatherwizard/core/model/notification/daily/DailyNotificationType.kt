package io.github.pknujsp.weatherwizard.core.model.notification.daily

import io.github.pknujsp.weatherwizard.core.common.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.IEnum

enum class DailyNotificationType : IEnum {
    CURRENT_WEATHER {
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.current_weather
        override val key: Int = ordinal
    },
    HOURLY_FORECAST {
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hourly_forecast
        override val key: Int = ordinal
    },
    DAILY_FORECAST {
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.daily_forecast
        override val key: Int = ordinal
    };

    companion object : BaseEnum<DailyNotificationType> {
        override val default: DailyNotificationType = CURRENT_WEATHER
        override val key: String = "DailyNotificationType"
        override val enums: Array<DailyNotificationType> = values()
    }
}