package io.github.pknujsp.everyweather.core.widgetnotification.widget.summary

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.widgetnotification.widget.WidgetUiModelMapper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WidgetAllInOneRemoteViewUiModelMapper : WidgetUiModelMapper<SavedWidgetContentState.Success, WidgetAllInOneRemoteViewUiModel>(
    hourlyForecastItemsCount = 12,
    dailyForecastItemsCount = 5,
) {
    override fun mapToUiModel(
        model: SavedWidgetContentState.Success,
        units: CurrentUnits,
    ): WidgetAllInOneRemoteViewUiModel {
        return model.let {
            val entity = it.entities.first()
            val dayNightCalculator = DayNightCalculator(it.latitude, it.longitude)

            val currentWeather =
                entity.toEntity<CurrentWeatherEntity>().run {
                    WidgetAllInOneRemoteViewUiModel.CurrentWeather(
                        temperature = temperature.convertUnit(units.temperatureUnit).toString(),
                        feelsLikeTemperature = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
                        weatherIcon =
                            weatherCondition.value.getWeatherIconByTimeOfDay(
                                dayNightCalculator.calculate(it.updatedAt.toCalendar()) == DayNightCalculator.DayNight.DAY,
                            ),
                    )
                }

            val hourlyForecast =
                entity.toEntity<HourlyForecastEntity>().run {
                    items.subList(0, hourlyForecastItemsCount).map { item ->
                        val calendar = ZonedDateTime.parse(item.dateTime.value)

                        WidgetAllInOneRemoteViewUiModel.HourlyForecast(
                            temperature =
                                item.temperature.convertUnit(units.temperatureUnit)
                                    .toString(),
                            weatherIcon =
                                item.weatherCondition.value.getWeatherIconByTimeOfDay(
                                    dayNightCalculator.calculate(calendar.toCalendar()) == DayNightCalculator.DayNight.DAY,
                                ),
                            dateTime = ZonedDateTime.parse(item.dateTime.value).hour.toString(),
                        )
                    }
                }

            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E", Locale.getDefault())

            val dailyForecast =
                entity.toEntity<DailyForecastEntity>().dayItems.subList(0, dailyForecastItemsCount).map { item ->
                    WidgetAllInOneRemoteViewUiModel.DailyForecast(
                        temperature = "${item.minTemperature.convertUnit(units.temperatureUnit)}/${
                            item.maxTemperature.convertUnit(units.temperatureUnit)
                        }",
                        weatherIcons = item.items.map { dayItem -> dayItem.weatherCondition.value.dayWeatherIcon },
                        date = dateFormatter.format(ZonedDateTime.parse(item.dateTime.value)),
                    )
                }

            WidgetAllInOneRemoteViewUiModel(currentWeather, hourlyForecast, dailyForecast)
        }
    }
}
