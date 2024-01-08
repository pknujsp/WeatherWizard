package io.github.pknujsp.weatherwizard.core.model.weather

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

@Stable
data class RequestWeatherArguments(
    val weatherProvider: WeatherProvider,
    val latitude: Double,
    val longitude: Double,
) : ControllerArgs