package io.github.pknujsp.weatherwizard.core.model.weather

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.common.util.toTimeZone
import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import java.time.ZonedDateTime

@Stable
data class RequestWeatherDataArgs(
    var latitude: Double = NaN,
    var longitude: Double = NaN,
    val weatherDataProvider: WeatherDataProvider,
    val targetAreaType: TargetAreaType
) : ControllerArgs{
     companion object{
        const val NaN = Double.NaN
    }
}