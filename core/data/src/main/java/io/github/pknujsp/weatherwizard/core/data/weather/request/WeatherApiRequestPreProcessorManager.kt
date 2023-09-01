package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.network.ApiRequestParameter

interface WeatherApiRequestPreProcessorManager {
    suspend fun getCurrentWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): ApiRequestParameter

    suspend fun getHourlyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): ApiRequestParameter

    suspend fun getDailyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): ApiRequestParameter

    suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): ApiRequestParameter
}