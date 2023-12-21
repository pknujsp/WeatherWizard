package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import java.time.ZonedDateTime

data class WidgetResponseDBEntity(
    val id: Int,
    val status: WidgetStatus,
    val locationType: LocationType,
    val weatherProvider: WeatherProvider,
    val widgetType: WidgetType,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: ZonedDateTime,
    val entities: List<WeatherEntityModel>,
) : EntityModel {
    inline fun <reified T : WeatherEntityModel> toEntity(): T {
        return entities.first { it is T } as T
    }
}