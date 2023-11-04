package io.github.pknujsp.weatherwizard.core.model.widget

import android.content.ComponentName
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import kotlin.reflect.KClass

enum class WidgetType(val categories: Array<WeatherDataMajorCategory>) {
    CURRENT_CONDITION(arrayOf(WeatherDataMajorCategory.CURRENT_CONDITION)),
    HOURLY_FORECAST(arrayOf(WeatherDataMajorCategory.HOURLY_FORECAST)), DAILY_FORECAST(arrayOf(WeatherDataMajorCategory.DAILY_FORECAST)),
    AIR_QUALITY(arrayOf(WeatherDataMajorCategory.AIR_QUALITY)), SUMMARY(arrayOf(WeatherDataMajorCategory.CURRENT_CONDITION,
        WeatherDataMajorCategory.HOURLY_FORECAST,
        WeatherDataMajorCategory.DAILY_FORECAST));

    companion object {
        fun fromOrdinal(ordinal: Int): WidgetType {
            return entries[ordinal]
        }

    }
}