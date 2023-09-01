package io.github.pknujsp.weatherwizard.core.model.weather.yesterday

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.TemperatureEntity


data class YesterdayWeatherEntity(
    val temperature: TemperatureEntity,
) : EntityModel