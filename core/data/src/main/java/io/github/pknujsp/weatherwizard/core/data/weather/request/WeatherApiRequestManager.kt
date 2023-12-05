package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel

interface WeatherApiRequestManager {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): Result<ApiResponseModel>

    suspend fun getHourlyForecast(latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long):
            Result<ApiResponseModel>

    suspend fun getDailyForecast(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): Result<ApiResponseModel>

    suspend fun getYesterdayWeather(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): Result<ApiResponseModel>
}