package io.github.pknujsp.weatherwizard.core.model.weather

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

@Stable
data class RequestWeatherDataArgs(
    var latitude: Double = Double.NaN,
    var longitude: Double = Double.NaN,
    val weatherProvider: WeatherProvider,
    val locationType: LocationType
) : ControllerArgs