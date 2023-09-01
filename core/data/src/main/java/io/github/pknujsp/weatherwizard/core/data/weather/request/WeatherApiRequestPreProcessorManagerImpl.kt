package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.data.weather.kma.KmaRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.network.ApiRequestParameter
import javax.inject.Inject

class WeatherApiRequestPreProcessorManagerImpl @Inject constructor(
    private val kmaPreProcessor: WeatherRequestPreProcessor
) : WeatherApiRequestPreProcessorManager {
    override suspend fun getCurrentWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getCurrentWeatherRequestParameter(latitude, longitude, requestId)

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override suspend fun getHourlyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getHourlyForecastRequestParameter(latitude, longitude, requestId)

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override suspend fun getDailyForecastRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getDailyForecastRequestParameter(latitude, longitude, requestId)

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): ApiRequestParameter = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> kmaPreProcessor.getYesterdayWeatherRequestParameter(latitude, longitude, requestId)

        is WeatherDataProvider.MetNorway -> TODO()
    }

}