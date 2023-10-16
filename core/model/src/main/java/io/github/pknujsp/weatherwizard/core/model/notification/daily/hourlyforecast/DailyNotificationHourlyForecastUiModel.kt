package io.github.pknujsp.weatherwizard.core.model.notification.daily.hourlyforecast

import android.app.PendingIntent
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import kotlin.properties.Delegates

class DailyNotificationHourlyForecastUiModel(
    val address: String,
    hourlyForecast: HourlyForecastEntity,
    dayNightCalculator: DayNightCalculator,
    units: CurrentUnits
) : UiModel {
    var refreshPendingIntent: PendingIntent by Delegates.notNull()

    companion object {
        private val itemRange = 0..<8

        fun createSample(units: CurrentUnits): List<HourlyForecast> {
            var temperture = 10.0
            var dateTime = 9
            val weatherIcon = WeatherConditionCategory.Clear.dayWeatherIcon

            return itemRange.map {
                HourlyForecast(
                    temperature = TemperatureValueType(temperture++, TemperatureUnit.default).convertUnit(units.temperatureUnit).toString(),
                    weatherIcon = weatherIcon,
                    dateTime = dateTime++.toString()
                )
            }
        }
    }


    val hourlyForecast = hourlyForecast.run {
        items.subList(itemRange.first, itemRange.last).map {
            val calendar = ZonedDateTime.parse(it.dateTime.value).toCalendar()

            HourlyForecast(
                temperature = it.temperature.convertUnit(units.temperatureUnit).toString(),
                weatherIcon = it.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar) ==
                        DayNightCalculator.DayNight.DAY),
                dateTime = ZonedDateTime.parse(it.dateTime.value).hour.toString()
            )
        }
    }


    data class HourlyForecast(
        val temperature: String,
        val weatherIcon: Int,
        val dateTime: String
    )
}