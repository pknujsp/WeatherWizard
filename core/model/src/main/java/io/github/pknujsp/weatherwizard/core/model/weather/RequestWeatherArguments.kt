package io.github.pknujsp.weatherwizard.core.model.weather

import androidx.compose.runtime.Stable
import dalvik.system.BaseDexClassLoader
import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

@Stable
data class RequestWeatherArguments(
    val weatherProvider: WeatherProvider,
    val latitude: Double,
    val longitude: Double,
) : ControllerArgs

data class BaseLocationModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationType: LocationType,
    val locationId: Long? = null,
)