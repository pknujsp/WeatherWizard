package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel

interface WeatherResponseMapperManager {

    fun mapCurrentWeather(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): CurrentWeatherEntity

    fun mapHourlyForecast(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): HourlyForecastEntity

    fun mapDailyForecast(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): DailyForecastEntity

    fun mapYesterdayWeather(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): YesterdayWeatherEntity

}