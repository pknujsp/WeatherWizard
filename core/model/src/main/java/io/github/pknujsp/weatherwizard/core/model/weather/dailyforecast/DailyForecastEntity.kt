package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType

data class DailyForecastEntity(
    val items: List<Item>,
) : EntityModel {

    data class Item(
        val dateTime: DateTimeValueType,
        val weatherCondition: WeatherConditionValueType,
        val minTemperature: TemperatureValueType,
        val maxTemperature: TemperatureValueType,
        val rainfallVolume: RainfallValueType,
        val snowfallVolume: SnowfallValueType,
        val rainfallProbability: ProbabilityValueType,
        val snowfallProbability: ProbabilityValueType,
        val precipitationVolume: PrecipitationValueType,
        val precipitationProbability: ProbabilityValueType,
    )
}