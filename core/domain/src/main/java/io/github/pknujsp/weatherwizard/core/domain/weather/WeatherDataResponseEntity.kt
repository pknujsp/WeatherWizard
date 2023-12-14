package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import java.time.ZonedDateTime

data class WeatherResponseEntity(
    val weatherDataMajorCategories: Set<MajorWeatherEntityType>,
    val responses: List<EntityModel>,
    val dayNightCalculator: DayNightCalculator,
    val responseTime: ZonedDateTime = ZonedDateTime.now()
) : DomainModel {
    inline fun <reified T : EntityModel> toEntity(): T = responses.first { it is T } as T

    fun export(majorWeatherEntityTypes: Set<MajorWeatherEntityType>) = majorWeatherEntityTypes.associateWith { type ->
        responses.first {
            type.entityClass.isInstance(it)
        }
    }

    val all
        get() = weatherDataMajorCategories.associateWith { type ->
            responses.first {
                type.entityClass.isInstance(it)
            }
        }
}

sealed interface WeatherResponseState {
    val requestId: Long
    val location: LocationTypeModel
    val weatherProvider: WeatherProvider

    data class Failure(
        override val requestId: Long, override val location: LocationTypeModel, override val weatherProvider: WeatherProvider,
    ) : WeatherResponseState

    data class Success(
        override val requestId: Long,
        override val location: LocationTypeModel,
        override val weatherProvider: WeatherProvider,
        val entity: WeatherResponseEntity
    ) : WeatherResponseState

}