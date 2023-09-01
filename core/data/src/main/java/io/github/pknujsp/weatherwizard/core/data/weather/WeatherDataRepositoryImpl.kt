package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import javax.inject.Inject

class WeatherDataRepositoryImpl @Inject constructor(
    private val weatherResponseMapperManager: WeatherResponseMapperManager,
    private val weatherApiRequestManager: WeatherApiRequestManager,
) : WeatherDataRepository {
    override suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<CurrentWeatherEntity> {
        return weatherApiRequestManager.getCurrentWeather(latitude, longitude, weatherDataProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapCurrentWeather(response, weatherDataProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }

    override suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<HourlyForecastEntity> {
        return weatherApiRequestManager.getHourlyForecast(latitude, longitude, weatherDataProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapHourlyForecast(response, weatherDataProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }

    override suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<DailyForecastEntity> {
        return weatherApiRequestManager.getDailyForecast(latitude, longitude, weatherDataProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapDailyForecast(response, weatherDataProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }

    override suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<YesterdayWeatherEntity> {
        return weatherApiRequestManager.getYesterdayWeather(latitude, longitude, weatherDataProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapYesterdayWeather(response, weatherDataProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }
}