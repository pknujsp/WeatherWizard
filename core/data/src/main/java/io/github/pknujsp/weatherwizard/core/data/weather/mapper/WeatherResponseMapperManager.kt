package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel

interface WeatherResponseMapperManager<out T : EntityModel> {

    fun map(response: ApiResponseModel, weatherProvider: WeatherProvider, majorWeatherEntityType: MajorWeatherEntityType): T

}