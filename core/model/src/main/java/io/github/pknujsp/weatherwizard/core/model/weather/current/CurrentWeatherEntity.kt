package io.github.pknujsp.weatherwizard.core.model.weather.current

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType

data class CurrentWeatherEntity(
    val weatherCondition: WeatherConditionValueType,
    val temperature: TemperatureValueType,
    val feelsLikeTemperature: TemperatureValueType,
    val humidity: HumidityValueType,
    val windSpeed: WindSpeedValueType,
    val windDirection: WindDirectionValueType,
    val precipitationVolume: PrecipitationValueType,
) : WeatherEntityModel()

class CurrentWeatherApiResponseWrapper(
    val byteArray: ByteArray
) : WeatherEntityModel()