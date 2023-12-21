package io.github.pknujsp.weatherwizard.core.model.weather.yesterday

import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import kotlinx.serialization.Serializable

@Serializable
data class YesterdayWeatherEntity(
    val temperature: TemperatureValueType,
) : WeatherEntityModel()