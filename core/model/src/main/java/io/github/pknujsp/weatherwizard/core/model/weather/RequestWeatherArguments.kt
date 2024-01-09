package io.github.pknujsp.weatherwizard.core.model.weather

import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

data class RequestWeatherArguments(
    val weatherProvider: WeatherProvider,
    val latitude: Double,
    val longitude: Double,
) : ControllerArgs