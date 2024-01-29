package io.github.pknujsp.everyweather.core.data.widget

import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.widget.WidgetType
import java.time.ZonedDateTime


sealed interface SavedWidgetContentState : EntityModel {
    val id: Int
    val widgetType: WidgetType
    val locationType: LocationType
    val updatedAt: ZonedDateTime

    data class Success(
        override val id: Int,
        override val widgetType: WidgetType,
        override val updatedAt: ZonedDateTime,
        override val locationType: LocationType,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val entities: List<EntityWithWeatherProvider>,
    ) : SavedWidgetContentState {

        class EntityWithWeatherProvider(
            val weatherProvider: WeatherProvider,
            val entities: List<WeatherEntityModel>,
        ) {
            inline fun <reified T : WeatherEntityModel> toEntity(): T {
                return entities.first { it is T } as T
            }
        }
    }

    data class Failure(
        override val id: Int,
        override val widgetType: WidgetType,
        override val locationType: LocationType,
        override val updatedAt: ZonedDateTime,
    ) : SavedWidgetContentState

    data class Pending(
        override val id: Int,
        override val widgetType: WidgetType,
        override val locationType: LocationType,
        override val updatedAt: ZonedDateTime,
    ) : SavedWidgetContentState
}