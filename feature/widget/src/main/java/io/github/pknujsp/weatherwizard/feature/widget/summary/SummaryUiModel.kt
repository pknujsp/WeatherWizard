package io.github.pknujsp.weatherwizard.feature.widget.summary

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SummaryUiModel(
    currentWeatherEntity: CurrentWeatherEntity,
    hourlyForecastEntity: HourlyForecastEntity,
    dailyForecastEntity: DailyForecastEntity,
    units: CurrentUnits,
    dayNightCalculator: DayNightCalculator,
    now: ZonedDateTime,
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E", Locale.getDefault())
) : RemoteViewUiModel {

    val currentWeather = currentWeatherEntity.run {
        CurrentWeather(temperature = temperature.convertUnit(units.temperatureUnit).toString(),
            feelsLikeTemperature = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
            weatherIcon = weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(now.toCalendar()) == io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator.DayNight.DAY))
    }

    val hourlyForecast = hourlyForecastEntity.run {
        items.subList(0, 12).map {
            val calendar = ZonedDateTime.parse(it.dateTime.value).toCalendar()

            HourlyForecast(temperature = it.temperature.convertUnit(units.temperatureUnit).toString(),
                weatherIcon = it.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar) == io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator.DayNight.DAY),
                dateTime = ZonedDateTime.parse(it.dateTime.value).hour.toString())
        }
    }

    val dailyForecast = dailyForecastEntity.dayItems.subList(0, 4).map {
        DailyForecast(temperature = "${
            it.minTemperature.convertUnit(units.temperatureUnit)
        } / ${
            it.maxTemperature.convertUnit(units.temperatureUnit)
        }",
            weatherIcons = it.items.map { item -> item.weatherCondition.value.dayWeatherIcon },
            date = dateFormatter.format(ZonedDateTime.parse(it.dateTime.value)))
    }


    data class CurrentWeather(
        val temperature: String, val feelsLikeTemperature: String, val weatherIcon: Int
    )

    data class HourlyForecast(
        val temperature: String, val weatherIcon: Int, val dateTime: String
    )

    data class DailyForecast(
        val temperature: String, val weatherIcons: List<Int>, val date: String
    )
}