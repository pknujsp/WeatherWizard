package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.data.module.ApiResponseMapperModule.KMA_WEATHER_RESPONSE_MAPPER
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.KmaHourlyForecastResponse
import javax.inject.Inject
import javax.inject.Named

class WeatherResponseMapperManagerImpl @Inject constructor(
    @Named(KMA_WEATHER_RESPONSE_MAPPER)
    private val kmaResponseMapper: WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse, KmaCurrentWeatherResponse>
) : WeatherResponseMapperManager {
    override fun mapCurrentWeather(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): CurrentWeatherEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapCurrentWeather(response as KmaCurrentWeatherResponse)
            is WeatherDataProvider.MetNorway -> TODO()
        }

    override fun mapHourlyForecast(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): HourlyForecastEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapHourlyForecast(response as KmaHourlyForecastResponse)
            is WeatherDataProvider.MetNorway -> TODO()
        }

    override fun mapDailyForecast(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): DailyForecastEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapDailyForecast(response as KmaDailyForecastResponse)
            is WeatherDataProvider.MetNorway -> TODO()
        }

    override fun mapYesterdayWeather(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): YesterdayWeatherEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapYesterdayWeather(response as KmaCurrentWeatherResponse)
            is WeatherDataProvider.MetNorway -> TODO()
        }

}