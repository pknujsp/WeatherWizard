package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import javax.inject.Inject

internal class WeatherResponseMapperManagerImpl(
    private val kmaResponseMapper: WeatherResponseMapper<EntityModel>,
    private val metNorwayResponseMapper: WeatherResponseMapper<EntityModel>
) : WeatherResponseMapperManager<EntityModel> {

    override fun map(
        response: ApiResponseModel, weatherProvider: WeatherProvider, majorWeatherEntityType: MajorWeatherEntityType
    ) = when (weatherProvider) {
        is WeatherProvider.Kma -> kmaResponseMapper.map(response, majorWeatherEntityType)
        is WeatherProvider.MetNorway -> metNorwayResponseMapper.map(response, majorWeatherEntityType)
    }
}