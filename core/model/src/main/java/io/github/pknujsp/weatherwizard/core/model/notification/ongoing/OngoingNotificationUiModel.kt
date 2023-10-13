package io.github.pknujsp.weatherwizard.core.model.notification.ongoing

import android.app.PendingIntent
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.util.Calendar
import kotlin.properties.Delegates

class OngoingNotificationUiModel(
    val address: String,
    currentWeather: CurrentWeatherEntity,
    hourlyForecast: HourlyForecastEntity,
    dayNightCalculator: DayNightCalculator,
    currentCalendar: Calendar,
    units: CurrentUnits
) : UiModel {
    var refreshPendingIntent: PendingIntent by Delegates.notNull()

    val currentWeather = currentWeather.run {
        CurrentWeather(
            temperature = temperature.convertUnit(units.temperatureUnit).toString(),
            feelsLikeTemperature = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
            weatherIcon = weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(currentCalendar) == DayNightCalculator.DayNight.DAY)
        )
    }

    val hourlyForecast = hourlyForecast.run {
        items.subList(0, 9).map {
            HourlyForecast(
                temperature = it.temperature.convertUnit(units.temperatureUnit).toString(),
                weatherIcon = it.weatherCondition.value.dayWeatherIcon,
                dateTime = ZonedDateTime.parse(it.dateTime.value).hour.toString()
            )
        }
    }


    data class CurrentWeather(
        val temperature: String,
        val feelsLikeTemperature: String,
        val weatherIcon: Int
    )

    data class HourlyForecast(
        val temperature: String,
        val weatherIcon: Int,
        val dateTime: String
    )
}