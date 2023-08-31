package io.github.pknujsp.weatherwizard.core.model.weather.current

import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueClass

data class CurrentWeather(
    val weatherCondition: WeatherConditionValueClass,
    val temperature: TemperatureValueClass,
    val feelsLikeTemperature: TemperatureValueClass,
    val humidity: HumidityValueClass,
    val windSpeed: WindSpeedValueClass,
    val windDirection: WindDirectionValueClass,
    val airQuality: AirQualityValueClass,
)