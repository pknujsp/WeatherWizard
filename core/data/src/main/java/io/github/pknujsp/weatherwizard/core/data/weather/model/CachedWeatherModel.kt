package io.github.pknujsp.weatherwizard.core.data.weather.model

import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import java.util.concurrent.ConcurrentHashMap

class CachedWeatherModel : EntityModel {
    private val dataMap = ConcurrentHashMap<MajorWeatherEntityType, ApiResponseModel>()

    fun <T : ApiResponseModel> put(majorWeatherEntityType: MajorWeatherEntityType, value: T) {
        dataMap[majorWeatherEntityType] = value
    }

    fun export(majorWeatherEntityTypes: Set<MajorWeatherEntityType>): List<Pair<MajorWeatherEntityType, ApiResponseModel>> {
        val valueList = mutableListOf<Pair<MajorWeatherEntityType, ApiResponseModel>>()
        for (majorWeatherEntityType in majorWeatherEntityTypes) {
            dataMap[majorWeatherEntityType]?.let {
                valueList.add(majorWeatherEntityType to it)
            }
        }

        return valueList
    }

}