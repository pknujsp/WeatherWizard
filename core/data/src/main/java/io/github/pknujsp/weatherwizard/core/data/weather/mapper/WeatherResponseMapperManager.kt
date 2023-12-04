package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel

interface WeatherResponseMapperManager {

    fun mapCurrentWeather(response: ApiResponseModel, weatherProvider: WeatherProvider): CurrentWeatherEntity

    fun mapHourlyForecast(response: ApiResponseModel, weatherProvider: WeatherProvider): HourlyForecastEntity

    fun mapDailyForecast(response: ApiResponseModel, weatherProvider: WeatherProvider): DailyForecastEntity

    fun mapYesterdayWeather(response: ApiResponseModel, weatherProvider: WeatherProvider): YesterdayWeatherEntity

}