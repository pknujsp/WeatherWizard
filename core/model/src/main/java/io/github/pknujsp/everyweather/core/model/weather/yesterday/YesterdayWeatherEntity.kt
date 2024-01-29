package io.github.pknujsp.everyweather.core.model.weather.yesterday

import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import kotlinx.serialization.Serializable

@Serializable
data class YesterdayWeatherEntity(
    val temperature: TemperatureValueType,
) : WeatherEntityModel()