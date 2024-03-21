package io.github.pknujsp.everyweather.core.data.weather.mapper

import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

interface WeatherResponseMapperManager<out T : WeatherEntityModel> {
    fun map(
        response: ApiResponseModel,
        weatherProvider: WeatherProvider,
        majorWeatherEntityType: MajorWeatherEntityType,
    ): T
}
