package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity

interface WeatherDataRepository {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider
    ): Result<CurrentWeatherEntity>

    suspend fun getHourlyForecast(latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider):
            Result<HourlyForecastEntity>

    suspend fun getDailyForecast(latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider): Result<DailyForecastEntity>
}