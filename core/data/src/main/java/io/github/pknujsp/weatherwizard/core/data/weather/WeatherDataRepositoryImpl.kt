package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
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
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<CurrentWeatherEntity> {
        return weatherApiRequestManager.getCurrentWeather(latitude, longitude, weatherProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapCurrentWeather(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }

    override suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<HourlyForecastEntity> {
        return weatherApiRequestManager.getHourlyForecast(latitude, longitude, weatherProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapHourlyForecast(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }

    override suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<DailyForecastEntity> {
        return weatherApiRequestManager.getDailyForecast(latitude, longitude, weatherProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapDailyForecast(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }

    override suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<YesterdayWeatherEntity> {
        return weatherApiRequestManager.getYesterdayWeather(latitude, longitude, weatherProvider, requestId)
            .fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapYesterdayWeather(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
    }
}