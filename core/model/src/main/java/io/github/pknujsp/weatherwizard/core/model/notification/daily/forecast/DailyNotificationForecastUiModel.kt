package io.github.pknujsp.weatherwizard.core.model.notification.daily.forecast

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DailyNotificationForecastUiModel(
    val address: String,
    hourlyForecast: HourlyForecastEntity,
    dailyForecast: DailyForecastEntity,
    dayNightCalculator: DayNightCalculator,
    units: CurrentUnits
) : UiModel {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E")
    private val hourRange get() = 0..8
    private val dateRange get() = 0..6


    val hourlyForecast =
        hourlyForecast.items.subList(hourRange.first, hourRange.last).map {
            val calendar = ZonedDateTime.parse(it.dateTime.value).toCalendar()

            HourlyForecast(
                temperature = it.temperature.convertUnit(units.temperatureUnit).toString(),
                weatherIcon = it.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar) ==
                        DayNightCalculator.DayNight.DAY),
                dateTime = ZonedDateTime.parse(it.dateTime.value).hour.toString()
            )
        }


    val dailyForecast =
        dailyForecast.dayItems.subList(dateRange.first, dateRange.last).map {
            DailyForecast(
                temperature = "${it.minTemperature.convertUnit(units.temperatureUnit)} / ${
                    it.maxTemperature.convertUnit(units
                        .temperatureUnit)
                }", weatherIcons = it.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
                date = dateFormatter.format(ZonedDateTime.parse(it.dateTime.value)))
        }


    data class HourlyForecast(
        val temperature: String,
        val weatherIcon: Int,
        val dateTime: String
    )

    data class DailyForecast(
        val temperature: String,
        val weatherIcons: List<Int>,
        val date: String
    )
}