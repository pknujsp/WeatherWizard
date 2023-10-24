package io.github.pknujsp.weatherwizard.core.model.notification.ongoing

import android.app.PendingIntent
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates

class OngoingNotificationUiModel(
    val address: String,
    val iconType: NotificationIconType,
    currentWeather: CurrentWeatherEntity,
    hourlyForecast: HourlyForecastEntity,
    dayNightCalculator: DayNightCalculator,
    currentCalendar: Calendar,
    units: CurrentUnits
) : UiModel {
    val time: String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentCalendar.time)
    val currentTemperature: String = currentWeather.temperature.toStringWithOnlyDegree()

    var refreshPendingIntent: PendingIntent by Delegates.notNull()

    val currentWeather = currentWeather.run {
        CurrentWeather(
            temperature = temperature.convertUnit(units.temperatureUnit).toString(),
            feelsLikeTemperature = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
            weatherIcon = weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(currentCalendar) == DayNightCalculator.DayNight.DAY)
        )
    }

    val hourlyForecast = hourlyForecast.run {
        items.subList(0, 8).map {
            val calendar = ZonedDateTime.parse(it.dateTime.value).toCalendar()

            HourlyForecast(
                temperature = it.temperature.convertUnit(units.temperatureUnit).toString(),
                weatherIcon = it.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar) ==
                        DayNightCalculator.DayNight.DAY),
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