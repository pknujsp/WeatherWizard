package io.github.pknujsp.weatherwizard.core.model.weather.yesterday

import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType


data class YesterdayWeatherEntity(
    val temperature: TemperatureValueType,
) : WeatherEntityModel()

class YesterdayWeatherApiResponseWrapper(
    val byteArray: ByteArray
) : WeatherEntityModel()