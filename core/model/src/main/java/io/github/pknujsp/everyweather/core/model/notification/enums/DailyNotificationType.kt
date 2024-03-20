package io.github.pknujsp.everyweather.core.model.notification.enums

import io.github.pknujsp.everyweather.core.model.settings.BaseEnum
import io.github.pknujsp.everyweather.core.model.settings.IEnum
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType

enum class DailyNotificationType(val categories: Array<MajorWeatherEntityType>) : IEnum {
    FORECAST(categories = arrayOf(MajorWeatherEntityType.HOURLY_FORECAST, MajorWeatherEntityType.DAILY_FORECAST)) {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.hourly_and_daily_forecast
        override val icon: Int? = null
        override val key: Int = ordinal
    }, ;

    companion object : BaseEnum<DailyNotificationType> {
        override val default: DailyNotificationType = FORECAST
        override val key: String = "DailyNotificationType"
        override val enums: Array<DailyNotificationType> = entries.toTypedArray()
    }
}
