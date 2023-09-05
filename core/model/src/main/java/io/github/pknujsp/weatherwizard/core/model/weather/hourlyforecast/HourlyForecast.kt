package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueClass

data class HourlyForecast(
    val items: List<Item>
) : UiModel {
    data class Item(
        val dateTime: DateTimeType,
        val weatherCondition: WeatherConditionValueClass,
        val temperature: TemperatureValueClass,
        val windSpeed: WindSpeedValueClass,
        val windDirection: WindDirectionValueClass,
    )
}