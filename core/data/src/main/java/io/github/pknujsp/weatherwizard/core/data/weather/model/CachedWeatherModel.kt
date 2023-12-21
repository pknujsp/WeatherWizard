package io.github.pknujsp.weatherwizard.core.data.weather.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import java.util.concurrent.ConcurrentHashMap

class CachedWeatherModel : EntityModel {
    private val dataMap = mutableMapOf<MajorWeatherEntityType, WeatherEntityModel>()

    fun <T : WeatherEntityModel> put(majorWeatherEntityType: MajorWeatherEntityType, value: T) {
        dataMap[majorWeatherEntityType] = value
    }

    fun export(majorWeatherEntityTypes: Set<MajorWeatherEntityType>): List<Pair<MajorWeatherEntityType, WeatherEntityModel>> {
        val valueList = mutableListOf<Pair<MajorWeatherEntityType, WeatherEntityModel>>()
        for (majorWeatherEntityType in majorWeatherEntityTypes) {
            valueList.add(majorWeatherEntityType to dataMap[majorWeatherEntityType]!!)
        }

        return valueList
    }

}