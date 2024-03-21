package io.github.pknujsp.everyweather.core.data.weather.model

import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType

class CachedWeatherModel : EntityModel {
    private val dataMap = mutableMapOf<MajorWeatherEntityType, WeatherEntityModel>()

    fun <T : WeatherEntityModel> put(
        majorWeatherEntityType: MajorWeatherEntityType,
        value: T,
    ) {
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
