package io.github.pknujsp.weatherwizard.core.model.weather

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.common.util.toTimeZone
import io.github.pknujsp.weatherwizard.core.model.ControllerArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import java.time.ZonedDateTime

data class RequestWeatherDataArgs(
    val latitude: Double,
    val longitude: Double,
    val weatherDataProvider: WeatherDataProvider,
    val requestId: Long,
    val requestDateTime:ZonedDateTime = ZonedDateTime.now()
) : ControllerArgs {
    val dayNightCalculator = DayNightCalculator(latitude, longitude, requestDateTime.toTimeZone())
    val currentCalendar = requestDateTime.toCalendar()
}