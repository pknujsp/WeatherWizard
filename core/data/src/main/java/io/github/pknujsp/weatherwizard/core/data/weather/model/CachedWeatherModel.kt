package io.github.pknujsp.weatherwizard.core.data.weather.model

import io.github.pknujsp.weatherwizard.core.data.weather.RequestWeatherData
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

data class CachedWeatherModel(
    val requestWeatherData: RequestWeatherData
) : EntityModel {
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

    operator fun contains(majorWeatherEntityTypes: Set<MajorWeatherEntityType>): Boolean =
        requestWeatherData.majorWeatherEntityTypes.containsAll(majorWeatherEntityTypes)
}