package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import javax.inject.Inject

class WeatherApiRequestPreProcessorManagerImpl @Inject constructor(
    private val kmaPreProcessor: WeatherRequestPreProcessor
) : WeatherApiRequestPreProcessorManager {
    override suspend fun getCurrentWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getCurrentWeatherRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

    override suspend fun getHourlyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getHourlyForecastRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")

    }

    override suspend fun getDailyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getDailyForecastRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

    override suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getYesterdayWeatherRequestParameter(latitude, longitude, requestId)
        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

}