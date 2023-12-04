package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

data class WeatherResponseEntity(
    val weatherDataMajorCategories: Set<MajorWeatherEntityType>, val responses: List<EntityModel>
) : EntityModel {
    inline fun <reified T : EntityModel> toEntity(): T = responses.first { it is T } as T
}

sealed interface WeatherResponseState {
    val requestId: Long
    val coordinate: LocationModel
    val weatherProvider: WeatherProvider

    data class Failure(
        override val requestId: Long, override val coordinate: LocationModel, override val weatherProvider: WeatherProvider
    ) : WeatherResponseState

    data class Success(
        override val requestId: Long,
        override val coordinate: LocationModel,
        override val weatherProvider: WeatherProvider,
        val entity: WeatherResponseEntity
    ) : WeatherResponseState

    data class PartiallySuccess(
        override val requestId: Long,
        override val coordinate: LocationModel,
        override val weatherProvider: WeatherProvider,
        val entity: WeatherResponseEntity,
        val failedCategories: List<MajorWeatherEntityType>
    ) : WeatherResponseState
}