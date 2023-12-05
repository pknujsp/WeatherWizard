package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

data class WidgetSettingsEntity(
    val id: Int,
    val location: LocationTypeModel = LocationTypeModel(),
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val widgetType: WidgetType,
) : EntityModel

data class WidgetSettingsEntityList(
    val widgetSettings: List<WidgetSettingsEntity>
) : EntityModel {
    val locationTypeGroups = LocationType.enums.associateWith { locationType ->
        widgetSettings.filter { it.location.locationType == locationType }
    }

}