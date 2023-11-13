package io.github.pknujsp.weatherwizard.core.model.widget

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class WidgetEntity(
    val id: Int, val content: Content, val widgetType: WidgetType
) : EntityModel {

    @Serializable
    class Content(
        @SerialName("latitude") val latitude: Double = 0.0,
        @SerialName("longitude") val longitude: Double = 0.0,
        @SerialName("addressName") val addressName: String = "",
        @SerialName("locationType") private val locationTypeKey: Int = LocationType.CurrentLocation.key,
        @SerialName("weatherProvider") private val weatherProviderKey: Int = WeatherDataProvider.default.key,
    ) : EntityModel {
        val locationType = LocationType.fromKey(locationTypeKey)
        val weatherProvider = WeatherDataProvider.fromKey(weatherProviderKey)
        val coordinate = Coordinate(latitude, longitude)
    }
}