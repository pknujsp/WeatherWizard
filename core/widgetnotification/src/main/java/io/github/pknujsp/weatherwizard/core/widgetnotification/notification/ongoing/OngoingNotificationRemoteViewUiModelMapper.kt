package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.model.OngoingNotificationRemoteViewUiModel
import java.time.ZonedDateTime

class OngoingNotificationRemoteViewUiModelMapper : UiModelMapper<WeatherResponseEntity, OngoingNotificationRemoteViewUiModel> {
    override fun mapToUiModel(model: WeatherResponseEntity, units: CurrentUnits): OngoingNotificationRemoteViewUiModel {
        return model.let {
            val currentWeather = it.toEntity<CurrentWeatherEntity>().run {
                OngoingNotificationRemoteViewUiModel.CurrentWeather(temperature = temperature.convertUnit(units.temperatureUnit).toString(),
                    feelsLikeTemperature = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString(),
                    weatherIcon = weatherCondition.value.getWeatherIconByTimeOfDay(it.dayNightCalculator.calculate(it.responseTime.toCalendar()) == DayNightCalculator.DayNight.DAY))
            }

            val hourlyForecast = it.toEntity<HourlyForecastEntity>().run {
                items.subList(0, 8).map { item ->
                    val time = ZonedDateTime.parse(item.dateTime.value)

                    OngoingNotificationRemoteViewUiModel.HourlyForecast(temperature = item.temperature.convertUnit(units.temperatureUnit)
                        .toString(),
                        weatherIcon = item.weatherCondition.value.getWeatherIconByTimeOfDay(it.dayNightCalculator.calculate(time.toCalendar()) == DayNightCalculator.DayNight.DAY),
                        dateTime = time.hour.toString())
                }
            }
            OngoingNotificationRemoteViewUiModel(currentWeather = currentWeather,
                hourlyForecast = hourlyForecast)
        }
    }

}