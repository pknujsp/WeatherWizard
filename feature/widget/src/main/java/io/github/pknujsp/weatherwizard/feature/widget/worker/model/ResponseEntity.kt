package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

data class ResponseEntity(
    val address: String,
    val coordinate: Coordinate,
    val widgetId: Int,
    val widgetType: WidgetType,
    val weatherDataProvider: WeatherDataProvider,
    val responses: List<EntityModel>
) : EntityModel {
    val isSuccessful: Boolean = responses.isNotEmpty()

    inline fun <reified T : EntityModel> toEntity(): T {
        return responses.first { it is T } as T
    }

}