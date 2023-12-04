package io.github.pknujsp.weatherwizard.core.model.widget

import io.github.pknujsp.weatherwizard.core.common.enum.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.enum.IEnum
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType

enum class WidgetType(val categories: Array<MajorWeatherEntityType>) : IEnum {
    CURRENT_CONDITION(arrayOf(MajorWeatherEntityType.CURRENT_CONDITION)) {

        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.current_weather
        override val key: Int = ordinal
    },
    HOURLY_FORECAST(arrayOf(MajorWeatherEntityType.HOURLY_FORECAST)) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hourly_forecast
    },
    DAILY_FORECAST(arrayOf(MajorWeatherEntityType.DAILY_FORECAST)) {

        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.daily_forecast
    },
    AIR_QUALITY(arrayOf(MajorWeatherEntityType.AIR_QUALITY)) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.air_quality

    },
    ALL_IN_ONE(arrayOf(MajorWeatherEntityType.CURRENT_CONDITION,
        MajorWeatherEntityType.HOURLY_FORECAST,
        MajorWeatherEntityType.DAILY_FORECAST)) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.all_in_one
    };

    companion object : BaseEnum<WidgetType> {
        override val default: WidgetType = ALL_IN_ONE
        override val key: String = "WidgetType"
        override val enums: Array<WidgetType> = entries.toTypedArray()
    }
}