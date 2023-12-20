package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

interface WeatherResponseMapperManager<out T : WeatherEntityModel> {

    fun map(response: ApiResponseModel, weatherProvider: WeatherProvider, majorWeatherEntityType: MajorWeatherEntityType): T
    fun map(response: ApiResponseModel, majorWeatherEntityType: MajorWeatherEntityType): T

}