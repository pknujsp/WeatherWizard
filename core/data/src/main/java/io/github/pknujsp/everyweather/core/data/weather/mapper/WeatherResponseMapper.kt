package io.github.pknujsp.everyweather.core.data.weather.mapper

import io.github.pknujsp.everyweather.core.data.ResponseMapper
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel

interface WeatherResponseMapper<out T : WeatherEntityModel> : ResponseMapper {
    fun map(apiResponseModel: ApiResponseModel, majorWeatherEntityType: MajorWeatherEntityType): T
}