package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.forecast

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DailyNotificationForecastUiModelMapper : UiModelMapper<WeatherResponseEntity, DailyNotificationForecastRemoteViewUiModel> {
    override fun mapToUiModel(model: WeatherResponseEntity, units: CurrentUnits): DailyNotificationForecastRemoteViewUiModel {
        return model.run {
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d E")
            val hourRange = 0..8
            val dateRange = 0..4

            val hourlyForecast = toEntity<HourlyForecastEntity>().run {

                items.subList(hourRange.first, hourRange.last).map { item ->
                    val calendar = ZonedDateTime.parse(item.dateTime.value)

                    DailyNotificationForecastRemoteViewUiModel.HourlyForecast(temperature = item.temperature.convertUnit(units.temperatureUnit)
                        .toString(),
                        weatherIcon = item.weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(calendar.toCalendar()) == DayNightCalculator.DayNight.DAY),
                        dateTime = calendar.hour.toString())
                }
            }

            val dailyForecast = toEntity<DailyForecastEntity>().run {
                dayItems.subList(dateRange.first, dateRange.last).map { item ->
                    DailyNotificationForecastRemoteViewUiModel.DailyForecast(temperature = "${item.minTemperature.convertUnit(units.temperatureUnit)} / ${
                        item.maxTemperature.convertUnit(units.temperatureUnit)
                    }",
                        weatherIcons = item.items.map { it.weatherCondition.value.dayWeatherIcon },
                        date = dateFormatter.format(ZonedDateTime.parse(item.dateTime.value)))
                }
            }
            DailyNotificationForecastRemoteViewUiModel(hourlyForecast, dailyForecast)
        }
    }

}