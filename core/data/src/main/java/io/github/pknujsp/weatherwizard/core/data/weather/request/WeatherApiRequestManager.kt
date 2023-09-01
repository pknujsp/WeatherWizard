package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel

interface WeatherApiRequestManager {
    fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel>

    fun getHourlyForecast(latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long):
            Result<ApiResponseModel>

    fun getDailyForecast(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel>

    fun getYesterdayWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel>
}