package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaHourlyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaYesterdayWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.metnorway.response.MetNorwayHourlyForecastResponse
import javax.inject.Inject

class WeatherResponseMapperManagerImpl @Inject constructor(
    private val kmaResponseMapper: WeatherResponseMapper<KmaCurrentWeatherResponse, KmaHourlyForecastResponse, KmaDailyForecastResponse,
            KmaYesterdayWeatherResponse>,
    private val metNorwayResponseMapper: WeatherResponseMapper<MetNorwayCurrentWeatherResponse, MetNorwayHourlyForecastResponse,
            MetNorwayDailyForecastResponse, KmaYesterdayWeatherResponse>
) : WeatherResponseMapperManager {
    override fun mapCurrentWeather(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): CurrentWeatherEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapCurrentWeather(response as KmaCurrentWeatherResponse)
            is WeatherDataProvider.MetNorway -> metNorwayResponseMapper.mapCurrentWeather(response as MetNorwayCurrentWeatherResponse)
        }

    override fun mapHourlyForecast(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): HourlyForecastEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapHourlyForecast(response as KmaHourlyForecastResponse)
            is WeatherDataProvider.MetNorway -> metNorwayResponseMapper.mapHourlyForecast(response as MetNorwayHourlyForecastResponse)
        }

    override fun mapDailyForecast(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): DailyForecastEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapDailyForecast(response as KmaDailyForecastResponse)
            is WeatherDataProvider.MetNorway -> metNorwayResponseMapper.mapDailyForecast(response as MetNorwayDailyForecastResponse)
        }

    override fun mapYesterdayWeather(response: ApiResponseModel, weatherDataProvider: WeatherDataProvider): YesterdayWeatherEntity =
        when (weatherDataProvider) {
            is WeatherDataProvider.Kma -> kmaResponseMapper.mapYesterdayWeather(response as KmaYesterdayWeatherResponse)
            is WeatherDataProvider.MetNorway -> TODO("기능 없음")
        }

}