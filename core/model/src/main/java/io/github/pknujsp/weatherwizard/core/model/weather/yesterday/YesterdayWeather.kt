package io.github.pknujsp.weatherwizard.core.model.weather.yesterday

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType

data class YesterdayWeather(
    val temperature: TemperatureValueType,
) : UiModel