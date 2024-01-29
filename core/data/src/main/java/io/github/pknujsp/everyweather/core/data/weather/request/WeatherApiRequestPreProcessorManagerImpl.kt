package io.github.pknujsp.everyweather.core.data.weather.request

import io.github.pknujsp.everyweather.core.data.weather.WeatherRequestPreProcessor
import io.github.pknujsp.everyweather.core.model.ApiRequestParameter
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

internal class WeatherApiRequestPreProcessorManagerImpl(
    private val kmaPreProcessor: WeatherRequestPreProcessor
) : WeatherApiRequestPreProcessorManager<ApiRequestParameter> {
    override suspend fun getCurrentWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaPreProcessor.getCurrentWeatherRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

    override suspend fun getHourlyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaPreProcessor.getHourlyForecastRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")

    }

    override suspend fun getDailyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaPreProcessor.getDailyForecastRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

    override suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaPreProcessor.getYesterdayWeatherRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

}