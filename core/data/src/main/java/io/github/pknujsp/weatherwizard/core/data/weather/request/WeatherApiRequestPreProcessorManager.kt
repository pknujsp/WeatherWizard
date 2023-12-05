package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter

interface WeatherApiRequestPreProcessorManager {
    suspend fun getCurrentWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): ApiRequestParameter

    suspend fun getHourlyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): ApiRequestParameter

    suspend fun getDailyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): ApiRequestParameter

    suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): ApiRequestParameter
}