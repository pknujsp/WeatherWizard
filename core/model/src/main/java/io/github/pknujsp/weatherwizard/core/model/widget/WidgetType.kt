package io.github.pknujsp.weatherwizard.core.model.widget

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory

enum class WidgetType(val categories: Array<WeatherDataMajorCategory>) {
    SUMMARY(arrayOf(WeatherDataMajorCategory.CURRENT_CONDITION, WeatherDataMajorCategory.HOURLY_FORECAST, WeatherDataMajorCategory
        .DAILY_FORECAST));

    companion object {
        fun fromOrdinal(ordinal: Int): WidgetType {
            return entries[ordinal]
        }
    }
}