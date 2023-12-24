package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WidgetAllInOneRemoteViewUiModelMapper : UiModelMapper<SavedWidgetContentState.Success, WidgetAllInOneRemoteViewUiModel> {
    override fun mapToUiModel(model: SavedWidgetContentState.Success, units: CurrentUnits): WidgetAllInOneRemoteViewUiModel {
        return model.let {
            val dayNightCalculator = DayNightCalculator(it.latitude, it.longitude)

            val currentWeather = it.toEntity<CurrentWeatherEntity>().run {
                WidgetAllInOneRemoteViewUiModel.CurrentWeather(temperature = temperature.convertUnit(units.temperatureUnit).toString(),
                    feelsLikeTemperature = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
                    weatherIcon = weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(it.updatedAt.toCalendar()) == DayNightCalculator.DayNight.DAY))
            }

            val hourlyForecast = it.toEntity<HourlyForecastEntity>().run {
                items.subList(0, 12).map { item ->
                    val calendar = ZonedDateTime.parse(item.dateTime.value)

                    WidgetAllInOneRemoteViewUiModel.HourlyForecast(temperature = item.temperature.convertUnit(units.temperatureUnit)
                        .toString(),
                        weatherIcon = item.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar.toCalendar()) == DayNightCalculator.DayNight.DAY),
                        dateTime = ZonedDateTime.parse(item.dateTime.value).hour.toString())
                }
            }

            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E", Locale.getDefault())

            val dailyForecast = it.toEntity<DailyForecastEntity>().dayItems.subList(0, 5).map { item ->
                WidgetAllInOneRemoteViewUiModel.DailyForecast(temperature = "${item.minTemperature.convertUnit(units.temperatureUnit)}/${
                    item.maxTemperature.convertUnit(units.temperatureUnit)
                }",
                    weatherIcons = item.items.map { dayItem -> dayItem.weatherCondition.value.dayWeatherIcon },
                    date = dateFormatter.format(ZonedDateTime.parse(item.dateTime.value)))
            }

            WidgetAllInOneRemoteViewUiModel(currentWeather, hourlyForecast, dailyForecast)
        }
    }

}