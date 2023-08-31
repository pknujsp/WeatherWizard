package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.ResponseMapper
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.network.datasource.CurrentWeatherResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.DailyForecastResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.HourlyForecastResponseModel

interface WeatherResponseMapper<C : CurrentWeatherResponseModel,
        H : HourlyForecastResponseModel,
        D : DailyForecastResponseModel> : ResponseMapper {
    fun mapCurrentWeather(response: C): CurrentWeatherEntity

    fun mapHourlyForecast(response: H): HourlyForecastEntity

    fun mapDailyForecast(response: D): DailyForecastEntity
}