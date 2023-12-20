package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherApiResponseWrapper
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastApiResponseWrapper
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastApiResponseWrapper
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherApiResponseWrapper

internal class WeatherResponseMapperManagerImpl(
    private val kmaResponseMapper: WeatherResponseMapper<WeatherEntityModel>,
    private val metNorwayResponseMapper: WeatherResponseMapper<WeatherEntityModel>,
    private val jsonParser: JsonParser
) : WeatherResponseMapperManager<WeatherEntityModel> {

    override fun map(
        response: ApiResponseModel, weatherProvider: WeatherProvider, majorWeatherEntityType: MajorWeatherEntityType
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaResponseMapper.map(response, majorWeatherEntityType)
        is WeatherProvider.MetNorway -> metNorwayResponseMapper.map(response, majorWeatherEntityType)
    }

    override fun map(response: ApiResponseModel, majorWeatherEntityType: MajorWeatherEntityType): WeatherEntityModel {
        val byteArray = jsonParser.parseToByteArray(response)
        return when (majorWeatherEntityType) {
            MajorWeatherEntityType.CURRENT_CONDITION -> CurrentWeatherApiResponseWrapper(byteArray)
            MajorWeatherEntityType.HOURLY_FORECAST -> HourlyForecastApiResponseWrapper(byteArray)
            MajorWeatherEntityType.DAILY_FORECAST -> DailyForecastApiResponseWrapper(byteArray)
            MajorWeatherEntityType.YESTERDAY_WEATHER -> YesterdayWeatherApiResponseWrapper(byteArray)
            else -> throw IllegalArgumentException("Unknown majorWeatherEntityType: $majorWeatherEntityType")
        }
    }
}