package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider


internal class WeatherResponseMapperManagerImpl(
    private val kmaResponseMapper: WeatherResponseMapper<WeatherEntityModel>,
    private val metNorwayResponseMapper: WeatherResponseMapper<WeatherEntityModel>,
) : WeatherResponseMapperManager<WeatherEntityModel> {

    override fun map(
        response: ApiResponseModel, weatherProvider: WeatherProvider, majorWeatherEntityType: MajorWeatherEntityType
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaResponseMapper.map(response, majorWeatherEntityType)
        is WeatherProvider.MetNorway -> metNorwayResponseMapper.map(response, majorWeatherEntityType)
    }

}