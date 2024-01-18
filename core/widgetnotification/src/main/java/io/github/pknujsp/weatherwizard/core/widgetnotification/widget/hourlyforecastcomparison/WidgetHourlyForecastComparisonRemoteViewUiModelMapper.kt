package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.hourlyforecastcomparison

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WidgetHourlyForecastComparisonRemoteViewUiModelMapper :
    UiModelMapper<SavedWidgetContentState.Success, WidgetHourlyForecastComparisonRemoteViewUiModel> {
    override fun mapToUiModel(
        model: SavedWidgetContentState.Success, units: CurrentUnits
    ): WidgetHourlyForecastComparisonRemoteViewUiModel {
        return model.let {
            val dayNightCalculator = DayNightCalculator(it.latitude, it.longitude)
            val items = it.entities.map { entity ->
                val currentWeather = entity.toEntity<CurrentWeatherEntity>().run {
                    val calendar = ZonedDateTime.now()
                    WidgetHourlyForecastComparisonRemoteViewUiModel.CurrentWeather(
                        temperature = temperature.convertUnit(units.temperatureUnit).toString(),
                        weatherIcon = weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar.toCalendar()) == DayNightCalculator.DayNight.DAY),
                    )
                }

                val hourlyForecast = entity.toEntity<HourlyForecastEntity>().run {
                    items.subList(0, 3).map { item ->
                        val time = ZonedDateTime.parse(item.dateTime.value)

                        val dateTimeString = "${
                            if (time.hour == 0) {
                                DateTimeFormatter.ofPattern("E ", Locale.getDefault()).format(time)
                            } else {
                                ""
                            }
                        }${time.hour}"
                        WidgetHourlyForecastComparisonRemoteViewUiModel.HourlyForecast(temperature = item.temperature.convertUnit(units.temperatureUnit)
                            .toString(),
                            weatherIcon = item.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(time.toCalendar()) == DayNightCalculator.DayNight.DAY),
                            dateTime = dateTimeString)
                    }
                }
                WidgetHourlyForecastComparisonRemoteViewUiModel.Item(
                    weatherProvider = entity.weatherProvider,
                    currentWeather = currentWeather,
                    hourlyForecast = hourlyForecast,
                )
            }
            WidgetHourlyForecastComparisonRemoteViewUiModel(items)
        }
    }

}