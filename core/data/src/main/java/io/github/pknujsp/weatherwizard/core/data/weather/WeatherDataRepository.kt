package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity

interface WeatherDataRepository {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<CurrentWeatherEntity>

    suspend fun getHourlyForecast(latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long):
            Result<HourlyForecastEntity>

    suspend fun getDailyForecast(latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long): Result<DailyForecastEntity>

    suspend fun getYesterdayWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<YesterdayWeatherEntity>
}