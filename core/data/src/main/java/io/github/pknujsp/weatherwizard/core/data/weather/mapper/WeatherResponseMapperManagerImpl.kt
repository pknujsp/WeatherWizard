package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
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
    override fun mapCurrentWeather(response: ApiResponseModel, weatherProvider: WeatherProvider): CurrentWeatherEntity =
        when (weatherProvider) {
            is WeatherProvider.Kma -> kmaResponseMapper.mapCurrentWeather(response as KmaCurrentWeatherResponse)
            is WeatherProvider.MetNorway -> metNorwayResponseMapper.mapCurrentWeather(response as MetNorwayCurrentWeatherResponse)
        }

    override fun mapHourlyForecast(response: ApiResponseModel, weatherProvider: WeatherProvider): HourlyForecastEntity =
        when (weatherProvider) {
            is WeatherProvider.Kma -> kmaResponseMapper.mapHourlyForecast(response as KmaHourlyForecastResponse)
            is WeatherProvider.MetNorway -> metNorwayResponseMapper.mapHourlyForecast(response as MetNorwayHourlyForecastResponse)
        }

    override fun mapDailyForecast(response: ApiResponseModel, weatherProvider: WeatherProvider): DailyForecastEntity =
        when (weatherProvider) {
            is WeatherProvider.Kma -> kmaResponseMapper.mapDailyForecast(response as KmaDailyForecastResponse)
            is WeatherProvider.MetNorway -> metNorwayResponseMapper.mapDailyForecast(response as MetNorwayDailyForecastResponse)
        }

    override fun mapYesterdayWeather(response: ApiResponseModel, weatherProvider: WeatherProvider): YesterdayWeatherEntity =
        when (weatherProvider) {
            is WeatherProvider.Kma -> kmaResponseMapper.mapYesterdayWeather(response as KmaYesterdayWeatherResponse)
            is WeatherProvider.MetNorway -> TODO("기능 없음")
        }

}