package io.github.pknujsp.weatherwizard.core.network.api.metnorway.response

import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType

data class MetNorwayCurrentWeatherResponse(
    val dateTime: DateTimeValueType,
    val temperature: TemperatureValueType,
    val feelsLikeTemperature: TemperatureValueType,
    val humidity: HumidityValueType,
    val windDirection: WindDirectionValueType,
    val windSpeed: WindSpeedValueType,
    val precipitationVolume: PrecipitationValueType,
    val weatherCondition: WeatherConditionValueType,
    val dewPointTemperature: TemperatureValueType,
    val airPressure: PressureValueType,
)