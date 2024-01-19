package io.github.pknujsp.weatherwizard.core.model.widget

import io.github.pknujsp.weatherwizard.core.model.settings.BaseEnum
import io.github.pknujsp.weatherwizard.core.model.settings.IEnum
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType

enum class WidgetType(val categories: Array<MajorWeatherEntityType>) : IEnum {
    TIME_HOURLY_FORECAST(arrayOf(MajorWeatherEntityType.CURRENT_CONDITION, MajorWeatherEntityType.HOURLY_FORECAST)) {
        override val icon: Int? = null
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.time_hourly_forecast_widget
        override val key: Int = ordinal
    },
    HOURLY_FORECAST_COMPARISON(arrayOf(MajorWeatherEntityType.CURRENT_CONDITION, MajorWeatherEntityType.HOURLY_FORECAST)) {
        override val icon: Int? = null
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.hourly_forecast_comparison_widget
    },
    DAILY_FORECAST_COMPARISON(arrayOf(MajorWeatherEntityType.DAILY_FORECAST)) {
        override val icon: Int? = null
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.daily_forecast_comparison_widget
    },
    ALL_IN_ONE(arrayOf(MajorWeatherEntityType.CURRENT_CONDITION,
        MajorWeatherEntityType.HOURLY_FORECAST,
        MajorWeatherEntityType.DAILY_FORECAST)) {
        override val icon: Int? = null
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.all_in_one_widget
    };


    companion object : BaseEnum<WidgetType> {
        override val default: WidgetType = ALL_IN_ONE
        override val key: String = "WidgetType"
        override val enums: Array<WidgetType> = entries.toTypedArray()
    }
}