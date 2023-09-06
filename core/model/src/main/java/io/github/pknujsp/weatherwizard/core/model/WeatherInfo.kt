package io.github.pknujsp.weatherwizard.core.model

import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather

data class WeatherInfo(
    val currentWeather: CurrentWeather,
    val hourlyForecast: HourlyForecast,
    val dailyForecast: DailyForecast,
    val yesterdayWeather: YesterdayWeather,
) : UiModel