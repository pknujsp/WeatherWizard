package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType

data class HourlyForecast(
    val items: List<Item>
) : UiModel {
    data class Item(
        val dateTime: DateTimeType,
        val weatherCondition: WeatherConditionValueType,
        val temperature: TemperatureValueType,
        val windSpeed: WindSpeedValueType,
        val windDirection: WindDirectionValueType,
    )
}