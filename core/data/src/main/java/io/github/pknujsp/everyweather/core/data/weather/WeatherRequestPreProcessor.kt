package io.github.pknujsp.everyweather.core.data.weather

import io.github.pknujsp.everyweather.core.model.ApiRequestParameter

interface WeatherRequestPreProcessor {
    suspend fun getCurrentWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long,
    ): ApiRequestParameter

    suspend fun getHourlyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long,
    ): ApiRequestParameter

    suspend fun getDailyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long,
    ): ApiRequestParameter

    suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long,
    ): ApiRequestParameter
}
