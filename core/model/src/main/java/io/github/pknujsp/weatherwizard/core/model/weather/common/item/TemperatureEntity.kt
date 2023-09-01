package io.github.pknujsp.weatherwizard.core.model.weather.common.item

import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureType

data class TemperatureEntity(
    val current: TemperatureType,
    val min: TemperatureType = TemperatureType.emptyValue(),
    val max: TemperatureType = TemperatureType.emptyValue(),
    val minFeelsLike: TemperatureType = TemperatureType.emptyValue(),
    val maxFeelsLike: TemperatureType = TemperatureType.emptyValue(),
)