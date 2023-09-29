package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.metnorway.parameter.MetNorwayRequestParameter
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDataSource
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayDataSource
import javax.inject.Inject

class WeatherApiRequestManagerImpl @Inject constructor(
    private val kmaDataSource: KmaDataSource,
    private val metNorwayDataSource: MetNorwayDataSource,
    private val weatherApiRequestPreProcessorManager: WeatherApiRequestPreProcessorManager
) : WeatherApiRequestManager {

    override suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma ->
            kmaDataSource.getCurrentWeather(weatherApiRequestPreProcessorManager.getCurrentWeatherRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaCurrentWeatherRequestParameter)

        is WeatherDataProvider.MetNorway -> metNorwayDataSource.getCurrentWeather(MetNorwayRequestParameter(latitude, longitude, requestId))
    }

    override suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma ->
            kmaDataSource.getHourlyForecast(weatherApiRequestPreProcessorManager.getHourlyForecastRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaHourlyForecastRequestParameter)


        is WeatherDataProvider.MetNorway -> metNorwayDataSource.getHourlyForecast(MetNorwayRequestParameter(latitude, longitude, requestId))
    }

    override suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<ApiResponseModel> = when (weatherDataProvider) {
        is WeatherDataProvider.Kma ->
            kmaDataSource.getDailyForecast(weatherApiRequestPreProcessorManager.getDailyForecastRequestParameter(latitude,
                longitude,
                weatherDataProvider,
                requestId) as KmaDailyForecastRequestParameter)

        is WeatherDataProvider.MetNorway -> metNorwayDataSource.getDailyForecast(MetNorwayRequestParameter(latitude, longitude, requestId))
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

        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

}