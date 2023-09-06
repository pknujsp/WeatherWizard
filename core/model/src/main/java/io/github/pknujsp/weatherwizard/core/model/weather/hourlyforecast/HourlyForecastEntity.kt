package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType

data class HourlyForecastEntity(
    val items: List<Item>
) : EntityModel {
    data class Item(
        val dateTime: DateTimeValueType,
        val weatherCondition: WeatherConditionValueType,
        val temperature: TemperatureValueType,
        val feelsLikeTemperature: TemperatureValueType,
        val humidity: HumidityValueType,
        val windSpeed: WindSpeedValueType,
        val windDirection: WindDirectionValueType,
        val rainfallVolume: RainfallValueType,
        val snowfallVolume: SnowfallValueType,
        val rainfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        val snowfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        val precipitationVolume: PrecipitationValueType,
        val precipitationProbability: ProbabilityValueType,
    )
}