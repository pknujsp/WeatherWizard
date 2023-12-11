package io.github.pknujsp.weatherwizard.core.data.weather.request

import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.metnorway.parameter.MetNorwayRequestParameter
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDataSource
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.MetNorwayDataSource
import javax.inject.Inject

internal class WeatherApiRequestManagerImpl(
    private val kmaDataSource: KmaDataSource,
    private val metNorwayDataSource: MetNorwayDataSource,
    private val weatherApiRequestPreProcessorManager: WeatherApiRequestPreProcessorManager<ApiRequestParameter>
) : WeatherApiRequestManager<ApiResponseModel> {

    private suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaDataSource.getCurrentWeather(weatherApiRequestPreProcessorManager.getCurrentWeatherRequestParameter(
            latitude,
            longitude,
            weatherProvider,
            requestId) as KmaCurrentWeatherRequestParameter)

        is WeatherProvider.MetNorway -> metNorwayDataSource.getCurrentWeather(MetNorwayRequestParameter(latitude, longitude, requestId))
    }

    private suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaDataSource.getHourlyForecast(weatherApiRequestPreProcessorManager.getHourlyForecastRequestParameter(
            latitude,
            longitude,
            weatherProvider,
            requestId) as KmaHourlyForecastRequestParameter)

        is WeatherProvider.MetNorway -> metNorwayDataSource.getHourlyForecast(MetNorwayRequestParameter(latitude, longitude, requestId))
    }

    private suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaDataSource.getDailyForecast(weatherApiRequestPreProcessorManager.getDailyForecastRequestParameter(
            latitude,
            longitude,
            weatherProvider,
            requestId) as KmaDailyForecastRequestParameter)

        is WeatherProvider.MetNorway -> metNorwayDataSource.getDailyForecast(MetNorwayRequestParameter(latitude, longitude, requestId))
    }

    private suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> {
            kmaDataSource.getYesterdayWeather(weatherApiRequestPreProcessorManager.getYesterdayWeatherRequestParameter(latitude,
                longitude,
                weatherProvider,
                requestId) as KmaYesterdayWeatherRequestParameter)
        }

        else -> throw IllegalArgumentException("Not supported weather data provider")
    }

    override suspend fun get(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        majorWeatherEntityType: MajorWeatherEntityType,
        requestId: Long
    ) = when (majorWeatherEntityType) {
        MajorWeatherEntityType.CURRENT_CONDITION -> getCurrentWeather(latitude, longitude, weatherProvider, requestId)
        MajorWeatherEntityType.HOURLY_FORECAST -> getHourlyForecast(latitude, longitude, weatherProvider, requestId)
        MajorWeatherEntityType.DAILY_FORECAST -> getDailyForecast(latitude, longitude, weatherProvider, requestId)
        MajorWeatherEntityType.YESTERDAY_WEATHER -> getYesterdayWeather(latitude, longitude, weatherProvider, requestId)
        else -> throw IllegalArgumentException("Not supported major weather entity type")
    }
}