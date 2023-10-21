package io.github.pknujsp.weatherwizard.core.model.notification.enums

import io.github.pknujsp.weatherwizard.core.common.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.IEnum

enum class DailyNotificationType : IEnum {

    FORECAST {
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hourly_and_daily_forecast
        override val key: Int = ordinal
    };

    companion object : BaseEnum<DailyNotificationType> {
        override val default: DailyNotificationType = FORECAST
        override val key: String = "DailyNotificationType"
        override val enums: Array<DailyNotificationType> = entries.toTypedArray()
    }
}