package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import javax.inject.Inject

class WeatherDataRepositoryImpl @Inject constructor(
) : WeatherDataRepository {
    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider
    ): Result<CurrentWeatherEntity> {
        TODO()
    }

    override suspend fun getHourlyForecast(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider
    ): Result<HourlyForecastEntity> {
        TODO()
    }

    override suspend fun getDailyForecast(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider
    ): Result<DailyForecastEntity> {
        TODO()
    }
}