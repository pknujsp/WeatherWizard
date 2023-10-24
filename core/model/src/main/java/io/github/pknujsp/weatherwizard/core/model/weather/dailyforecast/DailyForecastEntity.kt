package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType

data class DailyForecastEntity(
    val dayItems: List<DayItem>,
) : EntityModel {

    data class DayItem(
        val dateTime: DateTimeValueType,
        val minTemperature: TemperatureValueType,
        val maxTemperature: TemperatureValueType,
        val windMinSpeed: WindSpeedValueType = WindSpeedValueType.none,
        val windMaxSpeed: WindSpeedValueType = WindSpeedValueType.none,
        val items: List<Item>,
    ) {

        data class Item(
            val weatherCondition: WeatherConditionValueType,
            val rainfallVolume: RainfallValueType = RainfallValueType.none,
            val snowfallVolume: SnowfallValueType = SnowfallValueType.none,
            val rainfallProbability: ProbabilityValueType = ProbabilityValueType.none,
            val snowfallProbability: ProbabilityValueType = ProbabilityValueType.none,
            val precipitationVolume: PrecipitationValueType = PrecipitationValueType.none,
            val precipitationProbability: ProbabilityValueType = ProbabilityValueType.none,
        )
    }
}