package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

interface WeatherApiRequestPreProcessorManager<out T : ApiRequestParameter> {
    suspend fun getCurrentWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): T

    suspend fun getHourlyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): T

    suspend fun getDailyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): T

    suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): T
}