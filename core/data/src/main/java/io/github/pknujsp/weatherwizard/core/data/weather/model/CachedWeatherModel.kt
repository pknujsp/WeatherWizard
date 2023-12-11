package io.github.pknujsp.weatherwizard.core.data.weather.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import java.util.concurrent.ConcurrentHashMap

class CachedWeatherModel : EntityModel {
    private val dataMap = ConcurrentHashMap<MajorWeatherEntityType, EntityModel>()

    fun <T : EntityModel> put(majorWeatherEntityType: MajorWeatherEntityType, value: T) {
        dataMap[majorWeatherEntityType] = value
    }

    fun export(majorWeatherEntityTypes: Set<MajorWeatherEntityType>): WeatherModel {
        val valueList = mutableListOf<EntityModel>()
        for (majorWeatherEntityType in majorWeatherEntityTypes) {
            dataMap[majorWeatherEntityType]?.let {
                valueList.add(it)
            }
        }

        return WeatherModel(valueList)
    }

}