package io.github.pknujsp.everyweather.core.data.widget

import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.widget.WidgetStatus
import io.github.pknujsp.everyweather.core.model.widget.WidgetType

data class WidgetSettingsEntity(
    val id: Int,
    val location: LocationTypeModel = LocationTypeModel(),
    val weatherProviders: List<WeatherProvider> = listOf(WeatherProvider.default),
    val widgetType: WidgetType,
    val status: WidgetStatus = WidgetStatus.PENDING,
) : EntityModel

data class WidgetSettingsEntityList(
    val widgetSettings: List<WidgetSettingsEntity>,
) : EntityModel {
    val locationTypeGroups = widgetSettings.groupBy { it.location.locationType }
}
