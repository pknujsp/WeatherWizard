package io.github.pknujsp.weatherwizard.core.model.weather

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

@Stable
data class RequestWeatherDataArgs(
    var latitude: Double = NaN,
    var longitude: Double = NaN,
    val weatherDataProvider: WeatherDataProvider,
    val locationType: LocationType
) : ControllerArgs{
     companion object{
        const val NaN = Double.NaN
    }
}