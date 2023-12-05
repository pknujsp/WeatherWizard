package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity

interface WeatherDataRepository {
    suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<CurrentWeatherEntity>

    suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<HourlyForecastEntity>

    suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<DailyForecastEntity>

    suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<YesterdayWeatherEntity>
}

interface WeatherDataRepositoryInitializer {
    suspend fun initialize()
}