package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

data class WidgetSettingsEntity(
    val id: Int,
    val location: LocationTypeModel = LocationTypeModel(),
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val widgetType: WidgetType,
    val status: WidgetStatus = WidgetStatus.PENDING
) : EntityModel

data class WidgetSettingsEntityList(
    val widgetSettings: List<WidgetSettingsEntity>
) : EntityModel {
    val locationTypeGroups = widgetSettings.groupBy { it.location.locationType }
}