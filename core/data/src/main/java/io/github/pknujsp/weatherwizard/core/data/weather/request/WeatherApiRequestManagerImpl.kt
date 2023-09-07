package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSource
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import javax.inject.Inject

class WeatherApiRequestManagerImpl @Inject constructor(
    private val kmaDataSource: KmaDataSource, private val weatherApiRequestPreProcessorManager: WeatherApiRequestPreProcessorManager
) : WeatherApiRequestManager {


    override suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> {
            kmaDataSource.getCurrentWeather(weatherApiRequestPreProcessorManager.getCurrentWeatherRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaCurrentWeatherRequestParameter)
        }

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> {
            kmaDataSource.getHourlyForecast(weatherApiRequestPreProcessorManager.getHourlyForecastRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaHourlyForecastRequestParameter)
        }

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> {
            kmaDataSource.getDailyForecast(weatherApiRequestPreProcessorManager.getDailyForecastRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaDailyForecastRequestParameter)
        }

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> {
            kmaDataSource.getYesterdayWeather(weatherApiRequestPreProcessorManager.getYesterdayWeatherRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaYesterdayWeatherRequestParameter)
        }

        is WeatherDataProvider.MetNorway -> TODO()
    }


}