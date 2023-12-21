package io.github.pknujsp.weatherwizard.core.data.weather.mapper

import io.github.pknujsp.weatherwizard.core.data.ResponseMapper
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel

interface WeatherResponseMapper<out T : WeatherEntityModel> : ResponseMapper {
    fun map(apiResponseModel: ApiResponseModel, majorWeatherEntityType: MajorWeatherEntityType): T
}