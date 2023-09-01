package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDataSource
import javax.inject.Inject

class WeatherApiRequestManagerImpl @Inject constructor(
    private val kmaDataSource: KmaDataSource,
    private val weatherApiRequestPreProcessorManager: WeatherApiRequestPreProcessorManager
) : WeatherApiRequestManager {


    override fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma -> {
            TODO()
        }

        is WeatherDataProvider.MetNorway -> TODO()
    }

    override fun getHourlyForecast(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel> {
        TODO("Not yet implemented")
    }

    override fun getDailyForecast(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel> {
        TODO("Not yet implemented")
    }

    override fun getYesterdayWeather(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<ApiResponseModel> {
        TODO("Not yet implemented")
    }


}