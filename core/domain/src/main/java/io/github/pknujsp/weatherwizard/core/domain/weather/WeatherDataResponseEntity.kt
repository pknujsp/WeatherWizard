package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.core.annotation.KBindFunc
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

data class ResponseEntity(
    val weatherDataMajorCategories: Set<WeatherDataMajorCategory>, val responses: List<EntityModel>
) : EntityModel {
    inline fun <reified T : EntityModel> toEntity(): T = responses.first { it is T } as T
}


sealed interface ResponseState {
    val requestId: Long
    val coordinate: Coordinate
    val weatherDataProvider: WeatherDataProvider

    data class Failure(
        override val requestId: Long, override val coordinate: Coordinate, override val weatherDataProvider: WeatherDataProvider
    ) : ResponseState

    data class Success(
        override val requestId: Long,
        override val coordinate: Coordinate,
        override val weatherDataProvider: WeatherDataProvider,
        val entity: ResponseEntity
    ) : ResponseState

    data class PartiallySuccess(
        override val requestId: Long,
        override val coordinate: Coordinate,
        override val weatherDataProvider: WeatherDataProvider,
        val entity: ResponseEntity,
        val failedCategories: List<WeatherDataMajorCategory>
    ) : ResponseState
}